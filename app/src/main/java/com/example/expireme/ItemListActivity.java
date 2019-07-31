package com.example.expireme;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import utils.CustomItemAdapter;
import utils.DatabaseHelper;
import utils.FoodItem;
import utils.SwipeToDeleteCallback;

public class ItemListActivity extends AppCompatActivity implements CustomItemAdapter.ItemClickListener {

    // Request Code used to start Item Details activity
    static final int SHOW_ITEM = 0;
    static final int ADD_ITEM = 1;

    // View that displays list of items
    RecyclerView recyclerView;

    // Helper that controls item swipes
    ItemTouchHelper itemTouchHelper;

    // Custom adapter for displaying food items
    CustomItemAdapter myAdapter;

    // Filter for item list ("ALL", "SOON", "EXPIRED")
    String listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Get ListType
        listType = getIntent().getStringExtra("ListType");

        // Configures action bar (back button and title)
        configureActionBar();

        // Configures RecyclerView
        recyclerView = findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new CustomItemAdapter(this, listType, recyclerView);
        myAdapter.setClickListener(this);
        recyclerView.setAdapter(myAdapter);

        // Helper that controls item swipes (left to delete, right to edit, hold to select multiple)
        itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(myAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Updates list of items after returning to ListActivity
    @Override
    protected void onResume() {
        super.onResume();
        myAdapter.refreshItems();
    }

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

    // Navigates to Item Details screen when item is selected from the list
    @Override
    public void onItemClick(View view, int position) {
        // Trigger the next activity (ItemDetails)
        Intent intent = new Intent(ItemListActivity.this, ItemDetailsActivity.class);
        // Pass in the item name to item details activity
        intent.putExtra("ItemName", myAdapter.getItem(position).getName());
        intent.putExtra("ItemExpiration", myAdapter.getItem(position).getExpiryDate());
        intent.putExtra("ItemNotes", myAdapter.getItem(position).getNote());
        intent.putExtra("ItemAddedDate", myAdapter.getItem(position).getDateAdded());
        intent.putExtra("ItemId", myAdapter.getItem(position).getId());
        startActivityForResult(intent, SHOW_ITEM);
    }

    // Navigates to Add Item screen when Add Item button is pressed
    public void onAddItemClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(intent);
    }

    // Sets title of List Activity to filtered item name and enables back button
    private void configureActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (listType == null || listType.equals("ALL"))
            actionBar.setTitle("All Items");
        else if (listType.equals("SOON"))
            actionBar.setTitle("Expiring Soon");
        else if (listType.equals("EXPIRED"))
            actionBar.setTitle("Expired Items");
    }


    //TODO: WHAT IS THIS?
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}

