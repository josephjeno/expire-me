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
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.text.DecimalFormat;


public class HomeActivity extends AppCompatActivity {

    private FusedLocationProviderClient  fusedLocationClient;
    private JSONObject jsonObject;
    String apiKey = ""; // TODO: will be committed to release version only

    {
        try {
            jsonObject = new JSONObject("{\n" +
                        "   \"html_attributions\" : [],\n" +
                        "   \"next_page_token\" : \"CvQB7AAAAPzh-eaLooQJ1gxg88_EmHbqLfFt45aahApotdjuy70UqAyx07tuoRCpVM2gbDfpf8YfIbkZ96bzIo8T-6x3a-K8GXlgEX9_gULyzmfsGAhiK7yvc9N_P7nGqyS3pOSCxPAHYLZ7Id5zabLwXOllviOPItH5788c1A-7K-a8IfLp9aGn94ikX-ei40wKlIEGr8SWfle5Unmk0L_qIZOD8Xn_yNa3a4JFPby2affcEi0Kg1eEkEkkngSL-T8wXkjLS82gl_1FlmfMSvRvok5GJ90gmy72yhB5j88wyZLlrZvnVqwuXD0bmEMD_9eEwLm4pBIQA1GcTSIXRswedriQX3gTPBoUEE-rgsd8cPKvpg9ugEKrIy-o31Q\",\n" +
                        "   \"results\" : [\n" +
                        "      {\n" +
                        "         \"geometry\" : {\n" +
                        "            \"location\" : {\n" +
                        "               \"lat\" : 40.7477385,\n" +
                        "               \"lng\" : -73.98689379999999\n" +
                        "            },\n" +
                        "            \"viewport\" : {\n" +
                        "               \"northeast\" : {\n" +
                        "                  \"lat\" : 40.749029,\n" +
                        "                  \"lng\" : -73.98506640000001\n" +
                        "               },\n" +
                        "               \"southwest\" : {\n" +
                        "                  \"lat\" : 40.7465661,\n" +
                        "                  \"lng\" : -73.9883493\n" +
                        "               }\n" +
                        "            }\n" +
                        "         },\n" +
                        "         \"icon\" : \"http://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png\",\n" +
                        "         \"id\" : \"e175a5f113997bf0b6515718a67ce6cabc821bf4\",\n" +
                        "         \"name\" : \"Korea Town\",\n" +
                        "         \"place_id\" : \"ChIJ_f1FDqlZwokRqCItRNuQIKs\",\n" +
                        "         \"reference\" : \"CpQBiAAAAKjvKpniYmo7fcXUlXZi0rwKJIObZ2YdZfHW1oN2hYqWZBlJcfbbmbCe9s68497cdtEoOQalVthvJAS7A92EI1o4qXuxhonTaA7UOjcRS30xm4VNmvFFSDMjVx5B1nxWr0dx8mSsRY3ygbWw1d1-Tgxr_sO_LE8bsoRzezYlGM25BRq-6QCrzrzsWqu07-UIqhIQjFSlTMx4T9RqZAX1GCkevxoU4be-kyQ5hudhV8fnI2GpPWq-MFA\",\n" +
                        "         \"scope\" : \"GOOGLE\",\n" +
                        "         \"types\" : [ \"neighborhood\", \"political\" ],\n" +
                        "         \"vicinity\" : \"Manhattan\"\n" +
                        "      },\n" +
                        "     ],\n" +
                        "  status: \"OK\"\n" +
                        "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final TextView locationTextView = findViewById(R.id.locationTextView);
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
                                DecimalFormat df = new DecimalFormat("####.####");
                                double longitude = location.getLongitude();
                                double latitude = location.getLatitude();
                                Log.d("addOnSuccessListener",
                                        "lon=" + df.format(longitude) + " lat=" + df.format(latitude));
                                locationTextView.setText("Longitude=" + df.format(longitude) + "\nLatitude=" + df.format(latitude));
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

        // Initialize Places.
        //Places.initialize(getApplicationContext(), apiKey);

// Create a new Places client instance.
        //PlacesClient placesClient = Places.createClient(this);

        // https://maps.googleapis.com/maps/api/place/nearbysearch/output?
        // key=apiKey&location=-100.33,77.44&rankby=distance&type=supermarket



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

