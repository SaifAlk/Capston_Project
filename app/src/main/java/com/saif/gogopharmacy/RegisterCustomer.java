package com.saif.gogopharmacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterCustomer extends AppCompatActivity {


    // initiate UI reference
    private TextInputLayout FullName;
    private TextInputLayout Phone;
    private TextInputLayout Email;
    private TextInputLayout Password;
    private TextInputLayout Country;
    private TextInputLayout State;
    private TextInputLayout City;
    private TextInputLayout CompleteAddress;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        // create UI instance
        FullName = findViewById(R.id.txl_FullName);
        Phone = findViewById(R.id.txl_Phone);
        Email = findViewById(R.id.txl_Email);
        Password = findViewById(R.id.txl_Password);
        Country = findViewById(R.id.txl_Country);
        State = findViewById(R.id.txl_State);
        City = findViewById(R.id.txl_City);
        CompleteAddress = findViewById(R.id.txl_CompleteAddress);


    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    public void onRegisterClick(View view) {

    }
    public void SignUpAsPharmacist(View view)
    {
        startActivity(new Intent(this, RegisterPharmacy.class));
    }

    public void onGetCurrentLocationClick(View view) {
    }

    public void onProfileImageClick(View view) {
    }
}