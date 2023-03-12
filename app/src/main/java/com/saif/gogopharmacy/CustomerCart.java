package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.adapter.CustomerCartOrdersAdapter;
import com.saif.gogopharmacy.adapter.ShowPharmacyAdapter;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Cart;
import com.saif.gogopharmacy.model.Order;
import com.saif.gogopharmacy.model.Pharmacy;
import com.saif.gogopharmacy.model.Product;

import java.util.ArrayList;

public class CustomerCart extends AppCompatActivity {

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    // UI elements
    private RecyclerView recyclerView;
    private TextView basket_cart_number;
    private TextView delivery_fee_number;
    private TextView total_amount_number;
    private ImageView imageView;
    private LinearLayout total_layout;
    private Button confirm_order;
    private TextView tv_no_orders;

    private String pharmacyId;
    private ArrayList<Cart> carts;
    private Cart cart;


    private double total_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_cart);

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        // initiate UI elements
        recyclerView = findViewById(R.id.rv_orders);
        basket_cart_number = findViewById(R.id.tv_basket_cart_number);
        delivery_fee_number = findViewById(R.id.tv_delivery_fee_number);
        total_amount_number = findViewById(R.id.tv_total_amount_number);
        imageView = findViewById(R.id.img_pharmacy);
        total_layout = findViewById(R.id.ll_2);
        confirm_order = findViewById(R.id.btn_confirm_Order);
        tv_no_orders = findViewById(R.id.tv_no_orders);


        // Get a reference to the preferences object, which is Pharmacy_Id
        SharedPreferences preferences = getSharedPreferences("Pharmacy_Id", Context.MODE_PRIVATE);
        // Get the string value for the key, or return "default_value" if the key doesn't exist
        pharmacyId = preferences.getString("pharmacy_id", "");

        getOrdersToCart();

    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    private void getOrdersToCart() {
        // get the products
        reference.child("Cart").child(firebaseAuth.getUid()).child(pharmacyId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        carts = new ArrayList<>();
                        if (snapshot.exists()) {
                            imageView.setVisibility(View.INVISIBLE);
                            total_layout.setVisibility(View.VISIBLE);
                            confirm_order.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            tv_no_orders.setVisibility(View.INVISIBLE);
                            for (DataSnapshot db : snapshot.getChildren()) {
                                // handel the data with cart model
                                cart = db.getValue(Cart.class);
                                carts.add(cart); // add the data to the list
                            }
                            UpdateRecyclerViewUI(carts); // send list to this method
                            UpdateUI(carts);
                        } else {
                            tv_no_orders.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            total_layout.setVisibility(View.INVISIBLE);
                            confirm_order.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // show error message if there is an issue
                        new ToastMessage().ShowShortMessage(error.getMessage(),
                                CustomerCart.this);
                    }
                });
    }

    private void UpdateUI(ArrayList<Cart> carts) {
        double total_basket = 0;

        double price = 0;
        double total_delivery = Double.parseDouble(getIntent().getStringExtra("delivery_fee"));
        for (Cart cart : carts) {
            price = Double.parseDouble(cart.getPrice());
            total_basket = total_basket + price;

        }
        total_amount = total_basket + total_delivery;

        basket_cart_number.setText(String.valueOf(total_basket));
        delivery_fee_number.setText(String.valueOf(total_delivery));
        total_amount_number.setText(String.valueOf(total_amount));
    }

    private void UpdateRecyclerViewUI(ArrayList<Cart> carts) {
        //  get the list and pass it to CustomerCartOrdersAdapter class
        CustomerCartOrdersAdapter adapter = new CustomerCartOrdersAdapter(carts, CustomerCart.this);

        adapter.notifyDataSetChanged();
        // make the recyclerview Linear vertical
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        // show date by recyclerView
        recyclerView.setAdapter(adapter);
    }

    public void onConfirmOrder(View view) {

        // get product quantity from the list
        for (Cart c : carts) {
            // get the quantity from database
            reference.child("Product")
                    .child(c.getPharmacy_id())
                    .child(c.getProduct_name())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // attribute to calculate quantity
                            int cart_qty = 0;
                            int product_qty = 0;
                            int total_qty = 0;

                            // get the old quantity and subtract with new quantity
                            Product product = snapshot.getValue(Product.class);

                            product_qty = Integer.valueOf(product.getQuantity());
                            cart_qty = Integer.valueOf(cart.getQuantity());
                            total_qty = product_qty - cart_qty;

                            // update quantity
                            reference.child("Product")
                                    .child(c.getPharmacy_id())
                                    .child(c.getProduct_name())
                                    .child("quantity")
                                    .setValue(String.valueOf(total_qty));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            new ToastMessage().ShowShortMessage(error.getMessage(),
                                    CustomerCart.this);
                        }
                    });
        }
        // after updating the quantity call this method to add the order
        storeOrder();

    }

    private void storeOrder() {

        // get the orders details
        String order_by = firebaseAuth.getUid();
        String order_to = pharmacyId;
        String tot_amount = String.valueOf(total_amount);
        String state = "preparing";
        Long date = System.currentTimeMillis();  // return current time in milliseconds

        // store the order to the firebase
        Order order = new Order(String.valueOf(date), order_by, order_to, tot_amount, state, date);
        reference.child("Orders").
                child(String.valueOf(date))
                .setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for (Cart c : carts) {
                            // cet all the product from the cart
                            // store it in the database
                            reference.child("Orders")
                                    .child(String.valueOf(date))
                                    .child(c.getProduct_name())
                                    .setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            // clear the cart
                                            reference.child("Cart")
                                                    .child(order_by)
                                                    .removeValue();
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            new ToastMessage().ShowShortMessage(e.getMessage(),
                                                    CustomerCart.this);
                                        }
                                    });

                        }
                    }
                });
    }


}



