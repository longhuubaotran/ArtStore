package com.longtran.artstoremanager;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ManageOrderActivity extends AppCompatActivity {
    CheckBox pendingCkBox, completeCkBox, byNumCkBox, doneCkBox;
    TextView orderNumText;
    Button detailBtn;
    LinearLayout child;
    List<Integer> listOfPendingOrder;
    List<Integer> listOfCompleteOrder;
    int count = 0;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        initView();

        pendingCkBox.setOnCheckedChangeListener(pendingListener);
        completeCkBox.setOnCheckedChangeListener(completeListener);
    }

    private void initView() {
        pendingCkBox = findViewById(R.id.pending_ckbox);
        completeCkBox = findViewById(R.id.complete_ckbox);
        byNumCkBox = findViewById(R.id.number_ckbox);
        child = findViewById(R.id.insert_point);
    }

    private void bindDataToView(int orderNum, boolean flag) {
        View view = getLayoutInflater().inflate(R.layout.cardview_manage_order, child, false);

        orderNumText = view.findViewById(R.id.confirm_code_text);
        orderNumText.setText("Order Number: "+String.valueOf(orderNum));

        doneCkBox = view.findViewById(R.id.done_ckbox);
        // flag == false means this bind data for pending orders so done checkbox is enabled
        if (flag == false) {
            doneCkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        db.updateStatusAnOrder(orderNum);
                    }
                }
            });
        } else {
            // this will make done check box invisible because this order is completed
            doneCkBox.setVisibility(View.INVISIBLE);
        }

        detailBtn = view.findViewById(R.id.done_ckbox);
        child.addView(view);
    }

    CheckBox.OnCheckedChangeListener pendingListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                completeCkBox.setChecked(false);
                byNumCkBox.setChecked(false);
                listOfPendingOrder = db.getListOfPendingOrders();
                //this will reset the count and remove viewgroup, bind viewgroup with new data
                count = 0;
                child.removeAllViews();
                while (count < listOfPendingOrder.size()) {
                    bindDataToView(listOfPendingOrder.get(count), false);
                    count++;
                }
            }
        }
    };

    CheckBox.OnCheckedChangeListener completeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                pendingCkBox.setChecked(false);
                byNumCkBox.setChecked(false);
                listOfCompleteOrder = db.getListOfCompleteOrders();
                //this will reset the count and remove viewgroup, bind viewgroup with new data
                count = 0;
                child.removeAllViews();
                while (count < listOfCompleteOrder.size()) {
                    bindDataToView(listOfCompleteOrder.get(count), true);
                    count++;
                }
            }
        }
    };
}
