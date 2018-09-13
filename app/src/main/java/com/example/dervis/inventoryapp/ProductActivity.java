package com.example.dervis.inventoryapp;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dervis.inventoryapp.data.ProductContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER_ID = 1;
    private Uri mDataItemUri;
    private boolean mChangeDetected = false;
    private EditText editTextPrice;
    private EditText editTextName;
    private EditText editTextQuantity;
    private EditText editTextSupplierName;
    private EditText editTextSupplierPhoneNumber;

    View[] inputFields;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activity);

        editTextPrice = findViewById(R.id.et_price);
        editTextName = findViewById(R.id.et_product_name);
        editTextQuantity = findViewById(R.id.et_quantity);
        editTextSupplierName = findViewById(R.id.et_supplier);
        editTextSupplierPhoneNumber = findViewById(R.id.et_supplier_phone);


        //these input fields have been collected for listening to change in UI data.
        inputFields = new View[]{editTextName, editTextPrice, editTextQuantity, editTextSupplierName, editTextSupplierPhoneNumber, findViewById(R.id.btn_decrease_quantity), findViewById(R.id.btn_increase_quantity)};


        setupContactSupplierButton();

        setupChangeListener();

        loadItemFromUriIfAvailable();

        setupQuantityButtons();
    }

    private void setupContactSupplierButton() {
        findViewById(R.id.btn_call_supplier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChangeDetected) {
                    Utils.showConfirmDialog(
                            "Would You Like To Save Changes?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveChangesAndFinish();
                                    contactSupplier();
                                }
                            }, ProductActivity.this);
                } else {
                    contactSupplier();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                //check if activity is in product detail mode
                if (mDataItemUri != null) {
                    Utils.showConfirmDialog("Do you want to delete this Product?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCurrentProduct();
                            finish();
                        }
                    }, this);
                }
                return true;
            case R.id.action_save_changes:

                saveChangesAndFinish();

                return true;
            case android.R.id.home:
                if (mChangeDetected) {
                    //confirm before discarding inputted information
                    Utils.showConfirmDialog("Do You Want To Discard Changes?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //go the inventory activity by finishing current activity
                            ProductActivity.this.finish();
                        }
                    }, this);
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //hide the delete menu item if activity is in creating new product mode
        if (mDataItemUri == null) {
            menu.findItem(R.id.action_delete).setVisible(false);
            return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mChangeDetected) {
            Utils.showConfirmDialog("Do you want to discard changes?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss changes by letting the system handle the event as usual.
                            ProductActivity.super.onBackPressed();
                        }
                    }, this);
        } else {
            //let the system handle it as usual.
            super.onBackPressed();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case PRODUCT_LOADER_ID:
                return new CursorLoader(this, mDataItemUri,
                        null, null, null, null);
            default:
                throw new IllegalArgumentException("Unknown Loader ID!");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        loadDataFromCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        for (View v : inputFields) {
            if (v instanceof EditText) {
                ((EditText) v).setText("");
            }
        }
    }

    private void loadItemFromUriIfAvailable() {
        mDataItemUri = getIntent().getData();

        if (mDataItemUri != null) {
            getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
            getSupportActionBar().setTitle(getString(R.string.product_activity_edit_title));
        } else {
            getSupportActionBar().setTitle(getString(R.string.product_activity_add_title));
        }
    }

    private void contactSupplier() {
        if (!isInputValid()) {
            Toast.makeText(this, "Please Make Sure All Fields Are Filled", Toast.LENGTH_SHORT).show();
            return;
        }

        String s = editTextSupplierPhoneNumber.getText().toString();

        if (s.isEmpty())
            Toast.makeText(this, "Please Provide A number first", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + s));
            startActivity(intent);
        }
    }

    private void saveChangesAndFinish() {
        if (isInputValid()) {
            ContentResolver resolver = getContentResolver();

            if (mDataItemUri != null) {
                resolver.update(mDataItemUri, extractContentValues(), null, null);
            } else {
                resolver.insert(ProductContract.ProductEntry.CONTENT_URI, extractContentValues());
            }

            finish();
        } else {
            Toast.makeText(this, "Please Make Sure All Fields Are Filled", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputValid() {
        return !editTextName.getText().toString().isEmpty() &&
                !editTextPrice.getText().toString().isEmpty() &&
                !editTextQuantity.getText().toString().isEmpty() &&
                !editTextSupplierName.getText().toString().isEmpty() &&
                !editTextSupplierPhoneNumber.getText().toString().isEmpty();
    }

    /**
     * this method loops throw cursor columns and reacts according to column name.
     */
    private void loadDataFromCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {

            for (String name : cursor.getColumnNames()) {

                int columnIndex = cursor.getColumnIndexOrThrow(name);
                String cursorString = cursor.getString(columnIndex);

                switch (name) {
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_NAME:
                        editTextName.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE:
                        editTextPrice.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY:
                        editTextQuantity.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME:
                        editTextSupplierName.setText(cursorString);
                        break;
                    case ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE:
                        editTextSupplierPhoneNumber.setText(cursorString);
                        break;
                }
            }
        }
    }

    private ContentValues extractContentValues() {
        return ProductContract.ProductEntry.CreateContentValues(
                ((EditText) findViewById(R.id.et_product_name)).getText().toString(),
                Integer.parseInt(((EditText) findViewById(R.id.et_price)).getText().toString()),
                Integer.parseInt(((EditText) findViewById(R.id.et_quantity)).getText().toString()),
                ((EditText) findViewById(R.id.et_supplier)).getText().toString(),
                ((EditText) findViewById(R.id.et_supplier_phone)).getText().toString());
    }

    private void setupChangeListener() {
        for (View v : inputFields) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v1, MotionEvent event) {
                    mChangeDetected = true;
                    return false;
                }
            });
        }
    }

    private void setupQuantityButtons() {
        final View[] quantityButtons = new View[]{findViewById(R.id.btn_increase_quantity),
                findViewById(R.id.btn_decrease_quantity)};

        for (View v : quantityButtons) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String quantityString = editTextQuantity.getText().toString();
                    int quantity = 0;

                    if (!quantityString.isEmpty()) {
                        quantity = Integer.parseInt(quantityString);
                    }

                    switch (v.getId()) {
                        case R.id.btn_increase_quantity:

                            editTextQuantity.setText(String.valueOf(++quantity));

                            break;
                        case R.id.btn_decrease_quantity:

                            if (quantity > 0) {
                                editTextQuantity.setText(String.valueOf(--quantity));
                            } else {
                                Toast.makeText(ProductActivity.this, "Negative Values Are Not Allowed!", Toast.LENGTH_SHORT).show();
                            }

                            break;
                    }
                }
            });
        }
    }

    private void deleteCurrentProduct() {
        getContentResolver().delete(mDataItemUri, null, null);
    }
}
