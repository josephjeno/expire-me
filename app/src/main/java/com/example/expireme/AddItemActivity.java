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

public class AddItemActivity extends AppCompatActivity {

    static String itemId = "0";
    ImageButton backButton;
    ImageButton checkButton;

    TextView itemTitleTextView;
    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemAddedDateTextView;

    public static String getItemId() {
        return itemId;
    }

    public static void setItemId(String itemId) {
        itemId = itemId;
    }


    // When back button clicked
    public void onbackButtonClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(explicitIntent);
    }

    Button select_Date;
    Calendar calendar;
    int year,month,dayOfMonth;
    DatePickerDialog datePickerDialog;

    private void updateItemInfo(String newItemId) {
        //Extract resourceIDS
        setItemId(newItemId);
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

        itemId = "0";
        String newItemId = getIntent().getStringExtra("ItemId");
        Log.d("add_item", "new id=" + newItemId + "__" + itemId);
        if (newItemId != null) {
            updateItemInfo(newItemId);
        }
        Log.d("add_item", "new id=" + newItemId + "__" + itemId);
        //itemIdTextView.setText("555");
        backButton = findViewById(R.id.add_item_button_back);

        checkButton = findViewById(R.id.add_item_check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ItemListActivity.class);
                intent.putExtra("ItemName", itemNameTextView.getText().toString()   );
                intent.putExtra("ItemExpiration", itemExpirationTextView.getText().toString());
                intent.putExtra("ItemNotes", itemNotesTextView.getText().toString());
                intent.putExtra("ItemAddedDate", itemAddedDateTextView.getText().toString());
                intent.putExtra("ItemId", itemId);
                Log.d("add_item onclick", "id=" + itemId);
                startActivity(intent);
            }
        });

        //////////////////////////////////// old code below

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
