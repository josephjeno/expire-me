package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onAllItemsClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), ItemListActivity.class);
        startActivity(explicitIntent);
    }

    public void onAddItemClicked(View view) {
        Intent explicitIntent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(explicitIntent);
    }
}
