package com.example.expireme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    static final int EDIT_ITEM = 0;

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
    DatabaseHelper dbHelper;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK, null);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void populateComponents(FoodItem foodItem) {
        itemTitleTextView.setText("             ");
        itemNameTextView.setText(foodItem.getName());
        itemExpirationTextView.setText(foodItem.getExpiryDate());
        if (foodItem.getNote().length() == 0) {
            itemNotesTitleTextView.setTextSize(0);
            itemNotesTextView.setTextSize(0);
        } else
            itemNotesTextView.setText(foodItem.getNote());
        if (foodItem.getDateAdded().length() == 0) {
            itemAddedDateTitleTextView.setTextSize(0);
            itemAddedDateTextView.setTextSize(0);
        } else
            itemAddedDateTextView.setText(foodItem.getDateAdded());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        dbHelper = new DatabaseHelper(getApplicationContext());
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

        food = new FoodItem(
                itemName,
                itemNotes,
                itemAddedDate,
                itemExpiration
        );
        food.setId(itemID);
        populateComponents(food);
    }

    // When back button clicked
    // TODO: remove this
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
            //DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteItem(food.getId());
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
        startActivityForResult(intent, EDIT_ITEM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "itemID=" + itemID + " requestCode="+ requestCode + " resultCode=" + resultCode);
        if (requestCode == EDIT_ITEM && resultCode == RESULT_OK) {
            FoodItem dbFoodItem = dbHelper.getItemById(itemID);
            Log.d("onActivityResult", "itemID=" + itemID + " dbName="+ dbFoodItem.getName());
            populateComponents(dbFoodItem);
        }
    }
}
