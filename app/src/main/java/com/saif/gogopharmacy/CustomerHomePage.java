package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.saif.gogopharmacy.adapter.ShowPharmacyAdapter;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Customer;
import com.saif.gogopharmacy.model.Pharmacy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerHomePage extends AppCompatActivity implements LocationListener {

    // UI reference
    private ImageView customer_image;
    private ImageView img_no_data;
    private TextView customer_name;
    private RelativeLayout rl_Pharmacy;
    private RelativeLayout rl_Orders;
    private TextView tv_Pharmacy;
    private TextView tv_Orders;
    private LottieAnimationView lottieAnimationView;
    private RecyclerView recyclerView;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    // permission codes
    private static final int LOCATION_FINE_REQUEST_CODE = 1;
    // permission array
    private String[] LocationFinePermission;
    // Lan and Lon location
    private double latitude;
    private double longitude;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        // initiate UI  reference
        customer_image = findViewById(R.id.img_cir_PharmacyHomePage);
        customer_name = findViewById(R.id.tv_name);
        rl_Pharmacy = findViewById(R.id.rl_3_customer);
        rl_Orders = findViewById(R.id.rl_4_customer);
        tv_Pharmacy = findViewById(R.id.tv_pharmacys);
        tv_Orders = findViewById(R.id.tv_orders);
        lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.setVisibility(View.VISIBLE);
        img_no_data = findViewById(R.id.img_pharmacy);
        img_no_data.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.rv_pharmacy);



        getUserData();
        CheckLocationPermission();



    }

    private void CheckLocationPermission() {
        // initiate permission
        LocationFinePermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        // check if the user allow to use his location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            detectLocation();
        } else {
            ActivityCompat.requestPermissions(this, LocationFinePermission, LOCATION_FINE_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private boolean detectLocation() {
        try {
            // get the location service
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // if the SDK > 28 and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // check if the internet and location is working
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        && isConnected() && locationManager.isLocationEnabled()) {
                    // get location by internet
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    return true;
                } else {
                    // show error message
                    new ToastMessage().ShowLongMessage("Please Enable Internet And Location", this);
                }
            }

        } catch (Exception e) {
            Log.e("detectLocation", e.getMessage());
        }
        return false;
    }

    public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            // check internet is it enable
            if (nInfo != null && nInfo.isConnected()) {
                return true;
            }

        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return false;
    }
    private void getUserData() {
        reference.child("User").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Customer customer = snapshot.getValue(Customer.class);
                            customer_name.setText(customer.getFull_name());
                            try {
                                Picasso.get().
                                        load(customer.getProfile_image()).
                                        placeholder(R.drawable.medicine).
                                        into(customer_image);
                            } catch (Exception e) {
                                customer_image.setImageResource(R.drawable.profile);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void getPharmacy() {

        reference.child("User")
                .orderByChild("account_type")
                .equalTo("pharmacy")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Pharmacy> pharmacies = new ArrayList<>();
                        if (snapshot.exists())
                        {

                            for (DataSnapshot db : snapshot.getChildren()) {
                                Pharmacy pharmacy = db.getValue(Pharmacy.class);
                                pharmacies.add(pharmacy);

                            }
                            UpdateUI(pharmacies);
                        } else {
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                            img_no_data.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage().ShowShortMessage(error.getMessage(),
                                CustomerHomePage.this);
                    }
                });

    }

    private void UpdateUI(ArrayList<Pharmacy> pharmacies) {
        ArrayList<Pharmacy> pharmacies1 = new ArrayList<>();
        for (Pharmacy p : pharmacies) {
            Location locationB = new Location("");
            Location locationA = new Location("");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            locationB.setLatitude(p.getLatitude());
            locationB.setLongitude(p.getLongitude());
            float distance = Math.round(locationA.distanceTo(locationB)) / 1000; // in km
            if (distance <= 3.0) {
                pharmacies1.add(p);
            }
        }
        UpdateRecyclerViewUI(pharmacies1);
    }

    private void UpdateRecyclerViewUI(ArrayList<Pharmacy> pharmacies1) {
        //  get the list and pass it to TransfersAdapter class
        ShowPharmacyAdapter adapter = new ShowPharmacyAdapter(pharmacies1, this);
        // make the recyclerview Linear vertical
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        // show date by recyclerView
        recyclerView.setAdapter(adapter);
        lottieAnimationView.setVisibility(View.INVISIBLE);
    }

    public void onLogOutClick(View view) {
        firebaseAuth.signOut();
        startActivity(new Intent(CustomerHomePage.this, LogIn.class));
        finish();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        if (location != null) {
            // get the latitude and longitude for the location
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            // remove the update location
            locationManager.removeUpdates(this);
            getPharmacy();

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }


    public void onPharmacyClick(View view) {

        rl_Pharmacy.setVisibility(View.VISIBLE);
        rl_Orders.setVisibility(View.INVISIBLE);

        tv_Pharmacy.setTextColor(getResources().getColor(R.color.black));
        tv_Pharmacy.setBackgroundResource(R.drawable.shap_02);

        tv_Orders.setTextColor(getResources().getColor(R.color.white));
        tv_Orders.setBackgroundResource(R.drawable.shap_01);
    }

    public void onOrderClick(View view) {
        rl_Pharmacy.setVisibility(View.INVISIBLE);
        rl_Orders.setVisibility(View.VISIBLE);

        tv_Pharmacy.setTextColor(getResources().getColor(R.color.white));
        tv_Pharmacy.setBackgroundResource(R.drawable.shap_01);

        tv_Orders.setTextColor(getResources().getColor(R.color.black));
        tv_Orders.setBackgroundResource(R.drawable.shap_02);

    }


}