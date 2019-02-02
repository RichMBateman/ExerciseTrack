package com.bateman.rich.exercisetrack.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A database class for exercise tracking.  Based off the ExerciseAppDatabase class
 * created by Tim Buchalka in the udemy course Android Java Masterclass - Become an App Developer.
 * I don't think this is a good class to move into a class library as a reusable class, because
 * the database name and version must be supplied to the super constructor, so you can't just create methods
 * that a subclass will override (you're not allowed to call them before the super constructor has finished).
 * Besides, this class is fairly small, and the reusable elements are minor.  Everything else is specific to your database.
 *
 * Should only be visible to this package (visibility is package private).  Callers should use the AppProvider to work with the database.
 */
class ExerciseAppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "ExerciseAppDatabase";
    private static final String DATABASE_NAME = "ExerciseTrack.db";
    private static final int DATABASE_VERSION = 1;

    // Singleton
    private static ExerciseAppDatabase m_instance = null;
    private ExerciseAppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "ExerciseAppDatabase: constructor");
    }

    /**
     * Get the singleton instance of the ExerciseAppDatabase class.
     * @param context The content's providers context
     * @return A SQLite helper database object.
     */
    static ExerciseAppDatabase getInstance(Context context) {
        if(m_instance == null) {
            Log.d(TAG, "getInstance: creating new instance.");
            m_instance = new ExerciseAppDatabase(context);
        }
        return m_instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts.  Creating sqlIte database.");

        createExerciseAppSettingsTable(db);
        insertExerciseAppSettings(db);

        createExerciseEntryTable(db);

        createDayScheduleTable(db);
        createDayScheduleTriggerOnDeleteExercise(db);
        createDayScheduleTriggerOnInsertDaySchedule(db);
        createDayScheduleTriggerOnDeleteDaySchedule(db);
        createDayScheduleTriggerOnUpdateDaySchedulePosition(db);
        createViewDayScheduleWithExerciseNames(db);

//        createLogTable(db);
//        createLogTriggerOnDeleteDaySchedule(db);

        createLogDailyExerciseTable(db);
        //createLogDailyExerciseTriggerOnDeleteLog(db);
        createLogDailyExerciseTriggerOnDeleteExercise(db);
        Log.d(TAG, "onCreate: ends");
    }

    /**
     * Called when we are seeing whether we need to upgrade the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch(oldVersion) {
            case 1:
                // upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }

    private void createExerciseAppSettingsTable(SQLiteDatabase db) {
        String sSqlStatement;
        sSqlStatement = "CREATE TABLE " + ExerciseAppDBSetting.Contract.TABLE_NAME + " ("
                + ExerciseAppDBSetting.Contract.Columns.COL_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + " TEXT NOT NULL, "
                + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE + " TEXT NOT NULL);";
        Log.d(TAG, sSqlStatement);
        db.execSQL(sSqlStatement);
    }

    private void insertExerciseAppSettings(SQLiteDatabase db) {
        String sqlStmt = "INSERT INTO " + ExerciseAppDBSetting.Contract.TABLE_NAME
                + "(" + ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + ", " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE
                + ") values ('" + ExerciseAppDBSetting.SETTING_KEY_CURRENT_DAY + "', '0'); ";
        Log.d(TAG, "insertExerciseAppSettings: sqlStmt: " + sqlStmt);
        db.execSQL(sqlStmt);
        sqlStmt = "INSERT INTO " + ExerciseAppDBSetting.Contract.TABLE_NAME
                + "(" + ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + ", " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE
                + ") values ('" + ExerciseAppDBSetting.SETTING_KEY_DISABLE_TRIGGERS + "', 'No'); ";
        Log.d(TAG, "insertExerciseAppSettings: sqlStmt: " + sqlStmt);
        db.execSQL(sqlStmt);
    }

    /**
     * The exercise entry table is a simple table that holds onto the exercises done, as well as simple daily reminders.
     * These are simply strings that the user will pick from when making their schedule.
     * @param db The SQLite database
     */
    private void createExerciseEntryTable(SQLiteDatabase db) {
        String sSqlStatement;
        // One way of producing the create table statement.  Heavy reliance on hard-coded strings, which is bad..
        // sSQL = "CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER, CategoryID INTEGER);";
        sSqlStatement = "CREATE TABLE " + ExerciseEntry.Contract.TABLE_NAME + " ("
                + ExerciseEntry.Contract.Columns.COL_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ExerciseEntry.Contract.Columns.COL_NAME_NAME + " TEXT NOT NULL, "
                + ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER + " BOOLEAN NOT NULL);";
        Log.d(TAG, sSqlStatement);
        db.execSQL(sSqlStatement);
    }

    /**
     * The day schedule table allows a user to create blocks of exercises.  Each block represents the exercise
     * a user will do in a session/day.  Users cannot associate a daily reminder with a schedule block,
     * because those will show up every day.
     * @param db The SQLite database
     */
    private void createDayScheduleTable(SQLiteDatabase db) {
        String sSqlStatement = "CREATE TABLE " + DayScheduleEntry.Contract.TABLE_NAME + " ("
                + DayScheduleEntry.Contract.Columns.COL_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + " INTEGER NOT NULL, "
                + DayScheduleEntry.Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID + " INTEGER NOT NULL, "
                + DayScheduleEntry.Contract.Columns.COL_NAME_IS_DAY_SEPARATOR + " BOOLEAN NOT NULL);";
        Log.d(TAG, sSqlStatement);
        db.execSQL(sSqlStatement);
    }

    private void createDayScheduleTriggerOnDeleteExercise(SQLiteDatabase db) {
        AppDatabaseHelper.createTriggerOnDeleteParentRecord(db,
                "RemoveOnDeleteExercise",
                ExerciseEntry.Contract.TABLE_NAME,
                DayScheduleEntry.Contract.TABLE_NAME,
                ExerciseEntry.Contract.Columns.COL_NAME_ID,
                DayScheduleEntry.Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID);
    }

    /**
     * Creates a trigger for inserts to synchronize position across all rows.  Disables additional triggers from firing.
     * @param db
     */
    private void createDayScheduleTriggerOnInsertDaySchedule(SQLiteDatabase db) {
        String sSqlStatement = "CREATE TRIGGER OnInsertDaySchedule "
                + " AFTER INSERT ON " + DayScheduleEntry.Contract.TABLE_NAME
                + " BEGIN "
                + " UPDATE " + ExerciseAppDBSetting.Contract.TABLE_NAME
                +       " SET " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE + " = 'Yes' "
                +       " WHERE " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + " = '"
                +       ExerciseAppDBSetting.SETTING_KEY_DISABLE_TRIGGERS + "';"
                + " UPDATE " + DayScheduleEntry.Contract.TABLE_NAME
                + " SET " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + " = " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + " + 1 "
                + " WHERE " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION+ " >= NEW." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION
                + " AND " + DayScheduleEntry.Contract.Columns.COL_NAME_ID + " <> NEW." + DayScheduleEntry.Contract.Columns.COL_NAME_ID + "; "
                + " UPDATE " + ExerciseAppDBSetting.Contract.TABLE_NAME
                +       " SET " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE + " = 'No' "
                +       " WHERE " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + " = '"
                +       ExerciseAppDBSetting.SETTING_KEY_DISABLE_TRIGGERS + "';"
                + " END;";
        Log.d(TAG, "createDayScheduleTriggerOnInsertDaySchedule: " + sSqlStatement);
        db.execSQL(sSqlStatement);
    }

    /**
     * Creates a trigger for deletes to synchronize position across all rows.  Disables additional triggers from firing.
     * @param db
     */
    private void createDayScheduleTriggerOnDeleteDaySchedule(SQLiteDatabase db) {
        String sSqlStatement = "CREATE TRIGGER OnDeleteDaySchedule "
                + " AFTER DELETE ON " + DayScheduleEntry.Contract.TABLE_NAME
                + " BEGIN "
                + " UPDATE " + ExerciseAppDBSetting.Contract.TABLE_NAME
                +       " SET " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE + " = 'Yes' "
                +       " WHERE " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + " = '"
                +       ExerciseAppDBSetting.SETTING_KEY_DISABLE_TRIGGERS + "';"
                + " UPDATE " + DayScheduleEntry.Contract.TABLE_NAME
                + " SET " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + " = " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + " - 1 "
                + " WHERE " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION+ " > OLD." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + ";"
                + " UPDATE " + ExerciseAppDBSetting.Contract.TABLE_NAME
                +       " SET " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE + " = 'No' "
                +       " WHERE " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + " = '"
                +       ExerciseAppDBSetting.SETTING_KEY_DISABLE_TRIGGERS + "';"
                + " END;";
        Log.d(TAG, "createDayScheduleTriggerOnDeleteDaySchedule: " + sSqlStatement);
        db.execSQL(sSqlStatement);
    }

    /**
     * Creates a trigger for Updates (that can be optionally disabled).  Keeps "position" in sync for all records.
     * @param db
     */
    private void createDayScheduleTriggerOnUpdateDaySchedulePosition(SQLiteDatabase db) {
        String sSqlStatement = "CREATE TRIGGER OnUpdateDaySchedulePosition "
                + " AFTER UPDATE ON " + DayScheduleEntry.Contract.TABLE_NAME
                + " WHEN old." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION
                +       " <> new." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION
                +       " and 'No' = (SELECT " + ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE
                +       " FROM " + ExerciseAppDBSetting.Contract.TABLE_NAME + " WHERE "
                +       ExerciseAppDBSetting.Contract.Columns.COL_NAME_KEY + " = '"
                +       ExerciseAppDBSetting.SETTING_KEY_DISABLE_TRIGGERS + "')"
                + " BEGIN "
                + " UPDATE " + DayScheduleEntry.Contract.TABLE_NAME
                + " SET " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + " = old." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION
                + " WHERE " + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION+ " = new." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION
                + " AND " + DayScheduleEntry.Contract.Columns.COL_NAME_ID + " <> old." + DayScheduleEntry.Contract.Columns.COL_NAME_ID + "; "
                + " END;";
        Log.d(TAG, "createDayScheduleTriggerOnUpdateDaySchedulePosition: " + sSqlStatement);
        db.execSQL(sSqlStatement);
    }

    private void createViewDayScheduleWithExerciseNames(SQLiteDatabase db) {
        String sqlStatement = "CREATE VIEW " + DayScheduleEntry.ContractViewDaySchedules.TABLE_NAME
                + " AS SELECT " + DayScheduleEntry.Contract.TABLE_NAME + "." + DayScheduleEntry.ContractViewDaySchedules.Columns.COL_NAME_ID + ", "
                + DayScheduleEntry.Contract.TABLE_NAME + "." + DayScheduleEntry.ContractViewDaySchedules.Columns.COL_NAME_EXERCISE_ENTRY_ID + ", "
                + ExerciseEntry.Contract.TABLE_NAME + "." + ExerciseEntry.Contract.Columns.COL_NAME_NAME + ", "
                + DayScheduleEntry.Contract.TABLE_NAME + "." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION + ", "
                + DayScheduleEntry.Contract.TABLE_NAME + "." + DayScheduleEntry.Contract.Columns.COL_NAME_IS_DAY_SEPARATOR
                + " FROM " + DayScheduleEntry.Contract.TABLE_NAME + " LEFT OUTER JOIN " + ExerciseEntry.Contract.TABLE_NAME
                + " ON " + DayScheduleEntry.Contract.TABLE_NAME + "." + DayScheduleEntry.Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID + " = "
                + ExerciseEntry.Contract.TABLE_NAME + "." +  ExerciseEntry.Contract.Columns.COL_NAME_ID
                // This where is applied after the join completes.
                + " WHERE IFNULL(" + ExerciseEntry.Contract.TABLE_NAME + "." + ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER + ", 0) = 0 "
                + " ORDER BY " + DayScheduleEntry.Contract.TABLE_NAME + "." + DayScheduleEntry.Contract.Columns.COL_NAME_POSITION
                + ";";
        db.execSQL(sqlStatement);
        Log.d(TAG, "createViewDayScheduleWithExerciseNames: sqlStatement: " + sqlStatement);
    }

    /**
     * The log table keeps track of when the user does a block of exercise (from the day schedule table).
     * We keep track of when they started and when they finished.
     * The total number of minutes devoted to a day of exercise can be derived.
     * @param db The SQLite database
     */
//    private void createLogTable(SQLiteDatabase db) {
//        String sSqlStatement = "CREATE TABLE " + LogEntry.Contract.TABLE_NAME + " ("
//                + LogEntry.Contract.Columns.COL_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, "
//                + LogEntry.Contract.Columns.COL_NAME_DAY_SCHEDULE_ID + " INTEGER NOT NULL, "
//                + LogEntry.Contract.Columns.COL_NAME_START_DATETIME + " DATETIME NOT NULL, "
//                + LogEntry.Contract.Columns.COL_NAME_END_DATETIME + " DATETIME);";
//        Log.d(TAG, sSqlStatement);
//        db.execSQL(sSqlStatement);
//    }
//
//    private void createLogTriggerOnDeleteDaySchedule(SQLiteDatabase db) {
//        AppDatabaseHelper.createTriggerOnDeleteParentRecord(db,
//                "RemoveOnDeleteDaySchedule",
//                DayScheduleEntry.Contract.TABLE_NAME,
//                LogEntry.Contract.TABLE_NAME, DayScheduleEntry.Contract.Columns.COL_NAME_ID,
//                LogEntry.Contract.Columns.COL_NAME_ID);
//    }

    /**
     * The log daily exercise table keeps track of the specific exercise done on a certain day,
     * the total reps done, the difficulty for the user, and the weight.
     * For simple daily reminders, we'll use the totalRepsDone column to store a 1 if the reminder
     * was done, else 0.
     * @param db The SQLite database
     */
    private void createLogDailyExerciseTable(SQLiteDatabase db) {
        String sSqlStatement = "CREATE TABLE " + LogDailyExerciseEntry.Contract.TABLE_NAME + " ("
                + LogDailyExerciseEntry.Contract.Columns.COL_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + LogDailyExerciseEntry.Contract.Columns.COL_NAME_EXERCISE_ID + " INTEGER NOT NULL, "
                + LogDailyExerciseEntry.Contract.Columns.COL_NAME_START_DATETIME + " DATETIME NOT NULL, "
                + LogDailyExerciseEntry.Contract.Columns.COL_NAME_END_DATETIME + " DATETIME, "
                + LogDailyExerciseEntry.Contract.Columns.COL_NAME_TOTAL_REPS_DONE + " INTEGER NOT NULL, "
                + LogDailyExerciseEntry.Contract.Columns.COL_NAME_WEIGHT + " INTEGER NOT NULL, "
                + LogDailyExerciseEntry.Contract.Columns.COL_NAME_DIFFICULTY + " INTEGER NOT NULL);";
        Log.d(TAG, sSqlStatement);
        db.execSQL(sSqlStatement);
    }
//
//    private void createLogDailyExerciseTriggerOnDeleteLog(SQLiteDatabase db) {
//        AppDatabaseHelper.createTriggerOnDeleteParentRecord(db,
//                "RemoveOnDeleteLogEntry",
//                LogEntry.Contract.TABLE_NAME,
//                LogDailyExerciseEntry.Contract.TABLE_NAME,
//                LogEntry.Contract.Columns.COL_NAME_ID,
//                LogDailyExerciseEntry.Contract.Columns.COL_NAME_ID);
//    }

    private void createLogDailyExerciseTriggerOnDeleteExercise(SQLiteDatabase db) {
        AppDatabaseHelper.createTriggerOnDeleteParentRecord(db,
                "RemoveOnDeleteExerciseEntry",
                ExerciseEntry.Contract.TABLE_NAME,
                LogDailyExerciseEntry.Contract.TABLE_NAME,
                ExerciseEntry.Contract.Columns.COL_NAME_ID,
                LogDailyExerciseEntry.Contract.Columns.COL_NAME_ID);
    }


}
