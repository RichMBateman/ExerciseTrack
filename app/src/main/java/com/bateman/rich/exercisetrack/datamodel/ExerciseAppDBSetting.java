package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.bateman.rich.rmblibrary.persistence.ContentProviderHelper;

/**
 * Represents a simple key/value pair table for general app settings.
 */
public class ExerciseAppDBSetting {
    private static final String TAG = "ExerciseAppDBSetting";
    /**
     * Used to retrieve the current day schedule that is set in the database.
     * Will store, as a string, the id of the Day Schedule we want to use for the current day.
     */
    static final String SETTING_KEY_CURRENT_DAY = "CurrentDay";
    /**
     * A way to mark that we should ignore triggers from firing.
     * Values are simply Yes or No.
     */
    static final String SETTING_KEY_DISABLE_TRIGGERS = "DisableTriggers";

    private long m_id;
    private String m_key;

    public static class Contract {
        static final String TABLE_NAME = "ExerciseSettings";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private Contract() {} // prevent instances.

        public static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            static final String COL_NAME_KEY = "KeyName";
            static final String COL_NAME_VALUE = "Value";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public ExerciseAppDBSetting(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_ID));
        m_key = cursor.getString(cursor.getColumnIndex(Contract.Columns.COL_NAME_KEY));
    }

    /**
     * Loads the current day schedule id.
     */
    public static long getCurrentDayScheduleId(Context context) {
        String value = getSettingValue(context, SETTING_KEY_CURRENT_DAY);
        long id = -1;
        try {
            id = Long.parseLong(value);
        } catch (NumberFormatException exc) {
            Log.d(TAG, "getCurrentDayScheduleId: failed to parse: " + value);
        }
        return id;
    }

    /**
     * Sets the current day schedule id.
     */
    public static void setCurrentDayScheduleId(Context context, long dayScheduleId) {
        setSettingValue(context, SETTING_KEY_CURRENT_DAY, Long.toString(dayScheduleId));
    }

    @SuppressWarnings("SameParameterValue")
    private static void setSettingValue(Context context, String key, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExerciseAppDBSetting.Contract.Columns.COL_NAME_VALUE, value);
        final String where = Contract.Columns.COL_NAME_KEY + "=?";
        final String[] selection = new String[] {key};
        context.getContentResolver().update(ExerciseAppDBSetting.Contract.CONTENT_URI, contentValues, where, selection);
    }

    /**
     * Queries for a specific setting value, given a key.
     */
    @SuppressWarnings("SameParameterValue")
    private static String getSettingValue(Context context, String key) {
        final String[] projection =  new String[]{Contract.Columns.COL_NAME_VALUE};
        final String selection = Contract.Columns.COL_NAME_KEY +"=?";
        final String[] selectionArgs = {key};
        Cursor cursorDBSettings = context.getContentResolver().query(Contract.CONTENT_URI,
                projection, selection, selectionArgs, null);

        String keyValue = null;
        if(cursorDBSettings != null && cursorDBSettings.getCount() > 0) {
            cursorDBSettings.moveToFirst();
            keyValue = cursorDBSettings.getString(cursorDBSettings.getColumnIndex(Contract.Columns.COL_NAME_VALUE));
            cursorDBSettings.close();
        }
        return keyValue;
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
}
