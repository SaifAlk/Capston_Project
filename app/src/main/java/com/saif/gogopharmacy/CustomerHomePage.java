package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Customer;

public class CustomerHomePage extends AppCompatActivity {

    // UI reference
    private TextView Name;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

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
            startActivity(new Intent(CustomerHomePage.this, LogIn.class));
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
                            Customer customer = snapshot.getValue(Customer.class);
                            Name.setText("Welcome " + customer.getFull_name());
                        } else {
                            new ToastMessage()
                                    .ShowShortMessage
                                            ("Data Not Exists",
                                                    CustomerHomePage.this);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage()
                                .ShowShortMessage
                                        (error.getMessage(),
                                                CustomerHomePage.this);
                    }
                });
    }

}