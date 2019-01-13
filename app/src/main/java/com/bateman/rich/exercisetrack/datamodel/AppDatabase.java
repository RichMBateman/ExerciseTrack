package com.bateman.rich.exercisetrack.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A database class for exercise tracking.  Based off the AppDatabase class
 * created by Tim Buchalka in the udemy course Android Java Masterclass - Become an App Developer.
 * I don't think this is a good class to move into a class library as a reusable class, because
 * the database name and version must be supplied to the super consttructor, so you can't just create methods
 * that a subclass will override (you're not allowed to call them before the super constructor has finished).
 * Besides, this class is fairly small, and the reusable elements are minor.  Everything else is specific to your database.
 *
 * Should only be visible to this package (visibility is package private).  Callers should use the AppProvider to work with the database.
 */
class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";
    private static final String DATABASE_NAME = "ExerciseTrack.db";
    private static final int DATABASE_VERSION = 1;

    // Singleton
    private static AppDatabase m_instance = null;
    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    /**
     * Get the singleton instance of the AppDatabase class.
     * @param context The content's providers context
     * @return A SQLite helper database object.
     */
    static AppDatabase getInstance(Context context) {
        if(m_instance == null) {
            Log.d(TAG, "getInstance: creating new instance.");
            m_instance = new AppDatabase(context);
        }
        return m_instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSqlStatement;
        // One way of producting the create table statement.  Heavy reliance on hard-coded strings.
        // sSQL = "CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER, CategoryID INTEGER);";
        sSqlStatement = "CREATE TABLE " + ExerciseEntry.getContract().TABLE_NAME + " ("
                + ExerciseEntry.Contract.Columns.COL_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ExerciseEntry.Contract.Columns.COL_NAME_NAME + " TEXT NOT NULL);";
        Log.d(TAG, sSqlStatement);
        db.execSQL(sSqlStatement);

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
}
