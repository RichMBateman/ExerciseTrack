package com.bateman.rich.exercisetrack.gui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;

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
    private final RecyclerView m_recyclerViewLeft;
    private final RecyclerView m_recyclerViewRight;
    private final RVAdapterDaySchedule m_rvAdapterDaySchedule;
    private final RVAdapterExerciseEntry m_rvAdapterExerciseEntry;

    /**
     * Create a new DayScheduleDragManager, which will handle the dragging and dropping of items between
     * two RecyclerViews.
     * @param c
     */
    public DayScheduleDragManager(Context c, RVAdapterExerciseEntry rvAdapterExerciseEntry, RVAdapterDaySchedule rvAdapterDaySchedule, RecyclerView leftRV, RecyclerView rightRV) {
        m_context = c;
        m_rvAdapterExerciseEntry = rvAdapterExerciseEntry;
        m_rvAdapterDaySchedule = rvAdapterDaySchedule;
        m_recyclerViewLeft = leftRV;
        m_recyclerViewRight = rightRV;
    }

    // This function is not quite what I wanted.  I don't want the ENtIRE recycler view to respond to drag events.  Just individual parts.
//    public void registerRecyclerViewForAcceptingDrags(RecyclerView rv) {
//        DayScheduleReceiveDragHandler l = new DayScheduleReceiveDragHandler(m_context);
//        rv.setOnDragListener(l);
//    }

    /**
     * Registers a view (probably just textview holding an exercise name or "--day separator--"
     * so that it will have "Left" recycler view behavior.  Meaning you can drag it from the left
     * recycle view anywhere on the right view, and it won't disappear when dragging, or on drop.
     * Includes the exercise id of this view.
     * @param view
     */
    public void registerViewForLeftBehavior(View view, long exerciseId) {
        DayScheduleStartDragHandler l = new DayScheduleStartDragHandler(false, exerciseId);
        view.setOnLongClickListener(l);
        view.setOnTouchListener(l);
    }

    /**
     * No longer allowing elements on the right recycler view to be dragged.
     */
    /**
     * Registers a view for "Right" recycler view behvaior.  You can drag it anywhere on the right recycler view,
     * it will disappear from origin while dragging, and when dropped, the old item will be deleted.
     * @param view
     */
//    public void registerViewForRightBehavior(View view) {
//        DayScheduleStartDragHandler l = new DayScheduleStartDragHandler(true, 0);
//        view.setOnLongClickListener(l);
//        view.setOnTouchListener(l);
//    }

    /**
     * Registers a "Drop Me" placeholder textview for dragging events.
     * When something STARTS to drag, this view will become visible.
     * If someothing drags INTO this view, it will light up.
     * If you drop INTO this view, it will position the new exercise appropriately.
     * @param placeholder
     */
    public void registerForDropMePlaceholderBehavior(View placeholder, RVAdapterDaySchedule.ViewHolder viewHolder, int targetPosition) {
        DayScheduleReceiveDragHandler l = new DayScheduleReceiveDragHandler(m_context, viewHolder, targetPosition);
        placeholder.setOnDragListener(l);
    }

    private void setDropHerePlaceholderVisibility(boolean showDropHerePlaceholders) {
        int visibility = (showDropHerePlaceholders ? View.VISIBLE : View.GONE);
        RVAdapterDaySchedule.ViewHolder viewHolder;
        RecyclerView recyclerView = m_rvAdapterDaySchedule.getRecyclerView();

        for(int viewHolderIndex = 0; viewHolderIndex <= recyclerView.getChildCount(); viewHolderIndex++) {
            viewHolder = (RVAdapterDaySchedule.ViewHolder) recyclerView.findViewHolderForAdapterPosition(viewHolderIndex);
            if(viewHolder != null) {
                if(viewHolderIndex == 0 && recyclerView.getChildCount() == 1) {
                    // In this case, we only have one valid "drop here" text view, because there are no day schedules.
                    if(viewHolder.getTextViewExerciseName().getVisibility() == View.GONE) {
                        viewHolder.getTextViewDropHereTop().setVisibility(visibility);
                    } else {
                        viewHolder.getTextViewDropHereTop().setVisibility(visibility);
                        viewHolder.getTextViewDropHereBot().setVisibility(visibility);
                    }
                } else {
                    viewHolder.getTextViewDropHereTop().setVisibility(visibility);
                    if (viewHolderIndex == m_rvAdapterDaySchedule.getRecyclerView().getChildCount() - 1) {
                        viewHolder.getTextViewDropHereBot().setVisibility(visibility);
                    }
                }
            } else {
                Log.d(TAG, "handleStartDrag: null viewHolder found for position: " + viewHolderIndex);
            }
        }
    }

    /**
     * A class to handle listening to drag events.  In this case, we are handling simple touches and long clicks to start dragging.
     * http://www.vogella.com/tutorials/AndroidDragAndDrop/article.html
     *
     */
    private class DayScheduleStartDragHandler implements View.OnLongClickListener,
            View.OnTouchListener {

        private boolean m_hideOnDrag;
        private long m_exerciseId;

        private DayScheduleStartDragHandler(boolean hideOnDrag, long exerciseId) {
            m_hideOnDrag=hideOnDrag;
            m_exerciseId=exerciseId;
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
            // As we start dragging, make all appropriate "Drop Here" placeholders visible in the right recycler view.
            setDropHerePlaceholderVisibility(true);

            ClipData dragData = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

            // This starts a drag operation.  You specify the data whichi s passed to the drop target via an instance
            // of ClipData.
            // You also pass an instance of DragShadowBuilder.  This objects specifies the picture used for the drag operation.
            // You can also pass the view directly, which will cause an image of the view to be shown during the drag.
            view.startDrag(dragData, myShadow, m_exerciseId, 0);
            view.setVisibility((m_hideOnDrag ? View.GONE : View.VISIBLE));
        }
    }

    /**
     * The views which can be drop targets get an instance of OnDragListener assigned.  In this drop listener, you receive call
     * backs in case of predefined drag and drop related events.  (ACTION_(DRAG_STARTED, DRAG_EXITED, DROP, DRAG_ENDED)
     */
    private class DayScheduleReceiveDragHandler implements View.OnDragListener {
        private final DayScheduleListActivity m_activity;
        private final Context m_context;
        private final Drawable m_enterShape;
        private final Drawable m_normalShape;
        private final RVAdapterDaySchedule.ViewHolder m_viewHolder;
        private final int m_targetPosition;

        private DayScheduleReceiveDragHandler(Context c, RVAdapterDaySchedule.ViewHolder viewHolder, int targetPosition) {
            m_activity = (DayScheduleListActivity) c;
            m_context = c;
            m_viewHolder=viewHolder;
            m_targetPosition=targetPosition;
            m_enterShape = m_context.getResources().getDrawable(R.drawable.shape_droptarget);
            m_normalShape = m_context.getResources().getDrawable(R.drawable.shape_normal);
        }
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            boolean isTopPlaceHolder = (v.getId() == R.id.sm_maint_tv_drop_here_top);
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Let the user know they've entered a valid space for dropping
                    v.setBackground(m_enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    // The user left the valid space.
                    //v.setBackground(m_normalShape);
                    v.setBackground(null); // this will clear the background.
                    break;
                case DragEvent.ACTION_DROP:
                    long exerciseId = (long) event.getLocalState();
                    boolean isDaySeparator = (exerciseId == RVAdapterDaySchedule.DAY_SEPARATOR_ID);

                    // old code for detecting what we've dragged over.  Now we know.
//                    View rightRvElement = m_recyclerViewRight.findChildViewUnder(event.getX(), event.getY());
//                    // For now, if we don't find any right element... just set the position to the max.
//                    int itemPosition = 0;
//                    if(rightRvElement == null) {
//                        // There are no elements in the list, so we can say the position = 1.
//                        itemPosition = 1;
//                        // There are no items
//                    } else {
//                        itemPosition = (int) rightRvElement.getTag();
//                    }
                    DayScheduleEntry.saveNewDaySchedule(m_context, m_targetPosition, exerciseId, isDaySeparator);
                    LoaderManager.getInstance(m_activity).restartLoader(DayScheduleListActivity.LOADER_ID_DAY_SCHEDULES, null, m_activity);
//                    m_rvAdapterDaySchedule.notifyDataSetChanged();


                    // As we stop dragging, make all appropriate "Drop Here" placeholders invisible in the right recycler view.
                    setDropHerePlaceholderVisibility(false);


                    Log.d(TAG, "onDrag: dropped exercise " + exerciseId + " onto schedule.");
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
                    //v.setBackground(m_normalShape);
                    v.setBackground(null); // this will clear the background.
                    setDropHerePlaceholderVisibility(false);
                default:
                    break;
            }
            return true;
        }
    }

}
