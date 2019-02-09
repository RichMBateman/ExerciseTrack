package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

public class LogDailyExerciseEntry {
    private long m_id;
    private ExerciseEntry m_exerciseEntry;
    private long m_exerciseId;
    private Date m_startDateTime;
    private Date m_endDateTime;
    private int m_totalRepsDone;
    private int m_weight;
    private int m_difficulty;

    /**
     * Creates a new LogDailyExerciseEntry for the given VALID current day schedule id.  We assume it exists.
     * @param context
     * @param currentDayScheduleId
     * @return
     */
    public static LogDailyExerciseEntry fromDayScheduleId(Context context, long currentDayScheduleId) {
        LogDailyExerciseEntry entry = new LogDailyExerciseEntry();

        Cursor cursorDaySchedule = context.getContentResolver().query(DayScheduleEntry.Contract.CONTENT_URI, null,
                DayScheduleEntry.Contract.Columns.COL_NAME_ID +"=?", new String[]{Long.toString(currentDayScheduleId)}, null);

        cursorDaySchedule.moveToFirst();
        entry.m_exerciseId = cursorDaySchedule.getLong(cursorDaySchedule.getColumnIndex(DayScheduleEntry.Contract.Columns.COL_NAME_EXERCISE_ENTRY_ID));
        cursorDaySchedule.close();

        Cursor cursorExercise = context.getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI, null,
                    ExerciseEntry.Contract.Columns.COL_NAME_ID +"=?", new String[]{Long.toString(entry.m_exerciseId)}, null);

        cursorExercise.moveToFirst();
        entry.m_exerciseEntry = new ExerciseEntry(cursorExercise);
        cursorExercise.close();

        entry.m_difficulty=5; // default difficulty.

        return entry;
    }

    public static class Contract {
        public static final String TABLE_NAME = "LogDailyExercises";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        public static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private Contract() { } // prevent instantiation

        public static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_EXERCISE_ID = "ExerciseId";
            public static final String COL_NAME_START_DATETIME = "StartDateTime";
            public static final String COL_NAME_END_DATETIME = "EndDateTime";
            public static final String COL_NAME_TOTAL_REPS_DONE = "TotalRepsDone";
            public static final String COL_NAME_WEIGHT = "Weight";
            public static final String COL_NAME_DIFFICULTY = "Difficulty";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public static class ContractViewReport {
        public static final String TABLE_NAME = "ViewExerciseLogReport";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        public static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private ContractViewReport() { } // prevent instantiation

        public static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_EXERCISE_NAME = "ExerciseName";
            public static final String COL_NAME_TOTAL_REPS_DONE = "TotalRepsDone";
            public static final String COL_NAME_WEIGHT = "Weight";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public LogDailyExerciseEntry() {
        m_difficulty=5; // default difficulty.
    }

    public LogDailyExerciseEntry(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_ID));
        m_exerciseId = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_EXERCISE_ID));
        m_startDateTime = new Date(cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_START_DATETIME)));
        m_endDateTime = new Date(cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_END_DATETIME)));
        m_totalRepsDone = cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_TOTAL_REPS_DONE));
        m_weight = cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_WEIGHT));
        m_difficulty = cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_DIFFICULTY));
    }

    public ExerciseEntry getExerciseEntry(Context context)
    {
        if(m_exerciseEntry == null) {
            Cursor exerciseEntryCursor = context.getContentResolver().query(ExerciseEntry.Contract.CONTENT_URI,
                    null, ExerciseEntry.Contract.Columns.COL_NAME_ID +"=?",
                    new String[] {Long.toString(m_exerciseId)}, null);
            if(exerciseEntryCursor != null && exerciseEntryCursor.getCount() > 0) {
                exerciseEntryCursor.moveToFirst();
                m_exerciseEntry = new ExerciseEntry(exerciseEntryCursor);
                exerciseEntryCursor.close();
            }
        }
        return m_exerciseEntry;
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public long getExerciseId() {
        return m_exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        m_exerciseId = exerciseId;
    }

    public int getTotalRepsDone() {
        return m_totalRepsDone;
    }

    public void setTotalRepsDone(int totalRepsDone) {
        m_totalRepsDone = totalRepsDone;
    }

    public int getWeight() {
        return m_weight;
    }

    public void setWeight(int weight) {
        m_weight = weight;
    }

    public int getDifficulty() {
        return m_difficulty;
    }

    public void setDifficulty(int difficulty) {
        m_difficulty = difficulty;
    }

    public void addToTotalRepsDone(int numRepsToAdd) {
        m_totalRepsDone+=numRepsToAdd;
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
        return "id: " + m_id + ", exercise id: " + m_exerciseId +
                ", total reps done: " + m_totalRepsDone + ", weight: " + m_weight +
                ", difficulty: " + m_difficulty + "\r\n";
    }

    /**
     * Inserts or updates the appropriate database record for this log daily exercise entry.
     * @param c
     */
    public void save(Context c) {
        ContentValues values = new ContentValues();
        if(m_id > 0) {
            values.put(Contract.Columns.COL_NAME_ID, m_id);
        }
        values.put(Contract.Columns.COL_NAME_DIFFICULTY, m_difficulty);
        values.put(Contract.Columns.COL_NAME_EXERCISE_ID, m_exerciseId);
        if(m_endDateTime != null) {
            values.put(Contract.Columns.COL_NAME_END_DATETIME, m_endDateTime.getTime());
        }
        if(m_startDateTime != null) {
            values.put(Contract.Columns.COL_NAME_START_DATETIME, m_startDateTime.getTime());
        }
        values.put(Contract.Columns.COL_NAME_TOTAL_REPS_DONE, m_totalRepsDone);
        values.put(Contract.Columns.COL_NAME_WEIGHT, m_weight);

        if(m_id <= 0) {
            Uri returnedRow = c.getContentResolver().insert(Contract.CONTENT_URI, values);
            m_id = ContentProviderHelper.getId(returnedRow);
        } else {
            String where = Contract.Columns.COL_NAME_ID +"=?";
            String[] selectionArgs = {Long.toString(m_id)};
            c.getContentResolver().update(Contract.CONTENT_URI, values,
                    where, selectionArgs);
        }
    }
}
