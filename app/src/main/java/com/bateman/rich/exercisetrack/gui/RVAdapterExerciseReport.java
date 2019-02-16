package com.bateman.rich.exercisetrack.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.LogDailyExerciseEntry;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * RecyclerView adapter for the Exercise Report.
 */
public class RVAdapterExerciseReport extends RecyclerView.Adapter<RVAdapterExerciseReport.ViewHolder> {
    private final Context m_context;
    private Cursor m_cursorViewExerciseLogs;
    private final ArrayList<LinkedList<ViewHolderData>> m_arrayListOfLogLinkedLists;
    /**
     * 1-based.
     */
    private int m_currentPageNumber;
    /**
     * Holds the length of the longest list.
     */
    private int m_lastPageNumber;

    int getCurrentPageNumber() {
        return m_currentPageNumber;
    }

    void setCurrentPageNumber(int currentPageNumber) {
        m_currentPageNumber = currentPageNumber;
    }

    int getLastPageNumber() {
        return m_lastPageNumber;
    }

    private static class ViewHolderData {
        public String name;
        int reps;
        public int weight;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView weight;
        TextView reps;

        ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.exc_rpt_entry_exercise_value);
            this.weight = itemView.findViewById(R.id.exc_rpt_entry_exercise_weight);
            this.reps = itemView.findViewById(R.id.exc_rpt_entry_exercise_reps);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(m_context).inflate(R.layout.activity_exercise_report_entry, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(position < 0 || position > m_arrayListOfLogLinkedLists.size()) throw new IllegalArgumentException("i is outside the bounds of the array.");
        LinkedList<ViewHolderData> linkedList = m_arrayListOfLogLinkedLists.get(position);
        int elementToGrab = m_currentPageNumber - 1;
        boolean noMoreRecords = false;
        if(m_currentPageNumber >= linkedList.size()) {
            elementToGrab = linkedList.size() - 1;
            noMoreRecords=true;
        }
        ViewHolderData data = linkedList.get(elementToGrab);

        viewHolder.name.setText(data.name);
        viewHolder.reps.setText(Integer.toString(data.reps));
        viewHolder.weight.setText(Integer.toString(data.weight));

        if(noMoreRecords) {
            viewHolder.name.setBackgroundColor(Color.BLUE);
        } else {
            viewHolder.name.setBackground(null);
        }

    }

    @Override
    public int getItemCount() {
        return m_arrayListOfLogLinkedLists.size();
    }

    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == m_cursorViewExerciseLogs) {
            return null;
        }

        int numItems = getItemCount(); // store old item count

        final Cursor oldCursor = m_cursorViewExerciseLogs;
        m_cursorViewExerciseLogs = newCursor;
        refreshExerciseLogMap();
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems); // Use the old count
        }
        return oldCursor;
    }

    /**
     * Create a new RecyclerView adapter for the exercise report.
     * @param cursorViewExerciseLogs Must be sorted by exercise name ascending.
     */
    RVAdapterExerciseReport(Context context, Cursor cursorViewExerciseLogs) {
        m_context = context;
        m_cursorViewExerciseLogs = cursorViewExerciseLogs;
        m_arrayListOfLogLinkedLists = new ArrayList<>();
        refreshExerciseLogMap();
    }

    /**
     * Build the full hash map of Exercise Linked Lists.
     */
    private void refreshExerciseLogMap() {
        m_arrayListOfLogLinkedLists.clear();
        m_currentPageNumber = 1;

        if (m_cursorViewExerciseLogs != null && m_cursorViewExerciseLogs.getCount() > 0) {

            String targetExerciseName = "";
            m_lastPageNumber = Integer.MIN_VALUE;
            LinkedList<ViewHolderData> targetLinkedListOfLogsForExercise = null;
            int colIndexExerciseName = m_cursorViewExerciseLogs.getColumnIndex(LogDailyExerciseEntry.ContractViewReport.Columns.COL_NAME_EXERCISE_NAME);
            int colIndexWeight = m_cursorViewExerciseLogs.getColumnIndex(LogDailyExerciseEntry.ContractViewReport.Columns.COL_NAME_WEIGHT);
            int colIndexReps = m_cursorViewExerciseLogs.getColumnIndex(LogDailyExerciseEntry.ContractViewReport.Columns.COL_NAME_TOTAL_REPS_DONE);
            m_cursorViewExerciseLogs.moveToFirst();
            do {
                // Grab the current exercise we're on
                String currentExerciseName = m_cursorViewExerciseLogs.getString(colIndexExerciseName);
                if (!targetExerciseName.equals(currentExerciseName)) {
                    // we have a new exercise.
                    targetExerciseName = currentExerciseName;
                    targetLinkedListOfLogsForExercise = new LinkedList<>();
                    m_arrayListOfLogLinkedLists.add(targetLinkedListOfLogsForExercise);
                }
                int weight = m_cursorViewExerciseLogs.getInt(colIndexWeight);
                int reps = m_cursorViewExerciseLogs.getInt(colIndexReps);
                ViewHolderData viewHolderData = new ViewHolderData();
                viewHolderData.name = currentExerciseName;
                viewHolderData.reps = reps;
                viewHolderData.weight = weight;
                assert targetLinkedListOfLogsForExercise != null;
                targetLinkedListOfLogsForExercise.add(viewHolderData);

                // If this list is largest, remember its size.
                if (targetLinkedListOfLogsForExercise.size() > m_lastPageNumber) {
                    m_lastPageNumber = targetLinkedListOfLogsForExercise.size();
                }
            } while (m_cursorViewExerciseLogs.moveToNext());
        }
    }
}
