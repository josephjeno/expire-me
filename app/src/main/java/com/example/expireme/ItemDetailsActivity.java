package com.example.expireme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import utils.DatabaseHelper;
import utils.FoodItem;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageButton backButton;

    TextView itemTitleTextView;
    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemAddedDateTextView;
    FoodItem food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        backButton = findViewById(R.id.add_item_button_back);
        itemTitleTextView = findViewById(R.id.item_title);
        itemNameTextView = findViewById(R.id.item_name);
        itemExpirationTextView = findViewById(R.id.item_expiration);
        itemNotesTextView = findViewById(R.id.item_note);
        itemAddedDateTextView = findViewById(R.id.item_purchased);
        food = new FoodItem(
                getIntent().getStringExtra("ItemName"),
                getIntent().getStringExtra("ItemNotes"),
                getIntent().getStringExtra("ItemAddedDate"),
                getIntent().getStringExtra("ItemExpiration")
        );
        food.setId(getIntent().getLongExtra("ItemId", -1));
        itemTitleTextView.setText(food.getName());
        itemNameTextView.setText(food.getName());
        itemExpirationTextView.setText(food.getExpiryDate());
        itemNotesTextView.setText(food.getNote());
        itemAddedDateTextView.setText(food.getDateAdded());
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
        alert.setTitle("Delete Food Item!");
        alert.setMessage("Are you sure you want to delete?");
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
