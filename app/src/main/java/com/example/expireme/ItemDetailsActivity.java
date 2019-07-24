package com.example.expireme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import utils.DatabaseHelper;
import utils.FoodItem;

public class ItemDetailsActivity extends AppCompatActivity {

    //ImageButton backButton;

    TextView itemTitleTextView;
    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemNotesTitleTextView;
    TextView itemAddedDateTextView;
    TextView itemAddedDateTitleTextView;
    private Long itemID;
    String itemName;
    FoodItem food;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //backButton = findViewById(R.id.add_item_button_back);
        itemTitleTextView = findViewById(R.id.item_title);
        itemNameTextView = findViewById(R.id.item_name);
        itemExpirationTextView = findViewById(R.id.item_expiration);
        itemNotesTitleTextView = findViewById(R.id.itemDetailsNoteTitle);
        itemNotesTextView = findViewById(R.id.itemDetailsNoteText);
        itemAddedDateTitleTextView = findViewById(R.id.itemDetailsPurchasedOnTitle);
        itemAddedDateTextView = findViewById(R.id.itemDetailsPurchasedOnText);
        //Extract resourceIDS
        itemName = getIntent().getStringExtra("ItemName");
        String itemExpiration = getIntent().getStringExtra("ItemExpiration");
        String itemNotes = getIntent().getStringExtra("ItemNotes");
        String itemAddedDate = getIntent().getStringExtra("ItemAddedDate");
        itemID = getIntent().getLongExtra("ItemId", -1);
        itemTitleTextView.setText("             ");
        itemNameTextView.setText(itemName);
        itemExpirationTextView.setText(itemExpiration);
        if (itemNotes.length() == 0) {
            itemNotesTitleTextView.setTextSize(0);
            itemNotesTextView.setTextSize(0);
        } else
            itemNotesTextView.setText(itemNotes);
        if (itemAddedDate.length() == 0) {
            itemAddedDateTitleTextView.setTextSize(0);
            itemAddedDateTextView.setTextSize(0);
        } else
            itemAddedDateTextView.setText(itemAddedDate);      
        food = new FoodItem(
                itemName,
                itemNotes,
                itemAddedDate,
                itemExpiration
        );
        food.setId(itemID);
    }

    // When back button clicked
    public void onbackButtonClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(explicitIntent);
    }

    public void onDeleteButtonClicked(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // needs to be declared final to be accessed from inner class
        final View viewFromOuterClass = view;
        alert.setTitle(itemName);
        alert.setMessage("Are you sure you want to delete " + itemName +"?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
            public void onClick(DialogInterface dialog, int which) {
                helper.deleteItem(food.getId());
                onbackButtonClicked(viewFromOuterClass);
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void onEditButtonClicked(View view){
        Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
        intent.putExtra("userIntent", "editItem");
        intent.putExtra("food", food);
        startActivity(intent);

    }
}
