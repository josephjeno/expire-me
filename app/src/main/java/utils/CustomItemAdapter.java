package utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expireme.R;

import java.util.ArrayList;

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
    }

//    @Override
//    public Object getItem(int i) {
//        return items.get(i);
//    }
  
//    // Total number of items to be displayed on listView
//    @Override
//    public int getCount() {
//        return items.size();
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void deleteItem(int position) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.deleteItem(items.get(position).getId());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemName;
        TextView itemExpiration;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameAdapterItem);
            itemExpiration = itemView.findViewById(R.id.itemExpirationAdapterItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myClickListener != null) myClickListener.onItemClick(view, getAdapterPosition());
        }
    }


//        // Override the values of the child views
//        viewHolder.itemName.setText(items.get(i).getName());
//        viewHolder.itemExpiration.setText(items.get(i).getExpiryDate());
//        viewHolder.itemId.setText(items.get(i).getId().toString());

    // convenience method for getting data at click position
    FoodItem getItem(int id) {
        return items.get(id);
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
