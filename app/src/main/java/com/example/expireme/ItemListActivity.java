package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import utils.CustomItemAdapter;
import utils.ItemListAdapterItem;

public class ItemListActivity extends AppCompatActivity {

    ListView itemListView;
    ArrayList<ItemListAdapterItem> expirationItems = new ArrayList<ItemListAdapterItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        itemListView = findViewById(R.id.myItemView);

        // Populate the data into the arrayList
        // TODO: should be retrieving items from DB
        PopulateList();

        // Create an array adapter
        CustomItemAdapter myAdapter = new CustomItemAdapter(this, expirationItems);

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
        expirationItems.add(new ItemListAdapterItem("Milk", "Expired 7/9/2019", "Drink before it goes bad", "6/30/2019"));
        expirationItems.add(new ItemListAdapterItem("Tomatoes", "7/16/2019", null, "7/6/2019"));
        expirationItems.add(new ItemListAdapterItem("Chocolate Milk", "7/21/2019", "Shake before drinking", "7/02/2019"));
    }
}

