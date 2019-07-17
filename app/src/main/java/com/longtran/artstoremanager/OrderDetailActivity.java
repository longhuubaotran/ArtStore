package com.longtran.artstoremanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OrderDetailActivity extends AppCompatActivity {
    DatabaseHelper db;
    TextView firstNameText, lastNameText, phoneText, totalPriceOrderText;
    Button cancelBtn, nextBtn;
    LinearLayout child;
    List<ProductsPainting> productInCart; // List of product in cart
    ProductsPainting product;
    long customerId;
    double totalPriceOrder;
    int count = 0;
    int quantityEachItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        db = new DatabaseHelper(this);
        productInCart = new ArrayList<>();

        // these are 2 seperate list because the number of products in productInCart equals to the number of quantity in
        // quantity List
        productInCart = db.getProductInCart(); // get the list of product in cart from sqltie

        // get the customerId from the Customer activity, and pull out the information of this customer based on customerId
        customerId = (Long) (getIntent().getExtras().get("customerId"));
        Customer customer = db.getCustomerInformation(customerId);
        displayCustomerInfo(customer);

        // display information, quan tity of each product in cart
        while (count < productInCart.size()) {
            product = productInCart.get(count);
            quantityEachItem = db.getQuantityByProductId(product.getId());
            displayItemInCart(product, quantityEachItem);
            count++;
        }

        // set total price for order after displaying all data
        totalPriceOrderText.setText("Total Price: " + String.valueOf(totalPriceOrder) + "$");

        cancelBtn.setOnClickListener(cancelBtnListener);

        nextBtn.setOnClickListener(nextBtnListener);
    }

    private void initView() {
        child = findViewById(R.id.child_point);
        totalPriceOrderText = findViewById(R.id.total_price_order);
        firstNameText = findViewById(R.id.first_name_textView);
        lastNameText = findViewById(R.id.last_name_textView);
        phoneText = findViewById(R.id.phone_textView);
        cancelBtn = findViewById(R.id.cancel_order_btn);
        nextBtn = findViewById(R.id.next_btn);
    }

    private void displayCustomerInfo(Customer customer) {
        firstNameText.setText("First name: " + customer.getFirstName());
        lastNameText.setText("Last name: " + customer.getLastName());
        phoneText.setText("Phone: " + String.valueOf(customer.getPhone()));
    }

    private void displayItemInCart(ProductsPainting product, int quantityEachItem) {
        LayoutInflater inflater = getLayoutInflater(); // this is for inflating layout
        View view = inflater.inflate(R.layout.cardview_item_in_order_detail, child, false);

        // calculate total price for each item based on the quantity
        DecimalFormat f = new DecimalFormat(("##.00"));
        double totalPriceEach = quantityEachItem * product.getPrice();
        totalPriceEach = Double.parseDouble(f.format(totalPriceEach));
        totalPriceOrder += totalPriceEach;
        totalPriceOrder = Double.parseDouble(f.format(totalPriceOrder));

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

        //set quantity
        TextView quantity = view.findViewById(R.id.quantity_text);
        quantity.setText(String.valueOf(quantityEachItem));
        quantity.setTextColor(Color.parseColor("#3498DB"));

        //set quantity
        TextView totalPriceEachItem = view.findViewById(R.id.total_price_each_item);
        totalPriceEachItem.setText(String.valueOf(totalPriceEach));
        totalPriceEachItem.setTextColor(Color.parseColor("#3498DB"));

        child.addView(view); // add a card view to its parent view
    }

    // define the action for next button
    private View.OnClickListener nextBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setCancelable(true);
            builder.setTitle("Place an Order");
            builder.setMessage("Do you want to place this order ?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int confirmCode;
                            long orderId;
                            confirmCode = ThreadLocalRandom.current().nextInt(100, 1000);
                            orderId = db.createNewOrderCustomer(customerId, totalPriceOrder, confirmCode);
                            db.createANewOrderCustomerProduct(orderId,productInCart);
                            db.updateInstock(productInCart);
                            Intent intent = new Intent(getApplicationContext(),OrderSuccessActivity.class);
                            intent.putExtra("confirmCode",confirmCode);
                            startActivity(intent);
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
    };

    // define action for cancel button
    private View.OnClickListener cancelBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setCancelable(true);
            builder.setTitle("Delete Order");
            builder.setMessage("Do you want to delete this order ?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.cancelAnOrder(productInCart);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
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
    };
}
