package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Cart;
import com.saif.gogopharmacy.model.Product;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CustomerProductDetails extends AppCompatActivity {

    // UI elements
    private ImageView product_image;
    private TextView product_name;
    private TextView product_description;
    private TextView product_price;
    private TextView product_final_price;
    private TextView product_qty;
    private String product_id;
    private String productName;
    private ImageButton product_increase;
    private String product_img;


    // for price and qty calculate
    private int qty = 1;
    double price_double = 0;
    double finalCost = 0;
    double final_price_double = 0;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_details);

        product_image = findViewById(R.id.img_product_customer);
        product_name = findViewById(R.id.tv_product_name_customer);
        product_description = findViewById(R.id.tv_product_description_customer);
        product_price = findViewById(R.id.tv_product_price_customer);
        product_final_price = findViewById(R.id.tv_product_final_price_customer);
        product_qty = findViewById(R.id.tv_product_qty);

        product_id = getIntent().getStringExtra("product_id");
        productName = getIntent().getStringExtra("product_name");

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        getProductInfo();

    }

    private void getProductInfo() {
        reference.child("Product")
                .child(product_id)
                .child(productName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product product = snapshot.getValue(Product.class);
                        product_name.setText(product.getProduct_name());
                        product_description.setText(product.getProduct_description());
                        product_price.setText(product.getPrice());
                        product_final_price.setText(product.getFinal_price());
                        product_img = product.getImage();
                        try {
                            Picasso.get().
                                    load(product.getImage()).
                                    placeholder(R.drawable.medicine).
                                    into(product_image);
                        } catch (Exception e) {
                            product_image.setImageResource(R.drawable.medicine);
                        }
                        if (product.getDiscount_price().length() > 0) {
                            final_price_double = Double.parseDouble(product.getFinal_price());
                            finalCost = Double.parseDouble(product.getFinal_price());
                            product_final_price.setVisibility(View.VISIBLE);
                            // add strike through on price
                            product_price.setPaintFlags(product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            price_double = Double.parseDouble(product.getPrice());
                            finalCost = Double.parseDouble(product.getPrice());
                            product_final_price.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage().ShowShortMessage(error.getMessage(), CustomerProductDetails.this);
                    }
                });
    }

    public void onIncreaseQty(View view) {
        qty++;
        if (final_price_double > 0) {
            finalCost = finalCost + final_price_double;
            product_final_price.setText(String.valueOf(finalCost));
            product_qty.setText(String.valueOf(qty));
        } else {
            finalCost = finalCost + price_double;
            product_price.setText(String.valueOf(finalCost));
            product_qty.setText(String.valueOf(qty));
        }


    }

    public void onDecreaseQty(View view) {

        if (qty > 1) {
            qty--;
            if (final_price_double > 0) {
                finalCost = finalCost - final_price_double;
                product_final_price.setText(String.valueOf(finalCost));
                product_qty.setText(String.valueOf(qty));
            } else {
                finalCost = finalCost - price_double;
                product_price.setText(String.valueOf(finalCost));
                product_qty.setText(String.valueOf(qty));
            }
        }

    }

    public void onAddProductToCart(View view) {
        // get the product info
        String productName = product_name.getText().toString();

        // store into the product into firebase
        // path Cart/CustomerId
        reference.child("Cart").child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // check if there is order in the cart
                        if (!snapshot.exists()) {

                            // handel by cart
                            Cart cart = new Cart(productName,
                                    String.valueOf(finalCost),
                                    product_img, String.valueOf(qty), product_id);
                            // store the order in firebase
                            reference.child("Cart")
                                    .child(firebaseAuth.getUid())
                                    .child(product_id)
                                    .child(productName)
                                    .setValue(cart);

                            // inform the user
                            new ToastMessage().ShowShortMessage("Added to cart",
                                    CustomerProductDetails.this);
                            finish();
                        } else {
                            // if the cart have order you can order more products from same pharmacy
                            // check if the products from same pharmacy add it else show error message
                            reference.child("Cart")
                                    .child(firebaseAuth.getUid())
                                    .child(product_id)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            // check if the id of the pharmacy
                                            if (snapshot.exists()) {
                                                // handel by cart
                                                Cart cart = new Cart(productName,
                                                        String.valueOf(finalCost),
                                                        product_img, String.valueOf(qty), product_id);

                                                // store the order in firebase
                                                reference.child("Cart")
                                                        .child(firebaseAuth.getUid())
                                                        .child(product_id)
                                                        .child(productName)
                                                        .setValue(cart);

                                                // inform the user
                                                new ToastMessage().ShowShortMessage("Added to cart",
                                                        CustomerProductDetails.this);
                                                finish();

                                            } else {
                                                // show error message if he order from another pharmacy
                                                new ToastMessage().ShowShortMessage("There is order in your cart From another pharmacy",
                                                        CustomerProductDetails.this);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            new ToastMessage().ShowShortMessage(error.getMessage(),
                                                    CustomerProductDetails.this);
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage().ShowShortMessage(error.getMessage(),
                                CustomerProductDetails.this);
                    }
                });


    }

    public void onBackClick(View view) {
        onBackPressed();
    }


}