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

//    ImageButton backButton;
    ImageButton checkButton;

    TextView itemTitleTextView;
    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemAddedDateTextView;

    Calendar calendar;
    int year,month,dayOfMonth;
    DatePickerDialog datePickerDialog;

    private String userIntent;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemTitleTextView = findViewById(R.id.item_title_text);
        itemNameTextView = findViewById(R.id.add_item_name);
        itemExpirationTextView = findViewById(R.id.editTextExpirationDate);
        itemNotesTextView = findViewById(R.id.add_item_notes);
        itemAddedDateTextView = findViewById(R.id.editTextPurchasedOnDate);

        final FoodItem intentFood = getIntent().getParcelableExtra("food");
        userIntent = getIntent().getStringExtra("userIntent");

        if(intentFood != null){
            populateFields(intentFood);
        }

        //backButton = findViewById(R.id.add_item_button_back);
        checkButton = findViewById(R.id.add_item_check_button);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                if(userIntent == null){
                    FoodItem foodItem = new FoodItem(
                            itemName,
                            itemNotesTextView.getText().toString(),
                            itemAddedDateTextView.getText().toString(),
                            itemExpirationDate
                    );
                    dbHelper.addFoodItem(foodItem);
                    onbackButtonClicked(view);
                }else {
                    if(intentFood != null){
                        intentFood.setName(itemName);
                        intentFood.setNote(itemNotesTextView.getText().toString());
                        intentFood.setDateAdded(itemAddedDateTextView.getText().toString());
                        intentFood.setExpiryDate(itemExpirationDate);
                        updateFood(intentFood, dbHelper, view);
                    }
                }
            }
        });

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
    private void populateFields(FoodItem intentFood) {

        itemTitleTextView.setText(intentFood.getName());
        itemNameTextView.setText(intentFood.getName());
        itemNotesTextView.setText(intentFood.getNote());
        itemAddedDateTextView.setText(intentFood.getDateAdded());
        itemExpirationTextView.setText(intentFood.getExpiryDate());
    }

    // When back button clicked
    public void onbackButtonClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(explicitIntent);
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
                        onbackButtonClicked(view);
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



