package com.bateman.rich.exercisetrack.gui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bateman.rich.exercisetrack.R;
import com.bateman.rich.exercisetrack.datamodel.ContentProviderHelper;
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class ExerciseListActivity extends AppCompatActivity
                                  implements RecyclerViewItemClickListener.OnRecyclerClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        RVAdapterExerciseEntry.OnExerciseButtonClickListener
{
    private static final String TAG = "ExerciseListActivity";
    public static final int LOADER_ID = 0;

    private RVAdapterExerciseEntry m_rvAdapterExerciseEntry;

    private Button m_btnAddExercise;
    private EditText m_editTextExercise;
    private RecyclerView m_recyclerViewExercises;
    private CheckBox m_checkBoxIsReminder;

    @Override
    protected void onCreate(Bundle savedInstanceData) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceData);
        setContentView(R.layout.exercise_list);

        setupUI();
        setupRecyclerView();
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);

        Log.d(TAG, "onCreate: end");
    }

    private void setupUI() {
        m_btnAddExercise = findViewById(R.id.el_btn_add_exercise);
        m_btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String exerciseText = m_editTextExercise.getText().toString().trim();
                if(exerciseText.length() > 0) {
                    boolean alreadyExists = m_rvAdapterExerciseEntry.checkForRedundantExerciseName(exerciseText);
                    if (!alreadyExists) {

                        ContentValues values = new ContentValues();
                        values.put(ExerciseEntry.Contract.Columns.COL_NAME_NAME, exerciseText);
                        values.put(ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER, m_checkBoxIsReminder.isChecked());
                        getContentResolver().insert(ExerciseEntry.Contract.CONTENT_URI, values);

                        // Clear input fields
                        m_editTextExercise.setText("");
                        m_checkBoxIsReminder.setChecked(false);
                    } else {
                        Snackbar.make(view, exerciseText + " already exists.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(view, "Please enter an exercise name.", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        m_editTextExercise = findViewById(R.id.el_edittext_entry);
        m_recyclerViewExercises = findViewById(R.id.el_recyclerview_exercise_entries);
        m_checkBoxIsReminder = findViewById(R.id.el_cb_isdailyreminder);
    }

     private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: start");

        m_recyclerViewExercises.setLayoutManager(new LinearLayoutManager(this));
        // This is a mistake.  The only clicks I care about are on the button in the recycler view ViewHolder... and I can hook that up directly.
         // I have no indentions of swiping or anything like that.
        //m_recyclerViewExercises.addOnItemTouchListener(new RecyclerViewItemClickListener(this, m_recyclerViewExercises, this));

        m_rvAdapterExerciseEntry = new RVAdapterExerciseEntry(null, this);
        m_recyclerViewExercises.setAdapter(m_rvAdapterExerciseEntry);

        Log.d(TAG, "setupRecyclerView: end");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: start");
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: start");
    }

    @Override
    public void onItemSwipeRight(View view) {
        Log.d(TAG, "onItemSwipeRight: start");
    }

    @Override
    public void onItemSwipeLeft(View view) {
        Log.d(TAG, "onItemSwipeLeft: start");
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        Log.d(TAG, "onCreateLoader: start with id: " + id);

        String[] projection = ExerciseEntry.Contract.getProjectionFull();
        String sortOrder = ExerciseEntry.Contract.getSortOrderStandard();

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this,
                        ExerciseEntry.Contract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id" + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: start");
        m_rvAdapterExerciseEntry.swapCursor(data);
        int count = m_rvAdapterExerciseEntry.getItemCount();
        Log.d(TAG, "onLoadFinished: end.  Count is: " + count);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: start");
        m_rvAdapterExerciseEntry.swapCursor(null);
    }

    @Override
    public void onDeleteClick(@NonNull ExerciseEntry entry) {
        long exerciseId = entry.getId();
        getContentResolver().delete(ContentProviderHelper.buildUriFromId(ExerciseEntry.Contract.CONTENT_URI, exerciseId), null, null);
    }
}