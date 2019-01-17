package com.bateman.rich.exercisetrack.datamodel;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;

public class LogDailyExerciseEntry {
    private long m_id;
    private long m_logId;
    private long m_exerciseId;
    private int m_totalRepsDone;
    private int m_weight;
    private int m_difficulty;

    public static class Contract {
        public static final String TABLE_NAME = "LogDailyExercises";
        public static final Uri CONTENT_URI = ContentProviderHelper.buildContentUri(ExerciseAppProvider.CONTENT_AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);
        public static final String CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(ExerciseAppProvider.CONTENT_AUTHORITY, TABLE_NAME);

        private Contract() { } // prevent instantiation

        static class Columns {
            public static final String COL_NAME_ID = BaseColumns._ID;
            public static final String COL_NAME_LOG_ID = "LogId";
            public static final String COL_NAME_EXERCISE_ID = "ExerciseId";
            public static final String COL_NAME_TOTAL_REPS_DONE = "TotalRepsDone";
            public static final String COL_NAME_WEIGHT = "Weight";
            public static final String COL_NAME_DIFFICULTY = "Difficulty";

            private Columns() { /* private constructor; no instantiation allowed */ }
        }
    }

    public LogDailyExerciseEntry(int logId, int exerciseId, int totalRepsDone, int weight, int difficulty) {
        m_logId = logId;
        m_exerciseId = exerciseId;
        m_totalRepsDone = totalRepsDone;
        m_weight = weight;
        m_difficulty = difficulty;
    }

    public LogDailyExerciseEntry(Cursor cursor) {
        m_id = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_ID));
        m_logId = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_LOG_ID));
        m_exerciseId = cursor.getLong(cursor.getColumnIndex(Contract.Columns.COL_NAME_EXERCISE_ID));
        m_totalRepsDone = cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_TOTAL_REPS_DONE));
        m_weight = cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_WEIGHT));
        m_difficulty = cursor.getInt(cursor.getColumnIndex(Contract.Columns.COL_NAME_DIFFICULTY));
    }

    public long getId() {
        return m_id;
    }

    public void setId(long id) {
        m_id = id;
    }

    public long getLogId() {
        return m_logId;
    }

    public void setLogId(int logId) {
        m_logId = logId;
    }

    public long getExerciseId() {
        return m_exerciseId;
    }

    public void setExerciseId(int exerciseId) {
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

    @Override
    public String toString() {
        return "id: " + m_id + ", log id: " + m_logId + ", exercise id: " + m_exerciseId +
                ", total reps done: " + m_totalRepsDone + ", weight: " + m_weight +
                ", difficulty: " + m_difficulty + "\r\n";
    }
}
