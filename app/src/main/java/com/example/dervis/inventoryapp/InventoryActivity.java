package com.example.dervis.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import static com.example.dervis.inventoryapp.data.ProductContract.ProductEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_PRODUCTS_ID = 100;
    private ListView mProductsListView;
    private CursorAdapter mProductsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        mProductsListView = findViewById(R.id.lv_inventory);

        mProductsAdapter = new ProductsCursorAdapter(this, null);

        setupFab();

        setupListView();

        getSupportLoaderManager().initLoader(LOADER_PRODUCTS_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_insert_dummy:

                insertDummyRow();

                return true;
            case R.id.action_delete_all:

                //confirm that the user wants to delete all rows
                Utils.showConfirmDialog("Do You Want To Delete All Products?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAllRowsInTable();
                            }
                        },
                        this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_PRODUCTS_ID:
                return new CursorLoader(this, ProductEntry.CONTENT_URI,
                        null, null, null, null);
            default:
                throw new IllegalArgumentException("Unknown Loader ID!");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mProductsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mProductsAdapter.swapCursor(null);
    }


    private void deleteAllRowsInTable() {
        getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
    }

    private void insertDummyRow() {
        ContentValues values = ProductEntry.CreateContentValues("Apple", 1, 10, "An Apple Tree", "+123");

        this.getContentResolver().insert(ProductEntry.CONTENT_URI,
                values);
    }

    private void setupFab() {
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupListView() {
        mProductsListView.setEmptyView(findViewById(R.id.empty_view_message));

        mProductsListView.setAdapter(mProductsAdapter);

        mProductsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                Intent intent = new Intent(InventoryActivity.this, ProductActivity.class);

                intent.setData(uri);

                startActivity(intent);
            }
        });
    }
}
