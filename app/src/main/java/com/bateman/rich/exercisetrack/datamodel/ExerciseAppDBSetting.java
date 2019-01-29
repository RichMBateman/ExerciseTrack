package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Represents a simple key/value pair table for general app settings.
 */
public class ExerciseAppDBSetting {
    /**
     * Used to retrieve the current day schedule that is set in the database.
     * Will store, as a string, the id of the Day Schedule we want to use for the current day.
     */
    public static final String SETTING_KEY_CURRENT_DAY = "CurrentDay";
    /**
     * A way to mark that we should ignore triggers from firing.
     * Values are simply Yes or No.
     */
    public static final String SETTING_KEY_DISABLE_TRIGGERS = "DisableTriggers";

    private long m_id;
    private String m_key;
    private String m_val;

    public static class Contract {
        public static final String TABLE_NAME = "ExerciseSettings";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        public static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private Contract() {} // prevent instances.
        /**
         * Gets a full projection for this table (all columns).
         * @return
         */
        public static String[] getProjectionFull() {
            String[] projection = {Columns.COL_NAME_ID,
                    Columns.COL_NAME_KEY,
                    Columns.COL_NAME_VALUE};
            return projection;
        }

        public static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_KEY = "KeyName";
            public static final String COL_NAME_VALUE = "Value";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public ExerciseAppDBSetting(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_ID));
        m_key = cursor.getString(cursor.getColumnIndex(Contract.Columns.COL_NAME_KEY));
        m_val = cursor.getString(cursor.getColumnIndex(Contract.Columns.COL_NAME_VALUE));
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public String getKey() {
        return m_key;
    }

    public void setKey(String key) {
        m_key = key;
    }

    public String getVal() {
        return m_val;
    }

    public void setVal(String val) {
        m_val = val;
    }
}
