package com.example.expireme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import utils.CustomItemAdapter;
import utils.ItemListAdapterItem;
import utils.SwipeToDeleteCallback;

public class ItemListActivity extends AppCompatActivity implements CustomItemAdapter.ItemClickListener {
    RecyclerView recyclerView;
    ArrayList<ItemListAdapterItem> expirationItems = new ArrayList<ItemListAdapterItem>();
    // Create an array adapter
    CustomItemAdapter myAdapter;

    private void handleIncomingItem(String itemId) {
        String itemName = getIntent().getStringExtra("ItemName");
        String itemExpiration = getIntent().getStringExtra("ItemExpiration");
        String itemNotes = getIntent().getStringExtra("ItemNotes");
        String itemAddedDate = getIntent().getStringExtra("ItemAddedDate");

        Log.d("handleIncomingItem", itemId);
        Log.d("handleIncomingItem", itemName + itemExpiration + itemNotes +itemAddedDate);
        if (itemId.equals("0")) {
            expirationItems.add(new ItemListAdapterItem(
                    itemName, itemExpiration, itemNotes, itemAddedDate, itemId));
        } else {
            int i = 0;
            for (ItemListAdapterItem item: expirationItems) {
                if (item.getItemId().equals(itemId)) {
                    expirationItems.set(i,new ItemListAdapterItem(
                            itemName, itemExpiration, itemNotes, itemAddedDate, itemId));
                    break;
                }
                i++;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);


        // Populate the data into the arrayList
        // TODO: should be retrieving items from DB
        PopulateList();
        String itemId = getIntent().getStringExtra("ItemId");
        Log.d("itemList", "itemId = " + itemId);
        if (itemId != null) // we came here from an item view
            handleIncomingItem(itemId);

        // Connect the adapter to the list view
        recyclerView = findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new CustomItemAdapter(this, expirationItems);
        myAdapter.setClickListener(this);
        recyclerView.setAdapter(myAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(myAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onItemClick(View view, int position) {
        // Trigger the next activity (ItemDetails)
        Intent intent = new Intent(ItemListActivity.this, AddItemActivity.class);
        // Pass in the item name to item details activity
        intent.putExtra("ItemName", expirationItems.get(position).getItemName());
        intent.putExtra("ItemExpiration", expirationItems.get(position).getItemExpiration());
        intent.putExtra("ItemNotes", expirationItems.get(position).getItemNotes());
        intent.putExtra("ItemAddedDate", expirationItems.get(position).getItemAddedDate());
        intent.putExtra("ItemId", expirationItems.get(position).getItemId());
        startActivity(intent);
    }

    // When Add Item button clicked
    public void onAddItemClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(explicitIntent);
    }

    // Add data into arrayList
    private void PopulateList() {
        expirationItems.add(new ItemListAdapterItem("Milk", "Expired 7/9/2019", "Drink before it goes bad", "6/30/2019", "1001"));
        expirationItems.add(new ItemListAdapterItem("Tomatoes", "7/16/2019", null, "7/6/2019","1002"));
        expirationItems.add(new ItemListAdapterItem("Chocolate Milk", "7/21/2019", "Shake before drinking", "7/02/2019", "1003"));
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(myAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }




    private void deleteItemById(String id) {

        for (ItemListAdapterItem item: expirationItems) {
            if (item.getItemId().equals(id)) {
                Log.d("deleteItemById", "deleting " + item.getItemName() );
                expirationItems.remove(item);
                break;
            }
        }
        myAdapter.notifyDataSetChanged();
        // TODO: remove from database
    }

    private void confirmDelete(final String id, String name) {
        // code originally taken from:
        // http://www.apnatutorials.com/android/android-alert-confirm-prompt-dialog.php?categoryId=2&subCategoryId=34&myPath=android/android-alert-confirm-prompt-dialog.php
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(name);
        builder.setMessage("Are you sure you want to delete " + name + " ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItemById(id);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        builder.show();
    }

    private void confirmDelete(String id) {
        for (ItemListAdapterItem item: expirationItems) {
            if (item.getItemId().equals(id)) {
                Log.d("confirmDelete ID", "deleting " + id + " " + item.getItemName() );
                confirmDelete(id, item.getItemName());
                break;
            }
        }
    }

    public void myDeleteClickHandler(View view) {
        LinearLayout ll = (LinearLayout)view.getParent();
        Log.d("ItemListActivity", "clicked trashcan " + view.getId() + " " + ll.getId());
        Object tag = view.getTag();

        TextView tv_name = (TextView) ll.getChildAt(0);
        TextView tv_id = (TextView) ll.getChildAt(1);
        Log.d("ItemListActivity", "deleting " + tv_name.getText().toString());
        Log.d ("ItemListActivity2",  tv_id.getText().toString() );
        confirmDelete(tv_id.getText().toString());
        //confirmDelete(tv_id.getText().toString(), tv_name.getText().toString());
    }
}

