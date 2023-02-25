package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.adapter.PharmacyProductAdapter;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Product;

import java.util.ArrayList;

public class PharmacyHomePage extends AppCompatActivity {

    // UI reference
    private TextView Name;
    private RelativeLayout rl_Products;
    private RelativeLayout rl_Orders;
    private TextView tv_Products;
    private TextView tv_Orders;
    private ImageView img_product;
    private RecyclerView recyclerView;
    private LottieAnimationView lottieAnimationView;
    private SearchView search_product;
    private TextView tv_no_date;
    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_home_page);

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        // initiate UI  reference
        Name = findViewById(R.id.tv_name);
        rl_Products = findViewById(R.id.rl_3);
        rl_Orders = findViewById(R.id.rl_4);
        tv_Products = findViewById(R.id.tv_product);
        tv_Orders = findViewById(R.id.tv_orders);
        img_product = findViewById(R.id.img_product);
        search_product = findViewById(R.id.search_product);
        tv_no_date = findViewById(R.id.tv_no_date);
        tv_no_date.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.rv_products);
        lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.setVisibility(View.VISIBLE);
        getDate();
    }


    private void getDate() {

        // get user id
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userid = user.getUid();

        // get the date from firebase
        reference.child("Product").child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // create arraylist that handel the date
                        ArrayList<Product> productArrayList = new ArrayList<>();
                        // check if there is products
                        if (snapshot.exists()) {
                            img_product.setVisibility(View.INVISIBLE); // make the image invisible
                            for (DataSnapshot db : snapshot.getChildren()) {
                                // get all the date and put it in the arraylist
                                Product product = db.getValue(Product.class);
                                productArrayList.add(product);
                            }

                            // send the list to this method
                            UpdateUI(productArrayList);
                            searchEngine(productArrayList);
                        } else {
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            img_product.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage().ShowShortMessage(error.getMessage(),
                                PharmacyHomePage.this);
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void UpdateUI(ArrayList<Product> productArrayList) {

        //  get the list and pass it to TransfersAdapter class
        PharmacyProductAdapter adapter = new PharmacyProductAdapter(productArrayList, this);
        // make the recyclerview Linear vertical
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        // show date by recyclerView
        recyclerView.setAdapter(adapter);
        lottieAnimationView.setVisibility(View.INVISIBLE);
    }

    public void onAddProduct(View view) {
        startActivity(new Intent(PharmacyHomePage.this, PharmacyAddProduct.class));

    }

    private void searchEngine(ArrayList<Product> productArrayList) {
        search_product.clearFocus();
        search_product.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Product> products = new ArrayList<>();
                for (Product p : productArrayList) {
                    if (p.getProduct_name().toLowerCase().contains(newText.toLowerCase())) {
                        products.add(p);
                    }
                }
                if (products.isEmpty()) {
                    tv_no_date.setVisibility(View.VISIBLE);
                }
                tv_no_date.setVisibility(View.INVISIBLE);
                UpdateUI(products);
                return true;
            }
        });
    }

    public void onProductClick(View view) {
        rl_Products.setVisibility(View.VISIBLE);
        rl_Orders.setVisibility(View.INVISIBLE);

        tv_Products.setTextColor(getResources().getColor(R.color.black));
        tv_Products.setBackgroundResource(R.drawable.shap_02);

        tv_Orders.setTextColor(getResources().getColor(R.color.white));
        tv_Orders.setBackgroundResource(R.drawable.shap_01);

    }

    public void onOrderClick(View view) {
        rl_Products.setVisibility(View.INVISIBLE);
        rl_Orders.setVisibility(View.VISIBLE);

        tv_Products.setTextColor(getResources().getColor(R.color.white));
        tv_Products.setBackgroundResource(R.drawable.shap_01);

        tv_Orders.setTextColor(getResources().getColor(R.color.black));
        tv_Orders.setBackgroundResource(R.drawable.shap_02);
    }
}