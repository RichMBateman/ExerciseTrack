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
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;

public class RVAdapterCurrentDayExercise extends RecyclerView.Adapter<RVAdapterCurrentDayExercise.ViewHolder> {
    private static final String TAG = "RVAdapterCurrentDayExer";
//    interface OnButtonClickListener {
//        void onDeleteClick(@NonNull ExerciseEntry entry);
//    }

    private Context m_context;
    private Cursor m_cursor;
    //private RVAdapterCurrentDayExercise.OnButtonClickListener m_buttonClickListener;

    public RVAdapterCurrentDayExercise(Context context, Cursor cursor) {
        m_context = context;
        m_cursor = cursor;
    }

    public RVAdapterCurrentDayExercise.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_day_exercise_blockentry, parent, false);
        return new RVAdapterCurrentDayExercise.ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {
//        if(m_cursor == null || (m_cursor.getCount() == 0)) {
//            switch(m_mode) {
//                case EXERCISE_ENTRY:
//                    Log.d(TAG, "onBindViewHolder: providing instructions.");
//                    viewHolder.textViewExerciseName.setText("Enter exercise/reminder to start.");
//                    viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
//                    viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
//                    break;
//                case DAY_SCHEDULE:
//                    Log.d(TAG, "creating empty day separator");
//                    viewHolder.textViewExerciseName.setText(RVAdapterDaySchedule.DAY_SEPARATOR_LABEL);
//                    viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
//                    viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
//                    break;
//            }
//        } else {
//            boolean isDaySeparator = (position == 0 && m_mode == RVAdapterExerciseEntry.Mode.DAY_SCHEDULE);
//            if(!isDaySeparator) {
//                if(m_mode == RVAdapterExerciseEntry.Mode.DAY_SCHEDULE) {
//                    position--; // Decrement the position, because the 0th element is our "Day Separator" placeholder.
//                }
//                if(!m_cursor.moveToPosition(position)) {
//                    throw new IllegalStateException("Couldn't move cursor to position " + position);
//                }
//            }
//            final ExerciseEntry exerciseEntry = (isDaySeparator ? new ExerciseEntry(RVAdapterDaySchedule.DAY_SEPARATOR_ID,
//                    RVAdapterDaySchedule.DAY_SEPARATOR_LABEL, false) : new ExerciseEntry(m_cursor));
//            if (!isDaySeparator){
//                viewHolder.textViewExerciseName.setText(exerciseEntry.getName());
//                viewHolder.checkBoxIsReminder.setChecked(exerciseEntry.isDailyReminder());
//            } else {
//                viewHolder.textViewExerciseName.setText(RVAdapterDaySchedule.DAY_SEPARATOR_LABEL);
//            }
//
//            switch(m_mode) {
//                case EXERCISE_ENTRY:
//                    viewHolder.checkBoxIsReminder.setVisibility(View.VISIBLE);
//                    viewHolder.buttonDeleteEntry.setVisibility(View.VISIBLE);
//
//                    View.OnClickListener buttonListener = new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Log.d(TAG, "onClick: start");
//                            switch (view.getId()) {
//
//                                case R.id.sele_btn_delete:
//                                    if (m_buttonClickListener != null) {
//                                        m_buttonClickListener.onDeleteClick(exerciseEntry);
//                                    }
//                                    break;
//                                default:
//                                    Log.d(TAG, "onClick: found unexpected button id");
//                            }
//
//                        }
//                    };
//                    viewHolder.buttonDeleteEntry.setOnClickListener(buttonListener);
//                    break;
//                case DAY_SCHEDULE:
//                    viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
//                    viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
//                    break;
//            }
//
//            // Register for drag and drop
//            switch(m_mode) {
//                case DAY_SCHEDULE:
//                    viewHolder.itemView.setBackground(m_context.getResources().getDrawable(R.drawable.shape_normal));
//                    if(m_dayScheduleDragManager != null) {
//                        long id = (exerciseEntry == null ? RVAdapterDaySchedule.DAY_SEPARATOR_ID : exerciseEntry.getId());
//                        m_dayScheduleDragManager.registerViewForLeftBehavior(viewHolder.itemView, id);
//                    }
//                    break;
//            }
//        }
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
//        if((m_cursor == null) || (m_cursor.getCount() == 0)) {
//            return 1; // fib, because we populate a single ViewHolder with instructions, or the DAY_SEPARATOR for day schedule mode.
//        } else {
//            switch(m_mode) {
//                case EXERCISE_ENTRY:
//                    return m_cursor.getCount();
//                case DAY_SCHEDULE:
//                    return m_cursor.getCount() + 1; // The first entry is always the DAY_SEPARATOR
//            }
//        }
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";

//        private Button buttonDeleteEntry;
//        private TextView textViewExerciseName;
//        private CheckBox checkBoxIsReminder;

        public ViewHolder(View itemView) {
            super(itemView);

//            this.buttonDeleteEntry = itemView.findViewById(R.id.sele_btn_delete);
//            this.textViewExerciseName = itemView.findViewById(R.id.sele_textview_exercise_name);
//            this.checkBoxIsReminder = itemView.findViewById(R.id.sele_cb_isdailyreminder);
//
//            if(this.buttonDeleteEntry == null) throw new IllegalStateException("Unable to find delete button on exercise entry view holder.");
//            if(this.textViewExerciseName == null) throw new IllegalStateException("Unable to find text view on exercise entry view holder.");
//            if(this.checkBoxIsReminder == null) throw new IllegalStateException("Unable to find checkbox on exercise entry view holder.");
        }
    }
}
