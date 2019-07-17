package com.longtran.artstoremanager;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class LowInStockActivity extends AppCompatActivity {
    LinearLayout insertPoint;
    DatabaseHelper db;
    List<ProductsPainting> listOfLowInStockProDuct;
    ProductsPainting product;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_in_stock);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        listOfLowInStockProDuct = db.getListOfLowInStockProduct();

        initView();

        while (count < listOfLowInStockProDuct.size()) {
            product = listOfLowInStockProDuct.get(count);
            bindDataToView(product);
            count++;
        }
    }

    private void initView() {
        insertPoint = findViewById(R.id.insert_point);
    }

    private void bindDataToView(final ProductsPainting product) {
        View view = getLayoutInflater().inflate(R.layout.cardview_low_instock, insertPoint, false);

        ImageView imageView = view.findViewById(R.id.image);
        Glide.with(this).load(product.getImage()).into(imageView);

        TextView descriptionText = view.findViewById(R.id.description_text);
        descriptionText.setText(product.getDescription());

        TextView inStockText = view.findViewById(R.id.instock_text);
        inStockText.setText(String.valueOf(product.getQuantity()));

        final EditText inStockEdit = view.findViewById(R.id.edit_instock);

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newInStock = Integer.parseInt(inStockEdit.getText().toString());
                db.updateInStockOfProduct(product.getId(),newInStock);
                Toast.makeText(getApplicationContext(),"Save Successfully",Toast.LENGTH_SHORT).show();
            }
        });

        insertPoint.addView(view);
    }
}
