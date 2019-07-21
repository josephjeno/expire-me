package com.example.expireme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class ItemListActivity extends AppCompatActivity {

    ListView itemListView;
    ArrayList<ItemListAdapterItem> expirationItems = new ArrayList<ItemListAdapterItem>();
    // Create an array adapter
    CustomItemAdapter myAdapter = new CustomItemAdapter(this, expirationItems);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        itemListView = findViewById(R.id.myItemView);

        // Populate the data into the arrayList
        // TODO: should be retrieving items from DB
        PopulateList();

        // Connect the adapter to the list view
        itemListView.setAdapter(myAdapter);

        // Set the click listener
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Trigger the next activity (ItemDetails)
                Intent intent = new Intent(ItemListActivity.this, ItemDetailsActivity.class);

                // Pass in the item name to item details activity
                intent.putExtra("ItemName", expirationItems.get(i).getItemName());
                intent.putExtra("ItemExpiration", expirationItems.get(i).getItemExpiration());
                intent.putExtra("ItemNotes", expirationItems.get(i).getItemNotes());
                intent.putExtra("ItemAddedDate", expirationItems.get(i).getItemAddedDate());
                startActivity(intent);
            }
        });
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

