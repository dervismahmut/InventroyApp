package com.example.dervis.inventoryapp;

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
    public void bindView(View view, Context context, final Cursor cursor) {
        for (String name : cursor.getColumnNames()) {

            int columnIndex = cursor.getColumnIndexOrThrow(name);

            final String columnString = cursor.getString(columnIndex);

            switch (name) {
                case ProductEntry._ID:

                    view.findViewById(R.id.btn_sale).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContentValues values = new ContentValues();

                            DatabaseUtils.cursorRowToContentValues(cursor, values);

                            values.remove(ProductEntry._ID);

                            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);

                            Long id = Long.parseLong(columnString);

                            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);

                            Uri itemUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                            if (quantity > 0) {
                                v.getContext().getContentResolver().update(itemUri,
                                        values, null, null);
                            } else {
                                Toast.makeText(v.getContext(), "Can't have negative stock!", Toast.LENGTH_SHORT).show();
                            }
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
}
