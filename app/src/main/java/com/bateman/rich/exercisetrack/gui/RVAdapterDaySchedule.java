package com.bateman.rich.exercisetrack.gui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;

/**
 * A RecyclerView adapter for Day Schedules.
 */
public class RVAdapterDaySchedule extends RecyclerView.Adapter<RVAdapterDaySchedule.ViewHolder> {
    private static final String TAG = "RVAdapterDaySchedule";
    private Cursor m_cursor;

    public RVAdapterDaySchedule(Cursor cursor) {
        Log.d(TAG, "RVAdapterDaySchedule: start");
        m_cursor = cursor;
    }

    public RVAdapterDaySchedule.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sched_maint_schedule_box_container, parent, false);
        return new RVAdapterDaySchedule.ViewHolder(view);
    }

    public void onBindViewHolder(RVAdapterDaySchedule.ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: start");

        if(m_cursor == null || (m_cursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions.");
        } else {
            if(!m_cursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final DayScheduleEntry dayScheduleEntry = new DayScheduleEntry(m_cursor);
//            viewHolder.textViewExerciseName.setText(exerciseEntry.getName());
//            viewHolder.checkBoxIsReminder.setChecked(exerciseEntry.isDailyReminder());
//            viewHolder.checkBoxIsReminder.setVisibility(View.VISIBLE);
//            viewHolder.buttonDeleteEntry.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if((m_cursor == null) || (m_cursor.getCount() == 0)) {
            return 1; // fib, because we populate a single ViewHolder with instructions
        } else {
            return m_cursor.getCount();
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

        private LinearLayout linearLayoutContainer;

        public ViewHolder(View itemView) {
            super(itemView);

            this.linearLayoutContainer = itemView.findViewById(R.id.smsb_linearlayout);

            if(this.linearLayoutContainer == null) throw new IllegalStateException("Unable to find linear layout on Day Schedule view holder.");
        }
    }
}
