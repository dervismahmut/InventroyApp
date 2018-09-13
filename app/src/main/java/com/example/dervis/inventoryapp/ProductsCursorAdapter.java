package com.example.dervis.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.dervis.inventoryapp.data.ProductContract.ProductEntry;

class ProductsCursorAdapter extends CursorAdapter {

    public ProductsCursorAdapter(Context context, Cursor data) {
        super(context, data, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item,
                parent, false);
    }


    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        String column_id = ProductEntry._ID;
        String column_name = ProductEntry.COLUMN_PRODUCT_NAME;
        String column_price = ProductEntry.COLUMN_PRODUCT_PRICE;
        String column_quantity = ProductEntry.COLUMN_PRODUCT_QUANTITY;
        String column_supplier = ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME;


        int columnIndex_id = cursor.getColumnIndexOrThrow(column_id);
        int columnIndex_name = cursor.getColumnIndexOrThrow(column_name);
        int columnIndex_price = cursor.getColumnIndexOrThrow(column_price);
        int columnIndex_quantity = cursor.getColumnIndexOrThrow(column_quantity);
        int columnIndex_supplier = cursor.getColumnIndexOrThrow(column_supplier);


        final String id = cursor.getString(columnIndex_id);
        String name = cursor.getString(columnIndex_name);
        String price = cursor.getString(columnIndex_price);
        final String quantity = cursor.getString(columnIndex_quantity);
        String supplier = cursor.getString(columnIndex_supplier);


        ((TextView) view.findViewById(R.id.tv_name)).setText(name);
        ((TextView) view.findViewById(R.id.tv_price)).setText(price);
        ((TextView) view.findViewById(R.id.tv_quantity)).setText(quantity);
        ((TextView) view.findViewById(R.id.tv_supplier)).setText(supplier);


        view.findViewById(R.id.btn_sale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Long.parseLong(id));

                reduceQuantityForUri(context, uri, Integer.parseInt(quantity));
            }
        });


    }

    private void reduceQuantityForUri(Context context, Uri uri, int quantity) {


        if (quantity > 0) {
            ContentResolver resolver = context.getContentResolver();

            ContentValues contentValues = new ContentValues();

            contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,
                    --quantity);

            resolver.update(uri, contentValues, null, null);
        } else {
            Toast.makeText(context, "Negative Stock is not allowed", Toast.LENGTH_SHORT).show();
        }

    }
}
