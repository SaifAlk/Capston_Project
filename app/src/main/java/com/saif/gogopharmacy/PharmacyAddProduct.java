package com.saif.gogopharmacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saif.gogopharmacy.configuration.ToastMessage;
import com.saif.gogopharmacy.model.Product;

public class PharmacyAddProduct extends AppCompatActivity {

    // for dropDown list
    private String[] category;
    private ArrayAdapter arrayAdapter;
    private AutoCompleteTextView Category;

    // UI reference
    private TextInputLayout ProductName;
    private TextInputLayout ProductDescription;
    private TextInputLayout ProductCategory;
    private TextInputLayout ProductPrice;
    private TextInputLayout DiscountPrice;
    private TextInputLayout DiscountTotalPercent;
    private TextInputLayout FinalPrice;
    private Switch Discount;
    private ImageView imageView;

    // permission codes
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    // request code
    private static final int IMAGE_PICK_GALLERY_CODE = 2;

    // image uri
    private Uri imageUri;

    // Firebase reference
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // ProgressDialog reference
    private ProgressDialog progressDialog;

    // calculation
    double double_discount_price;
    double double_product_price;
    double total_discount = 0;
    double total_final_price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_add_product);

        category = getResources().getStringArray(R.array.Category);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_list, category);
        Category = findViewById(R.id.actv_ProductCategory);
        Category.setAdapter(arrayAdapter);
        // initiate UI
        ProductName = findViewById(R.id.txl_ProductName);
        ProductDescription = findViewById(R.id.txl_ProductDescription);
        ProductCategory = findViewById(R.id.txl_ProductCategory);
        ProductPrice = findViewById(R.id.txl_ProductPrice);
        DiscountPrice = findViewById(R.id.txl_DiscountPrice);
        DiscountTotalPercent = findViewById(R.id.txl_DiscountTotalPercent);
        FinalPrice = findViewById(R.id.txl_FinalPrice);
        Discount = findViewById(R.id.sw_Discount);
        imageView = findViewById(R.id.img_product);

        // disable
        DiscountPrice.setEnabled(false);
        DiscountTotalPercent.setEnabled(false);
        FinalPrice.setEnabled(false);


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

        checkSwitch();
        calculatePercentDiscount();

    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    public void onAddProductImage(View view) {
        // check permission
        if (ContextCompat.checkSelfPermission(PharmacyAddProduct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            pickFromGallery();
        } else {
            ActivityCompat.requestPermissions(PharmacyAddProduct.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check request code
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            // if request code is granted call open gallery
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromGallery();
            } else {
                // if not show toast message
                new ToastMessage().ShowShortMessage("Gallery permission is necessary", this);

            }
        }
    }

    private void pickFromGallery() {
        // open gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the code activity equal gallery code
        if (requestCode == IMAGE_PICK_GALLERY_CODE) {
            if (resultCode == RESULT_OK) // if the result done
            {
                // get the image and set in imageView
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }
    }

    private void calculatePercentDiscount() {

        DiscountPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    try {
                        double_discount_price = Integer.valueOf(s.toString());
                        total_discount = double_discount_price / 100;
                        DiscountTotalPercent.getEditText().setText(String.valueOf(total_discount) + "%");
                        calculateFinalPrice(total_discount);

                    } catch (Exception e) {
                        Log.e("double_discount_price", e.getMessage());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void calculateFinalPrice(double total_discount) {
        String product_price = ProductPrice.getEditText().getText().toString();
        try {
            double_product_price = Double.parseDouble(product_price);
            total_final_price = double_product_price - double_product_price * total_discount;
            FinalPrice.getEditText().setText(String.valueOf(total_final_price));

        } catch (Exception e) {
            Log.e("double_discount_price", e.getMessage());
        }
        ProductPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double_product_price = Double.parseDouble(s.toString());
                    total_final_price = double_product_price - double_product_price * total_discount;
                    FinalPrice.getEditText().setText(String.valueOf(total_final_price));

                } catch (Exception e) {
                    Log.e("double_discount_price", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkSwitch() {
        Discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DiscountPrice.setEnabled(true);
                } else {
                    DiscountPrice.getEditText().setText("");
                    DiscountTotalPercent.getEditText().setText("");
                    FinalPrice.getEditText().setText("");
                    DiscountPrice.setEnabled(false);

                }
            }
        });
    }

    private boolean validateInput() {
        // get the user input
        String product_name = ProductName.getEditText().getText().toString().trim();
        String product_description = ProductDescription.getEditText().getText().toString().trim();
        String category = Category.getText().toString();
        String product_price = ProductPrice.getEditText().getText().toString().trim();
        // get discount price
        String discount_price = DiscountPrice.getEditText().getText().toString().trim();
        boolean discount_switch = Discount.isChecked();
        double double_discount_price = 0;  // cast from string to double
        try {
            double_discount_price = Double.parseDouble(discount_price);
        } catch (Exception e) {
            Log.e("double_discount_price", e.getMessage());
        }

        if (product_name.isEmpty()) {
            ProductName.getEditText().setError("product name is required");
            return false;
        } else if (product_description.isEmpty()) {
            ProductDescription.getEditText().setError("product description is required");
            return false;
        } else if (product_price.isEmpty()) {
            ProductPrice.getEditText().setError("price name is required");
            return false;
        } else if (discount_switch) {
            if (discount_price.isEmpty()) {
                DiscountPrice.getEditText().setError("discount price is required");
                return false;
            }
            if (double_discount_price < 1 || double_discount_price > 100) {
                DiscountPrice.getEditText().setError("discount price must be between 1-100 required");
                return false;
            }
        } else if (category.isEmpty()) {
            Category.setError("category name is required");
            return false;
        }
        Category.setError(null);
        return true;
    }

    public void onAddProduct(View view) {
        if (validateInput()) {
            addProduct();
        }

    }

    private void addProduct() {
        // show dialog
        progressDialog.setMessage("Wait to Add Product");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // return current time in milliseconds
        Long date = System.currentTimeMillis();
        // get the input
        String product_name = ProductName.getEditText().getText().toString().trim();
        String product_description = ProductDescription.getEditText().getText().toString().trim();
        String category = Category.getText().toString();
        String product_price = ProductPrice.getEditText().getText().toString().trim();
        String discount_price = DiscountPrice.getEditText().getText().toString().trim();
        String discount_total_percent = DiscountTotalPercent.getEditText().getText().toString().trim();
        String final_price = FinalPrice.getEditText().getText().toString().trim();

        // get user id
        String userId = firebaseAuth.getUid();

        // path of the image
        String filePath = "product_images/" + product_name + "." + userId;

        // set the date in realtime database
        databaseReference.child("Product")
                .child(userId).child(product_name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            progressDialog.dismiss();
                            new ToastMessage().ShowShortMessage(
                                    "Product " + product_name + " exists",
                                    PharmacyAddProduct.this);
                        } else {

                            if (imageUri != null) {
                                storageReference.child(filePath).putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                taskSnapshot.getStorage().getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                // handel by product class
                                                                Product product = new Product(userId, product_name, product_description,
                                                                        category, product_price, discount_price, discount_total_percent,
                                                                        final_price, uri.toString(), date);
                                                                databaseReference.child("Product")
                                                                        .child(userId).child(product_name).setValue(product);

                                                                // show toast message
                                                                progressDialog.dismiss();
                                                                new ToastMessage().ShowShortMessage("Product Added Successfully",
                                                                        PharmacyAddProduct.this);
                                                                formEmpty();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                new ToastMessage().ShowShortMessage(
                                                                        e.getMessage(),
                                                                        PharmacyAddProduct.this);
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                new ToastMessage().ShowShortMessage(
                                                        e.getMessage(),
                                                        PharmacyAddProduct.this);
                                            }
                                        });

                            } else {
                                // handel by product class
                                Product product = new Product(userId, product_name, product_description,
                                        category, product_price, discount_price,
                                        discount_total_percent, final_price, "", date);
                                // set the date in realtime database
                                databaseReference.child("Product")
                                        .child(userId).child(product_name).setValue(product).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // stop dialog
                                                progressDialog.dismiss();
                                                // show toast message
                                                new ToastMessage().ShowShortMessage("Product Added Successfully"
                                                        , PharmacyAddProduct.this);
                                                formEmpty();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // stop dialog
                                                progressDialog.dismiss();
                                                // show toast message
                                                new ToastMessage().ShowShortMessage(e.getMessage(), PharmacyAddProduct.this);
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        new ToastMessage().ShowShortMessage(
                                error.getMessage(),
                                PharmacyAddProduct.this);
                    }
                });
    }

    private void formEmpty() {
        // get the user input
        ProductName.getEditText().setText(null);
        ProductDescription.getEditText().setText(null);
        Category.setText(null);
        ProductPrice.getEditText().setText(null);
        DiscountPrice.getEditText().setText(null);
        Discount.setChecked(false);
    }
}



