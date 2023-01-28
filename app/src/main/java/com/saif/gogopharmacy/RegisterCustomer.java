package com.saif.gogopharmacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterCustomer extends AppCompatActivity {


    // initiate UI reference
    private TextInputEditText FullName;
    private TextInputEditText Phone;
    private TextInputEditText Email;
    private TextInputEditText Password;
    private TextInputEditText Country;
    private TextInputEditText State;
    private TextInputEditText City;
    private TextInputEditText CompleteAddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        // create UI instance
        FullName = findViewById(R.id.edt_FullName);
        Phone = findViewById(R.id.edt_Phone);
        Email = findViewById(R.id.edt_Email);
        Password = findViewById(R.id.edt_Password);
        Country = findViewById(R.id.edt_Country);
        State = findViewById(R.id.edt_State);
        City = findViewById(R.id.edt_City);
        CompleteAddress = findViewById(R.id.edt_CompleteAddress);



    }

    public void onBackClick(View view)
    {
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