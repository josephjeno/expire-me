package com.example.expireme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    TextView expirationDate;
    TextView purchasedOnDate;
    Button select_Date;
    Calendar calendar;
    int year,month,dayOfMonth;
    DatePickerDialog datePickerDialog;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        expirationDate = findViewById(R.id.editTextExpirationDate);
        expirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddItemActivity", "onCreate.date.setOnClickListener.onClick");
                datePicker(expirationDate);
            }
        });

        purchasedOnDate = findViewById(R.id.editTextPurchasedOnDate);
        purchasedOnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AddItemActivity", "onCreate.date.setOnClickListener.onClick");
                datePicker(purchasedOnDate);
            }
        });

        calendar=Calendar.getInstance();
        Log.d("AddItemActivity", "onCreate");
    }

    //display date picker dialog
    public void datePicker(final TextView date) {
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("AddItemActivity", "datePicker");

        datePickerDialog = new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myOnDateSet(date, datePicker, i, i1,i2);

            }
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public void myOnDateSet(TextView date, DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("AddItemActivity", "onDateSet");
        date.setText((month+1) + "-" + dayOfMonth + "-" + year);
    }
}
