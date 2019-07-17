package com.longtran.artstoremanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ConvertByteArrToBitmap {
    List<ProductsPainting> productsList; // this list stores all products object from SQLITE
    List<ProductsPainting> productsInCart; // this list of products in cart
    List<Integer> listOfQuantity;
    Painting product;
    int count = 0;
    int quantity;
    LinearLayout child;
    private static final String TAG = "Called ";
    DatabaseHelper db;
    Button continueBtn;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        productsInCart = new ArrayList<>();// list of product in cart
        productsList = new ArrayList<>();// this list stores the all products object from SQLITE
        listOfQuantity = new ArrayList<>();
        productsList = db.getProductsList(); // get the list of all product from SQLITE
        productsInCart = db.getProductInCart();

        continueBtn = findViewById(R.id.continue_shopping_btn);
        nextBtn = findViewById(R.id.next_btn);
        child = findViewById(R.id.child_point);

        while (count < productsInCart.size()) {
            product = productsInCart.get(count); // get a product from cart
            quantity = db.getQuantityByProductId(product.getId());
            initializeDynamicalViews(product, quantity); // add view dynamically for each product;
            count++;
        }

        // this button goes back to Main Activity
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CustomerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        Log.i(TAG, "back");
    }

    private void initializeDynamicalViews(final Painting product, int quantity) {
        LayoutInflater inflater = getLayoutInflater(); // this is for inflating layout
        View view = inflater.inflate(R.layout.cardview_each_item, child, false);

        //set image for item
        ImageView imageView = view.findViewById(R.id.image_product);
        Glide.with(this)
                .load(product.getImage())
                .into(imageView);

        //set description for item
        TextView descriptionText = view.findViewById(R.id.description_text);
        descriptionText.setText(getString(R.string.description) + product.getDescription());
        descriptionText.setTextColor(Color.parseColor("#28B463"));

        //set price for item
        TextView price = view.findViewById(R.id.price_text);
        price.setText(getString(R.string.price) + String.valueOf(product.getPrice()));
        price.setTextColor(Color.parseColor("#138D75"));
//
        //set the amount of instock
        String instock = "There are " + Integer.toString(product.getQuantity()) + " items available";
        final TextView inStock = view.findViewById(R.id.instock_text);
        inStock.setText(instock);
        inStock.setTextColor(Color.parseColor("#16A085"));

        //set quantity
        final TextView quantityText = view.findViewById(R.id.quantity_text);
        quantityText.setText(String.valueOf(quantity));
        quantityText.setTextColor(Color.parseColor("#3498DB"));

        //set decrease quantity button
        Button decreaseBtn = view.findViewById(R.id.decrease_btn);
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt(quantityText.getText().toString());
                String newQuantity;
                if (i > 0 && i - 1 != 0) { // quantity must be greater than 0
                    i = i - 1;
                    newQuantity = String.valueOf(i);
                    quantityText.setText(newQuantity);
                    db.updateQuantity(product.getId(), i);
                } else {
                    Toast.makeText(v.getContext(), "Quantity cannot be negative or 0", Toast.LENGTH_LONG).show();
                }
            }
        });

        //set increase quantity button
        Button increaseBtn = view.findViewById(R.id.increase_btn);
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantityText.getText().toString());
                int currentInStock = product.getQuantity();
                String newQuantity;
                if (currentQuantity == currentInStock) {
                    Toast.makeText(v.getContext(), getString(R.string.cantExceedInStock), Toast.LENGTH_LONG).show();
                } else {
                    currentQuantity = currentQuantity + 1;
                    newQuantity = String.valueOf(currentQuantity);
                    quantityText.setText(newQuantity);
                    db.updateQuantity(product.getId(), currentQuantity);
                }
            }
        });

        // set delete button and set dialog ask for confirmation
        Button deleteBtn = view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(true);
                builder.setTitle("Delete an Item");
                builder.setMessage("Do you want to remove this item from cart");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteProductInCart(product.getId());
                                db.deleteAQuantity(product.getId());
                                refreshActivity();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        child.addView(view); // add a card view to its parent view
    }

    @Override
    public Bitmap getBitmapFromByteArr(byte[] imageArr) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageArr, 0, imageArr.length);
        return bitmap;
    }

    // refresh activity, this method is from StackOverFlow
    public void refreshActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
