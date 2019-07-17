package com.longtran.artstoremanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class ManageProductActivity extends AppCompatActivity {
    CardView cardAdd, cardEdit;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        initView();
        cardAdd.setOnClickListener(addListener); // open add product fragment
        cardEdit.setOnClickListener(editListener);
    }

    private void initView() {
        cardAdd = findViewById(R.id.card_add_product);
        cardEdit = findViewById(R.id.card_edit_delete_product);
    }

    private View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener editListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            intent.putExtra(SearchActivity.CALL_FROM_MANAGE_PRODUCT, "manageActivity");
            startActivity(intent);
        }
    };

}
