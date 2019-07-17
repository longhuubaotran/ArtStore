package com.longtran.artstoremanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class AdminActivity extends AppCompatActivity {
    CardView cardProduct, cardOrder, cardReport;
    DatabaseHelper db;
    List<ProductsPainting> listOfLowInStockProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        listOfLowInStockProduct = db.getListOfLowInStockProduct();

        // if there are product with instock <= 5, send notification
        if (listOfLowInStockProduct.size() > 0) {
            sendNotification();
        }

        initView();
        cardProduct.setOnClickListener(manageProductListener);
        cardOrder.setOnClickListener(manageOderListener);
        cardReport.setOnClickListener(manageReportListener);
    }

    private void initView() {
        cardProduct = findViewById(R.id.card_manage_product);
        cardOrder = findViewById(R.id.card_manage_order);
        cardReport = findViewById(R.id.card_manage_report);
    }

    View.OnClickListener manageProductListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ManageProductActivity.class);
            startActivity(intent);
        }
    };

    // send notification if there are product with instock < 5
    private void sendNotification() {
        Intent intent = new Intent(this, LowInStockActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Low in stock product")
                .setSmallIcon(R.drawable.warning)
                .setContentText("There are " + listOfLowInStockProduct.size() + " products running out of stock" + "\n" + "Click for more details")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    View.OnClickListener manageOderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ManageOrderActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener manageReportListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SaleReportActivity.class);
            startActivity(intent);
        }
    };
}
