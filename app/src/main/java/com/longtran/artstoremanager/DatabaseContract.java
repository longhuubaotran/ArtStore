package com.longtran.artstoremanager;

import android.provider.BaseColumns;

public class DatabaseContract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA = ",";
    private static final String BLOB_TYPE = " BLOB";
    private static final String REAL_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String NOT_NULL = " NOT NULL";
    private static final String FOREIGN_KEY = " FOREIGN KEY";
    private static final String REFERENCES = " REFERENCES";
    private static final String ON_UPDATE_CASCADE = " ON UPDATE CASCADE";
    private static final String ON_DELETE_SET_NULL = " ON DELETE SET NULL";
    private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE";


    public static final String TABLE_NAME_PRODUCTS = "Products";
    public static final String TABLE_NAME_CUSTOMER = "Customers";
    public static final String TABLE_NAME_ORDERS_CUSTOMER = "Orders";
    public static final String TABLE_NAME_ORDERS_CUSTOMER_PRODUCT = "Orders_Customer_Product";
    public static final String TABLE_NAME_REPORTS = "Reports";
    public static final String TABLE_NAME_MATERIAL = "Material";
    public static final String TABLE_NAME_CART = "Cart";
    public static final String TABLE_NAME_QUANTITY = "Quantity";

    public static final String COLUMN_NAME_PRODUCTSID = "productsID";
    public static final String COLUMN_NAME_CUSTOMERSID = "customersID";
    public static final String COLUMN_NAME_ORDERSID = "orders_ID";
    public static final String COLUMN_NAME_REPORTSID = "reportsID";
    public static final String COLUMN_NAME_MATERIALID = "materialID";
    public static final String COLUMN_NAME_CARTID = "cartID";
    public static final String COLUMN_NAME_QUANTITYID = "quantityID";
    public static final String COLUMN_NAME_ORDER_CUSTOMER_PRODUCT_ID = "orders_customer_product_ID";

    //default constructor
    private DatabaseContract() {
    }

    public static abstract class Table_Products implements BaseColumns {
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_SIZE = "size";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_QUANTITY = "instock";
        public static final String COLUMN_NAME_FAVORITE = "favorite";

        public static final String CREATE_TABLE_PRODUCTSS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_PRODUCTS + " (" +
                COLUMN_NAME_PRODUCTSID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA +
                COLUMN_NAME_IMAGE + BLOB_TYPE + COMMA +
                COLUMN_NAME_SIZE + TEXT_TYPE + COMMA +
                COLUMN_NAME_PRICE + REAL_TYPE + NOT_NULL + COMMA +
                COLUMN_NAME_QUANTITY + INTEGER_TYPE + COMMA +
                COLUMN_NAME_FAVORITE + INTEGER_TYPE + COMMA +
                COLUMN_NAME_MATERIALID + INTEGER_TYPE + NOT_NULL + COMMA +
                COLUMN_NAME_QUANTITYID + INTEGER_TYPE + COMMA +
                COLUMN_NAME_CARTID + INTEGER_TYPE + NOT_NULL + COMMA +
                FOREIGN_KEY + " (" +
                COLUMN_NAME_MATERIALID + ")" + REFERENCES + " " + TABLE_NAME_MATERIAL + " (" + COLUMN_NAME_MATERIALID + ")" +
                ON_UPDATE_CASCADE + COMMA +
                FOREIGN_KEY + " (" +
                COLUMN_NAME_QUANTITYID + ")" + REFERENCES + " " + TABLE_NAME_QUANTITY + " (" + COLUMN_NAME_QUANTITYID + ")" +
                ON_DELETE_SET_NULL + COMMA +
                FOREIGN_KEY + " (" +
                COLUMN_NAME_CARTID + ")" + REFERENCES + " " + TABLE_NAME_CART + " (" + COLUMN_NAME_CARTID + ")" +
                ON_DELETE_SET_NULL + ")";

        public static final String DROP_TABLE_PRODUCTS = "DROP TABLE IF EXIST " + TABLE_NAME_PRODUCTS;
    }

    public static abstract class Table_Customers implements BaseColumns {
        public static final String COLUMN_NAME_FIRSTNAME = "firstName";
        public static final String COLUMN_NAME_LASTNAME = "lastName";
        public static final String COLUMN_NAME_PHONE = "phone";

        public static final String CREATE_TABLE_CUSTOMERS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_CUSTOMER + " (" +
                COLUMN_NAME_CUSTOMERSID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_FIRSTNAME + TEXT_TYPE + NOT_NULL + COMMA +
                COLUMN_NAME_LASTNAME + TEXT_TYPE + NOT_NULL + COMMA +
                COLUMN_NAME_PHONE + INTEGER_TYPE + NOT_NULL + ")";

        public static final String DROP_TABLE_CUSTOMERS = "DROP TABLE IF EXISTS " + TABLE_NAME_CUSTOMER;
    }

    public static abstract class Table_Orders_Customer implements BaseColumns {
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_PRICE = "totalPrice";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CONFIRM_CODE = "cornfirmCode";

        public static final String CREATE_TABLE_ORDERS_CUSTOMER = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_ORDERS_CUSTOMER + " (" +
                COLUMN_NAME_ORDERSID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_CUSTOMERSID + INTEGER_TYPE + NOT_NULL + COMMA +
                COLUMN_NAME_PRICE + REAL_TYPE + COMMA +
                COLUMN_NAME_STATUS + TEXT_TYPE + NOT_NULL + COMMA +
                COLUMN_NAME_DATE + TEXT_TYPE + COMMA +
                COLUMN_NAME_CONFIRM_CODE + INTEGER_TYPE + NOT_NULL + COMMA +
                FOREIGN_KEY + " (" +
                COLUMN_NAME_CUSTOMERSID + ")" + REFERENCES + " " + TABLE_NAME_CUSTOMER + " (" +
                COLUMN_NAME_CUSTOMERSID + ")" + ON_DELETE_CASCADE + ")";

        public static final String DROP_TABLE_PRODUCTS_ORDERS = "DROP TABLE IF EXISTS " + TABLE_NAME_ORDERS_CUSTOMER;
    }

    public static abstract class Table_Reports implements BaseColumns {
        public static final String CREATE_TABLE_REPORTS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_REPORTS + " (" +
                COLUMN_NAME_REPORTSID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_ORDERSID + INTEGER_TYPE + NOT_NULL + COMMA +
                FOREIGN_KEY + " (" + COLUMN_NAME_ORDERSID + ")" + REFERENCES + " " + TABLE_NAME_ORDERS_CUSTOMER + " (" +
                COLUMN_NAME_ORDERSID + ")" + ON_UPDATE_CASCADE + ")";

        public static final String DROP_TABLE_REPORTS = "DROP TABLE IF EXISTS " + TABLE_NAME_REPORTS;
    }

    public static abstract class Table_Material implements BaseColumns {
        public static final String COLUMN_NAME_MATERIAL = "material";
        public static final String CREATE_TABLE_MATERIAL = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_MATERIAL + " (" +
                COLUMN_NAME_MATERIALID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_MATERIAL + TEXT_TYPE + NOT_NULL + ")";

        public static final String DROP_TABLE_MATERIAL = "DROP TABLE IF EXIST " + TABLE_NAME_MATERIAL;
    }

    public static abstract class Table_Cart implements BaseColumns {
        public static final String CREATE_TABLE_CART = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_CART + " (" +
                COLUMN_NAME_CARTID + " INTEGER PRIMARY KEY" + ")";

        public static final String DROP_TABLE_CART = "DROP TABLE IF EXISTS " + TABLE_NAME_CART;
    }

    public static abstract class Table_Quantity implements BaseColumns {
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String CREATE_TABLE_QUANTITY = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_QUANTITY + " (" +
                COLUMN_NAME_QUANTITYID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_QUANTITY + INTEGER_TYPE + ")";

        public static final String DROP_TABLE_QUANTITY = "DROP TABLE IF EXISTS " + TABLE_NAME_QUANTITY;
    }

    public static abstract class Table_Orders_Customer_Product implements BaseColumns {
        public static final String CREATE_TABLE_ORDERS_CUSTOMER_PRODUCT = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME_ORDERS_CUSTOMER_PRODUCT + " (" +
                COLUMN_NAME_ORDER_CUSTOMER_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                COLUMN_NAME_ORDERSID + INTEGER_TYPE  + COMMA +
                COLUMN_NAME_PRODUCTSID + INTEGER_TYPE  + COMMA +
                FOREIGN_KEY + " (" + COLUMN_NAME_ORDERSID + ")" + REFERENCES + " " + TABLE_NAME_ORDERS_CUSTOMER + " (" + COLUMN_NAME_ORDERSID + ")" + COMMA +
                FOREIGN_KEY + " (" + COLUMN_NAME_PRODUCTSID + ")" + REFERENCES + " " + TABLE_NAME_PRODUCTS + " (" + COLUMN_NAME_PRODUCTSID + ")" +")";

        public static final String DROP_TABLE_QUANTITY = "DROP TABLE IF EXISTS " + TABLE_NAME_QUANTITY;
    }
}
