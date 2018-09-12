package com.example.dervis.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
        for (String name : cursor.getColumnNames()) {

            int columnIndex = cursor.getColumnIndexOrThrow(name);

            final String columnString = cursor.getString(columnIndex);

            switch (name) {
                case ProductEntry._ID:

                    view.findViewById(R.id.btn_sale).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, Integer.parseInt(columnString));

                            reduceQuantityForUri(context, uri);
                        }
                    });

                case ProductEntry.COLUMN_PRODUCT_NAME:

                    ((TextView) view.findViewById(R.id.tv_name)).setText(columnString);

                    break;
                case ProductEntry.COLUMN_PRODUCT_PRICE:

                    ((TextView) view.findViewById(R.id.tv_price)).setText(columnString);

                    break;
                case ProductEntry.COLUMN_PRODUCT_QUANTITY:

                    ((TextView) view.findViewById(R.id.tv_quantity)).setText(columnString);

                    break;
                case ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME:

                    ((TextView) view.findViewById(R.id.tv_supplier)).setText(columnString);

                    break;
            }
        }
    }

    private void reduceQuantityForUri(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();

            DatabaseUtils.cursorRowToContentValues(cursor, contentValues);

            String columnProductQuantity = ProductEntry.COLUMN_PRODUCT_QUANTITY;

            Integer quantity = contentValues.getAsInteger(columnProductQuantity);

            if (quantity > 0) {
                contentValues.put(columnProductQuantity, quantity);

                resolver.update(uri, contentValues, null, null);
            }
        }
    }
}
