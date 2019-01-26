package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.bateman.rich.exercisetrack.gui.RVAdapterDaySchedule;

/**
 * Represents a schedule for a day.
 * Position is 1-based.  It represents the order of items as they appear in the day schedule.
 * You need to manually figure out which items then are grouped together by iterating through the list of items in order,
 * and stopping when you find a day-separator.
 */
public class DayScheduleEntry {
    private static final String TAG = "DayScheduleEntry";
    private long m_id;
    private int m_position;
    private boolean m_isDaySeparator;
    // This class refers to an ExerciseEntry.  I don't know what's better... have the fields of exercise entry as members,
    // or have an actual reference to the linked ExerciseEntry (which may be null for Day Separators)...
    // Going to try exclusively using exercise entry name... see if that's enough.
    private long m_exerciseEntryId;
    private String m_exerciseEntryName;
    private ExerciseEntry m_exerciseEntry;

    public static class Contract {
        public static final String TABLE_NAME = "DaySchedules";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        public static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private Contract() {} // prevent instantiation

        static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_EXERCISE_ENTRY_ID = "ExerciseEntryId";
            public static final String COL_NAME_POSITION = "Position";
            public static final String COL_NAME_IS_DAY_SEPARATOR = "IsDaySeparator";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public static class ContractViewDaySchedules {
        public static final String TABLE_NAME = "vwDaySchedules";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        public static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private ContractViewDaySchedules() {} // prevent instantiation

        static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_EXERCISE_ENTRY = ExerciseEntry.Contract.Columns.COL_NAME_NAME;
            public static final String COL_NAME_POSITION = Contract.Columns.COL_NAME_POSITION;
            public static final String COL_NAME_IS_DAY_SEPARATOR = Contract.Columns.COL_NAME_IS_DAY_SEPARATOR;

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public DayScheduleEntry() {

    }

    public DayScheduleEntry(int position, int exerciseEntryId) {
        m_position = position;
        m_exerciseEntryId = exerciseEntryId;
    }

    public DayScheduleEntry(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndex(DayScheduleEntry.Contract.Columns.COL_NAME_ID));
        m_position = cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_POSITION));
        m_exerciseEntryId = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID));
    }

    public static DayScheduleEntry createDayScheduleEntryFromView(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DayScheduleEntry.ContractViewDaySchedules.Columns.COL_NAME_ID));
        int position = cursor.getInt(cursor.getColumnIndex(ContractViewDaySchedules.Columns.COL_NAME_POSITION));
        String exerciseName = cursor.getString(cursor.getColumnIndex(ContractViewDaySchedules.Columns.COL_NAME_EXERCISE_ENTRY));
        boolean isDaySeparator = (cursor.getInt(cursor.getColumnIndex(ContractViewDaySchedules.Columns.COL_NAME_IS_DAY_SEPARATOR)) == 1);

        DayScheduleEntry dse = new DayScheduleEntry();
        dse.m_id = id;
        dse.m_position = position;
        dse.m_exerciseEntryName = exerciseName;
        dse.m_isDaySeparator = isDaySeparator;
        return dse;
    }

    public long getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public int getPosition() {
        return m_position;
    }

    public void setPosition(int position) {
        m_position = position;
    }

    public long getExerciseEntryId() {
        return m_exerciseEntryId;
    }

    public void setExerciseEntryId(long exerciseEntryId) {
        m_exerciseEntryId = exerciseEntryId;
    }

    public boolean isDaySeparator() {
        return m_isDaySeparator;
    }

    public void setDaySeparator(boolean daySeparator) {
        m_isDaySeparator = daySeparator;
    }

    public String getExerciseEntryName() {
        if(m_isDaySeparator)
            return RVAdapterDaySchedule.DAY_SEPARATOR_LABEL;
        else
            return m_exerciseEntryName;
    }

    public void setExerciseEntryName(String exerciseEntryName) {
        m_exerciseEntryName = exerciseEntryName;
    }

    public ExerciseEntry getExerciseEntry() {
        return m_exerciseEntry;
    }

    public void setExerciseEntry(ExerciseEntry exerciseEntry) {
        m_exerciseEntry = exerciseEntry;
    }

    @Override
    public String toString() {
        return "id: " + m_id + ", Position: " + m_position + ", exercise entry id: " + m_exerciseEntryId + "\r\n";
    }

    /**
     * Saves a new day schedule entry to the database.
     */
    public static void saveNewDaySchedule(Context c, int position, long exerciseId, boolean isDaySeparator) {
        ContentValues values = new ContentValues();
        values.put(Contract.Columns.COL_NAME_POSITION, position);
        values.put(Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID, exerciseId);
        values.put(Contract.Columns.COL_NAME_IS_DAY_SEPARATOR, isDaySeparator);
        Uri returnedRow = c.getContentResolver().insert(Contract.CONTENT_URI, values);
        Log.d(TAG, "saveNewDaySchedule: returnedRow: " + returnedRow);
    }

}
