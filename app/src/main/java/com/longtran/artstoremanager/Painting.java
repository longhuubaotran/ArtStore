package com.longtran.artstoremanager;

import java.util.Objects;

public class Painting {
    private int id;
    private String description;
    private String material;
    private Double price;
    private String size;
    private byte[] image;
    private int favorite;
    private int quantity;

    public Painting(int id, String description, String material, Double price, byte[] image, String size, int quantity, int favorite) {
        this.id = id;
        this.description = description;
        this.material = material;
        this.price = price;
        this.image = image;
        this.size = size;
        this.quantity = quantity;
        this.favorite = favorite;
    }

    public Painting() {
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getMaterial() {
        return material;
    }

    public Double getPrice() {
        return price;
    }

    public byte[] getImage() {
        return image;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getFavorite() {
        return favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Painting)) {
            return false;
        }
        Painting painting = (Painting) o;
        return this.id == painting.getId() &&
                this.description.equals(painting.getDescription()) &&
                this.material.equals(painting.getMaterial()) &&
                this.price.equals(painting.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, material, quantity) * 27;
    }
}
