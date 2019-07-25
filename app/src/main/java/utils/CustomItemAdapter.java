package utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.expireme.R;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class CustomItemAdapter extends RecyclerView.Adapter<CustomItemAdapter.MyViewHolder> {

    // Receive the context from ItemListActivity
    private Context context;

    // ArrayList with the item expiration data points to populate adapter
    private ArrayList<FoodItem> items;

    // RecyclerView
    private RecyclerView recyclerView;

    private ItemClickListener myClickListener;

    public CustomItemAdapter(Context context, ArrayList<FoodItem> items, RecyclerView recyclerView) {
        this.context = context;
        this.items = items;
        this.recyclerView = recyclerView;
    }

    // Getter for context
    public Context getContext() {
        return context;
    }



    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_adapter_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FoodItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemExpiration.setText(item.getExpiryDate());
        long diffInDays = differenceInDays(item.getDateExpiration());
        holder.itemCountdown.setText(getCountdownText(diffInDays));
        holder.itemCountdown.setTextColor(getCountdownColor(diffInDays));
    }
    

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Deletes item from DB and list and refreshes view
    public void deleteItem(int position) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.deleteItem(items.get(position).getId());
        items.remove(position);
        this.notifyDataSetChanged();
        showSnackbar();
    }

    public void showSnackbar() {
        Snackbar snackbar = Snackbar.make(recyclerView, "Test", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    // Calculates difference between expiration date and today's date
    private long differenceInDays(Date expirationDate) {
        Calendar calDate = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        calDate.set(Calendar.HOUR_OF_DAY, 0);
        calDate.set(Calendar.MINUTE, 0);
        calDate.set(Calendar.SECOND, 0);
        calDate.set(Calendar.MILLISECOND, 0);
        Date currentDate = calDate.getTime();
        long diffInMillies = expirationDate.getTime() - currentDate.getTime();
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    // Gets text to display for itemCountdown
    private String getCountdownText(long diffInDays) {
        if (diffInDays < 1) {
            return "!";
        } else if (diffInDays == 1) {
            return "1 day";
        } else if (diffInDays < 30){
            return diffInDays + " days";
        } else if ((diffInDays / 30) == 1) {
            return "1 month";
        } else {
            return (diffInDays / 30) + " months";
        }
    }

    // Gets color to display for countdown text
    private int getCountdownColor(long diffInDays) {
        if (diffInDays < 1) {
            return Color.RED;
        } else if (diffInDays < 3){
            return Color.rgb(235, 140, 52);
        } else {
            return Color.rgb(52, 235, 61);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemName;
        TextView itemExpiration;
        TextView itemCountdown;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameAdapterItem);
            itemExpiration = itemView.findViewById(R.id.itemExpirationAdapterItem);
            itemCountdown = itemView.findViewById(R.id.itemCountdownAdapterItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myClickListener != null) myClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.myClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
