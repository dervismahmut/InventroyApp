package com.example.dervis.inventoryapp;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

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
        setContentView(R.layout.product_activity);

        et_price = findViewById(R.id.et_price);
        et_name = findViewById(R.id.et_product_name);
        et_quantity = findViewById(R.id.et_quantity);
        et_supplier = findViewById(R.id.et_supplier);
        et_supplier_phone = findViewById(R.id.et_supplier_phone);

        inputFields = new View[]{et_name, et_price, et_quantity, et_supplier, et_supplier_phone, findViewById(R.id.btn_decrease_quantity), findViewById(R.id.btn_increase_quantity)};

        findViewById(R.id.btn_call_supplier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChangeDetected) {
                    showConfirmDialog(
                            "Would you like to save changes?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveChangesAndFinish();
                                    contactSupplier();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            },
                            ProductActivity.this);
                } else {
                    contactSupplier();
                }
            }
        });


        setupChangeListener();

        loadItemFromUriIfAvailable();

        setupQuantityButtons();
    }

    private void loadItemFromUriIfAvailable() {
        mItemUri = getIntent().getData();

        if (mItemUri != null) {
            getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
        }
    }

    private void setupQuantityButtons() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mItemUri == null) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public static void showConfirmDialog(String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);

        builder.setPositiveButton("Yes", positiveListener);

        builder.setNegativeButton("No", negativeListener);

        builder.create().show();
    }

    private boolean isInputValid() {
        return !et_name.getText().toString().isEmpty() &&
                !et_price.getText().toString().isEmpty() &&
                !et_quantity.getText().toString().isEmpty() &&
                !et_supplier.getText().toString().isEmpty() &&
                !et_supplier_phone.getText().toString().isEmpty();
    }

    private void contactSupplier() {
        String s = et_supplier_phone.getText().toString();

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
            ContentValues values = extractContentValues();

            ContentResolver resolver = getContentResolver();

            if (mItemUri != null) {
                resolver.update(mItemUri, values, null, null);
            } else {
                resolver.insert(ProductContract.ProductEntry.CONTENT_URI, values);
            }

            mChangeDetected = false;

            finish();
        } else {
            Toast.makeText(this, "Please Make Sure All Fields Are Filled", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                if (mItemUri != null) {
                    showConfirmDialog("Do you want to delete this Product?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContentResolver().delete(mItemUri, null, null);
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, this);
                } else {
                    showConfirmDialog("do you want to discard changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, this);
                }
                return true;
            case R.id.action_save_changes:

                saveChangesAndFinish();

                return true;
            case android.R.id.home:
                if (mChangeDetected) {
                    showConfirmDialog("Do you want to discard changes?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProductActivity.this.finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
    public void onBackPressed() {
        if (mChangeDetected) {
            showConfirmDialog("Do you want to discard changes?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProductActivity.this.finish();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    this);
        } else {
            super.onBackPressed();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, mItemUri,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        loadDataFromCursor(cursor);
    }

    private void loadDataFromCursor(Cursor cursor) {
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
        for (View v : inputFields) {
            if (v instanceof EditText) {
                ((EditText) v).setText("");
            }
        }
    }
}
