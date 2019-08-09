package utils;

import android.content.Context;
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

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private CustomItemAdapter myAdapter;
    private Drawable icon;
    private final ColorDrawable background;


    public SwipeToDeleteCallback(CustomItemAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        myAdapter = adapter;
        icon = ContextCompat.getDrawable(myAdapter.getContext(), R.drawable.delete);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        myAdapter.deleteItem(position);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        int iconSize = 100;
        int iconMargin = (itemView.getHeight() - iconSize) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
        int iconBottom = iconTop + iconSize;

        if (dX > 0) { // Swiping to the right

            int iconRight = itemView.getLeft() + iconMargin + iconSize;
            int iconLeft = itemView.getLeft() + iconMargin;

            int magicConstraint = (itemView.getLeft() + ((int) dX) < iconRight + iconMargin) ? (int)dX - iconSize - ( iconMargin * 2 ) : 0;
            iconLeft += magicConstraint;
            iconRight += magicConstraint;

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX), itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - iconSize;
            int iconRight = itemView.getRight() - iconMargin;

            int magicConstraint = (itemView.getRight() - ((int) dX) > iconLeft - iconMargin) ? (int)dX + iconSize + ( iconMargin * 2 ) : 0;
            iconLeft += magicConstraint;
            iconRight += magicConstraint;

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX),
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            icon.setBounds(0,0,0,0);
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }

    // Items in list can't be moved, are sorted by date
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }
}
