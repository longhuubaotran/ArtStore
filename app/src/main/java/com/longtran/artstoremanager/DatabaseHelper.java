package com.longtran.artstoremanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.Table_Cart.CREATE_TABLE_CART);
        db.execSQL(DatabaseContract.Table_Quantity.CREATE_TABLE_QUANTITY);
        db.execSQL(DatabaseContract.Table_Material.CREATE_TABLE_MATERIAL);
        db.execSQL(DatabaseContract.Table_Products.CREATE_TABLE_PRODUCTSS);
        db.execSQL(DatabaseContract.Table_Customers.CREATE_TABLE_CUSTOMERS);
        db.execSQL(DatabaseContract.Table_Orders_Customer.CREATE_TABLE_ORDERS_CUSTOMER);
        db.execSQL(DatabaseContract.Table_Orders_Customer_Product.CREATE_TABLE_ORDERS_CUSTOMER_PRODUCT);
        db.execSQL(DatabaseContract.Table_Reports.CREATE_TABLE_REPORTS);
        insertFirstRowToTableCart(db); // only 1 cart for each time, so only 1 row in Cart table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // insert to table products, an image must be compressed well before insert into sqlite
    public void insertTableProducts(String description, long material,
                                    Double price, String size, byte[] image, int quantity, int favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_DESCRIPTION, description);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_PRICE, price);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_SIZE, size);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_IMAGE, image);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_FAVORITE, favorite);
        values.put(DatabaseContract.COLUMN_NAME_MATERIALID, material);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY, quantity);
        values.put(DatabaseContract.COLUMN_NAME_QUANTITYID, 0);
        values.put(DatabaseContract.COLUMN_NAME_CARTID, 0);
        db.insert(DatabaseContract.TABLE_NAME_PRODUCTS, null, values);
        db.close();
    }


    public long insertNewMaterial(String material) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Material.COLUMN_NAME_MATERIAL, material);
        long materialID = db.insert(DatabaseContract.TABLE_NAME_MATERIAL, null, values);
        db.close();
        return materialID;
    }

    // get products from table then add to list
    public ArrayList<ProductsPainting> getProductsList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ProductsPainting> productsList = new ArrayList<>();
        String queryGetFabricPainting = "Select " + DatabaseContract.TABLE_NAME_PRODUCTS + ".*," + DatabaseContract.TABLE_NAME_MATERIAL + "." + DatabaseContract.Table_Material.COLUMN_NAME_MATERIAL + " from " + DatabaseContract.TABLE_NAME_PRODUCTS + " INNER JOIN " + DatabaseContract.TABLE_NAME_MATERIAL + " ON " + DatabaseContract.TABLE_NAME_PRODUCTS + "." + DatabaseContract.COLUMN_NAME_MATERIALID + " = " + DatabaseContract.TABLE_NAME_MATERIAL + "." + DatabaseContract.COLUMN_NAME_MATERIALID;
        Cursor cursor = db.rawQuery(queryGetFabricPainting, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    productsList.add(new ProductsPainting(cursor.getInt(cursor.getColumnIndex(DatabaseContract.COLUMN_NAME_PRODUCTSID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Material.COLUMN_NAME_MATERIAL)),
                            cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_PRICE)),
                            cursor.getBlob(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_SIZE)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_FAVORITE))));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        db.close();
        return new ArrayList<>(productsList);
    }

    private long getTotalOfRowsOfTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, tableName);
        db.close();
        return count;
    }

    // set or unset favorite for product
    public void setFavorite(String description, int favorite) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_FAVORITE, favorite);
        db.update(DatabaseContract.TABLE_NAME_PRODUCTS, values,
                DatabaseContract.Table_Products.COLUMN_NAME_DESCRIPTION + " =? ", new String[]{description});
        db.close();
    }

    private void insertFirstRowToTableCart(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.COLUMN_NAME_CARTID, 1);
        db.insert(DatabaseContract.TABLE_NAME_CART, null, values);
    }

    // add product To cart based on productId by updating cartId by 1, because  cartId = 0 means item not in cart
    public void addProductToCart(int productId, long quantityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.COLUMN_NAME_QUANTITYID, quantityId);
        values.put(DatabaseContract.COLUMN_NAME_CARTID, 1);
        db.update(DatabaseContract.TABLE_NAME_PRODUCTS, values, DatabaseContract.COLUMN_NAME_PRODUCTSID + " =? ", new String[]{String.valueOf(productId)});
        db.close();
    }

    //get all products have the same cart
    public ArrayList<ProductsPainting> getProductInCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ProductsPainting> productInCart = new ArrayList<>();

        // Although we want the product with cartID = 1 which means it's in cart,
        // but we need to include inner join with material table because we get material from it to create product object
        String query = "Select " + DatabaseContract.TABLE_NAME_PRODUCTS + ".*," + DatabaseContract.TABLE_NAME_MATERIAL + "." + DatabaseContract.Table_Material.COLUMN_NAME_MATERIAL + " from " + DatabaseContract.TABLE_NAME_PRODUCTS + " INNER JOIN " + DatabaseContract.TABLE_NAME_MATERIAL + " ON " + DatabaseContract.TABLE_NAME_PRODUCTS + "." + DatabaseContract.COLUMN_NAME_MATERIALID + " = " + DatabaseContract.TABLE_NAME_MATERIAL + "." + DatabaseContract.COLUMN_NAME_MATERIALID + " and " + DatabaseContract.COLUMN_NAME_CARTID + " =? ";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(1)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    productInCart.add(new ProductsPainting(cursor.getInt(cursor.getColumnIndex(DatabaseContract.COLUMN_NAME_PRODUCTSID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Material.COLUMN_NAME_MATERIAL)),
                            cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_PRICE)),
                            cursor.getBlob(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_SIZE)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_FAVORITE))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return new ArrayList<>(productInCart);
    }

    public boolean checkProductExistInCart(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * From " + DatabaseContract.TABLE_NAME_PRODUCTS + " where " + DatabaseContract.COLUMN_NAME_PRODUCTSID + " =? and " + DatabaseContract.TABLE_NAME_PRODUCTS + "." + DatabaseContract.COLUMN_NAME_CARTID + " =? ";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(productId), String.valueOf(1)});
        int result = cursor.getCount();
        if (result == 1) {
            return true;
        } else {
            return false;
        }
    }

    // delete an product in cart by setting cartID = 0, because cartID = 1 means it's in cart
    public void deleteProductInCart(int productID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.COLUMN_NAME_CARTID, 0);
        db.update(DatabaseContract.TABLE_NAME_PRODUCTS, values,
                DatabaseContract.COLUMN_NAME_PRODUCTSID + " =? ", new String[]{String.valueOf(productID)});
        db.close();
    }

    public long createANewCustomer(String firstName, String lastName, int phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Customers.COLUMN_NAME_FIRSTNAME, firstName);
        values.put(DatabaseContract.Table_Customers.COLUMN_NAME_LASTNAME, lastName);
        values.put(DatabaseContract.Table_Customers.COLUMN_NAME_PHONE, phone);
        long row = db.insert(DatabaseContract.TABLE_NAME_CUSTOMER, null, values);
        db.close();
        return row;
    }

    public long createNewOrderCustomer(long customerId, double price, int confirmCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        long orderId;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.COLUMN_NAME_CUSTOMERSID, customerId);
        values.put(DatabaseContract.Table_Orders_Customer.COLUMN_NAME_PRICE, price);
        values.put(DatabaseContract.Table_Orders_Customer.COLUMN_NAME_STATUS, "Pending");
        values.put(DatabaseContract.Table_Orders_Customer.COLUMN_NAME_CONFIRM_CODE, confirmCode);
        values.put(DatabaseContract.Table_Orders_Customer.COLUMN_NAME_DATE, dateFormat.format(date));
        orderId = db.insert(DatabaseContract.TABLE_NAME_ORDERS_CUSTOMER, null, values);
        db.close();
        return orderId;
    }

    public Customer getCustomerInformation(long customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Customer customer = new Customer();
        Cursor cursor;
        String query = "select * from " + DatabaseContract.TABLE_NAME_CUSTOMER + " where " + DatabaseContract.COLUMN_NAME_CUSTOMERSID + " =? ";
        cursor = db.rawQuery(query, new String[]{String.valueOf(customerId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    customer.setFirstName(cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Customers.COLUMN_NAME_FIRSTNAME)));
                    customer.setLastName(cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Customers.COLUMN_NAME_LASTNAME)));
                    customer.setPhone(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Customers.COLUMN_NAME_PHONE)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return customer;
    }

    // add the default quantity, the defaul quantity is 1
    public long addQuantity(int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        long quantityId;
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Quantity.COLUMN_NAME_QUANTITY, quantity);
        quantityId = db.insert(DatabaseContract.TABLE_NAME_QUANTITY, null, values);
        db.close();
        return quantityId;
    }

    //update quantity of a product in cart
    public void updateQuantity(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // update Quantity set quantity = ... where quantityId IN (Select  quantityId from Product where productId = ...)
        String query = "update " + DatabaseContract.TABLE_NAME_QUANTITY +
                " set " + DatabaseContract.Table_Quantity.COLUMN_NAME_QUANTITY + " = "
                + String.valueOf(quantity) + " where " + DatabaseContract.COLUMN_NAME_QUANTITYID +
                " In ( select " + DatabaseContract.COLUMN_NAME_QUANTITYID +
                " from " + DatabaseContract.TABLE_NAME_PRODUCTS +
                " where " + DatabaseContract.COLUMN_NAME_PRODUCTSID + " =  " + String.valueOf(productId) + ")";

        db.execSQL(query);
        db.close();
    }

    public void deleteAQuantity(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // by default, sqlite disables FK constraints because of backward compability
        // enable this every time working with FK constraint, in this case, delete a row in quantity table
        // also set quantityId in product table to null
        db.setForeignKeyConstraintsEnabled(true);
        String query = "delete " +
                " from " + DatabaseContract.TABLE_NAME_QUANTITY +
                " where " + DatabaseContract.COLUMN_NAME_QUANTITYID +
                " In ( select " + DatabaseContract.COLUMN_NAME_QUANTITYID +
                " from " + DatabaseContract.TABLE_NAME_PRODUCTS +
                " where " + DatabaseContract.COLUMN_NAME_PRODUCTSID + " =  " + String.valueOf(productId) + ")";
        db.execSQL(query);
        db.close();
    }

    //update all cartId = 0 for all product in cart after placing order successfully
    public void resetCartId() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.COLUMN_NAME_CARTID, 0);
        db.update(DatabaseContract.TABLE_NAME_PRODUCTS, values, null, null);
        db.close();
    }

    // return a quantity from quantity Table for a product based on productId
    public int getQuantityByProductId(int productId) {
        int quantity = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        // select quantity from Quantity inner join Products on Quantity.quantityId = Products.productId and productId = ...
        String query = "select " + DatabaseContract.TABLE_NAME_QUANTITY + "." + DatabaseContract.Table_Quantity.COLUMN_NAME_QUANTITY +
                " from " + DatabaseContract.TABLE_NAME_QUANTITY +
                " inner join " + DatabaseContract.TABLE_NAME_PRODUCTS +
                " on " + DatabaseContract.TABLE_NAME_QUANTITY + "." + DatabaseContract.COLUMN_NAME_QUANTITYID + " = "
                + DatabaseContract.TABLE_NAME_PRODUCTS + "." + DatabaseContract.COLUMN_NAME_QUANTITYID +
                " and " + DatabaseContract.COLUMN_NAME_PRODUCTSID + " = " + String.valueOf(productId);

        cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                quantity = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Quantity.COLUMN_NAME_QUANTITY));

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return quantity;
    }

    // this method will set cartId = 0 to products which have cartId = 1 in products Table
    // this all so delete all records in Quantity table and Customer table
    public void cancelAnOrder(List<ProductsPainting> productInCart) {
        ProductsPainting product;
        int count = 0;
        while (count < productInCart.size()) {
            product = productInCart.get(count);
            deleteProductInCart(product.getId());
            count++;
        }
        deleteAllRecordsInQuantityTable();
        deleteAllRecordsInCustomerTable();
    }

    private void deleteAllRecordsInQuantityTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + DatabaseContract.TABLE_NAME_QUANTITY;
        db.execSQL(query);
        db.close();
    }

    private void deleteAllRecordsInCustomerTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + DatabaseContract.TABLE_NAME_CUSTOMER;
        db.execSQL(query);
        db.close();
    }

    public void createANewOrderCustomerProduct(long orderId, List<ProductsPainting> productInCart) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ProductsPainting product;
        int count = 0;
        while (count < productInCart.size()) {
            product = productInCart.get(count);
            values.put(DatabaseContract.COLUMN_NAME_ORDERSID, orderId);
            values.put(DatabaseContract.COLUMN_NAME_PRODUCTSID, product.getId());
            db.insert(DatabaseContract.TABLE_NAME_ORDERS_CUSTOMER_PRODUCT, null, values);
            count++;
        }
        db.close();
    }

    private void updateNewInstockByProductId(int newInstock, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY, newInstock);
        db.update(DatabaseContract.TABLE_NAME_PRODUCTS, values, DatabaseContract.COLUMN_NAME_PRODUCTSID + " =? ", new String[]{String.valueOf(productId)});
        db.close();
    }

    // update the instock available of products in cart, then delete all records in quantity table
    public void updateInstock(List<ProductsPainting> productIncart) {
        int currentQuantity, newInstock;
        int count = 0;
        ProductsPainting product;
        while (count < productIncart.size()) {
            product = productIncart.get(count);
            currentQuantity = getQuantityByProductId(product.getId());
            newInstock = product.getQuantity() - currentQuantity;
            updateNewInstockByProductId(newInstock, product.getId());
            count++;
        }
        deleteAllRecordsInQuantityTable();
    }

    // get list of material with Id and value
    public List<SpinnerObject> getAllMaterial() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        List<SpinnerObject> listOfMaterial = new ArrayList<>();
        String query = "select * from " + DatabaseContract.TABLE_NAME_MATERIAL;
        cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                listOfMaterial.add(new SpinnerObject(cursor.getInt(cursor.getColumnIndex(DatabaseContract.COLUMN_NAME_MATERIALID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Material.COLUMN_NAME_MATERIAL))));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return listOfMaterial;
    }


    public void updateAProduct(int productId, String description, Double price, int inStock, byte[] byteArr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_DESCRIPTION, description);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_PRICE, price);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY, inStock);
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_IMAGE, byteArr);
        db.update(DatabaseContract.TABLE_NAME_PRODUCTS, values, DatabaseContract.COLUMN_NAME_PRODUCTSID + " =? ", new String[]{String.valueOf(productId)});
        db.close();
    }

    public void deleteAProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteAQuantity(productId);
        db.delete(DatabaseContract.TABLE_NAME_PRODUCTS, DatabaseContract.COLUMN_NAME_PRODUCTSID + " =? ", new String[]{String.valueOf(productId)});
        db.close();
    }

    public List<ProductsPainting> getListOfLowInStockProduct() {
        ArrayList<ProductsPainting> listOfLowInStockProduct = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "select * from " + DatabaseContract.TABLE_NAME_PRODUCTS +
                " inner join " + DatabaseContract.TABLE_NAME_MATERIAL + " on " +
                DatabaseContract.TABLE_NAME_PRODUCTS + "." + DatabaseContract.COLUMN_NAME_PRODUCTSID + " = " +
                DatabaseContract.TABLE_NAME_MATERIAL + "." + DatabaseContract.COLUMN_NAME_MATERIALID + " where "
                + DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY + " <= 20";
        cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                listOfLowInStockProduct.add(new ProductsPainting(cursor.getInt(cursor.getColumnIndex(DatabaseContract.COLUMN_NAME_PRODUCTSID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Material.COLUMN_NAME_MATERIAL)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_PRICE)),
                        cursor.getBlob(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_SIZE)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Products.COLUMN_NAME_FAVORITE))));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return listOfLowInStockProduct;
    }

    // update instock of a product
    public void updateInStockOfProduct(int productId, int newInStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Products.COLUMN_NAME_QUANTITY, newInStock);
        db.update(DatabaseContract.TABLE_NAME_PRODUCTS, values, DatabaseContract.COLUMN_NAME_PRODUCTSID + " =? ", new String[]{String.valueOf(productId)});
        db.close();
    }

    //get list of pending orders
    public List<Integer> getListOfPendingOrders() {
        ArrayList<Integer> listOfPendingOrders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "select " + DatabaseContract.Table_Orders_Customer.COLUMN_NAME_CONFIRM_CODE +
                " from " + DatabaseContract.TABLE_NAME_ORDERS_CUSTOMER + " where " +
                DatabaseContract.Table_Orders_Customer.COLUMN_NAME_STATUS + " =? ";
        cursor = db.rawQuery(query, new String[]{"Pending"});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                listOfPendingOrders.add(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Orders_Customer.COLUMN_NAME_CONFIRM_CODE)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return listOfPendingOrders;
    }

    //set status of an order to complete
    public void updateStatusAnOrder(int orderNum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Table_Orders_Customer.COLUMN_NAME_STATUS, "complete");
        db.update(DatabaseContract.TABLE_NAME_ORDERS_CUSTOMER, values, DatabaseContract.Table_Orders_Customer.COLUMN_NAME_CONFIRM_CODE + " =? ", new String[]{String.valueOf(orderNum)});
        db.close();
    }

    //get list of complete orders
    public List<Integer> getListOfCompleteOrders() {
        ArrayList<Integer> listOfCompleteOrders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String query = "select " + DatabaseContract.Table_Orders_Customer.COLUMN_NAME_CONFIRM_CODE +
                " from " + DatabaseContract.TABLE_NAME_ORDERS_CUSTOMER + " where " +
                DatabaseContract.Table_Orders_Customer.COLUMN_NAME_STATUS + " =? ";
        cursor = db.rawQuery(query, new String[]{"complete"});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                listOfCompleteOrders.add(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Table_Orders_Customer.COLUMN_NAME_CONFIRM_CODE)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return listOfCompleteOrders;
    }

    //get sum of total sale by date time
    public double getTotalSaleByMonth(String month, String year, String day) {
        double result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        String queryDay;
        String query;
        if (day.equals("None")) {
            query = " select totalPrice from " + "Orders" + " where " + DatabaseContract.Table_Orders_Customer.COLUMN_NAME_DATE + " like " + "'" + year + "-" + month + "%" + "'";
            cursor = db.rawQuery(query, null);
        } else {
            queryDay = " select totalPrice from " + "Orders" + " where " + DatabaseContract.Table_Orders_Customer.COLUMN_NAME_DATE + " like " + "'" + year + "-" + month + "-" + day + "%" + "'";
            cursor = db.rawQuery(queryDay, null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                result += cursor.getDouble(cursor.getColumnIndex("totalPrice"));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return result;
    }
}
//+ " where " + DatabaseContract.Table_Orders_Customer.COLUMN_NAME_DATE + " like " + "'" + year + "-" + month + "%" + "'"