package com.example.expireme;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


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

    static final int SHOW_ITEM = 0;
    DatabaseHelper dbHelper;

    // This is the view that displays the list of items
    RecyclerView recyclerView;
    ItemTouchHelper itemTouchHelper;

    // List of items
    ArrayList<FoodItem> items;

    CustomItemAdapter myAdapter;
    String listType;

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

    private void getItems() {
        // Populate the data into the arrayList
        items = dbHelper.getAllItems();
        // remove items according to list type
        listType = getIntent().getStringExtra("ListType");
        Log.d("ItemListActivity", "listType=" + listType);
        if (listType != null && !listType.equals("ALL")) {
            Date currentDate = new Date();

            ListIterator<FoodItem> listIterator = items.listIterator();
            while(listIterator.hasNext()) {
                FoodItem item = listIterator.next();
                // check if result of student is "Fail"
                long diffInMillies = item.getDateExpiration().getTime() - currentDate.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (listType.equals("EXPIRED")) {
                    if (diffInDays >= 0)
                        listIterator.remove();
                } else if (listType.equals("SOON")) {
                    if (diffInDays > 3 || diffInDays < 0)
                        listIterator.remove();
                }
            }
        }
    }

    private void setTitle(String listType) {
        if (listType == null || listType.equals("ALL"))
            getSupportActionBar().setTitle("All Items");
        else if (listType.equals("SOON"))
            getSupportActionBar().setTitle("Expiring Soon");
        else if (listType.equals("EXPIRED"))
            getSupportActionBar().setTitle("Expired Items");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Populate the data into the arrayList
        dbHelper = new DatabaseHelper(this);
        getItems();

        myAdapter = new CustomItemAdapter(this, items);
        myAdapter.setClickListener(this);
        recyclerView.setAdapter(myAdapter);

        // Helper that controls item swipes (left to delete, right to edit, hold to select multiple)
        itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(myAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        setTitle(listType);
    }

    // Navigates to Item Details screen when item is selected from the list
    @Override
    public void onItemClick(View view, int position) {
        // Trigger the next activity (ItemDetails)
        Intent intent = new Intent(ItemListActivity.this, ItemDetailsActivity.class);
        // Pass in the item name to item details activity
        intent.putExtra("ItemName", items.get(position).getName());
        intent.putExtra("ItemExpiration", items.get(position).getExpiryDate());
        intent.putExtra("ItemNotes", items.get(position).getNote());
        intent.putExtra("ItemAddedDate", items.get(position).getDateAdded());
        intent.putExtra("ItemId", items.get(position).getId());
        startActivityForResult(intent, SHOW_ITEM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", " requestCode="+ requestCode + " resultCode=" + resultCode);
        if (requestCode == SHOW_ITEM && resultCode == RESULT_OK) {

            Log.d("onActivityResult", "calling setupAdapter");
            /*
            getItems();
            myAdapter = new CustomItemAdapter(this, items);
            myAdapter.setClickListener(this);
            recyclerView.setAdapter(myAdapter);

            // Helper that controls item swipes (left to delete, right to edit, hold to select multiple)
            itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(myAdapter));
            itemTouchHelper.attachToRecyclerView(recyclerView);
            */
        }
    }


    // Navigates to Add Item screen when Add Item button is pressed
    public void onAddItemClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(explicitIntent);
    }

//     // Add data into arrayList
//    private void PopulateList() {
//        items.add(new FoodItem("Milk", "Expired 7/9/2019", "Drink before it goes bad", "6/30/2019"));
//        items.add(new FoodItem("Tomatoes", "7/16/2019", null, "7/6/2019"));
//        items.add(new FoodItem("Chocolate Milk", "7/21/2019", "Shake before drinking", "7/02/2019"));
//    }
  
//    private void deleteItemById(String id) {
//
//        for (ItemListAdapterItem item: expirationItems) {
//            if (item.getItemId().equals(id)) {
//                Log.d("deleteItemById", "deleting " + item.getItemName() );
//                expirationItems.remove(item);
//                break;
//            }
//        }
//        myAdapter.notifyDataSetChanged();
//        // TODO: remove from database
//    }

//    private void confirmDelete(final String id, String name) {
//        // code originally taken from:
//        // http://www.apnatutorials.com/android/android-alert-confirm-prompt-dialog.php?categoryId=2&subCategoryId=34&myPath=android/android-alert-confirm-prompt-dialog.php
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(name);
//        builder.setMessage("Are you sure you want to delete " + name + " ?");
//        builder.setCancelable(false);
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                deleteItemById(id);
//            }
//        });
//
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // do nothing
//            }
//        });
//
//        builder.show();
//    }
//
//    private void confirmDelete(String id) {
//        for (ItemListAdapterItem item: expirationItems) {
//            if (item.getItemId().equals(id)) {
//                Log.d("confirmDelete ID", "deleting " + id + " " + item.getItemName() );
//                confirmDelete(id, item.getItemName());
//                break;
//            }
//        }
//    }

//    public void myDeleteClickHandler(View view) {
//        LinearLayout ll = (LinearLayout)view.getParent();
//        Log.d("ItemListActivity", "clicked trashcan " + view.getId() + " " + ll.getId());
//        Object tag = view.getTag();
//
//        TextView tv_name = (TextView) ll.getChildAt(0);
//        TextView tv_id = (TextView) ll.getChildAt(1);
//        Log.d("ItemListActivity", "deleting " + tv_name.getText().toString());
//        Log.d ("ItemListActivity2",  tv_id.getText().toString() );
//        confirmDelete(tv_id.getText().toString());
//        //confirmDelete(tv_id.getText().toString(), tv_name.getText().toString());
//    }
}

