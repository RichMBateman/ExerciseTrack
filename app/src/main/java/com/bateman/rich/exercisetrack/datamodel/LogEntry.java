package com.bateman.rich.exercisetrack.datamodel;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

public class LogEntry {
    private long m_id;
    private long m_dayScheduleId;
    private Date m_startDateTime;
    private Date m_endDateTime;

    public static class Contract {
        public static final String TABLE_NAME = "LogEntries";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        public static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private Contract() {} // prevent instantiation

        static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_DAY_SCHEDULE_ID = "DayScheduleId";
            public static final String COL_NAME_START_DATETIME = "StartDateTime";
            public static final String COL_NAME_END_DATETIME = "EndDateTime";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public LogEntry(int dayScheduleId, Date startDateTime, Date endDateTime) {
        m_dayScheduleId = dayScheduleId;
        m_startDateTime = startDateTime;
        m_endDateTime = endDateTime;
    }

    public LogEntry(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_ID));
        m_dayScheduleId = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_DAY_SCHEDULE_ID));
        m_startDateTime = new Date(cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_START_DATETIME)));
        m_endDateTime = new Date(cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_START_DATETIME)));
    }

    public long getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public long getDayScheduleId() {
        return m_dayScheduleId;
    }

    public void setDayScheduleId(int dayScheduleId) {
        m_dayScheduleId = dayScheduleId;
    }

    public Date getStartDateTime() {
        return m_startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        m_startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return m_endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        m_endDateTime = endDateTime;
    }

    @Override
    public String toString() {
        return "id: " + m_id + ", day schedule id: " + m_dayScheduleId + ", Start Date: " + m_startDateTime.toString() +
                ", End Date: " + m_endDateTime.toString() + "\r\n";
    }
}
