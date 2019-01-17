package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentResolver;
import android.content.ContentValues;

/**
 * A class to facilitate testing.  Can generate test data.
 */
public class TestData {


    public static void generateTestData(ContentResolver contentResolver) {
        createExerciseEntries(contentResolver);
        createDaySchedule(contentResolver);
        createLogEntries(contentResolver);
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
        ExerciseEntry entry15 = new ExerciseEntry("5 Minutes Cycling Warmpup", true);

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
    }

    private static void saveExerciseEntry(ContentResolver contentResolver, ExerciseEntry entry) {
        ContentValues values = new ContentValues();
        values.put(ExerciseEntry.Contract.Columns.COL_NAME_ID, entry.getId());
        values.put(ExerciseEntry.Contract.Columns.COL_NAME_NAME, entry.getName());
        values.put(ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER, entry.isDailyReminder());

        contentResolver.insert(ExerciseEntry.Contract.CONTENT_URI, values);
    }

    private static void createDaySchedule(ContentResolver contentResolver) {

    }

    private static void saveDaySchedule(ContentResolver contentResolver, DayScheduleEntry entry) {
        ContentValues values = new ContentValues();
        values.put(DayScheduleEntry.Contract.Columns.COL_NAME_ID, entry.getId());
        values.put(DayScheduleEntry.Contract.Columns.COL_NAME_POSITION, entry.getPosition());
        values.put(DayScheduleEntry.Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID, entry.getExerciseEntryId());

        contentResolver.insert(ExerciseEntry.Contract.CONTENT_URI, values);
    }

    private static void createLogEntries(ContentResolver contentResolver) {

    }

    private static void saveLogEntry(ContentResolver contentResolver, LogEntry entry) {
//        ContentValues values = new ContentValues();
//        values.put(LogEntry.Contract.Columns.COL_NAME_ID, entry.getId());
//        values.put(LogEntry.Contract.Columns.COL_NAME_DAY_SCHEDULE_ID, entry.getDayScheduleId());
//        values.put(LogEntry.Contract.Columns.COL_NAME_START_DATETIME, entry.getStartDateTime());
//        values.put(LogEntry.Contract.Columns.COL_NAME_END_DATETIME, entry.getEndDateTime());
//
//        contentResolver.insert(ExerciseEntry.getContract().CONTENT_URI, values);
    }

    private static void createLogDailyExerciseEntries(ContentResolver contentResolver) {

    }

    private static void saveLogDaliyExerciseEntry(ContentResolver contentResolver, LogDailyExerciseEntry entry) {
        ContentValues values = new ContentValues();
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_ID, entry.getId());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_EXERCISE_ID, entry.getExerciseId());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_LOG_ID, entry.getLogId());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_TOTAL_REPS_DONE, entry.getTotalRepsDone());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_WEIGHT, entry.getWeight());
        values.put(LogDailyExerciseEntry.Contract.Columns.COL_NAME_DIFFICULTY, entry.getDifficulty());

        contentResolver.insert(ExerciseEntry.Contract.CONTENT_URI, values);
    }
}
