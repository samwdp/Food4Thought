package com.food4thought.test.databse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.food4thought.test.model.RestaurantModel;

import java.util.ArrayList;

/**
 * Created by samwdp on 09/04/2016.
 */
public class RestaurantDatabase extends SQLiteOpenHelper implements RestaurantListener{

    public static final String DATABASE_NAME = "favourites.db";
    public static final String TABLE_NAME = "favourites_table";
    public static final String TYPES_TABLE = "types_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "PLACE_ID";
    public static final String COL_3 = "ICON";
    public static final String COL_4 = "VICINITY";
    public static final String COL_5 = "SCOPE";
    public static final String COL_6 = "NAME";
    public static final String COL_7 = "RATING";
    public static final String COL_8 = "REFERENCE";
    public static final String COL_9 = "TYPES";
    public static final String COL_10 = "OPEN_NOW";
    public static final String COL_11 = "WEEKDAY_TEXT";
    public static final String COL_12 = "PLACE_ID";
    public static final String COL_13 = "KEY_ID";
    public static final String COL_14 = "";

    public static final String CREATE_TABLE_RESTURANT = "CREATE TABLE " + TABLE_NAME
            + " (" + COL_13+ " INTEGER PRIMARY KEY,"
            + COL_1+" TEXT,"
            + COL_2+" TEXT,"
            + COL_3+" TEXT,"
            + COL_4+" TEXT,"
            + COL_5+" TEXT,"
            + COL_6+" TEXT,"
            + COL_7+" DOUBLE,"
            + COL_8+" TEXT,"
            + COL_9+" TEXT,"
            + COL_10+" BOOBLEAN,"
            + COL_11+" TEXT,"
            + COL_12+" TEXT,";

    public static final String CREATE_TABLE_TYPES = "CREATE TABLE " + TYPES_TABLE
            +" (" + COL_13+ " INTEGER PRIMARY KEY,"
            +COL_9+" TEXT,";

    public RestaurantDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table" + DATABASE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,RESTUARANT");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void addRestaurant(RestaurantModel restaurantModel) {

    }

    @Override
    public ArrayList<RestaurantModel> getAllResturant() {
        return null;
    }

    @Override
    public int getResturantCount() {
        return 0;
    }
}
