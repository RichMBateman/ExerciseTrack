package com.bateman.rich.exercisetrack.datamodel;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Represents a schedule for a day.
 * Position is 1-based.  So Position of 1 represents the first day in the schedule.
 */
public class DayScheduleEntry {
    private long m_id;
    private int m_position;
    private long m_exerciseEntryId;

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

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
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

    @Override
    public String toString() {
        return "id: " + m_id + ", Position: " + m_position + ", exercise entry id: " + m_exerciseEntryId + "\r\n";
    }
}
