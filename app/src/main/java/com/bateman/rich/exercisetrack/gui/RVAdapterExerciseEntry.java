package com.bateman.rich.exercisetrack.gui;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;

/**
 * A RecyclerView Adapter for simple exercise entries.  Can operate in a couple modes.
 * One will show a button to delete the exercise (for the ExerciseEntry list screen), and
 * another will simply show the exercise name... and styled in such a way that it's clear it needs to be dragged and dropped.
 * Based off the Recycler View adapter class created by Tim Buchalka in the udemy course Android Java Masterclass - Become an App Developer.
 */
public class RVAdapterExerciseEntry extends RecyclerView.Adapter<RVAdapterExerciseEntry.ExerciseEntryViewHolder> {
    private static final String TAG = "RVAdapterExerciseEntry";
    private Context m_context;
    private Cursor m_cursor;
    private OnExerciseButtonClickListener m_buttonClickListener;
    private boolean m_onlyShowName;

    private android.support.v7.widget.RecyclerView.LayoutParams layoutParams;

    interface OnExerciseButtonClickListener {
        void onDeleteClick(@NonNull ExerciseEntry entry);
    }

    public RVAdapterExerciseEntry(Context context, Cursor cursor, OnExerciseButtonClickListener listener, boolean onlyShowName) {
        Log.d(TAG, "RVAdapterExerciseEntry: start");
        m_context = context;
        m_cursor = cursor;
        m_buttonClickListener = listener;
        m_onlyShowName=onlyShowName;
    }

    public ExerciseEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_exercise_list_entry, parent, false);

        if(m_onlyShowName) {
            view.setBackground(m_context.getResources().getDrawable(R.drawable.border));

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                    String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                    ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

                    v.startDrag(dragData, myShadow, null, 0);
                    return true;
                }
            });

            view.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            layoutParams = (android.support.v7.widget.RecyclerView.LayoutParams) v.getLayoutParams();
                            Log.d(TAG, "Action is DragEvent.ACTION_DRAG_STARTED");

                            // Do nothing
                            break;

                        case DragEvent.ACTION_DRAG_ENTERED:
                            Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENTERED");
                            int x_cord = (int) event.getX();
                            int y_cord = (int) event.getY();
                            break;

                        case DragEvent.ACTION_DRAG_EXITED:
                            Log.d(TAG, "Action is DragEvent.ACTION_DRAG_EXITED");
                            x_cord = (int) event.getX();
                            y_cord = (int) event.getY();
                            layoutParams.leftMargin = x_cord;
                            layoutParams.topMargin = y_cord;
                            v.setLayoutParams(layoutParams);
                            break;

                        case DragEvent.ACTION_DRAG_LOCATION:
                            Log.d(TAG, "Action is DragEvent.ACTION_DRAG_LOCATION");
                            x_cord = (int) event.getX();
                            y_cord = (int) event.getY();
                            break;

                        case DragEvent.ACTION_DRAG_ENDED:
                            Log.d(TAG, "Action is DragEvent.ACTION_DRAG_ENDED");

                            // Do nothing
                            break;

                        case DragEvent.ACTION_DROP:
                            Log.d(TAG, "ACTION_DROP event");

                            // Do nothing
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                        view.startDrag(data, shadowBuilder, view, 0);
                        view.setVisibility(View.INVISIBLE);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        return new ExerciseEntryViewHolder(view);
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
            Log.d(TAG, "onBindViewHolder: providing instructions.");
            viewHolder.textViewExerciseName.setText("Enter exercise/reminder to start.");
            viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
            viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
        } else {
            if(!m_cursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final ExerciseEntry exerciseEntry = new ExerciseEntry(m_cursor);
            viewHolder.textViewExerciseName.setText(exerciseEntry.getName());
            viewHolder.checkBoxIsReminder.setChecked(exerciseEntry.isDailyReminder());
            if(!m_onlyShowName) {
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
            } else {
                viewHolder.checkBoxIsReminder.setVisibility(View.GONE);
                viewHolder.buttonDeleteEntry.setVisibility(View.GONE);
            }
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
