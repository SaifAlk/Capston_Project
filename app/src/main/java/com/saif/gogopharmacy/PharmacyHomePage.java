package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Customer;
import com.saif.gogopharmacy.model.Pharmacy;

public class PharmacyHomePage extends AppCompatActivity {

    // UI reference
    private TextView Name;

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

        getDate();
    }

    private void checkUser() {
        FirebaseUser User = firebaseAuth.getCurrentUser();
        if (User != null) {
            getDate();
        } else {
            startActivity(new Intent(PharmacyHomePage.this, LogIn.class));
        }
    }

    private void getDate() {
        // get user Id
        String uId = firebaseAuth.getUid();
        // get the user date
        reference.child("User").child(uId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Pharmacy pharmacy = snapshot.getValue(Pharmacy.class);
                            Name.setText("Welcome " + pharmacy.getFull_name().toUpperCase());
                        }
                        else
                        {
                            new ToastMessage()
                                    .ShowShortMessage
                                            ("Data Not Exists",
                                                    PharmacyHomePage.this);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        new ToastMessage()
                                .ShowShortMessage
                                        (error.getMessage(),
                                                PharmacyHomePage.this);
                    }
                });
    }

    public void onAddProduct(View view)
    {
        startActivity(new Intent(PharmacyHomePage.this,PharmacyAddProduct.class));

    }
}