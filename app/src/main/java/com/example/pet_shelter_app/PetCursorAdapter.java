package com.example.pet_shelter_app;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.pet_shelter_app.DataBase.PetContract.PetSchema;
import com.example.pet_shelter_app.DataBase.PetDbHelper;

import org.w3c.dom.Text;

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.single_pet_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name_view = (TextView) view.findViewById(R.id.name);
        TextView breed_view = (TextView) view.findViewById(R.id.breed);



        String name = cursor.getString(cursor.getColumnIndex(PetSchema.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndex(PetSchema.COLUMN_PET_BREED));

        if (TextUtils.isEmpty(breed)) {
            breed = "Unknown Breed";
        }
        name_view.setText(name);
        breed_view.setText(breed);

    }
}
