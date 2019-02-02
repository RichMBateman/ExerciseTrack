package com.bateman.rich.exercisetrack.gui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;
import com.bateman.rich.exercisetrack.datamodel.ExerciseAppDBSetting;

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
    private final DayScheduleListActivity m_activity;
    private DayScheduleDragManager m_dayScheduleDragManager;
    private RecyclerView m_recyclerView;
    private long m_currentDayScheduleId;
    private RVAdapterExerciseEntry m_rvAdapterExerciseEntry;

    //    private final ArrayList<String> m_itemList = new ArrayList<>();

    public RVAdapterDaySchedule(Context c, Cursor cursor, DayScheduleListActivity activitiy) {
        Log.d(TAG, "RVAdapterDaySchedule: start");
        m_context = c;
        m_cursor = cursor;
        m_activity=activitiy;
    }

    public void setRvAdapterExerciseEntry(RVAdapterExerciseEntry rvAdapterExerciseEntry) {m_rvAdapterExerciseEntry=rvAdapterExerciseEntry;}

    /**
     * A handy method that is called when the recycler viwe is attached to the adapter.
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        m_recyclerView=recyclerView;
        initialzeCurrentDayScheduleId();
    }

    private void initialzeCurrentDayScheduleId() {
        String selection = ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + "='" + ExerciseAppDBSetting.SETTING_KEY_CURRENT_DAY +"'";
        String[] projection = {ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE};
        Cursor cursor =m_context. getContentResolver().query(ExerciseAppDBSetting.Contract.CONTENT_URI, projection, selection, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            m_currentDayScheduleId = Long.parseLong(cursor.getString(0));
        }
    }

    public RecyclerView getRecyclerView() {return m_recyclerView;}


    public void setDayScheduleDragManager(DayScheduleDragManager dayScheduleDragManager, RecyclerView rv) {
        m_dayScheduleDragManager = dayScheduleDragManager;
        // I no longer want the entire recycler view listening to events.
        //m_dayScheduleDragManager.registerRecyclerViewForAcceptingDrags(rv);
    }

    public RVAdapterDaySchedule.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sched_maint_exercise_item, parent, false);
        return new RVAdapterDaySchedule.ViewHolder(view);
    }

    public boolean isItemAboveAnExercise(int currentDaySchedulePosition) {
        boolean meetsCondition = false;
        int adapterPositionCurrentItem = currentDaySchedulePosition - 1;
        if(m_cursor.moveToPosition(adapterPositionCurrentItem)) {
            DayScheduleEntry entry = new DayScheduleEntry(m_cursor);
            if(entry.getExerciseEntryId() != DAY_SEPARATOR_ID)
                meetsCondition=true;
        }
        return meetsCondition;
    }

    public boolean isItemDaySeparator(int currentDaySchedulePosition) {
        boolean meetsCondition = false;
        int adapterPositionCurrentItem = currentDaySchedulePosition;
        if(m_cursor.moveToPosition(adapterPositionCurrentItem)) {
            DayScheduleEntry entry = new DayScheduleEntry(m_cursor);
            if(entry.getExerciseEntryId() == DAY_SEPARATOR_ID)
                meetsCondition=true;
        }
        return meetsCondition;
    }

    /**
     *
     * @param viewHolder
     * @param position Ranges from 0 to N - 1.
     */
    public void onBindViewHolder(RVAdapterDaySchedule.ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: start for position: " + position);

        viewHolder.textViewDropHereTop.setVisibility(View.GONE);
        viewHolder.textViewDropHereBot.setVisibility(View.GONE);

        int daySchedulePosition = position + 1; // daySchedulePosition is 1-based.  ViewHolder position is 0-based.
        // If you drop something ABOVE this, this item's position will be increased by one; the new item will then take its current position
        m_dayScheduleDragManager.registerForDropMePlaceholderBehavior(viewHolder.textViewDropHereTop, viewHolder, daySchedulePosition);
        // If you drop something BELOW this, it's position will be one more than this.
        m_dayScheduleDragManager.registerForDropMePlaceholderBehavior(viewHolder.textViewDropHereBot, viewHolder, daySchedulePosition + 1);

        if(m_cursor == null || m_cursor.getCount() == 0) {
            Log.d(TAG, "onBindViewHolder: No day schedule entries to load.");
            //m_dayScheduleDragManager.registerViewForRightBehavior(viewHolder.itemView);
            viewHolder.textViewExerciseName.setVisibility(View.GONE);
            viewHolder.btnMoveUp.setVisibility(View.GONE);
            viewHolder.btnMoveDown.setVisibility(View.GONE);
            viewHolder.btnDelete.setVisibility(View.GONE);
        } else {
            if(!m_cursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            viewHolder.textViewExerciseName.setVisibility(View.VISIBLE);


            viewHolder.btnMoveUp.setVisibility(View.VISIBLE);
            viewHolder.btnMoveDown.setVisibility(View.VISIBLE);
            viewHolder.btnDelete.setVisibility(View.VISIBLE);

            final DayScheduleEntry dayScheduleEntry = DayScheduleEntry.createDayScheduleEntryFromView(m_cursor);
            viewHolder.textViewExerciseName.setText(dayScheduleEntry.getExerciseEntryName());

            if(m_currentDayScheduleId == dayScheduleEntry.getId()) {
                viewHolder.textViewExerciseName.setBackgroundColor(Color.RED);
            } else {
                viewHolder.textViewExerciseName.setBackgroundColor(0);
            }

            viewHolder.itemView.setTag(dayScheduleEntry.getPosition());
            //m_dayScheduleDragManager.registerViewForRightBehavior(viewHolder.itemView);
            if(getItemCount() == 1) {
                viewHolder.btnMoveUp.setVisibility(View.GONE);
                viewHolder.btnMoveDown.setVisibility(View.GONE);
            } else if(position == 0) {
                viewHolder.btnMoveUp.setVisibility(View.GONE);
            } else if(position == getItemCount() - 1) {
                viewHolder.btnMoveDown.setVisibility(View.GONE);
            }

            viewHolder.textViewExerciseName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_currentDayScheduleId = dayScheduleEntry.getId();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE, Long.toString(m_currentDayScheduleId));
                    final String where = ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + "=?";
                    final String[] selection = new String[] {ExerciseAppDBSetting.SETTING_KEY_CURRENT_DAY};
                    m_context.getContentResolver().update(ExerciseAppDBSetting.Contract.CONTENT_URI, contentValues, where, selection);

                    notifyDataSetChanged();
                }
            });


            viewHolder.btnMoveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DayScheduleEntry.updateDaySchedulePosition(m_context, dayScheduleEntry.getId(), dayScheduleEntry.getPosition() -1);
//                    notifyDataSetChanged();
                    LoaderManager.getInstance(m_activity).restartLoader(DayScheduleListActivity.LOADER_ID_DAY_SCHEDULES, null, m_activity);
                }
            });

            viewHolder.btnMoveDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DayScheduleEntry.updateDaySchedulePosition(m_context, dayScheduleEntry.getId(), dayScheduleEntry.getPosition() +1);
                    //notifyDataSetChanged();
                    LoaderManager.getInstance(m_activity).restartLoader(DayScheduleListActivity.LOADER_ID_DAY_SCHEDULES, null, m_activity);
                }
            });

            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DayScheduleEntry.deleteDaySchedule(m_context, dayScheduleEntry.getId());
//                    notifyDataSetChanged();
                    LoaderManager.getInstance(m_activity).restartLoader(DayScheduleListActivity.LOADER_ID_DAY_SCHEDULES, null, m_activity);
                }
            });
        }
    }

//    public void updateViewHolderVisibilitiesBasedOnDataChanges() {
//        for(int viewHolderIndex = 0; viewHolderIndex <= m_recyclerView.getChildCount(); viewHolderIndex++) {
//            ViewHolder viewHolder = (RVAdapterDaySchedule.ViewHolder) m_recyclerView.findViewHolderForAdapterPosition(viewHolderIndex);
//            if(viewHolder != null) {
//                if(viewHolderIndex == 0) {
//                    if(m_cursor == null || m_cursor.getCount() == 0) {
//                        viewHolder.textViewExerciseName.setVisibility(View.GONE);
//                        viewHolder.btnMoveUp.setVisibility(View.GONE);
//                        viewHolder.btnMoveDown.setVisibility(View.GONE);
//                        viewHolder.btnDelete.setVisibility(View.GONE);
//                    } else {
//                        viewHolder.textViewExerciseName.setVisibility(View.VISIBLE);
//                        viewHolder.btnDelete.setVisibility(View.VISIBLE);
//                    }
//                }
//
//                if(viewHolderIndex == 0) {
//                    viewHolder.btnMoveUp.setVisibility(View.GONE); // hide top most move up button
//                    if(viewHolderIndex < getItemCount() - 1) {
//                        viewHolder.btnMoveDown.setVisibility(View.VISIBLE);
//                    } else {
//                        viewHolder.btnMoveDown.setVisibility(View.GONE);
//                    }
//                } else if(viewHolderIndex == getItemCount() - 1) {
//                    viewHolder.btnMoveUp.setVisibility(View.VISIBLE);
//                    viewHolder.btnMoveDown.setVisibility(View.GONE);
//                } else {
//                    viewHolder.btnMoveUp.setVisibility(View.VISIBLE);
//                    viewHolder.btnMoveDown.setVisibility(View.VISIBLE);
//                }
//
//            } else {
//                Log.d(TAG, "handleStartDrag: null viewHolder found for position: " + viewHolderIndex);
//            }
//        }
//    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if((m_cursor == null) || (m_cursor.getCount() == 0)) {
            return 1; // fib, because we want a single text holder view with a "Drop Here" placeholder
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

        // It's important to get the item count BEFORE replacing the cursor!
        // otherwise, if you call getItemCount later, you will get potentially wrong info.
        int numItems = getItemCount();

        final Cursor oldCursor = m_cursor;
        m_cursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
            // i know this sucks
            m_rvAdapterExerciseEntry.notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems);
        }

        //updateViewHolderVisibilitiesBasedOnDataChanges();
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
