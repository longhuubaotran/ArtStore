package com.longtran.artstoremanager;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public static final String CALL_FROM_MANAGE_PRODUCT = "";
    List<ProductsPainting> productsList;
    DatabaseHelper db;
    RecyclerView searchRecyclerView;
    SearchView searchView;
    RecyclerAdapter recyclerAdapter;
    String whereThisGetCalledFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final LogInActivity logInActivity = new LogInActivity();

        // if this search Activity is called from manage it will have different behavior
        whereThisGetCalledFrom = getIntent().getStringExtra(CALL_FROM_MANAGE_PRODUCT);

        db = new DatabaseHelper(this);
        productsList = db.getProductsList();

        searchRecyclerView = findViewById(R.id.recyclerInFragment);
        recyclerAdapter = new RecyclerAdapter(productsList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(new RecyclerAdapter.Listener() {
            @Override
            public void onClick(int id) {
                //this will open Edit or Delete activity
                if(whereThisGetCalledFrom.equals("manageActivity")){
                    Intent intent = new Intent(getApplicationContext(), EditDeleteActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }else {
                    // this call from Main so it will open product detail
                    Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.onActionViewExpanded(); // this makes search bar enable automatically when activity starts
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recyclerAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                recyclerAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
        }
        return false;
    }


}

