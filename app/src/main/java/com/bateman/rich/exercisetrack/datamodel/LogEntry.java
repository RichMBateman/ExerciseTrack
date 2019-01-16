package com.bateman.rich.exercisetrack.datamodel;

import android.provider.BaseColumns;

import java.util.Date;

public class LogEntry {
    private int m_id;
    private int m_dayScheduleId;
    private Date m_startDateTime;
    private Date m_endDateTime;

    private static final LogEntry.Contract s_contract = new LogEntry.Contract();

    public static LogEntry.Contract getContract() {return s_contract;}

    static class Contract extends DatabaseContractBase {
        private Contract() {
            super(ExerciseAppProvider.CONTENT_AUTHORITY_URI, ExerciseAppProvider.CONTENT_AUTHORITY, "Log");
        }

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

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public int getDayScheduleId() {
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
}
