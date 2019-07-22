package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import utils.FoodItem;

public class HomeActivity extends AppCompatActivity {

    ArrayList<FoodItem> allFoodItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        allFoodItems = dbHelper.getAllItems();
    }

    public void onAllItemsClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        explicitIntent.putParcelableArrayListExtra("foodItems", allFoodItems);
        startActivity(explicitIntent);
    }

    public void onAddItemClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(explicitIntent);
    }
}
