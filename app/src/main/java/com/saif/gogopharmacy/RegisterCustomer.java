package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Customer;
import com.saif.gogopharmacy.model.Pharmacy;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterCustomer extends AppCompatActivity implements LocationListener {


    // layout reference
    private TextInputLayout FullName;
    private TextInputLayout Phone;
    private TextInputLayout Email;
    private TextInputLayout Password;
    private TextInputLayout CompleteAddress;
    private CircleImageView UserImage;
    private LocationManager locationManager;
    // permission codes
    private static final int LOCATION_FINE_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 3;
    // request code
    private static final int IMAGE_PICK_CAMERA_CODE = 4;
    private static final int IMAGE_PICK_GALLERY_CODE = 5;
    // permission array
    private String[] LocationFinePermission;
    // image uri
    private Uri imageUri;
    // Lan and Lon location
    private double latitude;
    private double longitude;
    // reference from geocoder,addresses class
    private Geocoder geocoder;
    private List<Address> addresses;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    // ProgressDialog reference
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        // initiate layout object
        FullName = findViewById(R.id.txl_FullName);
        Phone = findViewById(R.id.txl_Phone);
        Email = findViewById(R.id.txl_Email);
        Password = findViewById(R.id.txl_Password);
        CompleteAddress = findViewById(R.id.txl_CompleteAddress);
        UserImage = findViewById(R.id.ci_UserImage);
        // disable these elements
        CompleteAddress.setEnabled(false);

        // initiate firebase objects
        firebaseAuth = FirebaseAuth.getInstance();
        // firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        // firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // initiate the progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        CheckLocationPermission();
    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    public void onGetCurrentLocationClick(View view) {
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
        // show toast message
        new ToastMessage().ShowShortMessage("Loading....", this);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {

        if (location != null) {
            // get the latitude and longitude for the location
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getAddress();
            // remove the update location
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    private void getAddress() {
        // // geocoding transforming a (latitude, longitude) coordinate into a (partial) address.
        // Get the address, city,
        // by using Geocoder class
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String complete_address = addresses.get(0).getAddressLine(0); // Complete Address
            CompleteAddress.getEditText().setText(complete_address);

        } catch (IOException e) {
            Log.e("Location", "Could not get the Address");
        }
    }

    public void onProfileImageClick(View view) {
        ShowImagePickDialog();
    }

    private void ShowImagePickDialog() {
        String[] option = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            // camera
                            if (ContextCompat.checkSelfPermission(RegisterCustomer.this,
                                    Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(RegisterCustomer.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            == PackageManager.PERMISSION_GRANTED) {
                                pickFromCamera();
                            } else {
                                ActivityCompat.requestPermissions(RegisterCustomer.this,
                                        new String[]{Manifest.permission.CAMERA,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        CAMERA_PERMISSION_REQUEST_CODE);
                            }
                        } else {
                            // gallery
                            if (ContextCompat.checkSelfPermission(RegisterCustomer.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                pickFromGallery();
                            } else {
                                ActivityCompat.requestPermissions(RegisterCustomer.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        STORAGE_PERMISSION_REQUEST_CODE);
                            }
                        }
                    }
                }).show();

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        // to make the Image Uri
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        // open the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // If location request is allowed
            case LOCATION_FINE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.
                    // get the current location
                    detectLocation();
                } else {
                    new ToastMessage().ShowShortMessage("Location permission is necessary", this);
                }
                break;
            // If camera request is allowed
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.
                    // Open Camera
                    pickFromCamera();
                } else {
                    new ToastMessage().ShowShortMessage("Camera permission is necessary", this);
                }
                break;
            // If camera request is allowed
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.
                    // Open gallery
                    pickFromGallery();
                } else {
                    new ToastMessage().ShowShortMessage("Gallery permission is necessary", this);
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                UserImage.setImageURI(imageUri);
            }

        } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
            if (resultCode == RESULT_OK) {
                UserImage.setImageURI(imageUri);
            }
        }
    }

    private boolean ValidateInput() {
        String full_name = FullName.getEditText().getText().toString().trim();
        String phone = Phone.getEditText().getText().toString().trim();
        String email = Email.getEditText().getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();
        String complete_address = CompleteAddress.getEditText().getText().toString().trim();

        if (full_name.isEmpty()) {
            FullName.getEditText().setError("Full Name is requirement");
            return false;
        } else if (phone.isEmpty()) {
            Phone.getEditText().setError("Phone is requirement");
            return false;
        } else if (phone.length() < 10) {
            Phone.getEditText().setError("Phone Number must be 10 numbers requirement");
            return false;
        } else if (email.isEmpty()) {
            Email.getEditText().setError("Email is requirement");
            return false;
        } else if (password.isEmpty()) {
            Password.getEditText().setError("Password is requirement");
            return false;
        } else if (complete_address.isEmpty()) {
            CheckLocationPermission();
            return false;
        }
        return true;
    }

    public void onRegisterClick(View view) {

        if (ValidateInput()) {
            // initiate the progressDialog
            progressDialog.setMessage("Creating Account...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // get user input (email,password)
            String email = Email.getEditText().getText().toString().trim();
            String password = Password.getEditText().getText().toString().trim();

            // Create an account in firebase
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            savePharmacyDataOnFirebase();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // dismiss the progress
                            progressDialog.dismiss();
                            String errorMessage = e.getMessage(); // get exception message
                            new ToastMessage().ShowLongMessage(errorMessage, RegisterCustomer.this); // Show the message

                        }
                    });
        }
    }
    private void savePharmacyDataOnFirebase() {
        // show this message
        progressDialog.setMessage("Saving Account Information");
        progressDialog.setCancelable(false);
        // user id from firebase
        String UserId = firebaseAuth.getUid();
        // return current time in milliseconds
        Long date = System.currentTimeMillis();
        // get the user input
        String full_name = FullName.getEditText().getText().toString().trim();
        String phone = Phone.getEditText().getText().toString().trim();
        String email = Email.getEditText().getText().toString().trim();
        String complete_address = CompleteAddress.getEditText().getText().toString().trim();
        boolean online = false;
        String account_type = "customer";

        // path of the image
        String filePath = "profile_images/" + UserId;

        // location geofire
        GeoFire geoFire = new GeoFire(databaseReference.child("User").child(UserId));
        GeoLocation geoLocation = new GeoLocation(latitude,longitude);


        // check if the user upload image
        if (imageUri != null) {
            // Upload image on firebase storage
            storageReference.child(filePath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    // handel date with Pharmacy class
                                    Customer customer = new Customer(UserId, full_name,
                                            phone, email, complete_address,
                                            uri.toString(), online, account_type, date);
                                    databaseReference.child("User").child(UserId)
                                            .setValue(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused)
                                                {

                                                    geoFire.setLocation("location", geoLocation);
                                                    progressDialog.dismiss();
                                                    new ToastMessage().ShowShortMessage("Register Successfully",RegisterCustomer.this);
                                                    startActivity(new Intent(RegisterCustomer.this, CustomerHomePage.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    new ToastMessage().ShowShortMessage(
                                                            "Register Failure, Try again !",
                                                            RegisterCustomer.this);
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    new ToastMessage().ShowLongMessage(e.getMessage(),RegisterCustomer.this);

                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    new ToastMessage().ShowLongMessage(e.getMessage(),RegisterCustomer.this);
                }
            });
        } else {
            // handel date with Pharmacy class
            Customer customer = new Customer(UserId, full_name,
                    phone, email, complete_address,
                    "", online, account_type, date);

            databaseReference.child("User").child(UserId).setValue(customer)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            geoFire.setLocation("location", geoLocation);
                            progressDialog.dismiss();
                            new ToastMessage().ShowShortMessage("Register Successfully",RegisterCustomer.this);
                            startActivity(new Intent(RegisterCustomer.this, CustomerHomePage.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            new ToastMessage().ShowShortMessage("Register Failure, Try again !",RegisterCustomer.this);
                        }
                    });
        }
    }

    public void SignUpAsPharmacist(View view) {
        startActivity(new Intent(this, RegisterPharmacy.class));
    }


}