package com.bateman.rich.exercisetrack.datamodel;

import android.database.sqlite.SQLiteDatabase;

/**
 * Reusable class for helping with creating database functions.
 */
public class AppDatabaseHelper {

    public static final int SQLITE_INT_TRUE = 1;
    public static final int SQLITE_INT_FALSE = 0;

    /**
     * Executes a Create Trigger statement that, on deleting rows from the parent table, all child
     * rows will be deleted that the specified foreign key matches on the parent's primary key.
     * @param db
     * @param parentTableName
     * @param childTableName
     * @param parentIdColName
     * @param childFkIdColName
     */
    public static void createTriggerOnDeleteParentRecord(SQLiteDatabase db, String triggerName, String parentTableName, String childTableName,
                                                         String parentIdColName, String childFkIdColName) {
        String sSqlStatement = "CREATE TRIGGER " + triggerName
                + " AFTER DELETE ON " + parentTableName
                + " FOR EACH ROW"
                + " BEGIN"
                + " DELETE FROM " + childTableName
                + " WHERE " + childTableName + "." + childFkIdColName + " = OLD." + parentIdColName + ";"
                + " END;";
        db.execSQL(sSqlStatement);
    }
}
