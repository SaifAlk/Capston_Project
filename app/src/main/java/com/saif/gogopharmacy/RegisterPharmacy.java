package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.icu.util.LocaleData;
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
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saif.gogopharmacy.model.Pharmacy;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterPharmacy extends AppCompatActivity implements LocationListener, OnMapReadyCallback {


    // layout reference
    private TextInputLayout FullName;
    private TextInputLayout ShopName;
    private TextInputLayout DeliveryFee;
    private TextInputLayout Phone;
    private TextInputLayout Email;
    private TextInputLayout Password;
    private TextInputLayout City;
    private TextInputLayout CompleteAddress;
    private CircleImageView UserImage;
    private LocationManager locationManager;
    // permission codes
    private static final int LOCATION_FINE_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 300;
    // request code
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 500;
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
    // Google map
    private GoogleMap map;
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
        setContentView(R.layout.activity_register_pharmacy);

        // initiate layout object
        FullName = findViewById(R.id.txl_FullName);
        ShopName = findViewById(R.id.txl_ShopName);
        DeliveryFee = findViewById(R.id.txl_DeliveryFee);
        Phone = findViewById(R.id.txl_Phone);
        Email = findViewById(R.id.txl_Email);
        Password = findViewById(R.id.txl_Password);
        City = findViewById(R.id.txl_City);
        CompleteAddress = findViewById(R.id.txl_CompleteAddress);
        UserImage = findViewById(R.id.ci_UserImage);
        // initiate firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // initiate firebaseDatabase and databaseReference
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // initiate firebaseStorage and storageReference
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // initiate the progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        // disable these elements
        City.setEnabled(false);
        CompleteAddress.setEnabled(false);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        ShowShortMessage("Loading....");
        try {
            // get the location service
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // if the SDK > 28 and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // check if the internet and location is working
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && isConnected() && locationManager.isLocationEnabled()) {
                    // get location by internet
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    return true;
                } else {
                    // show error message
                    ShowLongMessage("Please Enable Internet And Location");
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
        // clear map
        map.clear();
        // get the latitude and longitude for the location
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        // Add marker in the location
        LatLng FirstLocation = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions()
                .position(FirstLocation)
                .title("New Marker"));
        // move the camera to the location
        map.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(FirstLocation)
                        .zoom(15)
                        .bearing(0)
                        .tilt(0)
                        .build()));


        getAddress();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                LatLng OptionLocation = new LatLng(latitude, longitude);
                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(OptionLocation)
                        .title("New Marker"));
                // move the camera to the location
                map.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                                .target(OptionLocation)
                                .zoom(15)
                                .bearing(0)
                                .tilt(0)
                                .build()));
                getAddress();
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

        try {
            LocationListener.super.onProviderDisabled(provider);
        } catch (AbstractMethodError e) {
            ShowLongMessage("Location Disable");
            map.clear();
//            City.getEditText().setError("");
//            CompleteAddress.getEditText().setError("");
//            City.getEditText().setText("");
//            CompleteAddress.getEditText().setText("");
//            latitude = 0.0;
//            longitude = 0.0;
            map.getUiSettings().setZoomControlsEnabled(false);
            map.setOnMapClickListener(null);

        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        try {
            LocationListener.super.onProviderEnabled(provider);
        } catch (AbstractMethodError e) {
            map.getUiSettings().setZoomControlsEnabled(true);
            City.getEditText().setError(null);
            CompleteAddress.getEditText().setError(null);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;
        LatLng latLng = new LatLng(24.407834, 54.518840);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.getUiSettings().setZoomControlsEnabled(true);
        //  minimum  maximum zoom level.
        map.setMinZoomPreference(10.0f);
        map.setMaxZoomPreference(20.0f);

    }

    private void getAddress() {
        // // geocoding transforming a (latitude, longitude) coordinate into a (partial) address.
        // Get the address, city,
        // by using Geocoder class
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String complete_address = addresses.get(0).getAddressLine(0); // Complete Address
            City.getEditText().setText(city);
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
                            if (ContextCompat.checkSelfPermission(RegisterPharmacy.this,
                                    Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(RegisterPharmacy.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            == PackageManager.PERMISSION_GRANTED) {
                                pickFromCamera();
                            } else {
                                ActivityCompat.requestPermissions(RegisterPharmacy.this,
                                        new String[]{Manifest.permission.CAMERA,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        CAMERA_PERMISSION_REQUEST_CODE);
                            }
                        } else {
                            // gallery
                            if (ContextCompat.checkSelfPermission(RegisterPharmacy.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                pickFromGallery();
                            } else {
                                ActivityCompat.requestPermissions(RegisterPharmacy.this,
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

    @Override
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
                    ShowShortMessage("Location permission is necessary");
                }
                break;
            // If camera request is allowed
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.
                    // Open Camera
                    pickFromCamera();
                } else {
                    ShowShortMessage("Camera permission is necessary");
                }
                break;
            // If camera request is allowed
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted.
                    // Open gallery
                    pickFromGallery();
                } else {
                    ShowShortMessage("Gallery permission is necessary");
                }
                break;
        }
    }

    @Override
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
        String shop_name = ShopName.getEditText().getText().toString().trim();
        String delivery_fee = DeliveryFee.getEditText().getText().toString().trim();
        String phone = Phone.getEditText().getText().toString().trim();
        String email = Email.getEditText().getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();
        String city = City.getEditText().getText().toString().trim();
        String complete_address = CompleteAddress.getEditText().getText().toString().trim();
        int convertDeliveryFeeToInt = 0;
        try {
            convertDeliveryFeeToInt = Integer.parseInt(delivery_fee);
        } catch (Exception e) {
            Log.e("convertDeliveryFeeToInt", e.getMessage());
        }

        if (full_name.isEmpty()) {
            FullName.getEditText().setError("Full Name is requirement");
            return false;
        } else if (shop_name.isEmpty()) {
            ShopName.getEditText().setError("Shop Name is requirement");
            return false;
        } else if (delivery_fee.isEmpty()) {
            DeliveryFee.getEditText().setError("Delivery Fee is requirement");
            return false;
        } else if (convertDeliveryFeeToInt > 20) {
            DeliveryFee.getEditText().setError("Delivery Fee should by between 0-20");
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
        } else if (city.isEmpty() && complete_address.isEmpty()) {
            ShowLongMessage("Click GPS icon to detect your location, make sure internet and location are enabled");
//            CompleteAddress.getEditText().setError("");
//            City.getEditText().setError("");
            return false;
        } else {
            CompleteAddress.getEditText().setError(null);
            City.getEditText().setError(null);
            return true;
        }

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
                            ShowLongMessage(errorMessage); // Show the message

                        }
                    });
        }
    }

    private void savePharmacyDataOnFirebase() {
        // show this message
        progressDialog.setMessage("Saving Account Information");
        progressDialog.setCancelable(false);
        // user if from firebase
        String UserId = firebaseAuth.getUid();
        // return current time in milliseconds
        Long date = System.currentTimeMillis();
        // get the user input
        String full_name = FullName.getEditText().getText().toString().trim();
        String shop_name = ShopName.getEditText().getText().toString().trim();
        String delivery_fee = DeliveryFee.getEditText().getText().toString().trim();
        String phone = Phone.getEditText().getText().toString().trim();
        String email = Email.getEditText().getText().toString().trim();
        String password = Password.getEditText().getText().toString().trim();
        String city = City.getEditText().getText().toString().trim();
        String complete_address = CompleteAddress.getEditText().getText().toString().trim();
        boolean online = false;
        String account_type = "pharmacy";
        String account_state = "not_active";

        // path of the image
        String filePath = "profile_images/" + UserId;

        // check if the user upload image
        if (imageUri != null) {
            // Upload image on firebase storage
            storageReference.child(filePath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // handel date with Pharmacy class
                                    Pharmacy pharmacy = new Pharmacy(UserId, full_name, shop_name,
                                            delivery_fee, phone, email, city, complete_address,
                                            latitude, longitude, uri.toString(), online, account_type,
                                            account_state, date);
                                    databaseReference.child("User").child(UserId)
                                            .setValue(pharmacy).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    ShowShortMessage("Register Successfully");
                                                    startActivity(new Intent(RegisterPharmacy.this, LogIn.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    ShowShortMessage("Register Failure, Try again !");
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    ShowLongMessage(e.getMessage());

                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    ShowLongMessage(e.getMessage());
                }
            });
        } else {
            // handel date with Pharmacy class
            Pharmacy pharmacy = new Pharmacy(UserId, full_name, shop_name,
                    delivery_fee, phone, email, city, complete_address,
                    latitude, longitude, "", online, account_type,
                    account_state, date);
            databaseReference.child("User").child(UserId).setValue(pharmacy)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            ShowShortMessage("Register Successfully");
                            startActivity(new Intent(RegisterPharmacy.this, LogIn.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            ShowShortMessage("Register Failure, Try again !");
                        }
                    });
        }
    }


    private void ShowShortMessage(String Message) {
        // Get the message from outside, then Crate the toast
        Toast.makeText(this, Message, Toast.LENGTH_SHORT).show();
    }

    private void ShowLongMessage(String Message) {
        // Get the message from outside, then Crate the toast
        Toast.makeText(this, Message, Toast.LENGTH_LONG).show();
    }

}