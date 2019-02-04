package com.bateman.rich.exercisetrack;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bateman.rich.exercisetrack.datamodel.DayScheduleEntry;
import com.bateman.rich.exercisetrack.datamodel.ExerciseAppDBSetting;
import com.bateman.rich.exercisetrack.datamodel.ExerciseEntry;
import com.bateman.rich.exercisetrack.datamodel.LogDailyExerciseEntry;
import com.bateman.rich.exercisetrack.datamodel.LogEntry;
import com.bateman.rich.exercisetrack.datamodel.TestData;
import com.bateman.rich.exercisetrack.gui.AppDialog;
import com.bateman.rich.exercisetrack.gui.DayScheduleListActivity;
import com.bateman.rich.exercisetrack.gui.ExerciseListActivity;
import com.bateman.rich.exercisetrack.gui.RVAdapterCurrentDayExercise;
import com.bateman.rich.exercisetrack.gui.RVAdapterDaySchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
    implements AppDialog.DialogEvents,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";
    private static final int DIALOG_ID_CLEAR_EXERCISE_HISTORY=1001;
    private static final int DIALOG_ID_PURGE_SYSTEM_DATA=1002;

    private AlertDialog m_dialog = null;         // module scope because we need to dismiss it in onStop
    // e.g. when orientation changes) to avoid memory leaks.

    private TextView m_textViewInstructions;
    private Button m_btnStartStop;
    private Button m_btnRest;
    private TextView m_textViewSecondsToRest;
    private View m_currentExerciseLayout;

    private TextView m_textViewStartTimeLabel;
    private TextView m_textViewStartTimeDisplay;
    private TextView m_textViewEnterNumReps;
    private TextView m_textViewWeightLabel;
    private TextView m_textViewExerciseName;
    private TextView m_textViewWeightInput;
    private TextView m_textNewSetRepsInput;
    private Button m_btnCompleteSet;
    private SeekBar m_seekBarDifficulty;
    private TextView m_textViewDifficultyOutput;

    private static final int LOADER_ID = 0;
    private RVAdapterCurrentDayExercise m_rvAdapterCurrentDayExercise;

    private long m_currentDayScheduleId;
    private LogDailyExerciseEntry m_currentLogDailyExerciseEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupGui();
        hookupButtonEvents();

        determineCurrentExercise();
        flashDailyReminders();
    }

    private void hookupButtonEvents() {
        m_btnCompleteSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        m_btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_currentLogDailyExerciseEntry.getStartDateTime() == null) {
                    m_currentLogDailyExerciseEntry.setStartDateTime(new Date());
                    GregorianCalendar gCal = new GregorianCalendar();
                    //gCal.setTimeZone(TimeZone.getDefault());
                    gCal.setTime(m_currentLogDailyExerciseEntry.getStartDateTime());
                    m_textViewStartTimeLabel.setVisibility(View.VISIBLE);
                    m_textViewStartTimeDisplay.setVisibility(View.VISIBLE);
                    String textTime = getHourString(gCal);
                    m_textViewStartTimeDisplay.setText(textTime);
                    m_btnStartStop.setText("Complete Exercise");
                } else {
                    completeExercise();
                }
            }
        });

        m_btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupRestTimer();
            }
        });
    }

    private void setupRestTimer() {
        Context appContext = this;
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                m_textViewSecondsToRest.setText("hello");
                timer.cancel();
            }
        };

        int delayInMs = 0;
        int periodInMs = 1000;
        // "schedule" only does it ONE time.
        //timer.schedule(doAsynchronousTask, delayInMs);
        timer.scheduleAtFixedRate(doAsynchronousTask, delayInMs, periodInMs);

    }

    private String getHourString(GregorianCalendar gCal) {
        int hour = gCal.get(GregorianCalendar.HOUR);
        int minute = gCal.get(GregorianCalendar.MINUTE);
        int amPm = gCal.get(GregorianCalendar.AM_PM);
        String textTime = String.format("%2d:%2d%s", hour, minute, (amPm == GregorianCalendar.AM ? "AM":"PM")).replace(' ', '0');
        return textTime;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(BuildConfig.DEBUG) {
            MenuItem generate = menu.findItem(R.id.menu_generate_test_data);
            generate.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuId = item.getItemId();

        switch(menuId) {
            case R.id.menu_exercise_list:
                startActivity(new Intent(this, ExerciseListActivity.class));
                break;
            case R.id.menu_clear_exercise_history:
                handleMenuClearExerciseHistory();
                break;
            case R.id.menu_purge_system_data:
                handleMenuPurgeSystemData();
                break;
            case R.id.menu_about:
                handleMenuShowAboutDialog();
                break;
            case R.id.menu_email_report:
                handleMenuEmailReport();
                break;
            case R.id.menu_generate_test_data:
                handleMenuGenerateTestData();
                break;
            case R.id.menu_schedule_list:
                startActivity(new Intent(this, DayScheduleListActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleMenuShowAboutDialog() {
        @SuppressLint("InflateParams") View messageView = getLayoutInflater().inflate(R.layout.common_about_app, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);

        builder.setView(messageView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    if(m_dialog != null && m_dialog.isShowing()) {
                    m_dialog.dismiss();
                }
            }
        });

        m_dialog = builder.create();
        m_dialog.setCanceledOnTouchOutside(true);

        TextView tv = messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        TextView about_url = messageView.findViewById(R.id.about_url);
        if(about_url != null) {
            about_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String s = ((TextView) v).getText().toString();
                    intent.setData(Uri.parse(s));
                    try {
                        startActivity(intent);
                    } catch(ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No browser application found, cannot visit world-wide web", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        m_dialog.show();
    }

    private void handleMenuGenerateTestData() {
        TestData.generateTestData(getContentResolver());
    }

    private void handleMenuEmailReport() {
        String fileDataString = "";

        Cursor cursor = getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI, null, null, null, null);
        if(cursor != null & cursor.moveToFirst()) {
            fileDataString += "<exercise entries>\r\n";
            do {
                ExerciseEntry entry = new ExerciseEntry(cursor);
                fileDataString += entry.toString();
            } while(cursor.moveToNext());
            cursor.close();
        }

        cursor = getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null, null, null, null);
        if(cursor != null & cursor.moveToFirst()) {
            fileDataString += "<day schedule entries>\r\n";
            do {
                DayScheduleEntry entry = new DayScheduleEntry(cursor);
                fileDataString += entry.toString();
            } while(cursor.moveToNext());
            cursor.close();
        }

        cursor = getContentResolver().query(LogEntry.Contract.CONTENT_URI, null, null, null, null);
        if(cursor != null & cursor.moveToFirst()) {
            fileDataString += "<log entries>\r\n";
            do {
                LogEntry entry = new LogEntry(cursor);
                fileDataString += entry.toString();
            } while(cursor.moveToNext());
            cursor.close();
        }

        cursor = getContentResolver().query(LogDailyExerciseEntry.Contract.CONTENT_URI, null, null, null, null);
        if(cursor != null & cursor.moveToFirst()) {
            fileDataString += "<exercise entries>\r\n";
            do {
                LogDailyExerciseEntry entry = new LogDailyExerciseEntry(cursor);
                fileDataString += entry.toString();
            } while(cursor.moveToNext());
            cursor.close();
        }

        Uri fileUri = FileWriter.writeFile(this, "ExerciseAppData.txt", fileDataString);
        EmailSender.foo(this, "Exercise App Data", fileUri);
    }

    private void handleMenuClearExerciseHistory() {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CLEAR_EXERCISE_HISTORY);
        args.putString(AppDialog.DIALOG_MESSAGE, "Are you sure you want to clear all exercise history?");
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void handleMenuPurgeSystemData() {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_PURGE_SYSTEM_DATA);
        args.putString(AppDialog.DIALOG_MESSAGE, "Are you sure you want to complete purge all app data?");
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        switch(dialogId){
            case DIALOG_ID_CLEAR_EXERCISE_HISTORY:
                getContentResolver().delete(LogEntry.Contract.CONTENT_URI, null, null);
                break;
            case DIALOG_ID_PURGE_SYSTEM_DATA:
                getContentResolver().delete(ExerciseEntry.Contract.CONTENT_URI, null, null);
                // Triggers should automatically clear these.
    //            getContentResolver().delete(DayScheduleEntry.Contract.CONTENT_URI, null, null);
//                getContentResolver().delete(LogEntry.Contract.CONTENT_URI, null, null);
//                getContentResolver().delete(LogDailyExerciseEntry.Contract.CONTENT_URI, null, null);
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        switch(dialogId){
            case DIALOG_ID_CLEAR_EXERCISE_HISTORY:
                // No action required.
                break;
            case DIALOG_ID_PURGE_SYSTEM_DATA:
                // No action required.
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        switch(dialogId){
            case DIALOG_ID_CLEAR_EXERCISE_HISTORY:
                // No action required.
                break;
            case DIALOG_ID_PURGE_SYSTEM_DATA:
                // No action required.
                break;
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void setupGui() {
        m_textViewInstructions = findViewById(R.id.cd_tv_instructions_to_start);
        m_btnStartStop = findViewById(R.id.cd_btn_start_stop);
        m_btnRest = findViewById(R.id.cd_btn_rest);
        m_textViewSecondsToRest = findViewById(R.id.cd_ted_restseconds);
        m_textViewStartTimeDisplay = findViewById(R.id.cd_tv_startime_value);
        m_textViewStartTimeLabel = findViewById(R.id.cd_tv_startime_label);
        m_currentExerciseLayout = findViewById(R.id.cd_main_constraint_layout);

        m_textViewStartTimeLabel.setVisibility(View.INVISIBLE);
        m_textViewStartTimeDisplay.setVisibility(View.INVISIBLE);

//        m_textViewEnterNumReps = m_currentExerciseLayout.findViewById(R.id.cdblock_txtview_enter_num_reps);
//        m_textViewWeightLabel = m_currentExerciseLayout.findViewById(R.id.cdblock_txtview_weightlabel);
        m_textViewExerciseName = m_currentExerciseLayout.findViewById(R.id.cdblock_txtview_exercise_name);
//        m_textViewWeightInput = m_currentExerciseLayout.findViewById(R.id.cdblock_ted_weight);
//        m_textNewSetRepsInput = m_currentExerciseLayout.findViewById(R.id.cdblock_ted_newsetreps);
        m_btnCompleteSet = m_currentExerciseLayout.findViewById(R.id.cdblock_btn_newsetreps);
//        m_seekBarDifficulty = m_currentExerciseLayout.findViewById(R.id.cdblock_seekbar_difficulty);
    }

    private void determineCurrentExercise() {
        Cursor cursor = this.getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0) {
            m_textViewInstructions.setVisibility(View.GONE);
            m_textViewSecondsToRest.setVisibility(View.VISIBLE);
            m_btnRest.setVisibility(View.VISIBLE);
            m_btnStartStop.setVisibility(View.VISIBLE);
            m_currentExerciseLayout.setVisibility(View.VISIBLE);

            long dayScheduleId = ExerciseAppDBSetting.getCurrentDayScheduleId(this);

            // Attempt to find the day schedule with this id.  If you cannot find it, find the first day schedule (by position) and update the setting.
            Cursor matchingDaySchedule = getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null,
                    DayScheduleEntry.Contract.Columns.COL_NAME_ID +"=?", new String[]{Long.toString(dayScheduleId)}, null);
            if(matchingDaySchedule == null || matchingDaySchedule.getCount() == 0) {
                matchingDaySchedule = getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null,
                        null, null, DayScheduleEntry.Contract.Columns.COL_NAME_POSITION);
            }
            matchingDaySchedule.moveToFirst();
            m_currentDayScheduleId = matchingDaySchedule.getLong(matchingDaySchedule.getColumnIndex(DayScheduleEntry.Contract.Columns.COL_NAME_ID));
            matchingDaySchedule.close();

            updateGuiWithCurrentExercise();

        } else {
            m_textViewInstructions.setVisibility(View.VISIBLE);
            m_textViewSecondsToRest.setVisibility(View.GONE);
            m_btnRest.setVisibility(View.GONE);
            m_btnStartStop.setVisibility(View.GONE);
            m_currentExerciseLayout.setVisibility(View.GONE);
        }
    }

    private void populateGuiFromLogDailyExerciseEntry() {
        ExerciseEntry exerciseEntry = m_currentLogDailyExerciseEntry.getExerciseEntry();
        m_textViewExerciseName.setText(exerciseEntry.getName());
    }

    private void flashDailyReminders() {
        Cursor cursorReminders = getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI, null,
                ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER +"=?", new String[]{"1"}, null);
        if(cursorReminders!= null) {
            cursorReminders.moveToFirst();
            do {
                ExerciseEntry dailyReminder = new ExerciseEntry(cursorReminders);
                String reminderText = dailyReminder.getName();
                Snackbar.make(m_btnStartStop, reminderText, Snackbar.LENGTH_LONG).show();
            } while(cursorReminders.moveToNext());
            cursorReminders.close();
        }
    }

    /**
     * Called when the user signals they are done with the current exercise.
     */
    private void completeExercise() {
        m_currentLogDailyExerciseEntry.setEndDateTime(new Date());
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(m_currentLogDailyExerciseEntry.getEndDateTime());

        String textTime = getHourString(gCal);
        Snackbar.make(m_btnStartStop, "Exercise completed at: " + textTime, Snackbar.LENGTH_LONG).show();

        m_btnStartStop.setText("Start Exercise");

        long currentDayScheduleId = ExerciseAppDBSetting.getCurrentDayScheduleId(this);

        boolean hasNextExercise = false;

        Cursor daySchedules = getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null, null, null, null);
        if(daySchedules != null) {
            daySchedules.moveToFirst();
            do {
                DayScheduleEntry dse = new DayScheduleEntry(daySchedules);
                if(dse.getId() == currentDayScheduleId) {
                    if(daySchedules.moveToNext()) {
                        DayScheduleEntry nextEntry = new DayScheduleEntry(daySchedules);
                        if(nextEntry.getExerciseEntryId() != RVAdapterDaySchedule.DAY_SEPARATOR_ID) {
                            hasNextExercise = true;
                            ExerciseAppDBSetting.setCurrentDayScheduleId(this, nextEntry.getId());
                        }
                    }
                    break;
                }
            } while(daySchedules.moveToNext());
        }

        if(!hasNextExercise) {
            Snackbar.make(m_btnStartStop, "End of Routine!  Congrats!", Snackbar.LENGTH_LONG).show();
            prepareNextExercise(daySchedules);
        }

        daySchedules.close();
    }

    private void prepareNextExercise(Cursor daySchedules) {
        boolean exerciseFound = false;
        if(!daySchedules.moveToNext()) {
            daySchedules.moveToFirst();
        }
        do {
            DayScheduleEntry entry = new DayScheduleEntry(daySchedules);
            if(entry.getExerciseEntryId() != RVAdapterDaySchedule.DAY_SEPARATOR_ID) {
                m_currentDayScheduleId = daySchedules.getLong(daySchedules.getColumnIndex(DayScheduleEntry.Contract.Columns.COL_NAME_ID));
                exerciseFound=true;
            }
        } while(!exerciseFound);

        updateGuiWithCurrentExercise();
    }

    private void updateGuiWithCurrentExercise() {
        Cursor cursorUnfinishedExercise = getContentResolver().query(LogDailyExerciseEntry.Contract.CONTENT_URI,
                null, LogDailyExerciseEntry.Contract.Columns.COL_NAME_END_DATETIME + " is null",
                null, null);
        if(cursorUnfinishedExercise != null && cursorUnfinishedExercise.getCount() > 0) {
            cursorUnfinishedExercise.moveToFirst();
            m_currentLogDailyExerciseEntry = new LogDailyExerciseEntry(cursorUnfinishedExercise);
            cursorUnfinishedExercise.close();
        } else {
            m_currentLogDailyExerciseEntry = LogDailyExerciseEntry.fromDayScheduleId(this, m_currentDayScheduleId);
        }

        populateGuiFromLogDailyExerciseEntry();
    }
}
