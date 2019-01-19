package com.bateman.rich.exercisetrack.gui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;

import java.util.ArrayList;

/**
 * A RecyclerView adapter for Day Schedules.
 */
public class RVAdapterDaySchedule extends RecyclerView.Adapter<RVAdapterDaySchedule.ViewHolder> {
    private static final String TAG = "RVAdapterDaySchedule";
    private static final String DAY_SEPARATOR_LABEL = "--separator--";
    private final Context m_context;
    private Cursor m_cursor;
    private DayScheduleDragManager m_dayScheduleDragManager;
    private final ArrayList<String> m_itemList = new ArrayList<>();

    public RVAdapterDaySchedule(Context c, Cursor cursor, DayScheduleDragManager dayScheduleDragManager, RecyclerView rv) {
        Log.d(TAG, "RVAdapterDaySchedule: start");
        m_context = c;
        m_cursor = cursor;
        m_dayScheduleDragManager = dayScheduleDragManager;

        m_dayScheduleDragManager.registerRecyclerViewForAcceptingDrags(rv);
    }

    public RVAdapterDaySchedule.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sched_maint_exercise_item, parent, false);

        m_dayScheduleDragManager.registerViewForRightBehavior(view);

        return new RVAdapterDaySchedule.ViewHolder(view);
    }

    public void onBindViewHolder(RVAdapterDaySchedule.ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: start");
        if(position < m_itemList.size()) {
            viewHolder.textViewExerciseName.setText(m_itemList.get(position));
        } else {
            throw new IllegalStateException("Couldn't find an entry for position: " + position);
        }


//        if(m_cursor == null || (m_cursor.getCount() == 0)) {
//            Log.d(TAG, "onBindViewHolder: providing instructions.");
//        } else {
//            if(!m_cursor.moveToPosition(position)) {
//                throw new IllegalStateException("Couldn't move cursor to position " + position);
//            }
//
//            final DayScheduleEntry dayScheduleEntry = new DayScheduleEntry(m_cursor);
//            viewHolder.textViewExerciseName.setText(exerciseEntry.getName());
//            viewHolder.checkBoxIsReminder.setChecked(exerciseEntry.isDailyReminder());
//            viewHolder.checkBoxIsReminder.setVisibility(View.VISIBLE);
//            viewHolder.buttonDeleteEntry.setVisibility(View.VISIBLE);
//        }
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        deriveDayScheduleList();
        return m_itemList.size();
    }

    /**
     * Given a cursor of Day Schedules, this builds a list of exercise names or day separators.
     */
    private void deriveDayScheduleList() {
        m_itemList.clear();
        int lastPosition = 0;
        if((m_cursor != null) &&(m_cursor.getCount() > 0)) {
            do {
                DayScheduleEntry dayScheduleEntry = new DayScheduleEntry(m_cursor);
                int currentPosition = dayScheduleEntry.getPosition();
                if(currentPosition != lastPosition) {
                    if(lastPosition != 0) {
                        m_itemList.add(DAY_SEPARATOR_LABEL);
                    }
                    lastPosition = currentPosition;
                }

                // Look up the exercise entry for the DaySchedule.  I hate this.  I have to imagine this is not performant.
                // Something to look at in the future... like bulk loading all exercise entries and updating all day schedules on initial load, or something like that.
                ExerciseEntry matchingExerciseEntry = null;
                String selection = ExerciseEntry.Contract.Columns.COL_NAME_ID + "=?";
                Cursor cursorExerciseEntries = m_context.getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI,
                        ExerciseEntry.Contract.getProjectionFull(), selection, new String[]{String.format("%d", dayScheduleEntry.getExerciseEntryId())}, null);

                if(cursorExerciseEntries != null && cursorExerciseEntries.getCount() > 0) {
                    cursorExerciseEntries.moveToFirst();
                    matchingExerciseEntry = new ExerciseEntry(cursorExerciseEntries);
                    m_itemList.add(matchingExerciseEntry.getName());
                } else {
                    throw new IllegalStateException("Could not find a matching exercise entry for given day schedule.");
                }

                cursorExerciseEntries.close();
            } while(m_cursor.moveToNext());
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

        public ViewHolder(View itemView) {
            super(itemView);

            this.textViewExerciseName = itemView.findViewById(R.id.sm_textview_exercise_item);
            if(this.textViewExerciseName == null) throw new IllegalStateException("Unable to find text view on Day Schedule view holder.");
        }
    }
}
