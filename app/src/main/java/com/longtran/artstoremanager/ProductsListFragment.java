package com.longtran.artstoremanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ProductsListFragment extends Fragment {
    List<ProductsPainting> productsList;
    DatabaseHelper db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.products_list_frag, container, false);
        db = new DatabaseHelper(getActivity());
        productsList = db.getProductsList();

        // this recycler view is for displaying products in the list
        RecyclerView recyclerView = view.findViewById(R.id.recyclerInFragment);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(productsList);
        recyclerView.setAdapter(recyclerAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerAdapter.setListener(new RecyclerAdapter.Listener() {
            @Override
            public void onClick(int id) {
                Intent intent = new Intent(getActivity(),ProductDetailActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        return view;
    }
}
