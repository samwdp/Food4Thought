package com.food4thought.test.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.food4thought.test.model.RestaurantDataModel;
import com.food4thought.test.model.RestaurantModel;

import java.util.ArrayList;

/**
 * Handles the creation of the database with adding and getting the infromation
 * Created by samwdp on 09/04/2016.
 */
public class RestaurantDatabase extends SQLiteOpenHelper implements RestaurantListener{

    public static final String DATABASE_NAME = "favourites.db";
    public static final String TABLE_NAME = "favourites_table";
    public static final String COL_0 = "KEY_ID";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "PLACE_ID";
    public static final String COL_3 = "NAME";
    public static final String COL_4 = "RATING";
    public static final String COL_5 = "REFERENCE";
    public static final String COLUMN_NAME_NULLABLE = "null";

    public static final String CREATE_TABLE_RESTAURANT = "CREATE TABLE " + TABLE_NAME
            + " (" + COL_0+ " INTEGER PRIMARY KEY,"
            + COL_1+" TEXT UNIQUE,"
            + COL_2+" TEXT,"
            + COL_3+" TEXT,"
            + COL_4+" FLOAT,"
            + COL_5+" TEXT)";


    public RestaurantDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RESTAURANT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void addRestaurant(RestaurantDataModel restaurantModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_1, restaurantModel.getId());
        Log.w("JSON", restaurantModel.getName());
        values.put(COL_2, restaurantModel.getPlaceId());
        values.put(COL_3, restaurantModel.getName());
        values.put(COL_4, restaurantModel.getRating());

        long newRowId;
        newRowId = db.insert(
                TABLE_NAME,
                COLUMN_NAME_NULLABLE,
                values);

    }

    @Override
    public ArrayList<RestaurantDataModel> getAllRestaurant() {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor      = db.rawQuery(selectQuery, null);
        ArrayList<RestaurantDataModel> r = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                RestaurantDataModel rest = new RestaurantDataModel();
                rest.setId(cursor.getString(1));
                rest.setPlaceId(cursor.getString(2));
                rest.setRating(cursor.getFloat(3));
                Log.w("JSON", cursor.getString(3));
                rest.setReference(cursor.getString(4));
                if(rest != null) {
                    r.add(rest);
                } else{
                    Log.w("JSON", "nothing in resturant");
                }

            } while (cursor.moveToNext());
        }
        cursor.close();

        return r;
    }

    @Override
    public int getRestaurantCount() {
        return 0;
    }
}
