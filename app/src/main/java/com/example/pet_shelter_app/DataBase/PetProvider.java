package com.example.pet_shelter_app.DataBase;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.pet_shelter_app.DataBase.PetContract.PetSchema;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PetProvider extends ContentProvider {
    public static PetDbHelper mDbHelper;
    public static final UriMatcher sUriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
    public static final int PETS = 100;
    public static final int PETS_ID = 101;
    static{
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PETS_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                 cursor = database.query(PetSchema.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PETS_ID:
                selection = PetSchema._ID + "=?";
                selectionArgs = new String[]{String.valueOf(
                        ContentUris.parseId(uri)
                )};
                cursor = database.query(PetSchema.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetSchema.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetSchema.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return insertPet(uri,values);
            default:
                throw new IllegalArgumentException("cannot query unknown URI"+uri);
        }

    }

    private Uri insertPet(Uri uri, ContentValues values){
        // Check that the name is not null
        String name = values.getAsString(PetSchema.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the gender is valid
        Integer gender = values.getAsInteger(PetSchema.COLUMN_PET_GENDER);
        if (gender == null || !PetSchema.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetSchema.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long new_row_id = database.insert(PetSchema.TABLE_NAME,null,values);

        if(new_row_id == -1){
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,new_row_id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int DeletedRow ;
        switch (match) {
            case PETS:
                DeletedRow = database.delete(PetSchema.TABLE_NAME, selection, selectionArgs);
                break;
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = PetSchema._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                DeletedRow =  database.delete(PetSchema.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                DeletedRow = 0;
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (DeletedRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return DeletedRow;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch(match){
            case PETS:
                return updatePet(uri,values,selection,selectionArgs);
            case PETS_ID:
                selection = PetSchema._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(PetSchema.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetSchema.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(PetSchema.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetSchema.COLUMN_PET_GENDER);
            if (gender == null || !PetSchema.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }
        if (values.containsKey(PetSchema.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetSchema.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }
         SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PetSchema.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
