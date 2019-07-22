package com.example.expireme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

import utils.FoodItem;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static class FoodItemEntry implements BaseColumns{
        private static final String TABLE_NAME = "FoodItems";
        private static final String COLUMN_NAME_FOOD_NAME = "name";
        private static final String COLUMN_NAME_DATE_ADDED = "addedon";
        private static final String COLUMN_NAME_EXPIRY_DATE = "expirydate";
        private static final String COLUMN_NAME_NOTE = "note";
    }

    public DatabaseHelper(Context context) {
        super(context, FoodItemEntry.TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + FoodItemEntry.TABLE_NAME + " (" +
                FoodItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FoodItemEntry.COLUMN_NAME_FOOD_NAME + " TEXT, " +
                FoodItemEntry.COLUMN_NAME_DATE_ADDED + " TEXT, " +
                FoodItemEntry.COLUMN_NAME_EXPIRY_DATE + " TEXT, " +
                FoodItemEntry.COLUMN_NAME_NOTE + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    // Don't do anything on upgrade for now. Placeholder for possible
    // modifications to the database schema in future versions of the app.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addFoodItem(FoodItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodItemEntry.COLUMN_NAME_FOOD_NAME, item.getName());
        values.put(FoodItemEntry.COLUMN_NAME_NOTE, item.getNote());
        values.put(FoodItemEntry.COLUMN_NAME_DATE_ADDED, item.getDateAdded());
        values.put(FoodItemEntry.COLUMN_NAME_EXPIRY_DATE, item.getExpirtyDate());

        long result = db.insert(FoodItemEntry.TABLE_NAME, null, values);

        if (result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public ArrayList<FoodItem> getAllItems(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FoodItemEntry.COLUMN_NAME_FOOD_NAME,
                FoodItemEntry.COLUMN_NAME_DATE_ADDED,
                FoodItemEntry.COLUMN_NAME_EXPIRY_DATE,
                FoodItemEntry.COLUMN_NAME_NOTE
        };
        Cursor cursor = db.query(
                FoodItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        ArrayList<FoodItem> foodItems = new ArrayList<>();
        while (cursor.moveToNext()){
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FoodItemEntry._ID));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_FOOD_NAME));
            String dateadded = cursor.getString(
                    cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_DATE_ADDED));
            String expiryDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_EXPIRY_DATE));
            String note = cursor.getString(
                    cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_NOTE));
            FoodItem foodItem = new FoodItem(name, dateadded, expiryDate, note);
            foodItem.setId(itemId);
            foodItems.add(foodItem);
        }
        return foodItems;
    }


    public boolean deleteItem(Long itemId){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FoodItemEntry._ID + "=?";
        String[] selectionArgs = {itemId.toString()};
        int deletedRows = db.delete(FoodItemEntry.TABLE_NAME, selection, selectionArgs);
        if(deletedRows == 1){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean updateItem(FoodItem item){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FoodItemEntry.COLUMN_NAME_FOOD_NAME, item.getName());
        values.put(FoodItemEntry.COLUMN_NAME_NOTE, item.getNote());
        values.put(FoodItemEntry.COLUMN_NAME_DATE_ADDED, item.getDateAdded());
        values.put(FoodItemEntry.COLUMN_NAME_EXPIRY_DATE, item.getExpirtyDate());

        String selection = FoodItemEntry._ID + "=?";
        String[] selectionArgs = {item.getId().toString()};

        int count = db.update(FoodItemEntry.TABLE_NAME, values, selection, selectionArgs);

        if(count == 1){
            return true;
        }
        else{
            return false;
        }
    }
}
