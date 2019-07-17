package com.longtran.artstoremanager;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class SaleReportActivity extends AppCompatActivity {
    Spinner monthSpin, daySpin;
    EditText yearEdit;
    TextView resultText;
    Button doneBtn;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_report);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        initView();
        loadSpinner();
        doneBtn.setOnClickListener(doneListener);
    }

    private void initView() {
        monthSpin = findViewById(R.id.month_spinner);
        daySpin = findViewById(R.id.day_spinner);
        yearEdit = findViewById(R.id.year_edit);
        resultText = findViewById(R.id.sale_result_text);
        doneBtn = findViewById(R.id.done_btn);
    }

    private void loadSpinner() {
        String[] monthArr = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        ArrayList<String> daysArr = new ArrayList<>();
        daysArr.add("None");
        for (int i = 1; i <= 31; i++) {
            daysArr.add(String.valueOf(i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthArr);
        monthSpin.setAdapter(monthAdapter);
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysArr);
        daySpin.setAdapter(daysAdapter);
    }

    View.OnClickListener doneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String month = monthSpin.getSelectedItem().toString();
            String day = daySpin.getSelectedItem().toString();
            String year = yearEdit.getText().toString();
            double result = db.getTotalSaleByMonth(month, year, day);
            resultText.setText(month + "-" + day + "-" + year + " " + String.valueOf(result) + "$");
        }
    };
}
