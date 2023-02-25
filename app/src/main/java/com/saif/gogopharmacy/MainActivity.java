package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saif.gogopharmacy.configuration.ToastMessage;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private static int splash_screen = 4000;
    private ImageView logo_Image;
    private Animation logo_anim;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // initiate firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        // set the animation to the image
        logo_Image = findViewById(R.id.imageView);
        logo_anim = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        logo_Image.startAnimation(logo_anim);

        Handel();
    }

    private void CheckUserAuth() {
        // 1. Get the currentUser
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) // if he login
        {
            // Call these Methods
            checkUserType();
        } else {
            // Open the LogIn page
            startActivity(new Intent(MainActivity.this, LogIn.class));
            finish();
        }
    }

    private void checkUserType() {
        reference.child("User").child(firebaseAuth.getUid()).child("online").setValue(true);
        reference.child("User").orderByChild("userId").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot db : snapshot.getChildren()) {
                            String userType = db.child("account_type").getValue(String.class);
                            if (userType.equals("customer")) {
                                startActivity(new Intent(MainActivity.this, CustomerHomePage.class));
                                finish();
                            } else if (userType.equals("pharmacy")) {
                                startActivity(new Intent(MainActivity.this, PharmacyHomePage.class));
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        new ToastMessage().ShowShortMessage(error.getMessage(), MainActivity.this);

                    }
                });
    }

    private void Handel()
    {
        // Delay 4 sec before moving to the next page
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, LogIn.class));
                finish();
            }
        }, splash_screen);
    }
    @Override
    protected void onResume() {
        super.onResume();
//        Handel();

    }
}