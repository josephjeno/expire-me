package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import java.util.ArrayList;
import utils.CustomItemAdapter;
import utils.FoodItem;
import utils.SwipeToDeleteCallback;

public class ItemListActivity extends AppCompatActivity implements CustomItemAdapter.ItemClickListener {

    // This is the view that displays the list of items
    RecyclerView recyclerView;

    // List of items
    ArrayList<FoodItem> items = new ArrayList<>();

    CustomItemAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Populate the data into the arrayList
        // TODO: should be retrieving items from DB

        items = getIntent().getParcelableArrayListExtra("foodItems");

        // Connect the adapter to the list view
        recyclerView = findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new CustomItemAdapter(this, items);
        myAdapter.setClickListener(this);
        recyclerView.setAdapter(myAdapter);

        // Helper that controls item swipes (left to delete, right to edit, hold to select multiple)
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(myAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
        startActivity(intent);
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

