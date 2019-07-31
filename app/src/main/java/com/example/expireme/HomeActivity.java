package com.example.expireme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import utils.DatabaseHelper;
import utils.FoodItem;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;


public class HomeActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    ArrayList<FoodItem> foodItems = new ArrayList<>();
    private FusedLocationProviderClient  fusedLocationClient;
    String apiKey = ""; // TODO: will be committed to release version only
    static final int EXPIREME_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    static final int EXPIREME_PERMISSIONS_REQUEST_WIFI = 1;
    TextView locationTextView;
    TextView allItemsNumberTextView;
    TextView expiringSoonItemsNumberTextView;
    TextView expiredItemsNumberTextView;
    static final int ADD_ITEM = 1;

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
    }

    private void populateListsCount() {
        foodItems = dbHelper.getAllItems();
        int allSize = getFilteredItems("ALL").size();
        int soonSize = getFilteredItems("SOON").size();
        int expiredSize = getFilteredItems("EXPIRED").size();
        allItemsNumberTextView.setText(String.valueOf(allSize ));
        expiringSoonItemsNumberTextView.setText(String.valueOf(soonSize));
        expiredItemsNumberTextView.setText(String.valueOf(expiredSize));
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == EXPIREME_PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocation();
            } else {
                Log.e("getLastLocation", "User did not grant permissions");
            }
        }
    }

    private void updateLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // most common reason for location to be null is on emulators, location was never used before
                            Log.d("getLastLocation", "success");
                            if (location != null) {
                                DecimalFormat lonlatFormat = new DecimalFormat("####.####");
                                double longitude = location.getLongitude();
                                double latitude = location.getLatitude();
                                Log.d("addOnSuccessListener", "lon=" + lonlatFormat.format(longitude) + " lat=" + lonlatFormat.format(latitude));
                                locationTextView.setText("Longitude=" + lonlatFormat.format(longitude) + "\nLatitude=" + lonlatFormat.format(latitude));
                            } else
                                Log.d("getLastLocation", "location is null");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("getLastLocation", "failed");
                }
            });
        } catch (SecurityException e) {
            // this is just that the getLastLocation() won't complain
        }
        initPlaces();
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
            return;
        } else {
            Log.e("checkSelfPermission", "got permissions");
            handlePlaces();
        }
    }


    @RequiresPermission(allOf = {ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE})
    private void handlePlaces() {
        Log.e("initPlaces", "start");

        // Initialize Places.
        if (!Places.isInitialized()) {
            Log.e("initPlaces", "isInitialized needed");
            if (apiKey.equals("")) {
                Toast.makeText(this, "API Key not defined, unable to show nearby places",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Places.initialize(getApplicationContext(), apiKey);
        } else
            Log.e("initPlaces", "isInitialized needed not needed");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // https://maps.googleapis.com/maps/api/place/nearbysearch/output?
        // key=apiKey&location=-100.33,77.44&rankby=distance&type=supermarket

        List<Place.Field> listFields = Arrays.asList(
                Place.Field.ADDRESS,
                Place.Field.PRICE_LEVEL,
                Place.Field.RATING,
                Place.Field.TYPES,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.VIEWPORT,
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.PLUS_CODE);

        FindCurrentPlaceRequest currentPlaceRequest = FindCurrentPlaceRequest.newInstance(listFields);
//    FindCurrentPlaceRequest currentPlaceRequest = FindCurrentPlaceRequest.newInstance(getPlaceFields());
        Task<FindCurrentPlaceResponse> currentPlaceTask = placesClient.findCurrentPlace(currentPlaceRequest);

        currentPlaceTask.addOnSuccessListener((response) -> {
            Log.e("addOnSuccessListener", "success");
            boolean found = false;
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                Log.e("placeLikelihood" , "name=" + placeLikelihood.getPlace().getName());
                Log.e("placeLikelihood" , "types size=" + placeLikelihood.getPlace().getTypes().size());
                ListIterator<Place.Type> listIt = placeLikelihood.getPlace().getTypes().listIterator();
                while (listIt.hasNext()) {
                    Place.Type type = listIt.next();
                    Log.e("placeLikelihood", "type=" + type.toString());
                    if (type.toString().equals("BUS_STATION") || type.toString().equals("SUPERMARKET") ) {
                        locationTextView.setText(placeLikelihood.getPlace().getName());
                        found = true;
                        break;
                    }
                }
                if (found)
                    break;
            }
        } );

        currentPlaceTask.addOnFailureListener( (exception) -> {
            Log.e("addOnSuccessListener", "failure");
            exception.printStackTrace();
        });

        currentPlaceTask.addOnCompleteListener(task -> Log.e("findCurrentPlace", "exception"));

        /*
        FindCurrentPlaceRequest currentPlaceRequest = FindCurrentPlaceRequest.newInstance(listFields);
        try {
            Task<FindCurrentPlaceResponse> currentPlaceTask = placesClient.findCurrentPlace(currentPlaceRequest);
            currentPlaceTask.addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                    Log.e("findCurrentPlace", "success");
                    locationTextView.setText(stringify(findCurrentPlaceResponse));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("findCurrentPlace", "failure");

                }
            });
        } catch (SecurityException e) {
            Log.e("findCurrentPlace", "exception");
        }
        */
    }


    private String stringify(FindCurrentPlaceResponse response) {
        StringBuilder builder = new StringBuilder();

        builder.append(response.getPlaceLikelihoods().size()).append(" Current Place Results:");


        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
            builder
                    .append("Likelihood: ")
                    .append(placeLikelihood.getLikelihood())
                    .append("Place: ")
                    .append(placeLikelihood.getPlace().getName())
                    .append(placeLikelihood.getPlace().getAddress());
        }

        return builder.toString();
    }

    // Returns list of FoodItems from DB filtered by listType ("ALL", "SOON", "EXPIRED")
    public ArrayList<FoodItem> getFilteredItems(String listType) {
        ArrayList<FoodItem> items = new ArrayList<>();
        if (listType != null && !listType.equals("ALL")) {
            Date currentDate = new Date();
            ListIterator<FoodItem> listIterator = foodItems.listIterator();
            for (FoodItem item: foodItems) {
                long diffInMillies = item.getDateExpiration().getTime() - currentDate.getTime();
                long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
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
            populateListsCount();
        }
    }

}

