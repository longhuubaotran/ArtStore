package com.longtran.artstoremanager;

public class ArtPainting extends ProductsPainting {
    public ArtPainting(int id, String description, String material, Double price, byte[] image, String size, int quantity, int favorite) {
        super(id, description, material, price, image, size, quantity, favorite);
    }

    public ArtPainting() {
    }
}
