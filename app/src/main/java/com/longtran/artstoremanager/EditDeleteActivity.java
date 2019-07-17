package com.longtran.artstoremanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class EditDeleteActivity extends AppCompatActivity {
    private ImageView imageview;
    private TextView descriptionText, priceText, instockText;
    private EditText descriptEdit, priceEdit, instockEdit;
    private Button saveBtn, deleteBtn, galPicBtn;
    int productId;
    private static final int GALLERY = 1;
    private static final int PERMISSION_CODE = 321;
    List<ProductsPainting> listOfProduct;
    byte[] byteArray;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        db = new DatabaseHelper(this);
        productId = (Integer) getIntent().getExtras().get("id") - 1; // - 1 because list starts from 0
        listOfProduct = db.getProductsList();
        ProductsPainting product = listOfProduct.get(productId);
        byteArray = product.getImage(); // if user does not choose a new picture for product, we will not update it

        setDataToView(product);
        editTextDataOnTextView();

        galPicBtn.setOnClickListener(selectPicFromGalListener);
        saveBtn.setOnClickListener(saveListener);
        deleteBtn.setOnClickListener(deleteListener);
    }

    private void initView() {
        imageview = findViewById(R.id.image_product);
        descriptionText = findViewById(R.id.description_text);
        priceText = findViewById(R.id.price_text);
        instockText = findViewById(R.id.instock_text);
        descriptEdit = findViewById(R.id.edit_description);
        priceEdit = findViewById(R.id.edit_price);
        instockEdit = findViewById(R.id.edit_instock);
        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        galPicBtn = findViewById(R.id.selectPic_btn);
    }

    private void setDataToView(ProductsPainting product) {
        Glide.with(this).load(product.getImage()).into(imageview);
        descriptionText.setText(product.getDescription());
        priceText.setText(String.valueOf(product.getPrice()));
        instockText.setText(String.valueOf(product.getQuantity()));
    }

    private void editTextDataOnTextView() {
        descriptEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descriptionText.setText(descriptEdit.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        priceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                priceText.setText(priceEdit.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        instockEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                instockText.setText(instockEdit.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String description = descriptionText.getText().toString();
            Double price = Double.parseDouble(priceText.getText().toString());
            int inStock = Integer.parseInt(instockText.getText().toString());
            db.updateAProduct(productId + 1, description, price, inStock, byteArray);
            Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            db.deleteAProduct(productId + 1);
            Toast.makeText(getApplicationContext(), "Delete Successfully", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener selectPicFromGalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY);
        }
    };

    // get selected picture by user and display it, and store to byte array
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            Uri contentUri = data.getData();
            Glide.with(this).load(contentUri).into(imageview);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byteArray = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ask permission to use gallery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
        }
    }
}
