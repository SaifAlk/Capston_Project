package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Pharmacy;

public class LogIn extends AppCompatActivity {

    // UI elements reference
    private TextInputLayout Email;
    private TextInputLayout Password;
    private LottieAnimationView lottieAnimationView;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // initiate UI elements
        Email = findViewById(R.id.Input_Email);
        Password = findViewById(R.id.Input_Password);
        lottieAnimationView = findViewById(R.id.lottie_loading);
        lottieAnimationView.setVisibility(View.INVISIBLE);

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
    }

    public void LogInClick(View view) {
        if (validateLogInInput()) {
            loginUser();
        }
    }

    private void loginUser() {
        // get use input
        String email = Email.getEditText().getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();
        // show loading anim
        lottieAnimationView.setVisibility(View.VISIBLE);
        // sign in user
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //makeMeOnline();
                checkUserType();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new ToastMessage().ShowShortMessage("No User have such this email !!!",
                        LogIn.this);
                lottieAnimationView.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void checkUserType() {
        reference.child("User").orderByChild("userId").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot db : snapshot.getChildren()) {
                            String userType = db.child("account_type").getValue(String.class);
                            if (userType.equals("customer")) {
                                startActivity(new Intent(LogIn.this, CustomerHomePage.class));
                                finish();
                            } else if (userType.equals("pharmacy")) {
                                startActivity(new Intent(LogIn.this, PharmacyHomePage.class));
                                finish();

                            }
                            lottieAnimationView.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage().ShowShortMessage(error.getMessage(), LogIn.this);
                        lottieAnimationView.setVisibility(View.INVISIBLE);

                    }
                });
    }

    private boolean validateLogInInput() {
        // get user input
        String email = Email.getEditText().getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            Email.getEditText().setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            Password.getEditText().setError("Password is required");
            Password.setEndIconVisible(false);
            return false;
        }
        return true;
    }

    public void SignUpClick(View view) {
        startActivity(new Intent(this, RegisterCustomer.class));
    }


}