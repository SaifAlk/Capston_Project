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
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.adapter.PharmacyProductAdapter;
import com.saif.gogopharmacy.adapter.UserPharmacyProductAdapter;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Pharmacy;
import com.saif.gogopharmacy.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PharmacyDetails extends AppCompatActivity {

    // UI elements
    private ImageView pharmacy_image;
    private TextView pharmacy_city;
    private TextView pharmacy_delivery_fee;
    private TextView pharmacy_pharmacy_number;
    private ImageView no_data;
    private RecyclerView recyclerView;
    private LottieAnimationView lottieAnimationView;
    private TextView no_product;
    private SearchView searchView;
    private String userid;
    private String delivery_fee;


    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_details);

        // initiate UI
        pharmacy_image = findViewById(R.id.img_pharmacy_details);
        pharmacy_city = findViewById(R.id.tv_city);
        pharmacy_delivery_fee = findViewById(R.id.tv_delivery_fee);
        pharmacy_pharmacy_number = findViewById(R.id.tv_pharmacy_number);
        no_data = findViewById(R.id.img_no_data);
        recyclerView = findViewById(R.id.rv_products);
        lottieAnimationView = findViewById(R.id.animationView);
        no_product = findViewById(R.id.tv_no_date_details);
        searchView = findViewById(R.id.search_product);

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        // make animation visible
        lottieAnimationView.setVisibility(View.VISIBLE);
        no_product.setVisibility(View.INVISIBLE);

        // get the pharmacy id
        userid = getIntent().getStringExtra("pharmacy_id");

        getPharmacyInformation();
        getPharmacyProducts();
    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    private void getPharmacyInformation() {
        // get pharmacy details
        reference.child("User").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Pharmacy pharmacy = snapshot.getValue(Pharmacy.class);
                    pharmacy_city.setText(pharmacy.getCity());
                    pharmacy_delivery_fee.setText(pharmacy.getDelivery_fee());
                    pharmacy_pharmacy_number.setText(pharmacy.getPhone_number());
                    delivery_fee = pharmacy.getDelivery_fee();
                    try {
                        Picasso.get().
                                load(pharmacy.getProfile_image()).
                                placeholder(R.drawable.medicine).
                                into(pharmacy_image);
                    } catch (Exception e) {
                        pharmacy_image.setImageResource(R.drawable.medicine);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new ToastMessage().ShowShortMessage(error.getMessage(),
                        PharmacyDetails.this);
            }
        });

    }

    private void getPharmacyProducts() {
        // get the date from firebase
        reference.child("Product").child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // create arraylist that handel the date
                        ArrayList<Product> productArrayList = new ArrayList<>();
                        // check if there is products
                        if (snapshot.exists()) {
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage().ShowShortMessage(error.getMessage(),
                                PharmacyDetails.this);
                        lottieAnimationView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void searchEngine(ArrayList<Product> productArrayList) {
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                        no_product.setVisibility(View.INVISIBLE);

                    }
                }
                if(products.isEmpty())
                {
                    no_product.setVisibility(View.VISIBLE);
                }
                UpdateUI(products);
                return true;
            }
        });
    }

    private void UpdateUI(ArrayList<Product> productArrayList) {
        //  get the list and pass it to TransfersAdapter class
        UserPharmacyProductAdapter adapter = new UserPharmacyProductAdapter(productArrayList, this);
        // make the recyclerview Linear vertical
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        // show date by recyclerView
        recyclerView.setAdapter(adapter);
        lottieAnimationView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPharmacyProducts();
        getPharmacyInformation();
    }

    public void onCartClick(View view)
    {
        Intent intent = new Intent(PharmacyDetails.this, CustomerCart.class);
        intent.putExtra("delivery_fee",delivery_fee);
        startActivity(intent);
    }
}