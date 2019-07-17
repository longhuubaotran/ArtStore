package com.longtran.artstoremanager;

import java.io.Serializable;

public class ProductsPainting extends Painting{

    public ProductsPainting(int id, String description, String material, Double price, byte[] image, String size, int quantity, int favorite) {
        super(id, description, material, price, image, size, quantity, favorite);
    }

    public ProductsPainting() {
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
