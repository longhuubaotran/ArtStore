package com.longtran.artstoremanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CustomerActivity extends AppCompatActivity {
    EditText lastName, firstName, phone;
    String firstNameText, lastNameText;
    String phoneText;
    int phoneNum;
    long customerId;
    Button backBtn, nextBtn;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        initView();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CartActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get the input of user
                firstNameText = firstName.getText().toString();
                lastNameText = lastName.getText().toString();
                phoneText = phone.getText().toString();

                // if the input is valid, insert user's input in Sqlite and open another activity
                if (checkInput(firstNameText, lastNameText, phoneText)) {
                    // return a customerId after adding to sqlite then pass this customerId to another Acti
                    customerId = db.createANewCustomer(firstNameText, lastNameText, phoneNum);
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Next Step");
                    builder.setMessage("Is your information correct ?");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                                    intent.putExtra("customerId", customerId);
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
            }
        });
    }

    private void initView() {
        firstName = findViewById(R.id.first_name_text);
        lastName = findViewById(R.id.last_name_text);
        phone = findViewById(R.id.phone_text);
        backBtn = findViewById(R.id.back_btn);
        nextBtn = findViewById(R.id.next_btn);
    }

    // check the input of user
    private boolean checkInput(String firstNameText, String lastNameText, String phoneText) {
        if (firstNameText.isEmpty() || lastNameText.isEmpty() || phoneText.isEmpty()) {
            Toast.makeText(this, "Missing required information", Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                phoneNum = Integer.parseInt(phoneText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Phone number must be number", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}
