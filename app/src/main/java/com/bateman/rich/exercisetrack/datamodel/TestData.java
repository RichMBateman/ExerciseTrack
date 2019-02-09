package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bateman.rich.exercisetrack.gui.RVAdapterDaySchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A class to facilitate testing.  Can generate test data.
 */
public class TestData {

    private static ArrayList<ExerciseEntry> m_exerciseEntryList = new ArrayList<>();
    private static ArrayList<DayScheduleEntry> m_dayScheduleEntryList = new ArrayList<>();

    public static void generateTestData(ContentResolver contentResolver) {
        createExerciseEntries(contentResolver);
        createDaySchedules(contentResolver);
        createLogDailyExerciseEntries(contentResolver);
    }

    private static void createExerciseEntries(ContentResolver contentResolver) {
        ExerciseEntry entry1 = new ExerciseEntry("Bench Press", false);
        ExerciseEntry entry2 = new ExerciseEntry("WG Bench Press", false);
        ExerciseEntry entry3 = new ExerciseEntry("Decline Bench Press", false);
        ExerciseEntry entry4 = new ExerciseEntry("Incline Bench Press", false);
        ExerciseEntry entry5 = new ExerciseEntry("Overhead Press", false);
        ExerciseEntry entry6 = new ExerciseEntry("Deadlift", false);
        ExerciseEntry entry7 = new ExerciseEntry("WG Deadlift", false);
        ExerciseEntry entry8 = new ExerciseEntry("Deficit Deadlift", false);
        ExerciseEntry entry9 = new ExerciseEntry("Elevated Deadlift", false);
        ExerciseEntry entry10 = new ExerciseEntry("Back Squats", false);
        ExerciseEntry entry11 = new ExerciseEntry("Front Squats", false);
        ExerciseEntry entry12 = new ExerciseEntry("Cycling", false);
        ExerciseEntry entry13 = new ExerciseEntry("Push Up", false);
        ExerciseEntry entry14 = new ExerciseEntry("Wide Push Up", false);
        ExerciseEntry entry15 = new ExerciseEntry("5 Minutes Cycling Warm up", true);

        saveExerciseEntry(contentResolver, entry1);
        saveExerciseEntry(contentResolver, entry2);
        saveExerciseEntry(contentResolver, entry3);
        saveExerciseEntry(contentResolver, entry4);
        saveExerciseEntry(contentResolver, entry5);
        saveExerciseEntry(contentResolver, entry6);
        saveExerciseEntry(contentResolver, entry7);
        saveExerciseEntry(contentResolver, entry8);
        saveExerciseEntry(contentResolver, entry9);
        saveExerciseEntry(contentResolver, entry10);
        saveExerciseEntry(contentResolver, entry11);
        saveExerciseEntry(contentResolver, entry12);
        saveExerciseEntry(contentResolver, entry13);
        saveExerciseEntry(contentResolver, entry14);
        saveExerciseEntry(contentResolver, entry15);

        // There's no reason to use below.  We can grab id ON insert.
//        // Load the saved exercise entries (which have ids set), so we can refer to them when creating our day schedule.
//        Cursor exerciseEntryCursor = contentResolver.query(ExerciseEntry.Contract.CONTENT_URI, null, null, null, null );
//        if(exerciseEntryCursor != null & exerciseEntryCursor.getCount() > 0) {
//            exerciseEntryCursor.moveToFirst();
//
//            // For ease, just group exercise into a random amount (1-3), and keep creating day schedules until you're done.
//            do {
//                ExerciseEntry ee = new ExerciseEntry(exerciseEntryCursor);
//                m_exerciseEntryList.add(ee);
//
//            } while(exerciseEntryCursor.moveToNext());
//        }
//        exerciseEntryCursor.close();
    }

    private static void saveExerciseEntry(ContentResolver contentResolver, ExerciseEntry entry) {
        ContentValues values = new ContentValues();
        values.put(ExerciseEntry.Contract.Columns.COL_NAME_NAME, entry.getName());
        values.put(ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER, entry.isDailyReminder());

        entry.setId(ContentProviderHelper.getId(contentResolver.insert(ExerciseEntry.Contract.CONTENT_URI, values)));
        m_exerciseEntryList.add(entry);
    }

    private static void createDaySchedules(ContentResolver contentResolver) {
        int numSchedulesToCreate = (int) (Math.random() * 20 + 5); // make some random number of schedules
        int position = 1;
        while (numSchedulesToCreate > 0) {


            int numExercisesToGrab = (int) (Math.random() * 3 + 1);
            while(numExercisesToGrab > 0) {
                int randomSelectionExercise = (int) (Math.random() * m_exerciseEntryList.size());
                long randomSelectionExerciseId = m_exerciseEntryList.get(randomSelectionExercise).getId();

                DayScheduleEntry dayScheduleEntry = new DayScheduleEntry();
                dayScheduleEntry.setExerciseEntryId(randomSelectionExerciseId);
                dayScheduleEntry.setDaySeparator(false);
                dayScheduleEntry.setPosition(position);
                saveDaySchedule(contentResolver, dayScheduleEntry);

                numExercisesToGrab--;
                position++;
            }
            DayScheduleEntry dayScheduleEntry = new DayScheduleEntry();
            dayScheduleEntry.setExerciseEntryId(RVAdapterDaySchedule.DAY_SEPARATOR_ID);
            dayScheduleEntry.setDaySeparator(true);
            dayScheduleEntry.setPosition(position);
            saveDaySchedule(contentResolver, dayScheduleEntry);

            position++;
            numSchedulesToCreate--;
        }
    }

    private static void saveDaySchedule(ContentResolver contentResolver, DayScheduleEntry entry) {
        ContentValues values = new ContentValues();
        // This is a mistake.  We don't have the id, yet.  I had this here back when i had zero clue
        // how we would learn the id of the recently inserted item.
        //values.put(DayScheduleEntry.Contract.Columns.COL_NAME_ID, entry.getId());
        values.put(DayScheduleEntry.Contract.Columns.COL_NAME_POSITION, entry.getPosition());
        values.put(DayScheduleEntry.Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID, entry.getExerciseEntryId());
        values.put(DayScheduleEntry.Contract.Columns.COL_NAME_IS_DAY_SEPARATOR, entry.isDaySeparator());

        entry.setId(ContentProviderHelper.getId(contentResolver.insert(DayScheduleEntry.Contract.CONTENT_URI, values)));
        m_dayScheduleEntryList.add(entry);
    }

    private static void createLogDailyExerciseEntries(ContentResolver contentResolver) {
        // Create 3 days of logs
        int dayCount = 3;
        while(dayCount > 0) {
            for(DayScheduleEntry dayScheduleEntry : m_dayScheduleEntryList) {
                if(!dayScheduleEntry.isDaySeparator()) {
                    // Don't create entries for day separators.
                    LogDailyExerciseEntry logDailyExerciseEntry = new LogDailyExerciseEntry();
                    logDailyExerciseEntry.setDifficulty((int) (Math.random() * 10 + 1));
                    logDailyExerciseEntry.setWeight((int) (Math.random() * 18 + 1) * 5);
                    logDailyExerciseEntry.setTotalRepsDone((int) (Math.random() * 51 + 10));
                    Date startTime = new Date();
                    Date endTime = new Date();
                    getRandomDateTimes(startTime, endTime);
                    logDailyExerciseEntry.setEndDateTime(endTime);
                    logDailyExerciseEntry.setStartDateTime(startTime);
                    logDailyExerciseEntry.setExerciseId(dayScheduleEntry.getExerciseEntryId());

                    saveLogDaliyExerciseEntry(contentResolver, logDailyExerciseEntry);
                }
            }
            dayCount--;
        }
    }

    private static void saveLogDaliyExerciseEntry(ContentResolver contentResolver, LogDailyExerciseEntry entry) {
        ContentValues values = new ContentValues();
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_EXERCISE_ID, entry.getExerciseId());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_TOTAL_REPS_DONE, entry.getTotalRepsDone());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_WEIGHT, entry.getWeight());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_DIFFICULTY, entry.getDifficulty());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_START_DATETIME, entry.getStartDateTime().getTime());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_END_DATETIME, entry.getEndDateTime().getTime());

        contentResolver.insert(LogDailyExerciseEntry.Contract.CONTENT_URI, values);
    }
// from tim buchalka
    private static int getRandomInt(int max) {
        return (int) Math.round(Math.random() * max);
    }

    // Modified version from randomDateTime() From Tim Buchalka
    private static void getRandomDateTimes(Date startTime, Date endTime) {
        GregorianCalendar calendarToday = new GregorianCalendar();
        int year = calendarToday.get(GregorianCalendar.YEAR);

        int sec = getRandomInt(59);
        int min = getRandomInt(59);
        int hour = getRandomInt(23);
        int month = getRandomInt(11);

        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = 1 + getRandomInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) -1);

        gc.set(year, month, day, hour, min, sec);
        startTime.setTime(gc.getTimeInMillis());

        endTime.setTime(gc.getTimeInMillis() + getRandomInt(1000 * 60 * 30) + 1000 * 60 * 15);
    }
}
