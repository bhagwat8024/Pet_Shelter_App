package com.example.pet_shelter_app.DataBase;
import com.example.pet_shelter_app.DataBase.PetContract.PetSchema;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PetDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "shelter.db";
    public static final int DATABASE_VERSION = 1;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetSchema.TABLE_NAME + " ( "
                + PetSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetSchema.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetSchema.COLUMN_PET_BREED + " TEXT, "
                + PetSchema.COLUMN_PET_GENDER + " INTEGER NOT NULL , "
                + PetSchema.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
