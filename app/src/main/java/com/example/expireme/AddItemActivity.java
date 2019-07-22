package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import utils.DatabaseHelper;
import utils.FoodItem;

public class AddItemActivity extends AppCompatActivity {

    ImageButton backButton;
    ImageButton checkButton;

    TextView itemTitleTextView;
    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemAddedDateTextView;

    // When back button clicked
    public void onbackButtonClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(explicitIntent);
    }

    Button select_Date;
    Calendar calendar;
    int year,month,dayOfMonth;
    DatePickerDialog datePickerDialog;

    private void updateItemInfo() {
        //Extract resourceIDS
        String itemName = getIntent().getStringExtra("ItemName");
        String itemExpiration = getIntent().getStringExtra("ItemExpiration");
        String itemNotes = getIntent().getStringExtra("ItemNotes");
        String itemAddedDate = getIntent().getStringExtra("ItemAddedDate");

        itemTitleTextView.setText(itemName);
        itemNameTextView.setText(itemName);
        itemExpirationTextView.setText(itemExpiration);
        itemNotesTextView.setText(itemNotes);
        itemAddedDateTextView.setText(itemAddedDate);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemTitleTextView = findViewById(R.id.item_title_text);
        itemNameTextView = findViewById(R.id.add_item_name);
        itemExpirationTextView = findViewById(R.id.editTextExpirationDate);
        itemNotesTextView = findViewById(R.id.add_item_notes);
        itemAddedDateTextView = findViewById(R.id.editTextPurchasedOnDate);


        backButton = findViewById(R.id.add_item_button_back);
        checkButton = findViewById(R.id.add_item_check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add item to database
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                FoodItem foodItem = new FoodItem(
                        itemNameTextView.getText().toString(),
                        itemNotesTextView.getText().toString(),
                        itemAddedDateTextView.getText().toString(),
                        itemExpirationTextView.getText().toString()
                );
                dbHelper.addFoodItem(foodItem);
                onbackButtonClicked(view);
            }
        });

        //////////////////////////////////// iold code below

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

}
