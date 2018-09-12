package com.example.dervis.inventoryapp;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dervis.inventoryapp.data.ProductContract;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER_ID = 1;
    private Uri mItemUri;
    private boolean mChangeDetected = false;
    private EditText et_price;
    private EditText et_name;
    private EditText et_quantity;
    private EditText et_supplier;
    private EditText et_supplier_phone;

    View[] inputFields;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        et_price = findViewById(R.id.et_price);
        et_name = findViewById(R.id.et_product_name);
        et_quantity = findViewById(R.id.et_quantity);
        et_supplier = findViewById(R.id.et_supplier);
        et_supplier_phone = findViewById(R.id.et_supplier_phone);

        inputFields = new View[]{et_name, et_price, et_quantity, et_supplier, et_supplier_phone};

        setupChangeListener();

        mItemUri = getIntent().getData();

        if (mItemUri != null) {
            getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
        }

        View[] quantityButtons = new View[]{findViewById(R.id.btn_increase_quantity),
                findViewById(R.id.btn_decrease_quantity)};

        for (View v : quantityButtons) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(et_quantity.getText().toString());

                    switch (v.getId()) {
                        case R.id.btn_increase_quantity:

                            et_quantity.setText(String.valueOf(++quantity));

                            break;
                        case R.id.btn_decrease_quantity:

                            if (quantity > 0) {
                                et_quantity.setText(String.valueOf(--quantity));
                            } else {
                                Toast.makeText(ProductActivity.this, "Negative Values Are Not Allowed!", Toast.LENGTH_SHORT).show();
                            }

                            break;
                    }
                }
            });
        }
    }

    private void setupChangeListener() {

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mChangeDetected = true;
                return false;
            }
        };

        for (View v : inputFields) {
            v.setOnTouchListener(touchListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save_changes:
                ContentValues values = extractContentValues();

                ContentResolver resolver = getContentResolver();

                if (mItemUri != null) {
                    resolver.update(mItemUri, values, null, null);
                } else {
                    resolver.insert(ProductContract.ProductEntry.CONTENT_URI, values);
                }

                finish();
                return true;
            case android.R.id.home:
                if (mChangeDetected) {
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setMessage("Are You Sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProductActivity.this.finish();
            }
        });

        builder.show();

        return false;
    }

    private ContentValues extractContentValues() {
        return ProductContract.ProductEntry.CreateContentValues(
                ((EditText) findViewById(R.id.et_product_name)).getText().toString(),
                Integer.parseInt(((EditText) findViewById(R.id.et_price)).getText().toString()),
                Integer.parseInt(((EditText) findViewById(R.id.et_quantity)).getText().toString()),
                ((EditText) findViewById(R.id.et_supplier)).getText().toString(),
                ((EditText) findViewById(R.id.et_supplier_phone)).getText().toString());
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, mItemUri,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            for (String name : cursor.getColumnNames()) {
                int columnIndex = cursor.getColumnIndexOrThrow(name);
                String cursorString = cursor.getString(columnIndex);

                switch (name) {
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_NAME:
                        et_name.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE:
                        et_price.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY:
                        et_quantity.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME:
                        et_supplier.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE:
                        et_supplier_phone.setText(cursorString);
                        break;
                }
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
