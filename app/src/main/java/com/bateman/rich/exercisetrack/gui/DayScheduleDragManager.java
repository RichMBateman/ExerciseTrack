package com.bateman.rich.exercisetrack.gui;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bateman.rich.exercisetrack.R;

/**
 * Handles the dragging and dropping of objects in the Day Schedule activity.
 * You register two recycle views, and it will handle dragging and dropping.
 * The left recycle view can be dragged from (and its items won't be removed);
 * if you drag from the right recycle view, you can only move them up or down.
 * (You will need to press a delete button to delete them)
 */
public class DayScheduleDragManager {
    private static final String TAG = "DayScheduleDragManager";

    private final Context m_context;
    private RecyclerView m_recyclerViewLeft;
    private RecyclerView m_recyclerViewRight;

    /**
     * Create a new DayScheduleDragManager, which will handle the dragging and dropping of items between
     * two RecyclerViews.
     * @param c
     */
    public DayScheduleDragManager(Context c) {
        m_context = c;
    }

    public void registerRecyclerViewForAcceptingDrags(RecyclerView rv) {
        DayScheduleReceiveDragHandler l = new DayScheduleReceiveDragHandler(m_context);
        rv.setOnDragListener(l);
    }

    /**
     * Registers a view (probably just textview holding an exercise name or "--day separator--"
     * so that it will have "Left" recycler view behavior.  Meaning you can drag it from the left
     * recycle view anywhere on the right view, and it won't disappear when dragging, or on drop.
     * @param view
     */
    public void registerViewForLeftBehavior(View view) {
        DayScheduleStartDragHandler l = new DayScheduleStartDragHandler(false);
        view.setOnLongClickListener(l);
        view.setOnTouchListener(l);
    }

    /**
     * Registers a view for "Right" recycler view behvaior.  You can drag it anywhere on the right recycler view,
     * it will disappear from origin while dragging, and when dropped, the old item will be deleted.
     * @param view
     */
    public void registerViewForRightBehavior(View view) {
        DayScheduleStartDragHandler l = new DayScheduleStartDragHandler(true);
        view.setOnLongClickListener(l);
        view.setOnTouchListener(l);
    }

    /**
     * A class to handle listening to drag events.  In this case, we are handling simple touches and long clicks to start dragging.
     * http://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
     *
     */
    private class DayScheduleStartDragHandler implements View.OnLongClickListener,
            View.OnTouchListener {

        private boolean m_hideOnDrag = false;

        private DayScheduleStartDragHandler(boolean hideOnDrag) {
            m_hideOnDrag=hideOnDrag;
        }

        @Override
        public boolean onLongClick(View view) {
            handleStartDrag(view);
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleStartDrag(view);
                return true;
            }
            return false;
        }

        private void handleStartDrag(View view) {
            ClipData dragData = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

            // This starts a drag operation.  You specify the data whichi s passed to the drop target via an instance
            // of ClipData.
            // You also pass an instance of DragShadowBuilder.  This objects specifies the picture used for the drag operation.
            // You can also pass the view directly, which will cause an image of the view to be shown during the drag.
            view.startDrag(dragData, myShadow, view, 0);
            view.setVisibility((m_hideOnDrag ? View.GONE : View.VISIBLE));
        }
    }

    /**
     * The views which can be drop targets get an instance of OnDragListener assigned.  In this drop listener, you receive call
     * backs in case of predefined drag and drop related events.  (ACTION_(DRAG_STARTED, DRAG_EXITED, DROP, DRAG_ENDED)
     */
    private class DayScheduleReceiveDragHandler implements View.OnDragListener {
        private final Context m_context;
        private final Drawable m_enterShape;
        private final Drawable m_normalShape;

        private DayScheduleReceiveDragHandler(Context c) {
            m_context = c;
            m_enterShape = m_context.getResources().getDrawable(R.drawable.shape_droptarget);
            m_normalShape = m_context.getResources().getDrawable(R.drawable.shape_normal);
        }
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Let the user know they've entered a valid space for dropping
                    v.setBackground(m_enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    // The user left the valid space.
                    v.setBackground(m_normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // On dropping the exercise or day separator, need to do different things.
                    // Probably need to call an insert, or delete...
                    // Below is just sample code from a previous example that doesn't make sense in my case.
//                    // Dropped, reassign View to ViewGroup
//                    View view = (View) event.getLocalState();
//                    ViewGroup owner = (ViewGroup) view.getParent();
//                    owner.removeView(view);
//                    LinearLayout container = (LinearLayout) v;
//                    container.addView(view);
//                    view.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(m_normalShape);
                default:
                    break;
            }
            return true;
        }
    }

}
