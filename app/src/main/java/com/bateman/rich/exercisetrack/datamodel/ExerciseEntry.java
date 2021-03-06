package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.bateman.rich.rmblibrary.persistence.AppDatabaseHelper;
import com.bateman.rich.rmblibrary.persistence.ContentProviderHelper;

/**
 * I had tried creating a "DatabaseContractBase" which would handle creating the CONTENT URI, CONTENT_ITEM_TYPE, etc.
 * UNFORTUNATELY, those require that the ExerciseAppProvider already be setup.  The ExerciseAppProvider, when it
 * is initializing its static data... DEPENDS on all these Contract classes.  So there is an insidious cycle of dependencies.
 * To eliminate this problem, Contracts will ALWAYS be purely static.
 */
public class ExerciseEntry {
    private long m_id;
    private String m_name;
    private boolean m_isDailyReminder;

    public static class Contract {
        static final String TABLE_NAME = "ExerciseEntries";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private Contract() {} // prevent instances.
        /**
         * Gets a full projection for this table (all columns).
         */
        public static String[] getProjectionFull() {
            return new String[]{Columns.COL_NAME_ID,
                    Columns.COL_NAME_NAME,
                    Columns.COL_NAME_IS_DAILY_REMINDER};
        }

        public static String getSortOrderStandard() {
            return Columns.COL_NAME_NAME + "," + Columns.COL_NAME_IS_DAILY_REMINDER + " COLLATE NOCASE";
        }

        public static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            static final String COL_NAME_NAME = "Name";
            public static final String COL_NAME_IS_DAILY_REMINDER = "IsDailyReminder";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public ExerciseEntry(long id, String name, boolean isDailyReminder) {
        m_id = id;
        m_name = name;
        m_isDailyReminder = isDailyReminder;
    }

    public ExerciseEntry(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_ID));
        m_name = cursor.getString(cursor.getColumnIndex(Contract.Columns.COL_NAME_NAME));
        m_isDailyReminder = (cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_IS_DAILY_REMINDER)) == AppDatabaseHelper.SQLITE_INT_TRUE);
    }

    public ExerciseEntry(String name, boolean isDailyReminder) {
        m_name = name;
        m_isDailyReminder = isDailyReminder;
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public boolean isDailyReminder() {
        return m_isDailyReminder;
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + m_id + ", Name: " + m_name + ", Is Daily Reminder?: " + m_isDailyReminder + "\r\n";
    }

    /**
     * Saves a new exercise entry to the database.
     */
    public static void saveNewExerciseEntry(Context c, String exerciseText, boolean isDailyReminder) {
        ContentValues values = new ContentValues();
        values.put(ExerciseEntry.Contract.Columns.COL_NAME_NAME, exerciseText);
        values.put(ExerciseEntry.Contract.Columns.COL_NAME_IS_DAILY_REMINDER, isDailyReminder);
        c.getContentResolver().insert(ExerciseEntry.Contract.CONTENT_URI, values);
    }
}
