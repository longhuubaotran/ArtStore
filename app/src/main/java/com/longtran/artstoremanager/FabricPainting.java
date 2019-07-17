package com.longtran.artstoremanager;

import com.longtran.artstoremanager.Painting;

public class FabricPainting extends ProductsPainting {
    public FabricPainting(int id, String description, String material, Double price, byte[] image, String size, int quantity, int favorite) {
        super(id, description, material, price, image, size, quantity, favorite);
    }

    public FabricPainting() {
    }
}
