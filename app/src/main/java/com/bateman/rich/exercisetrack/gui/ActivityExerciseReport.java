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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.LogDailyExerciseEntry;

import java.security.InvalidParameterException;

public class ActivityExerciseReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ActivityExerciseReport";
    private static final int LOADER_ID_MAIN_CURSOR = 1;
    private RVAdapterExerciseReport m_rvAdapterExerciseReport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_report);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupButtonClicks();
        setupRecyclerView();
        LoaderManager.getInstance(this).initLoader(LOADER_ID_MAIN_CURSOR, null, this);
    }

    private void setupButtonClicks() {
        Button btnPrev = findViewById(R.id.exc_rpt_btn_prev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = m_rvAdapterExerciseReport.getCurrentPageNumber();
                if(currentPage > 1) {
                    m_rvAdapterExerciseReport.setCurrentPageNumber(currentPage - 1);
                    m_rvAdapterExerciseReport.notifyDataSetChanged();
                    updateLabelXofY();
                }
            }
        });

        Button btnNext = findViewById(R.id.exc_rpt_btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = m_rvAdapterExerciseReport.getCurrentPageNumber();
                int lastPage = m_rvAdapterExerciseReport.getLastPageNumber();
                if(currentPage < lastPage) {
                    m_rvAdapterExerciseReport.setCurrentPageNumber(currentPage + 1);
                    m_rvAdapterExerciseReport.notifyDataSetChanged();
                    updateLabelXofY();
                }
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView rv =findViewById(R.id.exc_rpt_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        if(m_rvAdapterExerciseReport == null) {
            m_rvAdapterExerciseReport=new RVAdapterExerciseReport(this, null);
        }
        rv.setAdapter(m_rvAdapterExerciseReport);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_MAIN_CURSOR:
                return new CursorLoader(this,
                        LogDailyExerciseEntry.ContractViewReport.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished");
        Cursor oldCursor = m_rvAdapterExerciseReport.swapCursor(data);
        if(oldCursor != null) {
            oldCursor.close();
        }
        int count = m_rvAdapterExerciseReport.getItemCount();
        Log.d(TAG, "onLoadFinished: count is " + count);

        updateLabelXofY();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        Cursor oldCursor = m_rvAdapterExerciseReport.swapCursor(null);
        if(oldCursor != null) {
            oldCursor.close();
        }
    }

    private void updateLabelXofY() {
        TextView textViewXofY = findViewById(R.id.exc_rpt_x_of_y);
        String pageXOfYtext = getString(R.string.page_x_of_y, m_rvAdapterExerciseReport.getCurrentPageNumber(), m_rvAdapterExerciseReport.getLastPageNumber());
        textViewXofY.setText(pageXOfYtext);
    }
}
