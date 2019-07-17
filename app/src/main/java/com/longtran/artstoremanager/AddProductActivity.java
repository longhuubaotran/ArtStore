package com.longtran.artstoremanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {
    ImageView image;
    Button selectPicFromGalBtn, addBtn, newMaterialBtn, backBtn;
    EditText descriptionEdit, priceEdit, instockEdit, newMaterialEdit;
    Spinner materialSpinner;
    long materialId;
    DatabaseHelper db;
    private static final int GALLERY = 1;
    private static final int PERMISSION_CODE = 321;
    byte[] byteArray;
    List<SpinnerObject> listOfMaterial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        db = new DatabaseHelper(this);
        initView();
        loadMaterialToSpinner();
        selectPicFromGalBtn.setOnClickListener(picFromGal);
        addBtn.setOnClickListener(addBtnListener);
        newMaterialBtn.setOnClickListener(addNewMaterialListener);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        image = findViewById(R.id.image_product);
        selectPicFromGalBtn = findViewById(R.id.pic_from_gallery_btn);
        addBtn = findViewById(R.id.add_btn);
        backBtn = findViewById(R.id.back_main_btn);
        descriptionEdit = findViewById(R.id.edit_description);
        priceEdit = findViewById(R.id.edit_price);
        instockEdit = findViewById(R.id.edit_instock);
        materialSpinner = findViewById(R.id.material_spinner);
        newMaterialBtn = findViewById(R.id.add_new_material_btn);
    }

    // define listener select pic from gallery
    private View.OnClickListener picFromGal = new View.OnClickListener() {
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
            Glide.with(this).load(contentUri).into(image);
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

    // get list of material and load to spinner to display
    private void loadMaterialToSpinner() {
        listOfMaterial = db.getAllMaterial();

        // array adapter uses toString() to display in spinner
        ArrayAdapter<SpinnerObject> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, listOfMaterial);
        materialSpinner.setAdapter(adapter);
    }

    // define listener for adding new product
    private View.OnClickListener addBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String description;
            boolean checkInput = false;
            double price = 0;
            int instock = 0;
            description = descriptionEdit.getText().toString();
            materialId = ((SpinnerObject) materialSpinner.getSelectedItem()).getDatabaseId();
            try {
                price = Double.parseDouble(priceEdit.getText().toString());
                instock = Integer.parseInt(instockEdit.getText().toString());
                checkInput = true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (checkInput == true) {
                // when insert to sqlite the image must be compress well with very low size, otherwise it gives error
                // but that error takes us to the line getting ID instead of getting byte arryay
                db.insertTableProducts(description, materialId, price, " ", byteArray, instock, 0);
                Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Price and instock must be a number", Toast.LENGTH_SHORT).show();
            }
        }
    };


    // define listener for adding new material, add new material by alert dialog
    private View.OnClickListener addNewMaterialListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // inflate layout contains edit text, this will show up in alert dialog
            LayoutInflater layoutInflater = getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.new_material_in_dialog, null);
            newMaterialEdit = view.findViewById(R.id.newMaterial);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
            alertDialog.setCancelable(true);
            alertDialog.setMessage("Enter a new mterial");
            alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //add new material to SQlite then reload the spinner to display the recent added material
                    materialId = db.insertNewMaterial(newMaterialEdit.getText().toString());
                    loadMaterialToSpinner();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.setView(view); // set the edit text in spinner
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    };
}
