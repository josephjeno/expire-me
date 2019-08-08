package utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expireme.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;


public class CustomItemAdapter extends RecyclerView.Adapter<CustomItemAdapter.MyViewHolder> implements Filterable {

    // Receive the context from ItemListActivity
    private Context context;

    // ArrayList with the item expiration data points to populate adapter
    private ArrayList<FoodItem> items;
    private ArrayList<FoodItem> itemsFiltered;

    // Database helper
    private DatabaseHelper dbHelper;

    // Filter for item list ("ALL", "SOON", "EXPIRED")
    private String listType;

    // Used for undo button
    private RecyclerView recyclerView;
    private FoodItem recentlyDeleted;
    private int recentlyDeletedPosition;

    private ItemClickListener myClickListener;

    public CustomItemAdapter(Context context, String listType, RecyclerView recyclerView) {
        this.context = context;
        this.listType = listType;
        this.recyclerView = recyclerView;

        dbHelper = new DatabaseHelper(context);
        this.items = dbHelper.getFilteredItems(listType);
        this.itemsFiltered = this.items;
    }

    // GETTER/SETTER METHODS

    // Getter for context
    public Context getContext() {
        return context;
    }

    // Get FoodItem at given position
    public FoodItem getItem(int position) {
        return itemsFiltered.get(position);
    }

    // DATABASE METHODS

    // Refreshes list of items
    public void refreshItems() {
        this.items = dbHelper.getFilteredItems(listType);
        this.itemsFiltered = this.items;
        notifyDataSetChanged();
    }

    // Deletes item from DB and list and refreshes view
    public void deleteItem(int position) {
        recentlyDeleted = itemsFiltered.get(position);
        recentlyDeletedPosition = position;
        itemsFiltered.remove(position);
        notifyItemRemoved(position);
        displayUndoSnackbar();
    }

    private void displayUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(recyclerView, "Deleted " + recentlyDeleted.getName(), Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", v -> Log.d("SNACKBAR_UNDO", "Reinserting item " + recentlyDeleted.getName()));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == DISMISS_EVENT_ACTION) {
                    itemsFiltered.add(recentlyDeletedPosition, recentlyDeleted);
                    notifyItemInserted(recentlyDeletedPosition);
                } else {
                    dbHelper.deleteItem(recentlyDeleted.getId());
                }
            }
        });
        snackbar.show();
    }

    // RECYCLERVIEW ADAPTER OVERRIDE METHODS

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_adapter_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FoodItem item = itemsFiltered.get(position);
        int expirationPercent = getExpirationProgress(item);
        holder.itemName.setText(item.getName());
        holder.itemExpiration.setText(item.getExpiryDate());
        holder.itemCountdown.setText(getCountdownText(item.daysUntilExpiration()));
        holder.itemCountdown.setTextColor(getCountdownColor(expirationPercent));
        Log.e("onBindViewHolder", "item=" + item.getName() + " setting progess %=" + expirationPercent);

        holder.itemProgress.setProgress(0);
        // Set progress bar color
        if (expirationPercent == 100) {
            holder.itemProgress.setProgressDrawable(context.getDrawable(R.drawable.circular_progress_bar_red));
        } else if (expirationPercent >= 70) {
            holder.itemProgress.setProgressDrawable(context.getDrawable(R.drawable.circular_progress_bar_yellow));
        }
        holder.itemProgress.setProgress(expirationPercent);
    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    // HELPER METHODS

    // Gets text to display for itemCountdown
    private String getCountdownText(long diffInDays) {
        if (diffInDays < 0) {
            return "Expired!";
        } else if (diffInDays < 1) {
            return "Today!";
        }else if (diffInDays == 1) {
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
    private int getCountdownColor(int expirationPercent) {
        if (expirationPercent == 100) {
            return Color.parseColor("#FB774E");
        } else if (expirationPercent >= 70){
            return Color.parseColor("#F4A42D");
        } else {
            return Color.parseColor("#90C454");
        }
    }

    // Gets progress towards expiration
    private int getExpirationProgress(FoodItem item) {
        Date expirationDate = item.getDateExpiration();
        Date addedDate = item.getDateFromString(item.getDateAdded());
        int progress;
        if (addedDate == null) {
            // No added date set, set progress to 0
            progress = 0;
        } else {
            Date now = new Date();
            long divisorInMillies = expirationDate.getTime() - addedDate.getTime();
            if (divisorInMillies > 0 && (expirationDate.getTime() > now.getTime())) {
                // Calculate expiration percent
                long dividendInMillies = now.getTime() - addedDate.getTime();
                double division = ((double) dividendInMillies / divisorInMillies) * 100;
                progress = (int) division;
            } else {
                // Item is already expired, set progress to 100
                progress = 100;
            }
        }
        return progress;
    }

    // Used to filter list
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemsFiltered = items;
                } else {

                    ArrayList<FoodItem> filteredList = new ArrayList<>();
                    for (FoodItem row : items) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    itemsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsFiltered = (ArrayList<FoodItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemName;
        TextView itemExpiration;
        TextView itemCountdown;
        ProgressBar itemProgress;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameAdapterItem);
            itemExpiration = itemView.findViewById(R.id.itemExpirationAdapterItem);
            itemCountdown = itemView.findViewById(R.id.itemCountdownAdapterItem);
            itemProgress = itemView.findViewById(R.id.itemProgressBar);
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
