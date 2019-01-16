package com.bateman.rich.exercisetrack.datamodel;

import android.provider.BaseColumns;

import java.util.Date;

public class LogDailyExerciseEntry {
    private int m_id;
    private int m_logId;
    private int m_exerciseId;
    private int m_totalRepsDone;
    private int m_weight;
    private int m_difficulty;

    private static final LogDailyExerciseEntry.Contract s_contract = new LogDailyExerciseEntry.Contract();

    public static LogDailyExerciseEntry.Contract getContract() {return s_contract;}

    static class Contract extends DatabaseContractBase {
        private Contract() {
            super(ExerciseAppProvider.CONTENT_AUTHORITY_URI, ExerciseAppProvider.CONTENT_AUTHORITY, "Log");
        }

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

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public int getLogId() {
        return m_logId;
    }

    public void setLogId(int logId) {
        m_logId = logId;
    }

    public int getExerciseId() {
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
}
