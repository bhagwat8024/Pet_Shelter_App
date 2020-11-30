package com.example.pet_shelter_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.ConsumerIrManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.pet_shelter_app.DataBase.PetContract;
import com.example.pet_shelter_app.DataBase.PetContract.PetSchema;
import com.example.pet_shelter_app.DataBase.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static PetDbHelper mPetdbHelper;
    private static SQLiteDatabase mReadDb;
    private static SQLiteDatabase mWriteDb;
    private static final int PET_LOADER = 0;

    PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView petListView = (ListView) findViewById(R.id.listView);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);
        mCursorAdapter = new PetCursorAdapter(this,null);
        petListView.setAdapter(mCursorAdapter);
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this,EditorActivity.class);

                Uri ContentUri = ContentUris.withAppendedId(PetSchema.CONTENT_URI,id);
                intent.setData(ContentUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(PET_LOADER, null, this);

    }
/*
    private void displayDataBaseInfo() {

        String[] projection={
                PetSchema._ID,
                PetSchema.COLUMN_PET_NAME,
                PetSchema.COLUMN_PET_BREED,
                PetSchema.COLUMN_PET_GENDER,
                PetSchema.COLUMN_PET_WEIGHT
        };

        Cursor cursor = getContentResolver().query(PetSchema.CONTENT_URI,projection,null,null,null);
       // Cursor cursor = mReadDb.query(PetSchema.TABLE_NAME,projection,null,null,null,null,null);

        ListView petListView = (ListView) findViewById(R.id.listView);
        PetCursorAdapter adapter = new PetCursorAdapter(this,cursor);

        petListView.setAdapter(adapter);
    }
    */
    private void insertPet(){
        ContentValues Values=new ContentValues();
        Values.put(PetSchema.COLUMN_PET_NAME,"Toto");
        Values.put(PetSchema.COLUMN_PET_BREED,"Terrier");
        Values.put(PetSchema.COLUMN_PET_GENDER,PetSchema.GENDER_MALE);
        Values.put(PetSchema.COLUMN_PET_WEIGHT,7);

        Uri newUri = getContentResolver().insert(PetSchema.CONTENT_URI,Values);

    }
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetSchema.CONTENT_URI, null, null);
    }
    // Both Are Creating Menu and Decide Behaviour of Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.insert:
                insertPet();
                return true;
            case R.id.delete:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                PetSchema._ID,
                PetSchema.COLUMN_PET_NAME,
                PetSchema.COLUMN_PET_BREED
        };
        return new CursorLoader(this,
                PetSchema.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}