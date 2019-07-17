package com.longtran.artstoremanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity implements ConvertByteArrToBitmap {
    DatabaseHelper dbHelper;
    List<ProductsPainting> productsList;
    List<Integer> productsInCart;
    boolean isChecked = false;
    ImageView imageView;
    TextView description;
    TextView price;
    CheckBox favorite;
    Button addToCart_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        productsList = dbHelper.getProductsList();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        final int productIdInSqLite = getIntent().getIntExtra("id", 0);

        // productIdInList - 1 because the id in SQLITE starts from 1, and int Arraylist starts from 0
        final int productIdInList = productIdInSqLite - 1;
        final ProductsPainting product = productsList.get(productIdInList);

        initView(); // initialize view
        isChecked = (product.getFavorite() == 1); // true if == 1

        //display data
        Glide.with(this)
                .load(product.getImage())
                .into(imageView);
        description.setText(product.getDescription());
        price.setText("Price: " + String.valueOf(product.getPrice()) + "$");
        price.setTextColor(Color.parseColor("#421BC3"));
        favorite.setChecked(isChecked);

        //set or unset favorite
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.getFavorite() == 1) {
                    dbHelper.setFavorite(product.getDescription(), 0);
                } else {
                    dbHelper.setFavorite(product.getDescription(), 1);
                }
            }
        });

        addToCart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.checkProductExistInCart(productIdInSqLite)) {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.itemAlreadyInCart), Toast.LENGTH_LONG).show();
                } else {
                    long quantityId;
                    quantityId = dbHelper.addQuantity(1); // when adding a product to cart, adding quantity 1 to sqlite too
                    dbHelper.addProductToCart(productIdInSqLite, quantityId);
                }
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

    }

    private ProductsPainting getProductFromList(int position) {
        ProductsPainting product = new ProductsPainting(
                productsList.get(position).getId(),
                productsList.get(position).getDescription(),
                productsList.get(position).getMaterial(),
                productsList.get(position).getPrice(),
                productsList.get(position).getImage(),
                productsList.get(position).getSize(),
                productsList.get(position).getQuantity(),
                productsList.get(position).getFavorite());
        return product;
    }

    @Override
    public Bitmap getBitmapFromByteArr(byte[] imageArr) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageArr, 0, imageArr.length);
        return bitmap;
    }

    private void initView() {
        imageView = findViewById(R.id.image);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        favorite = findViewById(R.id.favorite_checkBox);
        addToCart_btn = findViewById(R.id.addToCart_btn);
    }
}
