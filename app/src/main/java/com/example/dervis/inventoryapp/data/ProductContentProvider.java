package com.example.dervis.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.dervis.inventoryapp.data.ProductContract.ProductEntry;

public class ProductContentProvider extends ContentProvider {

    private ProductDbHelper mDbHelper;

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS,
                PRODUCTS);

        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,
                ProductContract.PATH_PRODUCTS + "/#",
                PRODUCT_ID);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion Not Supported For Uri:" + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        if (!areInsertionValuesValid(values))
            return null;


        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(ProductEntry.TABLE_NAME, null, values);

        if (id > -1) {
            Toast.makeText(getContext(), "New Row Id: " + id, Toast.LENGTH_SHORT).show();
            notifyChangeInData(uri);
            return ContentUris.withAppendedId(uri, id);
        } else {
            Toast.makeText(getContext(), "Insertion Was Unsuccessful!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private boolean areInsertionValuesValid(ContentValues values) {

        String columnProductName = ProductEntry.COLUMN_PRODUCT_NAME;
        if (values.containsKey(columnProductName)) {
            if (values.getAsString(columnProductName) == null)
                throw new IllegalArgumentException("A Name is required");
        } else {
            throw new IllegalArgumentException("A Name is required");
        }

        String columnProductPrice = ProductEntry.COLUMN_PRODUCT_PRICE;
        if (values.containsKey(columnProductPrice)) {
            if (values.getAsString(columnProductPrice) == null)
                throw new IllegalArgumentException("A Name is required");
        } else {
            throw new IllegalArgumentException("A Name is required");
        }

        String columnProductQuantity = ProductEntry.COLUMN_PRODUCT_QUANTITY;
        if (values.containsKey(columnProductQuantity)) {
            if (values.getAsString(columnProductQuantity) == null)
                throw new IllegalArgumentException("Price is required");
        } else {
            throw new IllegalArgumentException("A Name is required");
        }

        String columnProductSupplierName = ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
        if (values.containsKey(columnProductSupplierName)) {
            if (values.getAsString(columnProductSupplierName) == null)
                throw new IllegalArgumentException("Supplier's name is required");
        } else {
            throw new IllegalArgumentException("A Name is required");
        }

        String columnProductSupplierPhone = ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE;
        if (values.containsKey(columnProductSupplierPhone)) {
            if (values.getAsString(columnProductSupplierPhone) == null)
                throw new IllegalArgumentException("suppliers Phone Number is required");
        } else {
            throw new IllegalArgumentException("A Name is required");
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:

                return queryProducts(uri, projection, selection, selectionArgs, sortOrder);
            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = extractSelectionArgs(uri);

                return queryProducts(uri, projection, selection, selectionArgs, sortOrder);
            default:
                throw new IllegalArgumentException("Uri Query Supported:" + uri);
        }
    }

    private Cursor queryProducts(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(ProductEntry.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:

                return deleteProduct(uri, selection, selectionArgs);
            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = extractSelectionArgs(uri);

                return deleteProduct(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Uri Query not Supported:" + uri);
        }
    }

    private int deleteProduct(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deleteRows = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);

        if (deleteRows > 0) {
            Toast.makeText(getContext(), "Rows Deleted :" + deleteRows, Toast.LENGTH_SHORT).show();
            notifyChangeInData(uri);
        } else {
            Toast.makeText(getContext(), "Deletion Unsuccessful", Toast.LENGTH_SHORT).show();
        }

        return deleteRows;
    }

    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return ProductEntry.CONTENT_DIR_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Uri is not Supported:" + uri);
        }
    }

    private void notifyChangeInData(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    private String[] extractSelectionArgs(Uri uri) {
        return new String[]{String.valueOf(ContentUris.parseId(uri))};
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:

                selection = ProductEntry._ID + "=?";
                selectionArgs = extractSelectionArgs(uri);

                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Uri is not Supported: " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (!areUpdateValuesValid(values))
            return 0;

        int rowsAffected = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsAffected > 0) {
            Toast.makeText(getContext(), "Rows Updated :" + rowsAffected, Toast.LENGTH_SHORT).show();
            notifyChangeInData(uri);
        } else {
            Toast.makeText(getContext(), "Update Unsuccessful", Toast.LENGTH_SHORT).show();
        }

        return rowsAffected;
    }

    private boolean areUpdateValuesValid(ContentValues values) {

        String columnProductName = ProductEntry.COLUMN_PRODUCT_NAME;
        if (values.containsKey(columnProductName)) {
            if (values.getAsString(columnProductName) == null)
                throw new IllegalArgumentException("A Name is required");
        }

        String columnProductPrice = ProductEntry.COLUMN_PRODUCT_PRICE;
        if (values.containsKey(columnProductPrice)) {
            if (values.getAsString(columnProductPrice) == null)
                throw new IllegalArgumentException("A Name is required");
        }

        String columnProductQuantity = ProductEntry.COLUMN_PRODUCT_QUANTITY;
        if (values.containsKey(columnProductQuantity)) {
            if (values.getAsString(columnProductQuantity) == null)
                throw new IllegalArgumentException("Price is required");
        }

        String columnProductSupplierName = ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME;
        if (values.containsKey(columnProductSupplierName)) {
            if (values.getAsString(columnProductSupplierName) == null)
                throw new IllegalArgumentException("Supplier's name is required");
        }

        String columnProductSupplierPhone = ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE;
        if (values.containsKey(columnProductSupplierPhone)) {
            if (values.getAsString(columnProductSupplierPhone) == null)
                throw new IllegalArgumentException("suppliers Phone Number is required");
        }

        return true;
    }
}
