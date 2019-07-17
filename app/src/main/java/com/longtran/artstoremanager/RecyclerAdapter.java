package com.longtran.artstoremanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideContext;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    List<ProductsPainting> productsList;
    List<ProductsPainting> filteredProductList;
    private Listener listener;
    Context conText;

    interface Listener {
        void onClick(int id);
    }

    public RecyclerAdapter(List<ProductsPainting> productsList) {
        this.filteredProductList = productsList;
        this.productsList = new ArrayList<>(filteredProductList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.cardview, viewGroup, false);
        this.conText = viewGroup.getContext(); // get context for this recycler adapter
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder viewHolder, final int position) {
        ProductsPainting productsPainting = filteredProductList.get(position);
        CardView cardView = viewHolder.cardView;
        ImageView imageView = cardView.findViewById(R.id.image_cardview);
        TextView description = cardView.findViewById(R.id.description_cardview);
        TextView price = cardView.findViewById(R.id.price_cardview);

        // convert to bitmap from byte array of productsPainting
        Glide.with(conText)
                .load(productsPainting.getImage())
                .skipMemoryCache(true)
                .into(imageView);

        description.setText(productsPainting.getDescription());
        price.setText("Price: " + productsPainting.getPrice().toString() + "$");
        price.setTextColor(Color.parseColor("#421BC3"));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pass an product object id to onClick because if we pass position, we will get wrong product when performing searching
                // searching will return a list with match products only, but the index is still in original list
                listener.onClick(filteredProductList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredProductList.size();
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<ProductsPainting> filteredList = new ArrayList<>(); // this is temporary list
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    filteredProductList.addAll(productsList); // if no text, display all products are in list
                } else {
                    for (ProductsPainting product : productsList) {
                        if (product.getDescription().toLowerCase().contains(charString)) {
                            filteredList.add(product); // add match products to this list
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredProductList.clear();
                filteredProductList.addAll((List) results.values); // add all match products in temp list to this list
                notifyDataSetChanged();
            }
        };
    }
}
