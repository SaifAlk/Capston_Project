package com.saif.gogopharmacy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.saif.gogopharmacy.configuration.ToastMessage;

public class LogIn extends AppCompatActivity
{

    // UI elements reference
    private TextInputLayout Email;
    private TextInputLayout Password;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // initiate UI elements
        Email = findViewById(R.id.Input_Email);
        Password = findViewById(R.id.Input_Password);

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void LogInClick(View view)
    {
        if(validateLogInInput())
        {
            new ToastMessage().ShowShortMessage("Ok",this);
        }
    }

    private boolean validateLogInInput()
    {
        // get user input
        String email = Email.getEditText().getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();

        if(email.isEmpty())
        {
            Email.getEditText().setError("Email is required");
            return false;
        }
        if(password.isEmpty())
        {
            Password.getEditText().setError("Password is required");
            Password.setEndIconVisible(false);
            return false;
        }
        return true;
    }

    public void SignUpClick(View view)
    {
        startActivity(new Intent(this, RegisterCustomer.class));
    }
}