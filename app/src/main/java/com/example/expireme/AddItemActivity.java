package com.example.expireme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import utils.DatabaseHelper;
import utils.FoodItem;

public class AddItemActivity extends AppCompatActivity {

    ImageButton checkButton;

    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemAddedDateTextView;

    Calendar calendar;
    int year,month,dayOfMonth;
    DatePickerDialog datePickerDialog;

    private String userIntent;
    private FoodItem foodItem = null;

    // Database helper
    private DatabaseHelper dbHelper;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemNameTextView = findViewById(R.id.add_item_name);
        itemExpirationTextView = findViewById(R.id.editTextExpirationDate);
        itemNotesTextView = findViewById(R.id.add_item_notes);
        itemAddedDateTextView = findViewById(R.id.editTextPurchasedOnDate);

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

        //backButton = findViewById(R.id.add_item_button_back);
        checkButton = findViewById(R.id.add_item_check_button);

        itemExpirationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddItemActivity", "onCreate.date.setOnClickListener.onClick");
                datePicker(itemExpirationTextView);
            }
        });

        itemAddedDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddItemActivity", "onCreate.date.setOnClickListener.onClick");
                datePicker(itemAddedDateTextView);
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    public void onCheckButtonClicked() {
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

    //display date picker dialog
    public void datePicker(final TextView date) {
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("AddItemActivity", "datePicker");

        datePickerDialog = new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myOnDateSet(date, datePicker, i, i1,i2);

            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public void myOnDateSet(TextView date, DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("AddItemActivity", "onDateSet");
        date.setText((month+1) + "-" + dayOfMonth + "-" + year);
    }

    public void updateFood(final FoodItem foodItem, final DatabaseHelper dbHelper, final View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(foodItem.getName());
        alert.setMessage("Are you sure you want to update " + foodItem.getName() +"?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                        dbHelper.updateItem(foodItem);
                        String message = foodItem.getName() + " has been updated";
                        Toast toast = Toast.makeText(
                            getApplicationContext(),
                            message,
                            Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                }
                });
                alert.show();
            }

}



