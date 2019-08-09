package utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expireme.R;

/**
 * CallBack when an item is swiped in the ItemListActivity.
 */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private CustomItemAdapter myAdapter;

    // Trashcan that appears when user swipes left or right
    private Drawable trashIcon;

    // Background that appears behind item when swiping
    private final ColorDrawable itemSwipeBackground;

    public SwipeToDeleteCallback(CustomItemAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        myAdapter = adapter;
        trashIcon = ContextCompat.getDrawable(myAdapter.getContext(), R.drawable.delete);
        itemSwipeBackground = new ColorDrawable(Color.RED);
    }

    /**
     * Call when item is swiped. Deletes item from list and database.
     * @param viewHolder RecyclerView ViewHolder
     * @param direction direction of the swipe
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        myAdapter.deleteItem(position);
    }

    /*
     * Animates swiping motion
     */
    @Override
    public void onChildDraw(@NonNull Canvas c,@NonNull RecyclerView recyclerView,@NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        // Identify icon sizes and margins (used when calculating drawing animation)
        int iconSize = 100;
        int iconMargin = (itemView.getHeight() - iconSize) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
        int iconBottom = iconTop + iconSize;

        // Right-swipe
        if (dX > 0) {

            int iconRight = itemView.getLeft() + iconMargin + iconSize;
            int iconLeft = itemView.getLeft() + iconMargin;

            int margin = (itemView.getLeft() + ((int) dX) < iconRight + iconMargin) ? (int)dX - iconSize - ( iconMargin * 2 ) : 0;
            iconLeft += margin;
            iconRight += margin;

            trashIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            itemSwipeBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());

        // Left-swipe
        } else if (dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - iconSize;
            int iconRight = itemView.getRight() - iconMargin;

            int margin = (itemView.getRight() - ((int) dX) > iconLeft - iconMargin) ? (int)dX + iconSize + ( iconMargin * 2 ) : 0;
            iconLeft += margin;
            iconRight += margin;

            trashIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            itemSwipeBackground.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());

        // Not swiped
        } else {
            trashIcon.setBounds(0,0,0,0);
            itemSwipeBackground.setBounds(0, 0, 0, 0);
        }

        itemSwipeBackground.draw(c);
        trashIcon.draw(c);
    }

    // Items in list can't be moved, are sorted by date
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }
}
