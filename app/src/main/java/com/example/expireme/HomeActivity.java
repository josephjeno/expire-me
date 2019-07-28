package com.example.expireme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends AppCompatActivity {

    private FusedLocationProviderClient  fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                == PackageManager.PERMISSION_GRANTED ) {
            Log.e("getLastLocation", "GOT permissions");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            Log.e("getLastLocation", "success");
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("getLastLocation", "fail");
                }
            }).addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Log.e("getLastLocation", "complete");
                }
            });
        } else {
            Log.e("getLastLocation", "no permissions");

        }




    }

    public void onAllItemsClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), ItemListActivity.class);
        intent.putExtra("ListType", "ALL");
        startActivity(intent);
    }

    public void onSoonToExpireClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), ItemListActivity.class);
        intent.putExtra("ListType", "SOON");
        startActivity(intent);
    }

    public void onExpiredItemsClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), ItemListActivity.class);
        intent.putExtra("ListType", "EXPIRED");
        startActivity(intent);
    }

    public void onAddItemClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(intent);
    }
}
