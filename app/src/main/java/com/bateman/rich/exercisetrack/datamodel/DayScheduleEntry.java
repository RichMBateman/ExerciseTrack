package com.bateman.rich.exercisetrack.datamodel;

import android.provider.BaseColumns;

public class DayScheduleEntry {
    private int m_id;
    private int m_position;
    private int m_exerciseEntryId;

    private static final DayScheduleEntry.Contract s_contract = new DayScheduleEntry.Contract();

    public static DayScheduleEntry.Contract getContract() {return s_contract;}

    static class Contract extends DatabaseContractBase {
        private Contract() {
            super(ExerciseAppProvider.CONTENT_AUTHORITY_URI, ExerciseAppProvider.CONTENT_AUTHORITY, "DaySchedules");
        }

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
