package com.bateman.rich.exercisetrack.gui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;

/**
 * A RecyclerView adapter for Day Schedules.
 * The Cursor will always accurately reflect how items are presented.
 */
public class RVAdapterDaySchedule extends RecyclerView.Adapter<RVAdapterDaySchedule.ViewHolder> {
    private static final String TAG = "RVAdapterDaySchedule";
    public static final long DAY_SEPARATOR_ID = -25000;
    public static final String DAY_SEPARATOR_LABEL = "--separator--";
    private final Context m_context;
    private Cursor m_cursor;
    private DayScheduleDragManager m_dayScheduleDragManager;
    private RecyclerView m_recyclerView;
//    private final ArrayList<String> m_itemList = new ArrayList<>();

    public RVAdapterDaySchedule(Context c, Cursor cursor) {
        Log.d(TAG, "RVAdapterDaySchedule: start");
        m_context = c;
        m_cursor = cursor;
    }

    /**
     * A handy method that is called when the recycler viwe is attached to the adapter.
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        m_recyclerView=recyclerView;
    }

    public RecyclerView getRecyclerView() {return m_recyclerView;}


    public void setDayScheduleDragManager(DayScheduleDragManager dayScheduleDragManager, RecyclerView rv) {
        m_dayScheduleDragManager = dayScheduleDragManager;
        m_dayScheduleDragManager.registerRecyclerViewForAcceptingDrags(rv);
    }

    public RVAdapterDaySchedule.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sched_maint_exercise_item, parent, false);
        return new RVAdapterDaySchedule.ViewHolder(view);
    }

    public void onBindViewHolder(RVAdapterDaySchedule.ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: start");

        viewHolder.textViewDropHereTop.setVisibility(View.GONE);
        viewHolder.textViewDropHereBot.setVisibility(View.GONE);

        if(m_cursor == null || m_cursor.getCount() == 0) {
            Log.d(TAG, "onBindViewHolder: No day schedule entries to load.");
        } else {
            if(!m_cursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final DayScheduleEntry dayScheduleEntry = DayScheduleEntry.createDayScheduleEntryFromView(m_cursor);
            viewHolder.textViewExerciseName.setText(dayScheduleEntry.getExerciseEntryName());
            viewHolder.itemView.setTag(dayScheduleEntry.getPosition());
            m_dayScheduleDragManager.registerViewForRightBehavior(viewHolder.itemView);
        }
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if((m_cursor == null) || (m_cursor.getCount() == 0)) {
            return 0; // fib, because we populate a single ViewHolder with instructions, or the DAY_SEPARATOR for day schedule mode.
        } else {
            int recordCount = m_cursor.getCount();
            Log.d(TAG, "getItemCount: there are " + recordCount + " records.");
            return recordCount;
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed.
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == m_cursor) {
            return null;
        }

        int numItems = getItemCount();

        final Cursor oldCursor = m_cursor;
        m_cursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems);
        }
        return oldCursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";

        private TextView textViewExerciseName;
        private TextView textViewDropHereTop;
        private TextView textViewDropHereBot;
        private ImageButton btnMoveDown;
        private ImageButton btnMoveUp;
        private ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            this.textViewExerciseName = itemView.findViewById(R.id.sm_textview_exercise_item);
            this.textViewDropHereTop = itemView.findViewById(R.id.sm_maint_tv_drop_here_top);
            this.textViewDropHereBot = itemView.findViewById(R.id.sm_maint_tv_drop_here_bot);
            this.btnDelete = itemView.findViewById(R.id.sm_maint_btn_delete);
            this.btnMoveDown = itemView.findViewById(R.id.sm_maint_btn_move_down);
            this.btnMoveUp = itemView.findViewById(R.id.sm_maint_btn_move_up);

            if(this.textViewExerciseName == null) throw new IllegalStateException("Unable to find textViewExerciseName on Day Schedule view holder.");
            if(this.textViewDropHereTop == null) throw new IllegalStateException("Unable to find textViewDropHereTop on Day Schedule view holder.");
            if(this.textViewDropHereBot == null) throw new IllegalStateException("Unable to find textViewDropHereBot on Day Schedule view holder.");
            if(this.btnDelete == null) throw new IllegalStateException("Unable to find btnDelete on Day Schedule view holder.");
            if(this.btnMoveDown == null) throw new IllegalStateException("Unable to find btnMoveDown on Day Schedule view holder.");
            if(this.btnMoveUp == null) throw new IllegalStateException("Unable to find btnMoveUp on Day Schedule view holder.");
        }

        public TextView getTextViewExerciseName() {
            return textViewExerciseName;
        }

        public TextView getTextViewDropHereTop() {
            return textViewDropHereTop;
        }

        public TextView getTextViewDropHereBot() {
            return textViewDropHereBot;
        }

        public ImageButton getBtnMoveDown() {
            return btnMoveDown;
        }

        public ImageButton getBtnMoveUp() {
            return btnMoveUp;
        }

        public ImageButton getBtnDelete() {
            return btnDelete;
        }
    }


    // 2019.01.26: I had this idea that the data in the database would need to be modified so that it could be presented to the user.
    // My thought was "day separators" would be derived based on how the data was stored, but I found this made things unnecessarily
    // complicated.  Below is how i did it... but it succcks.
    /**
     * Given a cursor of Day Schedules, this builds a list of exercise names or day separators.
     */
//    private void deriveDayScheduleList() {
//        Log.d(TAG, "deriveDayScheduleList: start");
//        m_itemList.clear();
//        int lastPosition = 0;
//        if((m_cursor != null) &&(m_cursor.getCount() > 0)) {
//            m_cursor.moveToFirst();
//            Log.d(TAG, "deriveDayScheduleList: there are " + m_cursor.getCount() + " item(s) in the cursor.");
//            do {
//                DayScheduleEntry dayScheduleEntry = new DayScheduleEntry(m_cursor);
//                int currentPosition = dayScheduleEntry.getPosition();
//                if(currentPosition != lastPosition) {
//                    if(lastPosition != 0) {
//                        m_itemList.add(DAY_SEPARATOR_LABEL);
//                    }
//                    lastPosition = currentPosition;
//                }
//
//                // Look up the exercise entry for the DaySchedule.  I hate this.  I have to imagine this is not performant.
//                // Something to look at in the future... like bulk loading all exercise entries and updating all day schedules on initial load, or something like that.
//                ExerciseEntry matchingExerciseEntry = null;
//                String selection = ExerciseEntry.Contract.Columns.COL_NAME_ID + "=?";
//                Cursor cursorExerciseEntries = m_context.getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI,
//                        ExerciseEntry.Contract.getProjectionFull(), selection, new String[]{String.format("%d", dayScheduleEntry.getExerciseEntryId())}, null);
//
//                if(cursorExerciseEntries != null && cursorExerciseEntries.getCount() > 0) {
//                    cursorExerciseEntries.moveToFirst();
//                    matchingExerciseEntry = new ExerciseEntry(cursorExerciseEntries);
//                    m_itemList.add(matchingExerciseEntry.getName());
//                } else {
//                    throw new IllegalStateException("Could not find a matching exercise entry for given day schedule.");
//                }
//
//                cursorExerciseEntries.close();
//            } while(m_cursor.moveToNext());
//        }
//
//    }

}
