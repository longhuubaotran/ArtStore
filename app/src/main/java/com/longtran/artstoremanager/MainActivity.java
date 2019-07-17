package com.longtran.artstoremanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    boolean dbExists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // this makes sure adding sample products to SQLite only one time
        if(!loadPreference()){
            generateProducts();
        }

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_menu:
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                break;
            case R.id.action_search_reference:
                Intent intentSearchActivity = new Intent(this, SearchActivity.class);
                startActivity(intentSearchActivity);
                break;
            case R.id.admin_login:
                Intent intentAdminActivity = new Intent(this, LogInActivity.class);
                startActivity(intentAdminActivity);
                break;
        }
        return false;
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ProductsListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.productsList);
            }
            return null;
        }
    }

    private byte[] convertBitmapToByteArr(int image) {

        // create a File from R.drawable.xx then compress that file, and write to ByteArr
        Bitmap bitmap = decodeFile(createFileFromResource(image, "a.jpeg"));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArr = stream.toByteArray();
        bitmap.recycle();
        return byteArr;
    }

    // this method creates a File from R.drawable.xxx to use the decodeFile()
    private File createFileFromResource(int resId, String fileName) {
        File f;
        try {
            f = new File(getCacheDir() + File.separator + fileName);
            InputStream is = getResources().openRawResource(resId);
            OutputStream out = new FileOutputStream(f);

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = is.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            is.close();
        } catch (IOException ex) {
            f = null;
        }
        return f;
    }

    // this method compresses the File image to avoid OutOfMemory error, this returns a bitmap
    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            final int IMAGE_MAX_SIZE = 400;
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }


    // insert products into database automatically
    private void generateProducts() {
        long artMaterial, bagMaterial, hatMaterial, clothMaterial, fabricMaterial;
        db = new DatabaseHelper(MainActivity.this);
        artMaterial = db.insertNewMaterial("art");
        bagMaterial = db.insertNewMaterial("bag");
        hatMaterial = db.insertNewMaterial("hat");
        clothMaterial = db.insertNewMaterial("cloth");
        fabricMaterial = db.insertNewMaterial("fabric");
        int count = 0;
        int quantity;
        Double price;
        DecimalFormat f = new DecimalFormat(("##.00")); // round off price to 2 decimal

        while (count <= 37) {

            price = ThreadLocalRandom.current().nextDouble(10.0, 105.0);
            price = Double.parseDouble(f.format(price));
            quantity = ThreadLocalRandom.current().nextInt(1, 50);

            if (count >= 0 && count <= 7) {
                db.insertTableProducts("Art Painting " + count, artMaterial, price, " ",
                        convertBitmapToByteArr(getImageId("a" + String.valueOf(count))), quantity, 0);
            } else if (count >= 8 && count <= 15) {
                db.insertTableProducts("Bag Painting " + count, bagMaterial, price, " ",
                        convertBitmapToByteArr(getImageId("a" + String.valueOf(count))), quantity, 0);
            } else if (count >= 16 && count <= 23) {
                db.insertTableProducts("Fabric Painting " + count, fabricMaterial, price, " ",
                        convertBitmapToByteArr(getImageId("a" + String.valueOf(count))), quantity, 0);
            } else if (count >= 24 && count <= 30) {
                db.insertTableProducts("Hat Painting " + count, hatMaterial, price, " ",
                        convertBitmapToByteArr(getImageId("a" + String.valueOf(count))), quantity, 0);
            } else if (count >= 31 && count <= 37) {
                db.insertTableProducts("Cloth Painting " + count, clothMaterial, price, " ",
                        convertBitmapToByteArr(getImageId("a" + String.valueOf(count))), quantity, 0);
            }
            quantity = 0;
            count++;
        }
        dbExists = true;
        db.close();
    }

    // pass an Image names into this method to get the format R.drawable.xxxx
    private int getImageId(String name) {
        Class res = R.drawable.class;
        Field field = null;
        int imageId = 0;
        try {
            field = res.getField(name);
            imageId = field.getInt(null);
        } catch (NoSuchFieldException e) {
            Log.i("Reflection", "Missing variable" + name);
        } catch (IllegalAccessException e) {
            Log.i("Reflection", "Illegal Access to fiel" + name);
        }
        return imageId;
    }

    // save the state of app after generating sample products for DB for the first time
    private void savePreference() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("boolean", dbExists);
        editor.commit();
    }

    // if sample products already were generated, it will not do it again
    private Boolean loadPreference(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        dbExists = sharedPreferences.getBoolean("boolean", false);
        return dbExists;
    }

    // save the state when activity is destroyed
    @Override
    protected void onStop() {
        super.onStop();
        savePreference();
    }
}
