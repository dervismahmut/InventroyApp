package com.example.dervis.inventoryapp.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.dervis.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse(
            ContentResolver.SCHEME_CONTENT + "://" + CONTENT_AUTHORITY
    );

    public static final String PATH_PRODUCTS = "products";

    public static class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = PATH_PRODUCTS;

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone";

        public static ContentValues CreateContentValues(String name, int price, int quantity,
                                                        String supplierName, String supplierPhone) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_PRODUCT_NAME, name);
            values.put(COLUMN_PRODUCT_PRICE, price);
            values.put(COLUMN_PRODUCT_QUANTITY, quantity);
            values.put(COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
            values.put(COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

            return values;
        }
    }
}
