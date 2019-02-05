package com.bateman.rich.exercisetrack;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.GregorianCalendar;
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

    private final Context m_context = this;

    private TextView m_textViewInstructions;
    private Button m_btnStartStop;
    private Button m_btnRest;
    private TextView m_textViewSecondsToRest;
    private View m_currentExerciseLayout;

    private TextView m_textViewStartTimeLabel;
    private TextView m_textViewStartTimeDisplay;
    private TextView m_textViewEnterNumRepsLabel;
    private TextView m_textViewWeightLabel;
    private TextView m_textViewExerciseName;
    private TextView m_textViewWeightInput;
    private TextView m_textNewSetRepsInput;
    private Button m_btnCompleteSet;
    private SeekBar m_seekBarDifficulty;
    private TextView m_textViewDifficultyOutput;
    private TextView m_textViewTotalRepsDoneValue;

    private static final int LOADER_ID = 0;
    private RVAdapterCurrentDayExercise m_rvAdapterCurrentDayExercise;

    private long m_currentDayScheduleId;
    private LogDailyExerciseEntry m_currentLogDailyExerciseEntry;

    private boolean m_resting;
    private int m_secondsToRest;
    private Timer m_timerRest;

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
        m_seekBarDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_currentLogDailyExerciseEntry.setDifficulty(progress);
                if(fromUser) {
                    m_currentLogDailyExerciseEntry.save(m_context);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        m_btnCompleteSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_currentLogDailyExerciseEntry.getStartDateTime() == null) {
                    startExercise();
                }

                int numRepsToAdd = Integer.parseInt(m_textNewSetRepsInput.getText().toString());
                m_currentLogDailyExerciseEntry.addToTotalRepsDone(numRepsToAdd);
                m_currentLogDailyExerciseEntry.save(m_context);

                m_textViewTotalRepsDoneValue.setText(Integer.toString(m_currentLogDailyExerciseEntry.getTotalRepsDone()));
            }
        });

        m_btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_currentLogDailyExerciseEntry.getStartDateTime() == null) {
                    startExercise();
                    m_currentLogDailyExerciseEntry.save(m_context);
                } else {
                    completeExercise();
                }
            }
        });

        m_btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_resting) {
                    m_resting = true;
                    setupRestTimer();
                } else {
                    m_resting=false;
                    cancelRestTimer();
                    m_textViewSecondsToRest.setText("120");
                }

            }
        });
    }

    private void startExercise() {
        flashDailyReminders();
        m_currentLogDailyExerciseEntry.setStartDateTime(new Date());
        GregorianCalendar gCal = new GregorianCalendar();
        //gCal.setTimeZone(TimeZone.getDefault());
        gCal.setTime(m_currentLogDailyExerciseEntry.getStartDateTime());
        m_textViewStartTimeLabel.setVisibility(View.VISIBLE);
        m_textViewStartTimeDisplay.setVisibility(View.VISIBLE);
        String textTime = getHourString(gCal);
        m_textViewStartTimeDisplay.setText(textTime);
        m_btnStartStop.setText("Complete Exercise");
    }

    private void cancelRestTimer() {
        m_timerRest.cancel();
        m_resting=false;
        m_btnRest.setText("Rest");
        m_textViewSecondsToRest.setText("120");
    }

    private void vibratePhone() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        // That version code is O for Oreo, not ZERO.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Default amplitude is based on the device.
            // Otherwise you can specify a number from 1 and 255
            v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(1500);
        }
    }

    private void setupRestTimer() {
        m_timerRest = new Timer();
        Context appContext = this;
        final Handler handler = new Handler();
        m_btnRest.setText("Cancel");

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int secondsToRest = Integer.parseInt(m_textViewSecondsToRest.getText().toString());
                        secondsToRest -= 60;
                        m_textViewSecondsToRest.setText(Integer.toString(secondsToRest));
                        if(secondsToRest <= 0) {
                            vibratePhone();
                            cancelRestTimer();
                        }
                    }
                });
            }
        };

        int delayInMs = 0;
        int periodInMs = 1000; // we want the event to fire every second.
        // "schedule" only fires the task ONE time.
        //timer.schedule(doAsynchronousTask, delayInMs);
        // "scheduleAtFixedRate" will fire multiple times.
        m_timerRest.scheduleAtFixedRate(doAsynchronousTask, delayInMs, periodInMs);

    }

    private String getHourString(GregorianCalendar gCal) {
        int hour = gCal.get(GregorianCalendar.HOUR);
        if(hour == 0) hour = 12; // 12 is represented as 0, not 12.  Great!
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
        try {


            String fileDataString = "";

            Cursor cursor = getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI, null, null, null, null);
            if (cursor != null & cursor.moveToFirst()) {
                fileDataString += "<exercise entries>\r\n";
                do {
                    ExerciseEntry entry = new ExerciseEntry(cursor);
                    fileDataString += entry.toString();
                } while (cursor.moveToNext());
                cursor.close();
            }

            cursor = getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null, null, null, null);
            if (cursor != null & cursor.moveToFirst()) {
                fileDataString += "<day schedule entries>\r\n";
                do {
                    DayScheduleEntry entry = new DayScheduleEntry(cursor);
                    fileDataString += entry.toString();
                } while (cursor.moveToNext());
                cursor.close();
            }

            cursor = getContentResolver().query(LogDailyExerciseEntry.Contract.CONTENT_URI, null, null, null, null);
            if (cursor != null & cursor.moveToFirst()) {
                fileDataString += "<log entries>\r\n";
                do {
                    LogDailyExerciseEntry entry = new LogDailyExerciseEntry(cursor);
                    fileDataString += entry.toString();
                } while (cursor.moveToNext());
                cursor.close();
            }

            //String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String baseDir = Environment.getExternalStorageDirectory().toString();
            // This "exerciseapp_data" will be visible to user on their phone.
            File myAppDir = new File(baseDir + "/exerciseapp_data");
            Log.d(TAG, "handleMenuEmailReport: does myAppDir " + myAppDir + " Exist?: " + myAppDir.exists());
            if(!myAppDir.exists()) myAppDir.mkdirs();
            Log.d(TAG, "handleMenuEmailReport: does myAppDir " + myAppDir + " Exist?: " + myAppDir.exists());

            String filename = "ExerciseAppData.txt";
            File writtenFile = new File(myAppDir, filename);
            if(writtenFile.exists()) writtenFile.delete();

            try {
                FileOutputStream out = new FileOutputStream(writtenFile);
                out.write(fileDataString.getBytes());
                out.flush();
                out.close();
            } catch(Exception exc) {
                Log.e(TAG, "handleMenuEmailReport:", exc);
            }

            Log.d(TAG, "handleMenuEmailReport: does file exist?: " + writtenFile.exists());

            //File writtenFileDirectory = new File(Environment.getExternalStorageDirectory(), "exerciseApp");
//            if (!writtenFileDirectory.exists()) {
//                Log.d(TAG, "handleMenuEmailReport: writtenFileDirectory does not exist.  Creating.  " + writtenFileDirectory);
//                writtenFileDirectory.mkdirs();
//                if (!writtenFileDirectory.exists()) {
//                    Log.d(TAG, "handleMenuEmailReport: writtenFileDirectory STILL does not exist!");
//                }
//            }
            //File writtenFile = new File(writtenFileDirectory, filename);
            //File writtenFile = new File(baseDir + File.separator + filename);
//            writtenFile.getParentFile().mkdirs();
//            if(!writtenFile.exists()) {
//                writtenFile.createNewFile();
//            }
            //FileOutputStream stream = new FileOutputStream(writtenFile);
            //FileWriter fw = new FileWriter(writtenFile);
//            FileWriter fw = new FileWriter(writtenFile);
//            fw.append(fileDataString);
//            fw.flush();
//            fw.close();

            //FileWriterBad fileWriter = new FileWriterBad();

            // WOOPS.  this is my own helper class.  Not java.io.FileWriterBad!!!
            //Uri fileUri = FileWriterBad.writeFile(this, "ExerciseAppData.txt", fileDataString);

            //Uri fileUri = Uri.parse(writtenFile.getAbsolutePath());
            Uri fileUri = Uri.parse("file://" + writtenFile);
            Log.d(TAG, "handleMenuEmailReport: file uri to e-mail: " + fileUri);
            EmailSender.launchEmailIntentWithAttachment(this, "Exercise App Data",fileUri);
//            Log.d(TAG, "handleMenuEmailReport: fileUri of written file is: " + fileUri);
//            EmailSender.launchEmailIntentWithAttachment(this, "Exercise App Data", fileUri);
        } catch (Exception exc) {
            Log.e(TAG, "handleMenuEmailReport: ", exc);
        }
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

        m_textViewEnterNumRepsLabel = m_currentExerciseLayout.findViewById(R.id.cdblock_txtview_enter_num_reps);
        m_textViewWeightLabel = m_currentExerciseLayout.findViewById(R.id.cdblock_txtview_weightlabel);
        m_textViewExerciseName = m_currentExerciseLayout.findViewById(R.id.cdblock_txtview_exercise_name);
        m_textViewWeightInput = m_currentExerciseLayout.findViewById(R.id.cdblock_ted_weight);
        m_textNewSetRepsInput = m_currentExerciseLayout.findViewById(R.id.cdblock_ted_newsetreps);
        m_btnCompleteSet = m_currentExerciseLayout.findViewById(R.id.cdblock_btn_newsetreps);
        m_seekBarDifficulty = m_currentExerciseLayout.findViewById(R.id.cdblock_seekbar_difficulty);
        m_textViewTotalRepsDoneValue = m_currentExerciseLayout.findViewById(R.id.cdblock_txtview_totalreps_amount);
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
            ExerciseAppDBSetting.setCurrentDayScheduleId(this, m_currentDayScheduleId);
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
        ExerciseEntry exerciseEntry = m_currentLogDailyExerciseEntry.getExerciseEntry(this);
        m_textViewExerciseName.setText(exerciseEntry.getName());

        m_textViewWeightInput.setText(Integer.toString(m_currentLogDailyExerciseEntry.getWeight()));
        m_textViewTotalRepsDoneValue.setText(Integer.toString(m_currentLogDailyExerciseEntry.getTotalRepsDone()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            m_seekBarDifficulty.setProgress(m_currentLogDailyExerciseEntry.getDifficulty(), true);
        } else {
            m_seekBarDifficulty.setProgress(m_currentLogDailyExerciseEntry.getDifficulty());
        }
    }

    private void flashDailyReminders() {
        // Build up a string containing all reminder messages.  I don't think flashing one reminder after another
        // is straightforward (or even desirable)
        StringBuilder reminderTextSB = new StringBuilder();
        Cursor cursorReminders = getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI, null,
                ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER +"=?", new String[]{"1"}, null);
        if(cursorReminders != null) {
            if(cursorReminders.getCount() > 0) {
                cursorReminders.moveToFirst();
                do {
                    ExerciseEntry dailyReminder = new ExerciseEntry(cursorReminders);
                    String reminderText = dailyReminder.getName();
                    reminderTextSB.append(reminderText);
                    reminderTextSB.append("\r\n");
                } while (cursorReminders.moveToNext());

                Snackbar.make(m_btnStartStop, reminderTextSB, Snackbar.LENGTH_INDEFINITE).show();
            }
            cursorReminders.close();
        }
    }

    /**
     * Called when the user signals they are done with the current exercise.
     */
    private void completeExercise() {
        // Wrap up the current exercise.  Set the end date, and save it.
        m_currentLogDailyExerciseEntry.setEndDateTime(new Date());
        m_currentLogDailyExerciseEntry.save(m_context);
        m_textViewStartTimeDisplay.setText("");

        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(m_currentLogDailyExerciseEntry.getEndDateTime());
        String textTime = getHourString(gCal);
        // You can only show one snackbar at a time!  So this one gets hidden by one at end of function.
        //Snackbar.make(m_btnStartStop, "Exercise completed at: " + textTime, Snackbar.LENGTH_LONG).show();

        m_btnStartStop.setText("Start Exercise");

        // Get the day schedule id for what we JUST wrapped up.
        long currentDayScheduleId = ExerciseAppDBSetting.getCurrentDayScheduleId(this);
        Log.d(TAG, "completeExercise: We just completed the exercise for day schedule id: " + currentDayScheduleId);

        boolean hasNextExerciseInRoutine = false;

        // The goal is to get the next exercise that is STILL IN THIS ROUTINE!
        // AND TO ADVANCE THE CURSOR THERE
        Cursor cursorDaySchedules = getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null, null, null,
                DayScheduleEntry.Contract.Columns.COL_NAME_POSITION); // must sort by position in order for algorithm to work.
        if(cursorDaySchedules != null) {
            cursorDaySchedules.moveToFirst();
            do {
                DayScheduleEntry dse = new DayScheduleEntry(cursorDaySchedules);
                if(dse.getId() == currentDayScheduleId) {
                    if(cursorDaySchedules.moveToNext()) {
                        DayScheduleEntry nextEntry = new DayScheduleEntry(cursorDaySchedules);
                        if (nextEntry.getExerciseEntryId() != RVAdapterDaySchedule.DAY_SEPARATOR_ID) {
                            hasNextExerciseInRoutine = true;
                            ExerciseAppDBSetting.setCurrentDayScheduleId(this, nextEntry.getId());
                        } else {
                            // We found a day separator.  The next exercise (if there is one, will be the next one, or the first in cursor)
                            if(!cursorDaySchedules.moveToNext()) {
                                cursorDaySchedules.moveToFirst();
                            }
                        }
                    } else {
                        // If we run out of records, then there is definitely no next exercise in this routine.
                        // We need to move to the first record
                        cursorDaySchedules.moveToFirst();
                    }
                    // We're done!  Exit loop.
                    break;
                }
            } while(cursorDaySchedules.moveToNext());
        }

        String message = "Exercise completed at: " + textTime + "\r\n";
        if(!hasNextExerciseInRoutine) {
            message += "End of Routine!  Congrats!";
        } else {
            message += "Moving on to next exercise...";
        }
        Snackbar.make(m_btnStartStop, message, Snackbar.LENGTH_LONG).show();
        m_currentDayScheduleId = cursorDaySchedules.getLong(cursorDaySchedules.getColumnIndex(DayScheduleEntry.Contract.Columns.COL_NAME_ID));
        ExerciseAppDBSetting.setCurrentDayScheduleId(this, m_currentDayScheduleId);
        Log.d(TAG, "completeExercise: moving onto day schedule id: " + m_currentDayScheduleId);
        updateGuiWithCurrentExercise();

        cursorDaySchedules.close();
    }

    private void updateGuiWithCurrentExercise() {
        m_currentLogDailyExerciseEntry = null;

        Cursor cursorUnfinishedExercise = getContentResolver().query(LogDailyExerciseEntry.Contract.CONTENT_URI,
                null, LogDailyExerciseEntry.Contract.Columns.COL_NAME_END_DATETIME + " is null",
                null, null);
        if(cursorUnfinishedExercise != null) {
            int cursorCount = cursorUnfinishedExercise.getCount();
            Log.d(TAG, "updateGuiWithCurrentExercise: Found " + cursorCount + " unfinished exercise(s).");
            if (cursorUnfinishedExercise.getCount() > 0) {
                cursorUnfinishedExercise.moveToFirst();
                m_currentLogDailyExerciseEntry = new LogDailyExerciseEntry(cursorUnfinishedExercise);
                Toast.makeText(this, "Resuming in-progress workout.", Toast.LENGTH_LONG).show();
            }
            cursorUnfinishedExercise.close();
        }

        if (m_currentLogDailyExerciseEntry == null) {
            m_currentLogDailyExerciseEntry = LogDailyExerciseEntry.fromDayScheduleId(this, m_currentDayScheduleId);
        }

        populateGuiFromLogDailyExerciseEntry();
    }
}
