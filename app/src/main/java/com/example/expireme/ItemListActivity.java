package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import utils.CustomItemAdapter;
import utils.FoodItem;

public class ItemListActivity extends AppCompatActivity {
    ListView itemListView;
    ArrayList<FoodItem> items = new ArrayList<>();
    // Create an array adapter
    CustomItemAdapter myAdapter = new CustomItemAdapter(this, items);

//    private void handleIncomingItem(String itemId) {
//        String itemName = getIntent().getStringExtra("ItemName");
//        String itemExpiration = getIntent().getStringExtra("ItemExpiration");
//        String itemNotes = getIntent().getStringExtra("ItemNotes");
//        String itemAddedDate = getIntent().getStringExtra("ItemAddedDate");
//
//        Log.d("handleIncomingItem", itemId);
//        Log.d("handleIncomingItem", itemName + itemExpiration + itemNotes +itemAddedDate);
//        if (itemId.equals("0")) {
//            expirationItems.add(new ItemListAdapterItem(
//                    itemName, itemExpiration, itemNotes, itemAddedDate, itemId));
//        } else {
//            int i = 0;
//            for (ItemListAdapterItem item: expirationItems) {
//                if (item.getItemId().equals(itemId)) {
//                    expirationItems.set(i,new ItemListAdapterItem(
//                            itemName, itemExpiration, itemNotes, itemAddedDate, itemId));
//                    break;
//                }
//                i++;
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        itemListView = findViewById(R.id.myItemView);

        // Populate the data into the arrayList
        // TODO: should be retrieving items from DB

        items = getIntent().getParcelableArrayListExtra("foodItems");

        // Connect the adapter to the list view
        itemListView.setAdapter(myAdapter);

        // Set the click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Trigger the next activity (ItemDetails)
                Intent intent = new Intent(ItemListActivity.this, AddItemActivity.class);
                // Pass in the item name to item details activity
                intent.putExtra("ItemName", items.get(i).getName());
                intent.putExtra("ItemExpiration", items.get(i).getExpirtyDate());
                intent.putExtra("ItemNotes", items.get(i).getNote());
                intent.putExtra("ItemAddedDate", items.get(i).getDateAdded());
                intent.putExtra("ItemId", items.get(i).getId());
                startActivity(intent);
            }
        });
    }

    // When Add Item button clicked
    public void onAddItemClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(explicitIntent);
    }

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

