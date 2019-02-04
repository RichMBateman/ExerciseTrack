package com.bateman.rich.exercisetrack.gui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;

/**
 * A RecyclerView Adapter for simple exercise entries.  Can operate in a couple modes.
 * One will show a button to delete the exercise (for the ExerciseEntry list screen), and
 * another will simply show the exercise name... and styled in such a way that it's clear it needs to be dragged and dropped.
 * Based off the Recycler View adapter class created by Tim Buchalka in the udemy course Android Java Masterclass - Become an App Developer.
 */
public class RVAdapterExerciseEntry extends RecyclerView.Adapter<RVAdapterExerciseEntry.ExerciseEntryViewHolder> {

    /**
     * A mode that controls how this adapter behaves.
     */
    public enum Mode {
        EXERCISE_ENTRY,
        DAY_SCHEDULE
    }

    /**
     * An interface for handling delete clicks.  Only applicable in EXERCISE_ENTRY mode.
     */
    interface OnExerciseButtonClickListener {
        void onDeleteClick(@NonNull ExerciseEntry entry);
    }

    private static final String TAG = "RVAdapterExerciseEntry";
    private final Mode m_mode;
    private Context m_context;
    private Cursor m_cursor;
    private OnExerciseButtonClickListener m_buttonClickListener;
    private DayScheduleDragManager m_dayScheduleDragManager;

    public RVAdapterExerciseEntry(Mode m, Context context, Cursor cursor) {
        Log.d(TAG, "RVAdapterExerciseEntry: start");
        m_mode = m;
        m_context = context;
        m_cursor = cursor;
    }

    public void setOnExerciseButtonClickListener(OnExerciseButtonClickListener l) {
        m_buttonClickListener = l;
    }

    public void setDayScheduleDragManager(DayScheduleDragManager m) {
        m_dayScheduleDragManager=m;
    }

    public ExerciseEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_exercise_list_entry, parent, false);

        // initially, i registered for drag and drop behavior, here, but I don't know for which exercise
        // this view holder is being created.  Furthermore, the exercise entry bound to a view may change.
        return new ExerciseEntryViewHolder(view);
    }

    private int getCountOfDaySchedules() {
        Cursor cursorOfDaySchedules = m_context.getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null, null, null, null);
        int countOfSchedules = 0;
        if(cursorOfDaySchedules != null) {
            countOfSchedules = cursorOfDaySchedules.getCount();
            cursorOfDaySchedules.close();
        }
        return countOfSchedules;
    }

    /**
     * Checks to see whether there is already an exercise with this name.
     * @param name The name of the exercise to check.
     * @return Whether this name already exists.
     */
    public boolean checkForRedundantExerciseName(String name) {
        boolean nameExists = false;
        if(m_cursor != null && m_cursor.getCount() > 0) {
            m_cursor.moveToFirst();
            do {
                ExerciseEntry entry = new ExerciseEntry(m_cursor);
                if(entry.getName().equalsIgnoreCase(name)) {
                    nameExists = true;
                    break;
                }
            } while(m_cursor.moveToNext());
        }
        return nameExists;
    }

    public void onBindViewHolder(ExerciseEntryViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: start");

        if(m_cursor == null || (m_cursor.getCount() == 0)) {
            switch(m_mode) {
                case EXERCISE_ENTRY:
                    Log.d(TAG, "onBindViewHolder: providing instructions.");
                    viewHolder.textViewExerciseName.setText("Enter exercise/reminder to start.");
                    viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
                    viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
                    break;
                case DAY_SCHEDULE:
                    // If there's no exercises... actually don't do anything.
//                    Log.d(TAG, "creating empty day separator");
//                    viewHolder.textViewExerciseName.setText(RVAdapterDaySchedule.DAY_SEPARATOR_LABEL);
//                    viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
//                    viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
                    break;
            }
        } else {
            boolean daySchedulesExist = (getCountOfDaySchedules() > 0);
            boolean isDaySeparatorItem = (position == 0 && m_mode == Mode.DAY_SCHEDULE && daySchedulesExist);
            if(!isDaySeparatorItem) {
                if(m_mode == Mode.DAY_SCHEDULE && daySchedulesExist) {
                    position--; // Decrement the position, because the 0th element is our "Day Separator" placeholder, which only exists
                        // if someone created day schedules already.
                }
                if(!m_cursor.moveToPosition(position)) {
                    throw new IllegalStateException("Couldn't move cursor to position " + position);
                }
            }
            final ExerciseEntry exerciseEntry = (isDaySeparatorItem ? new ExerciseEntry(RVAdapterDaySchedule.DAY_SEPARATOR_ID,
                    RVAdapterDaySchedule.DAY_SEPARATOR_LABEL, false) : new ExerciseEntry(m_cursor));
            if (!isDaySeparatorItem){
                viewHolder.textViewExerciseName.setText(exerciseEntry.getName());
                viewHolder.checkBoxIsReminder.setChecked(exerciseEntry.isDailyReminder());
            } else {
                viewHolder.textViewExerciseName.setText(RVAdapterDaySchedule.DAY_SEPARATOR_LABEL);
            }

            switch(m_mode) {
                case EXERCISE_ENTRY:
                    viewHolder.checkBoxIsReminder.setVisibility(View.VISIBLE);
                    viewHolder.buttonDeleteEntry.setVisibility(View.VISIBLE);

                    View.OnClickListener buttonListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick: start");
                            switch (view.getId()) {

                                case R.id.sele_btn_delete:
                                    if (m_buttonClickListener != null) {
                                        m_buttonClickListener.onDeleteClick(exerciseEntry);
                                    }
                                    break;
                                default:
                                    Log.d(TAG, "onClick: found unexpected button id");
                            }

                        }
                    };
                    viewHolder.buttonDeleteEntry.setOnClickListener(buttonListener);
                    break;
                case DAY_SCHEDULE:
                    viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
                    viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
                    break;
            }

            // Register for drag and drop
            switch(m_mode) {
                case DAY_SCHEDULE:
                    viewHolder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.shape_normal));
                    if(m_dayScheduleDragManager != null) {
                        long id = (exerciseEntry == null ? RVAdapterDaySchedule.DAY_SEPARATOR_ID : exerciseEntry.getId());
                        m_dayScheduleDragManager.registerViewForLeftBehavior(viewHolder.itemView, id);
                    }
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if((m_cursor == null) || (m_cursor.getCount() == 0)) {
            switch(m_mode) {
                case EXERCISE_ENTRY:
                    return 1; // If we're in the exercise entry mode, return 1, so we can show instructions.
                case DAY_SCHEDULE:
                    return 0; // If we're in day schedule mode, show nothing.
            }
            return 1; // fib, because we populate a single ViewHolder with instructions, or the DAY_SEPARATOR for day schedule mode.
        } else {
            switch(m_mode) {
                case EXERCISE_ENTRY:
                    return m_cursor.getCount();
                case DAY_SCHEDULE:
                    int daySeparatorInclusion = (getCountOfDaySchedules() > 0 ? 1 : 0);
                    return m_cursor.getCount() + daySeparatorInclusion; // The first entry is always the DAY_SEPARATOR, IFF there are existing day schedules.
            }
        }
        return 0;
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

        // It's important to get the item count BEFORE replacing the cursor!
        // otherwise, if you call getItemCount later, you will get potentially wrong info.
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

    static class ExerciseEntryViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ExerciseEntryViewHolder";

        private Button buttonDeleteEntry;
        private TextView textViewExerciseName;
        private CheckBox checkBoxIsReminder;

        public ExerciseEntryViewHolder(View itemView) {
            super(itemView);

            this.buttonDeleteEntry = itemView.findViewById(R.id.sele_btn_delete);
            this.textViewExerciseName = itemView.findViewById(R.id.sele_textview_exercise_name);
            this.checkBoxIsReminder = itemView.findViewById(R.id.sele_cb_isdailyreminder);

            if(this.buttonDeleteEntry == null) throw new IllegalStateException("Unable to find delete button on exercise entry view holder.");
            if(this.textViewExerciseName == null) throw new IllegalStateException("Unable to find text view on exercise entry view holder.");
            if(this.checkBoxIsReminder == null) throw new IllegalStateException("Unable to find checkbox on exercise entry view holder.");
        }
    }
}
