package com.longtran.artstoremanager;

import java.io.Serializable;
import java.util.ArrayList;

public class Cart {
    private ArrayList<Painting> productsCart = new ArrayList<>();

    public Cart() {
    }

    public ArrayList<Painting> getProductsCart() {
        return productsCart;
    }

    public boolean checkExist(Painting product) {
        if (productsCart.contains(product)) {
            return true;
        } else {
            return false;
        }
    }
    public void addToCart(Painting product){
        this.productsCart.add(product);
    }
}
