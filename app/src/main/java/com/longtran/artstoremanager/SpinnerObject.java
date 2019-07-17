package com.longtran.artstoremanager;

public class SpinnerObject {
    int databaseId;
    String value;

    public SpinnerObject(int databaseId, String value) {
        this.databaseId = databaseId;
        this.value = value;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
