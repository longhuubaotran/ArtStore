package com.longtran.artstoremanager;

public class WearablePainting extends ProductsPainting {
    public WearablePainting(int id, String description, String material, Double price, byte[] image, String size, int quantity, int favorite) {
        super(id, description, material, price, image, size, quantity, favorite);
    }

    public WearablePainting() {
    }
}
