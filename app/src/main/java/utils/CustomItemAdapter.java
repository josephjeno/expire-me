package utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.expireme.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CustomItemAdapter extends RecyclerView.Adapter<CustomItemAdapter.MyViewHolder> {

    // Receive the context from ItemListActivity
    private Context context;

    // ArrayList with the item expiration data points to populate adapter
    private ArrayList<FoodItem> items;

    private ItemClickListener myClickListener;

    public CustomItemAdapter(Context context, ArrayList<FoodItem> items) {
        this.context = context;
        this.items = items;
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
        String countdownText = convertCountdownText(item.getExpiryDate());
        holder.itemCountdown.setText(countdownText);
        //TODO get countdown color
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
    }

    // Returns difference between expiration date and today's date
    private String convertCountdownText(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Date currentDate = new Date();
        Date expirationDate = new Date(); // In case item expiration date isn't populated for some reason
        try {
            expirationDate = sdf.parse(date);
        } catch (ParseException e) {
            Log.v("Exception", e.getLocalizedMessage());
        }
        long diffInMillies = expirationDate.getTime() - currentDate.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (diffInDays <= 0) {
            return "!";
        } else if (diffInDays < 30){
            return diffInDays + " days";
        } else {
            return (diffInDays / 30) + " months";
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
