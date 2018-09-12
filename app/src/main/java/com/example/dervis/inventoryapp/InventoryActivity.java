package com.example.dervis.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import static com.example.dervis.inventoryapp.data.ProductContract.ProductEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCTS_LOADER_ID = 100;
    private ListView mListView;
    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inventory_activity);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        mListView = findViewById(R.id.lv_inventory);

        mListView.setEmptyView(findViewById(R.id.empty_view_message));

        mAdapter = new ProductsCursorAdapter(this, null);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                Intent intent = new Intent(InventoryActivity.this, ProductActivity.class);

                intent.setData(uri);

                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(PRODUCTS_LOADER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ContentResolver contentResolver = this.getContentResolver();

        switch (item.getItemId()) {

            case R.id.action_insert_dummy:

                ContentValues values = ProductEntry.CreateContentValues("A", 1, 1, "B", "C");

                contentResolver.insert(ProductEntry.CONTENT_URI,
                        values);
                return true;

            case R.id.action_delete_all:

                contentResolver.delete(ProductEntry.CONTENT_URI,
                        null, null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, ProductEntry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
