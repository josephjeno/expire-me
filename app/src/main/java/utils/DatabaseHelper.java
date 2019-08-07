package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

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
        values.put(FoodItemEntry.COLUMN_NAME_EXPIRY_DATE, item.getExpiryDate());

        long result = db.insert(FoodItemEntry.TABLE_NAME, null, values);

        return result != -1;
    }

    private FoodItem getFoodItemFromCursor(Cursor cursor) {
        Log.d("getFoodItemFromCursor", "count=" + cursor.getCount() );
        if (cursor.getCount() == 0) { return null; }
        long itemId = cursor.getLong(
                cursor.getColumnIndexOrThrow(FoodItemEntry._ID));
        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_FOOD_NAME));
        String dateAdded = cursor.getString(
                cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_DATE_ADDED));
        String expiryDate = cursor.getString(
                cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_EXPIRY_DATE));
        String note = cursor.getString(
                cursor.getColumnIndexOrThrow(FoodItemEntry.COLUMN_NAME_NOTE));
        FoodItem foodItem = new FoodItem(name, note, dateAdded, expiryDate);
        foodItem.setId(itemId);
        return foodItem;
    }

    public FoodItem getItemById(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FoodItemEntry.COLUMN_NAME_FOOD_NAME,
                FoodItemEntry.COLUMN_NAME_DATE_ADDED,
                FoodItemEntry.COLUMN_NAME_EXPIRY_DATE,
                FoodItemEntry.COLUMN_NAME_NOTE
        };
        String selection = FoodItemEntry._ID + "=?";
        String[] selectionArgs = {id.toString()};
        Log.d("getItemById", selection + id);
        Cursor cursor = db.query(
                FoodItemEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        cursor.moveToNext();
        FoodItem foodItem = getFoodItemFromCursor(cursor);
        Log.d("getItemById", foodItem.getName());
        return foodItem;
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
        String sortOrder = FoodItemEntry.COLUMN_NAME_EXPIRY_DATE + " DESC";
        Cursor cursor = db.query(
                FoodItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        ArrayList<FoodItem> foodItems = new ArrayList<>();
        while (cursor.moveToNext()){
            foodItems.add(getFoodItemFromCursor(cursor));
        }
        // now sort these items according to expiration date
        Collections.sort(foodItems, new Comparator<FoodItem>() {
            @Override
            public int compare(FoodItem o1, FoodItem o2) {
                return o1.getDateExpiration().compareTo(o2.getDateExpiration());
            }
        });
        return foodItems;
    }

    // Returns list of FoodItems from DB filtered by listType ("ALL", "SOON", "EXPIRED")
    public ArrayList<FoodItem> getFilteredItems(String listType) {
        // Populate the data into the arrayList
        ArrayList<FoodItem> items = getAllItems();
        // remove items according to list type
        Log.d("ItemListActivity", "listType=" + listType);
        if (listType != null && !listType.equals("ALL")) {
            Date currentDate = new Date();

            ListIterator<FoodItem> listIterator = items.listIterator();
            while(listIterator.hasNext()) {
                FoodItem item = listIterator.next();
                // check if result of student is "Fail"
                long diffInMillies = item.getDateExpiration().getTime() - currentDate.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (listType.equals("EXPIRED")) {
                    if (diffInDays >= 0)
                        listIterator.remove();
                } else if (listType.equals("SOON")) {
                    if (diffInDays > 3 || diffInDays < 0)
                        listIterator.remove();
                }
            }
        }
        return items;
    }

    public boolean deleteItem(Long itemId){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = FoodItemEntry._ID + "=?";
        String[] selectionArgs = {itemId.toString()};
        int deletedRows = db.delete(FoodItemEntry.TABLE_NAME, selection, selectionArgs);
        return deletedRows == 1;
    }

    public boolean updateItem(FoodItem item){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FoodItemEntry.COLUMN_NAME_FOOD_NAME, item.getName());
        values.put(FoodItemEntry.COLUMN_NAME_NOTE, item.getNote());
        values.put(FoodItemEntry.COLUMN_NAME_DATE_ADDED, item.getDateAdded());
        values.put(FoodItemEntry.COLUMN_NAME_EXPIRY_DATE, item.getExpiryDate());

        String selection = FoodItemEntry._ID + "=?";
        String[] selectionArgs = {item.getId().toString()};

        int count = db.update(FoodItemEntry.TABLE_NAME, values, selection, selectionArgs);

        return count == 1;
    }
}
