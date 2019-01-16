package com.bateman.rich.exercisetrack.datamodel;

import android.database.Cursor;
import android.provider.BaseColumns;

public class ExerciseEntry {
    private long m_id;
    private String m_name;
    private boolean m_isDailyReminder;
    private static final Contract s_contract = new Contract();

    public static Contract getContract() {return s_contract;}

    public static class Contract extends DatabaseContractBase {
        private Contract() {
            super(ExerciseAppProvider.CONTENT_AUTHORITY_URI, ExerciseAppProvider.CONTENT_AUTHORITY, "ExerciseEntries");
        }

        /**
         * Gets a full projection for this table (all columns).
         * @return
         */
        public String[] getProjectionFull() {
            String[] projection = {Columns.COL_NAME_ID,
                    Columns.COL_NAME_NAME,
                    Columns.COL_NAME_IS_DAILY_REMINDER};
            return projection;
        }

        public String getSortOrderStandard() {
            String sortOrder = Columns.COL_NAME_NAME + "," + Columns.COL_NAME_IS_DAILY_REMINDER + " COLLATE NOCASE";
            return sortOrder;
        }

        static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_NAME = "Name";
            public static final String COL_NAME_IS_DAILY_REMINDER = "IsDailyReminder";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
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

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
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

    public void setDailyReminder(boolean dailyReminder) {
        m_isDailyReminder = dailyReminder;
    }
}
