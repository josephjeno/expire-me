package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageButton backButton;

    TextView itemTitleTextView;
    TextView itemNameTextView;
    TextView itemExpirationTextView;
    TextView itemNotesTextView;
    TextView itemAddedDateTextView;

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

    // When back button clicked
    public void onbackButtonClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(explicitIntent);
    }
}
