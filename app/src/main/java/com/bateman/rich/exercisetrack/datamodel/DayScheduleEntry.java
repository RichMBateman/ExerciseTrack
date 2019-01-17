package com.bateman.rich.exercisetrack.datamodel;

import android.net.Uri;
import android.provider.BaseColumns;

public class DayScheduleEntry {
    private int m_id;
    private int m_position;
    private int m_exerciseEntryId;

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

    public int getId() {
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

    public int getExerciseEntryId() {
        return m_exerciseEntryId;
    }

    public void setExerciseEntryId(int exerciseEntryId) {
        m_exerciseEntryId = exerciseEntryId;
    }
}
