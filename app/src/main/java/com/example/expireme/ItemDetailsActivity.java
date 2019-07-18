package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        backButton = findViewById(R.id.add_item_button_back);
    }

    // When back button clicked
    public void onbackButtonClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(explicitIntent);
    }
}
