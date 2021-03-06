package com.bateman.rich.exercisetrack.gui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;

import java.security.InvalidParameterException;

public class DayScheduleListActivity extends AppCompatActivity
                                     implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "DayScheduleListActivity";
    public static final int LOADER_ID_AVAILABLE_EXERCISES = 1;
    public static final int LOADER_ID_DAY_SCHEDULES = 2;

    private RVAdapterExerciseEntry m_rvAdapterExerciseEntry;

    private RVAdapterDaySchedule m_rvAdapterDaySchedule;

    
    @Override
    protected void onCreate(Bundle savedInstanceData) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceData);
        setContentView(R.layout.schedule_maintenance);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupRecyclerViews();

        LoaderManager.getInstance(this).initLoader(LOADER_ID_AVAILABLE_EXERCISES, null, this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID_DAY_SCHEDULES, null, this);
        Log.d(TAG, "onCreate: end");
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        Log.d(TAG, "onCreateLoader: start with id: " + id);

        switch (id) {
            case LOADER_ID_AVAILABLE_EXERCISES:
                String[] projection = ExerciseEntry.Contract.getProjectionFull();
                String sortOrder = ExerciseEntry.Contract.getSortOrderStandard();
                return new CursorLoader(this,
                        ExerciseEntry.Contract.CONTENT_URI,
                        projection,
                        ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER + "=?",
                        new String[]{"0"},
                        sortOrder);
            case LOADER_ID_DAY_SCHEDULES:
                return new CursorLoader(this,
                        DayScheduleEntry.ContractViewDaySchedules.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id" + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: start");
        int loaderId = loader.getId();
        switch(loaderId) {
            case LOADER_ID_AVAILABLE_EXERCISES:
                m_rvAdapterExerciseEntry.swapCursor(data);
                Log.d(TAG, "onLoadFinished: there are " + m_rvAdapterExerciseEntry.getItemCount() + " exercise entries.");
                break;
            case LOADER_ID_DAY_SCHEDULES:
                Cursor oldCursor = m_rvAdapterDaySchedule.swapCursor(data);
                if(oldCursor != null) oldCursor.close();
                Log.d(TAG, "onLoadFinished: there are " + m_rvAdapterDaySchedule.getItemCount() + " day schedules.");
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: start");
        int loaderId = loader.getId();
        switch(loaderId) {
            case LOADER_ID_AVAILABLE_EXERCISES:
                Cursor oldCursor = m_rvAdapterExerciseEntry.swapCursor(null);
                if(oldCursor != null) oldCursor.close();
                Log.d(TAG, "onLoaderReset: there are " + m_rvAdapterExerciseEntry.getItemCount() + " exercise entries.");
                break;
            case LOADER_ID_DAY_SCHEDULES:
                Cursor oldCursor2 = m_rvAdapterDaySchedule.swapCursor(null);
                if(oldCursor2 != null) oldCursor2.close();
                Log.d(TAG, "onLoaderReset: there are " + m_rvAdapterDaySchedule.getItemCount() + " day schedules.");
                break;
        }
    }


    private void setupRecyclerViews() {
        RecyclerView recyclerViewAvailableExercises = findViewById(R.id.sm_recyclerview_exercises);
        RecyclerView recyclerViewScheduledDays = findViewById(R.id.sm_recyclerview_scheduleboxes);

        recyclerViewAvailableExercises.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewScheduledDays.setLayoutManager(new LinearLayoutManager(this));

        m_rvAdapterExerciseEntry = new RVAdapterExerciseEntry(RVAdapterExerciseEntry.Mode.DAY_SCHEDULE, this,null);
        m_rvAdapterDaySchedule = new RVAdapterDaySchedule(this,null, this);
        m_rvAdapterDaySchedule.setRvAdapterExerciseEntry(m_rvAdapterExerciseEntry);

        DayScheduleDragManager dayScheduleDragManager = new DayScheduleDragManager(this, m_rvAdapterDaySchedule);
        m_rvAdapterExerciseEntry.setDayScheduleDragManager(dayScheduleDragManager);

        m_rvAdapterDaySchedule.setDayScheduleDragManager(dayScheduleDragManager);

        recyclerViewAvailableExercises.setAdapter(m_rvAdapterExerciseEntry);
        recyclerViewScheduledDays.setAdapter(m_rvAdapterDaySchedule);
    }
}
