package com.longtran.artstoremanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OrderSuccessActivity extends AppCompatActivity {

    TextView confirmCodeText;
    Button doneBtn;
    int confirmCode;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        db = new DatabaseHelper(this);
        initView();

        confirmCode = (Integer) getIntent().getExtras().get("confirmCode");
        confirmCodeText.setText("You can track your order by this code: " + String.valueOf(confirmCode));

        // back to Main activity
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.resetCartId(); // no product in cart after making order
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        confirmCodeText = findViewById(R.id.confirm_code);
        doneBtn = findViewById(R.id.done_btn);
    }
}
