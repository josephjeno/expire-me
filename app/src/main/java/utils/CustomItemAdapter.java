package utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.expireme.R;

import java.util.ArrayList;

public class CustomItemAdapter extends BaseAdapter {

    // Receive the context from ItemListActivity
    Context context;

    // ArrayList with the item expiration data points to populate adapter
    ArrayList<ItemListAdapterItem> expirationItems;

    public CustomItemAdapter(Context context, ArrayList<ItemListAdapterItem> expirationItems) {
        this.context = context;
        this.expirationItems = expirationItems;
    }

    // Total number of items to be displayed on listView
    @Override
    public int getCount() {
        return expirationItems.size();
    }

    // Extracts the data of the item at specific location in the list
    @Override
    public Object getItem(int i) {
        return expirationItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // Initialize ViewHolder
        ViewHolder viewHolder;

        if (view == null) {
            // Create and return the view
            view = View.inflate(context, R.layout.item_list_adapter_item, null);

            // Create an object of viewHolder -> get hold of my child view references
            viewHolder = new ViewHolder();

            viewHolder.itemName = view.findViewById(R.id.itemNameAdapterItem);
            viewHolder.itemExpiration = view.findViewById(R.id.itemExpirationAdapterItem);
            viewHolder.itemId = view.findViewById(R.id.itemIdAdapterItem);

            // Link the viewHolder to my view
            view.setTag(viewHolder);
        } else {
            // If viewHolder already exists then restore
            viewHolder = (ViewHolder) view.getTag();
        }

        // Override the values of the child views
        viewHolder.itemName.setText(expirationItems.get(i).getItemName());
        viewHolder.itemExpiration.setText(expirationItems.get(i).getItemExpiration());
        viewHolder.itemId.setText(expirationItems.get(i).getItemId());

        return view;
    }

    // Class to hold my child views for optimization
    static class ViewHolder{
        TextView itemName;
        TextView itemExpiration;
        TextView itemId;
    }
}
