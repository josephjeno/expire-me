/*
 * Copyright (c) 2019. Anik Bhattacharjee, Joseph Jeno, Amir Shlomo Yaakobovich
 * All rights reserved.
 */

package com.example.expireme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import utils.DatabaseHelper;
import utils.ExpirationJobService;
import utils.FoodItem;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;


public class HomeActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ArrayList<FoodItem> foodItems = new ArrayList<>();
    private static final int EXPIREME_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private TextView locationTextView;
    private TextView allItemsNumberTextView;
    private TextView expiringSoonItemsNumberTextView;
    private TextView expiredItemsNumberTextView;
    private static final int ADD_ITEM = 1;
    private int soonSize;
    private int expiredSize;
    private boolean placesAcquired = false;
    private String foundPlaceName;
    //private String foundPlaceAddress;
    private String foundPlaceId;
    private double latitude;
    private double nearby_lat = 181;
    private double longitude;
    private double nearby_lon = 181;
    private ComponentName componentName;
    private int jobId = 0;
    private static int jobDelayOffset = 60*60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = new DatabaseHelper(this);
        locationTextView = findViewById(R.id.locationTextView);
        allItemsNumberTextView = findViewById(R.id.text_all_item_count);
        expiringSoonItemsNumberTextView = findViewById(R.id.text_ste_count);
        expiredItemsNumberTextView = findViewById(R.id.text_expired_count);

        populateListsCount();
        initLocation();

        locationTextView.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Location clicked", Toast.LENGTH_LONG).show();
            Uri gmmIntentUri = Uri.parse(makeLonLatString());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });
        componentName = new ComponentName(this, ExpirationJobService.class);
    }

    // Updates list of items after returning to ListActivity
    @Override
    protected void onResume() {
        super.onResume();
        populateListsCount();
    }

    private void populateListsCount() {
        foodItems = dbHelper.getAllItems();
        int allSize = getFilteredItems("ALL").size();
        soonSize = getFilteredItems("SOON").size();
        expiredSize = getFilteredItems("EXPIRED").size();
        allItemsNumberTextView.setText(String.valueOf(allSize));
        expiringSoonItemsNumberTextView.setText(String.valueOf(soonSize));
        expiredItemsNumberTextView.setText(String.valueOf(expiredSize));
        initPlaces();
    }

    private String makeLonLatString() {
        // "geo:37.7749,-122.4194"
        double lat;
        double lon;
        if (nearby_lon == 181) {
            lat = latitude;
            lon = longitude;
        } else {
            lat = nearby_lat;
            lon = nearby_lon;
        }
        DecimalFormat lonlatFormat = new DecimalFormat("####.####");
        return "https://www.google.com/maps/search/?api=1&query=" +
                lonlatFormat.format(lat) + "," + lonlatFormat.format(lon) +"&query_place_id=" + foundPlaceId;
    }

    private void askUserForWifiPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                    EXPIREME_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    private void askUserForPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                EXPIREME_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull  String[] permissions,@NonNull  int[] grantResults) {
        if (requestCode == EXPIREME_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            } else {
                Log.e("getLastLocation", "User did not grant permissions");
            }
        }
    }

    private void updateLocation() {
        FusedLocationProviderClient  fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // most common reason for location to be null is on emulators, location was never used before
                    Log.d("getLastLocation", "success");
                    if (location != null) {
                        DecimalFormat lonlatFormat = new DecimalFormat("####.####");
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        Log.d("addOnSuccessListener", "lon=" + lonlatFormat.format(longitude) + " lat=" + lonlatFormat.format(latitude));
                        String locationString = "Longitude=" + lonlatFormat.format(longitude) + "\nLatitude=" + lonlatFormat.format(latitude);
                        locationTextView.setText(locationString);
                        initPlaces();
                    } else
                        Log.d("getLastLocation", "location is null");
                }).addOnFailureListener(e -> Log.e("getLastLocation", "failed"));
        } catch (SecurityException e) {
            // this is just so getLastLocation() won't complain
        }
    }

    private void initLocation() {
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                == PackageManager.PERMISSION_GRANTED ) {
            Log.e("getLastLocation", "GOT permissions");
            updateLocation();
        } else {
            Log.e("getLastLocation", "no permissions");
            askUserForPermission();
        }
    }

    private void initPlaces() {
        askUserForWifiPermission();
        askUserForPermission();

        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("checkSelfPermission", "no wifi permissions");
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("checkSelfPermission", "no location permissions");
        } else {
            Log.e("checkSelfPermission", "got permissions");
            handlePlaces();
        }
    }

    private void setLocationMessage(String name) {
        DecimalFormat distanceFormat = new DecimalFormat("##.#");

        String msg;
        if (name == null)
            msg = "Unfortunately, there's no supermarket nearby.";
        else
            msg = name +
                " is " +
                distanceFormat.format(distance(latitude, longitude, nearby_lat, nearby_lon)) +
                " miles away, would you see it on the map?";
        Log.e("setLocationMessage", latitude + " " + longitude + " " + nearby_lat + " " + nearby_lon);
        locationTextView.setVisibility(View.VISIBLE);
        locationTextView.setText(msg);
    }

    @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE})
    private void handlePlaces() {
        Log.e("handlePlaces", "start");

        if (!(expiredSize > 0 || soonSize > 0)) {
            locationTextView.setVisibility(View.GONE);
            Log.e("handlePlaces", "nothing expiring/soon, returning");
            return;
        }
        // this is for mock testing
        // TODO: will be committed to release version only
        String apiKey = "";
        if (apiKey.equals("")) {
            //Toast.makeText(this, "API Key not defined, unable to show nearby places", Toast.LENGTH_LONG).show();
            setLocationMessage("Wollaston's Market");
            return;
        }
        if (placesAcquired) {// only get places once!
            Log.e("handlePlaces", "place already acquired once");
            setLocationMessage(foundPlaceName);

            return;
        }
        if (!Places.isInitialized()) {
            Log.e("initPlaces", "calling initialize()");
            Places.initialize(getApplicationContext(), apiKey);
        } else
            Log.e("initPlaces", "isInitialized, init not needed");
        PlacesClient placesClient = Places.createClient(this);

        // https://maps.googleapis.com/maps/api/place/nearbysearch/output?
        // key=apiKey&location=-100.33,77.44&rankby=distance&type=supermarket

        List<Place.Field> listFields = Arrays.asList(
                Place.Field.ADDRESS,
                Place.Field.TYPES,
                Place.Field.NAME,
                Place.Field.ID,
                Place.Field.LAT_LNG
        );

        FindCurrentPlaceRequest currentPlaceRequest = FindCurrentPlaceRequest.newInstance(listFields);
        Task<FindCurrentPlaceResponse> currentPlaceTask = placesClient.findCurrentPlace(currentPlaceRequest);
        placesAcquired = true;
        currentPlaceTask.addOnSuccessListener((response) -> {
            Log.e("addOnSuccessListener", "success");
            boolean found = false;
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                Log.e("placeLikelihood" , "name=" + placeLikelihood.getPlace().getName());
                Log.e("placeLikelihood" , "types size=" + placeLikelihood.getPlace().getTypes().size());
                for (Place.Type type : placeLikelihood.getPlace().getTypes()) {
                    Log.e("placeLikelihood", "type=" + type.toString());
                    if (type.toString().equals("SUPERMARKET")) {
                        foundPlaceName = placeLikelihood.getPlace().getName();
                        //foundPlaceAddress = placeLikelihood.getPlace().getAddress();
                        foundPlaceId = placeLikelihood.getPlace().getId();
                        found = true;
                        nearby_lat = placeLikelihood.getPlace().getLatLng().latitude;
                        nearby_lon = placeLikelihood.getPlace().getLatLng().longitude;
                        Log.e("addOnSuccessListener", "lat=" + nearby_lat + " lon=" + nearby_lon);
                        Log.e("addOnSuccessListener", "distance=" + distance(latitude, longitude, nearby_lat, nearby_lon));
                        setLocationMessage(foundPlaceName);
                        break;
                    }
                }
                // TODO: do we want to look for other nearby results? or is one result enough?
                if (found)
                    return;
            }
            setLocationMessage(null);
        } );
        currentPlaceTask.addOnFailureListener( (exception) -> {
            Log.e("addOnSuccessListener", "failure");
            exception.printStackTrace();
        });
        //currentPlaceTask.addOnCompleteListener(task -> Log.e("findCurrentPlace", "addOnCompleteListener"));
    }

    private float distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        Log.e("distance", lat_a + " " + lng_a + " " + lat_b + " " + lng_b);
        if (lat_a>180)
            return 1.2f;

        float[] results = new float[3];
        Location.distanceBetween(lat_a, lng_a, lat_b, lng_b, results);
        Log.e("distance", "results[0]="+ results[0] + " results[1]=" + results[1] + " results[2]=" + results[2]);
        return results[0] / 1609;
    }

    // Returns list of FoodItems from DB filtered by listType ("ALL", "SOON", "EXPIRED")
    private ArrayList<FoodItem> getFilteredItems(String listType) {
        ArrayList<FoodItem> items = new ArrayList<>();
        if (listType != null && !listType.equals("ALL")) {
            Date currentDate = new Date();
            for (FoodItem item: foodItems) {
                long diffInMilliseconds = item.getDateExpiration().getTime() - currentDate.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);
                if (listType.equals("EXPIRED")) {
                    if (diffInDays < 0)
                        items.add(item);
                } else if (listType.equals("SOON")) {
                    if (diffInDays <= 3 && diffInDays >= 0)
                        items.add(item);
                }
            }
        } else
            items = foodItems;
        return items;
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
        startActivityForResult(intent, ADD_ITEM);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", " requestCode="+ requestCode + " resultCode=" + resultCode);
        if (resultCode == RESULT_OK && requestCode == ADD_ITEM) {
            Log.d("MainActivity", "onActivityResult.refreshItems");
            //populateListsCount();
        }
    }

    @Override
    protected void onStop() {
        Log.e("Home:onStop", "onStop");
        stopService(new Intent(this, ExpirationJobService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent startServiceIntent = new Intent(this, ExpirationJobService.class);
        startService(startServiceIntent);
        scheduleJob();
    }

    private void scheduleJob() {
        Log.e("Home:scheduleJob", "scheduleJob started, jobDelayOffset="+jobDelayOffset);
        JobInfo.Builder builder = new JobInfo.Builder(jobId++, componentName);
        PersistableBundle extras = new PersistableBundle();

        builder.setMinimumLatency((jobDelayOffset) * 1000);
        builder.setOverrideDeadline((jobDelayOffset+70) * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);
        builder.setExtras(extras);

        // Schedule job
        Log.e("Home:scheduleJob", "Scheduling job");
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        Objects.requireNonNull(jobScheduler).schedule(builder.build());
        jobDelayOffset = 60*60*24;
    }
}

