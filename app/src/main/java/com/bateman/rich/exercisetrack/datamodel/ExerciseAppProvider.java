package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Based off the AppProvider class created by Tim Buchalka in the udemy course Android Java Masterclass - Become an App Developer.
 *
 * This is the only class that knows about {@link ExerciseAppDatabase}
 *
 * In order to use the ContentProvider, you must register it in AndroidManifest.xml
 */
public class ExerciseAppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";
    private static final UriMatcher s_uriMatcher = buildUriMatcher();
    static final String CONTENT_AUTHORITY = "com.bateman.rich.exercisetrack.datamodel.provider";
    static final Uri CONTENT_AUTHORITY_URI = ContentProviderHelper.buildContentAuthorityUri(CONTENT_AUTHORITY);

    // This tree of ids is used with the UriMatcher.
    // It refers to different objects in our database.
    private static final int EXERCISE_ENTRIES = 100;
    private static final int EXERCISE_ENTRIES_ID = 101;
    private static final int DAY_SCHEDULE_ENTRIES = 200;
    private static final int DAY_SCHEDULE_ENTRIES_ID = 201;
    private static final int LOG_ENTRIES = 300;
    private static final int LOG_ENTRIES_ID = 301;
    private static final int LOG_DAILY_EXERCISE_ENTRIES = 400;
    private static final int LOG_DAILY_EXERCISE_ENTRIES_ID = 401;

    private ExerciseAppDatabase m_exerciseAppDatabase;


    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: start");
        m_exerciseAppDatabase = ExerciseAppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable // This annotation means the returned object may be null.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = s_uriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(match) {
            case EXERCISE_ENTRIES:
                queryBuilder.setTables(ExerciseEntry.Contract.TABLE_NAME);
                break;

            case EXERCISE_ENTRIES_ID:
                queryBuilder.setTables(ExerciseEntry.Contract.TABLE_NAME);
                long taskId = ContentProviderHelper.getId(uri);
                queryBuilder.appendWhere(ExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + taskId);
                break;

            case DAY_SCHEDULE_ENTRIES:
                queryBuilder.setTables(DayScheduleEntry.Contract.TABLE_NAME);
                break;

            case DAY_SCHEDULE_ENTRIES_ID:
                queryBuilder.setTables(DayScheduleEntry.Contract.TABLE_NAME);
                long dayScheduleId = ContentProviderHelper.getId(uri);
                queryBuilder.appendWhere(DayScheduleEntry.Contract.Columns.COL_NAME_ID + " = " + dayScheduleId);
                break;

            case LOG_ENTRIES:
                queryBuilder.setTables(LogEntry.Contract.TABLE_NAME);
                break;

            case LOG_ENTRIES_ID:
                queryBuilder.setTables(LogEntry.Contract.TABLE_NAME);
                long logEntryId = ContentProviderHelper.getId(uri);
                queryBuilder.appendWhere(LogEntry.Contract.Columns.COL_NAME_ID + " = " + logEntryId);
                break;

            case LOG_DAILY_EXERCISE_ENTRIES:
                queryBuilder.setTables(LogDailyExerciseEntry.Contract.TABLE_NAME);
                break;

            case LOG_DAILY_EXERCISE_ENTRIES_ID:
                queryBuilder.setTables(LogDailyExerciseEntry.Contract.TABLE_NAME);
                long logDailyExerciseEntryid = ContentProviderHelper.getId(uri);
                queryBuilder.appendWhere(LogDailyExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + logDailyExerciseEntryid);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        SQLiteDatabase db = m_exerciseAppDatabase.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: rows in returned cursor = " + cursor.getCount());
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = s_uriMatcher.match(uri);
        switch (match) {
            case EXERCISE_ENTRIES:
                return ExerciseEntry.Contract.CONTENT_TYPE;
            case EXERCISE_ENTRIES_ID:
                return ExerciseEntry.Contract.CONTENT_ITEM_TYPE;

            case DAY_SCHEDULE_ENTRIES:
                return DayScheduleEntry.Contract.CONTENT_TYPE;
            case DAY_SCHEDULE_ENTRIES_ID:
                return DayScheduleEntry.Contract.CONTENT_ITEM_TYPE;

            case LOG_ENTRIES:
                return LogEntry.Contract.CONTENT_TYPE;
            case LOG_ENTRIES_ID:
                return LogEntry.Contract.CONTENT_ITEM_TYPE;

            case LOG_DAILY_EXERCISE_ENTRIES:
                return LogDailyExerciseEntry.Contract.CONTENT_TYPE;
            case LOG_DAILY_EXERCISE_ENTRIES_ID:
                return LogDailyExerciseEntry.Contract.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "Entering insert, called with uri:" + uri);
        final int match = s_uriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch(match) {
            case EXERCISE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                recordId = db.insert(ExerciseEntry.Contract.TABLE_NAME, null, values);
                if(recordId >=0) {
                    returnUri = ContentProviderHelper.buildUriFromId(ExerciseEntry.Contract.CONTENT_URI, recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            case DAY_SCHEDULE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                recordId = db.insert(DayScheduleEntry.Contract.TABLE_NAME, null, values);
                if(recordId >=0) {
                    returnUri = ContentProviderHelper.buildUriFromId(DayScheduleEntry.Contract.CONTENT_URI, recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            case LOG_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                recordId = db.insert(LogEntry.Contract.TABLE_NAME, null, values);
                if(recordId >=0) {
                    returnUri = ContentProviderHelper.buildUriFromId(LogEntry.Contract.CONTENT_URI, recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            case LOG_DAILY_EXERCISE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                recordId = db.insert(LogDailyExerciseEntry.Contract.TABLE_NAME, null, values);
                if(recordId >=0) {
                    returnUri = ContentProviderHelper.buildUriFromId(LogDailyExerciseEntry.Contract.CONTENT_URI, recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (recordId >= 0) {
            // something was inserted
            Log.d(TAG, "insert: Setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "insert: nothing inserted");
        }

        Log.d(TAG, "Exiting insert, returning " + returnUri);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        final int match = s_uriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch(match) {
            case EXERCISE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.delete(ExerciseEntry.Contract.TABLE_NAME, selection, selectionArgs);
                break;

            case EXERCISE_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long exerciseEntryId = ContentProviderHelper.getId(uri);
                selectionCriteria = ExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + exerciseEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ExerciseEntry.Contract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case DAY_SCHEDULE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.delete(DayScheduleEntry.Contract.TABLE_NAME, selection, selectionArgs);
                break;

            case DAY_SCHEDULE_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long dayScheduleEntryId = ContentProviderHelper.getId(uri);
                selectionCriteria = DayScheduleEntry.Contract.Columns.COL_NAME_ID + " = " + dayScheduleEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(DayScheduleEntry.Contract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case LOG_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.delete(LogEntry.Contract.TABLE_NAME, selection, selectionArgs);
                break;

            case LOG_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long logEntryId = ContentProviderHelper.getId(uri);
                selectionCriteria = LogEntry.Contract.Columns.COL_NAME_ID + " = " + logEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(LogEntry.Contract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            case LOG_DAILY_EXERCISE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.delete(LogDailyExerciseEntry.Contract.TABLE_NAME, selection, selectionArgs);
                break;

            case LOG_DAILY_EXERCISE_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long logDailyExerciseEntryId = ContentProviderHelper.getId(uri);
                selectionCriteria = LogDailyExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + logDailyExerciseEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(LogDailyExerciseEntry.Contract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if(count > 0) {
            // something was deleted
            Log.d(TAG, "delete: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "delete: nothing deleted");
        }

        Log.d(TAG, "Exiting update, returning " + count);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        final int match = s_uriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch(match) {
            case EXERCISE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.update(ExerciseEntry.Contract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case EXERCISE_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long exerciseEntryId = ContentProviderHelper.getId(uri);
                selectionCriteria = ExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + exerciseEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ExerciseEntry.Contract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            case DAY_SCHEDULE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.update(DayScheduleEntry.Contract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case DAY_SCHEDULE_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long dayScheduleId = ContentProviderHelper.getId(uri);
                selectionCriteria = DayScheduleEntry.Contract.Columns.COL_NAME_ID + " = " + dayScheduleId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(DayScheduleEntry.Contract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            case LOG_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.update(LogEntry.Contract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case LOG_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long logEntryId = ContentProviderHelper.getId(uri);
                selectionCriteria = LogEntry.Contract.Columns.COL_NAME_ID + " = " + logEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(LogEntry.Contract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;
            case LOG_DAILY_EXERCISE_ENTRIES:
                db = m_exerciseAppDatabase.getWritableDatabase();
                count = db.update(LogDailyExerciseEntry.Contract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case LOG_DAILY_EXERCISE_ENTRIES_ID:
                db = m_exerciseAppDatabase.getWritableDatabase();
                long logDailyExerciseEntryId = ContentProviderHelper.getId(uri);
                selectionCriteria = LogDailyExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + logDailyExerciseEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(LogDailyExerciseEntry.Contract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if(count > 0) {
            // something was deleted
            Log.d(TAG, "update: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing deleted");
        }

        Log.d(TAG, "Exiting update, returning " + count);
        return count;
    }
    private static UriMatcher buildUriMatcher() {
        Log.d(TAG, "buildUriMatcher: start");
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/ExerciseEntries
        matcher.addURI(CONTENT_AUTHORITY, ExerciseEntry.Contract.TABLE_NAME, EXERCISE_ENTRIES);
        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/ExerciseEntries/8 (8 representing an arbitrary ID number)
        matcher.addURI(CONTENT_AUTHORITY, ContentProviderHelper.buildUriPathForId(ExerciseEntry.Contract.TABLE_NAME), EXERCISE_ENTRIES_ID);

        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/DayScheduleEnrties
        matcher.addURI(CONTENT_AUTHORITY, DayScheduleEntry.Contract.TABLE_NAME, DAY_SCHEDULE_ENTRIES);
        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/ExerciseEntries/8 (8 representing an arbitrary ID number)
        matcher.addURI(CONTENT_AUTHORITY, ContentProviderHelper.buildUriPathForId(DayScheduleEntry.Contract.TABLE_NAME), DAY_SCHEDULE_ENTRIES_ID);

        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/LogEntry
        matcher.addURI(CONTENT_AUTHORITY, LogEntry.Contract.TABLE_NAME, LOG_ENTRIES);
        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/LogEntry/8 (8 representing an arbitrary ID number)
        matcher.addURI(CONTENT_AUTHORITY, ContentProviderHelper.buildUriPathForId(LogEntry.Contract.TABLE_NAME), LOG_ENTRIES_ID);

        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/LogDailyExerciseEntry
        matcher.addURI(CONTENT_AUTHORITY, LogDailyExerciseEntry.Contract.TABLE_NAME, LOG_DAILY_EXERCISE_ENTRIES);
        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/LogDailyExerciseEntry/8 (8 representing an arbitrary ID number)
        matcher.addURI(CONTENT_AUTHORITY, ContentProviderHelper.buildUriPathForId(LogDailyExerciseEntry.Contract.TABLE_NAME), LOG_DAILY_EXERCISE_ENTRIES_ID);

        Log.d(TAG, "buildUriMatcher: end.  returning UriMatcher: " + matcher);
        return matcher;
    }

}
