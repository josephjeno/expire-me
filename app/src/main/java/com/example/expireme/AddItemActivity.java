package com.example.expireme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import utils.DatabaseHelper;
import utils.FoodItem;
import utils.StoredFood;

public class AddItemActivity extends AppCompatActivity {

    private AutoCompleteTextView itemNameTextView;
    private TextView itemExpirationTextView;
    private TextView itemNotesTextView;
    private TextView itemAddedDateTextView;

    private Calendar calendar;

    private String userIntent;
    private FoodItem foodItem = null;

    // Database helper
    private DatabaseHelper dbHelper;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);

        itemNameTextView = findViewById(R.id.add_item_name);
        List<String> names = new ArrayList<>();
        List<Integer> days = new ArrayList<>();
        for(StoredFood food: dbHelper.getAllStoredItems()){
            names.add(food.getName());
            days.add(food.getDays());
        }
        String [] foodNames = new String[names.size()];
        names.toArray(foodNames);
        Integer [] foodDays = new Integer[days.size()];
        days.toArray(foodDays);
        Log.d("Array:", "onCreate: " + days.toString());
        ArrayAdapter<String> foodAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodNames);
        itemNameTextView.setAdapter(foodAdapter);
        itemExpirationTextView = findViewById(R.id.editTextExpirationDate);
        itemNotesTextView = findViewById(R.id.add_item_notes);
        itemAddedDateTextView = findViewById(R.id.editTextPurchasedOnDate);

        itemNameTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date date = new Date();
            String time = sdf.format(date);
            itemAddedDateTextView.setText(time);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, foodDays[i]);
            Date expirationDate = calendar.getTime();
            itemExpirationTextView.setText(sdf.format(expirationDate));
        });

        dbHelper = new DatabaseHelper(getApplicationContext());

        long foodItemKey = getIntent().getLongExtra("food", -1);
        userIntent = getIntent().getStringExtra("userIntent");

        if(foodItemKey != -1){
            foodItem = dbHelper.getItemById(foodItemKey);
            populateFields(foodItem);
            getSupportActionBar().setTitle("Edit Item");
        } else {
            getSupportActionBar().setTitle("Add new Item");
        }

        itemExpirationTextView.setOnClickListener(v -> {
            Log.d("AddItemActivity", "onCreate.date.setOnClickListener.onClick");
            datePicker(itemExpirationTextView);
        });

        itemAddedDateTextView.setOnClickListener(v -> {
            Log.d("AddItemActivity", "onCreate.date.setOnClickListener.onClick");
            datePicker(itemAddedDateTextView);
        });

        // Set purchased date to today by default
        if (itemAddedDateTextView.getText().length() == 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("M-d-yyyy");
            itemAddedDateTextView.setText(dateFormat.format(new Date()));
        }

        calendar=Calendar.getInstance();
        Log.d("AddItemActivity", "onCreate");
    }

    // Used to display custom Action Bar (with buttons)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_add_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Used to handle custom Action Bar button clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.add_item_check_button:
                onCheckButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void populateFields(FoodItem intentFood) {

        itemNameTextView.setText(intentFood.getName());
        itemNotesTextView.setText(intentFood.getNote());
        itemAddedDateTextView.setText(intentFood.getDateAdded());
        itemExpirationTextView.setText(intentFood.getExpiryDate());
    }

    private void finishAddItem() {
        setResult(RESULT_OK, null);
        finish();
    }

    private void onCheckButtonClicked() {
        // verify minimum-requirements data name+expiration were given
        String itemName = itemNameTextView.getText().toString();
        String itemExpirationDate = itemExpirationTextView.getText().toString();
        Log.d("checkButton.OnClick", "name=" + itemName + "___" + itemExpirationDate + "___");
        if (itemName.length() == 0 || itemExpirationDate.length() == 0) {
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "Please indicate both Name and Expiration date!",
                    Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // add item to database
        if(userIntent == null){
            FoodItem foodItem = new FoodItem(
                    itemName,
                    itemNotesTextView.getText().toString(),
                    itemAddedDateTextView.getText().toString(),
                    itemExpirationDate
            );
            dbHelper.addFoodItem(foodItem);

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            try{
                Date dateAddedDate = sdf.parse(itemAddedDateTextView.getText().toString());
                Date expirationDateDate = sdf.parse(itemExpirationDate);
                Calendar added = new GregorianCalendar();
                Calendar expiration = new GregorianCalendar();
                added.setTime(dateAddedDate);
                expiration.setTime(expirationDateDate);
                Integer days = daysBetween(expiration.getTime(), added.getTime());
                StoredFood storedFood = new StoredFood(
                        itemName,
                        days
                );
                dbHelper.addStoredFoodItem(storedFood);
            }catch (Exception e){
                Log.d("AddItemActivity", "onCheckButtonClicked: " + e.toString());
            }

            finishAddItem();
        }else {
            if(foodItem != null){
                foodItem.setName(itemName);
                foodItem.setNote(itemNotesTextView.getText().toString());
                foodItem.setDateAdded(itemAddedDateTextView.getText().toString());
                foodItem.setExpiryDate(itemExpirationDate);
                dbHelper.updateItem(foodItem);
                finishAddItem();
            }
        }
    }

    private Integer daysBetween(Date d1, Date d2){
        return (int)( (d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24));
    }
    //display date picker dialog
    private void datePicker(final TextView date) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("AddItemActivity", "datePicker");

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemActivity.this, (datePicker, i, i1, i2) -> myOnDateSet(date, i, i1,i2), year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void myOnDateSet(TextView date, int year, int month, int dayOfMonth) {
        Log.d("AddItemActivity", "onDateSet");
        date.setText((month+1) + "-" + dayOfMonth + "-" + year);
    }
}



