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
 * This is the only class that knows about {@link AppDatabase}
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

    private AppDatabase m_appDatabase;


    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: start");
        m_appDatabase = AppDatabase.getInstance(getContext());
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
                queryBuilder.setTables(ExerciseEntry.getContract().TABLE_NAME);
                break;

            case EXERCISE_ENTRIES_ID:
                queryBuilder.setTables(ExerciseEntry.getContract().TABLE_NAME);
                long taskId = ExerciseEntry.getContract().getId(uri);
                queryBuilder.appendWhere(ExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + taskId);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        SQLiteDatabase db = m_appDatabase.getReadableDatabase();
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
                return ExerciseEntry.getContract().CONTENT_TYPE;

            case EXERCISE_ENTRIES_ID:
                return ExerciseEntry.getContract().CONTENT_ITEM_TYPE;

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
                db = m_appDatabase.getWritableDatabase();
                recordId = db.insert(ExerciseEntry.getContract().TABLE_NAME, null, values);
                if(recordId >=0) {
                    returnUri = ExerciseEntry.getContract().buildUriFromId(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case EXERCISE_ENTRIES_ID:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(TimingsContract.Timings.buildTimingUri(recordId));
//                if(recordId >=0) {
//                    returnUri = TimingsContract.Timings.buildTimingUri(recordId);
//                } else {
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }
//                break;

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
                db = m_appDatabase.getWritableDatabase();
                count = db.delete(ExerciseEntry.getContract().TABLE_NAME, selection, selectionArgs);
                break;

            case EXERCISE_ENTRIES_ID:
                db = m_appDatabase.getWritableDatabase();
                long exerciseEntryId = ExerciseEntry.getContract().getId(uri);
                selectionCriteria = ExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + exerciseEntryId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ExerciseEntry.getContract().TABLE_NAME, selectionCriteria, selectionArgs);
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
                db = m_appDatabase.getWritableDatabase();
                count = db.update(ExerciseEntry.getContract().TABLE_NAME, values, selection, selectionArgs);
                break;

            case EXERCISE_ENTRIES_ID:
                db = m_appDatabase.getWritableDatabase();
                long taskId = ExerciseEntry.getContract().getId(uri);
                selectionCriteria = ExerciseEntry.Contract.Columns.COL_NAME_ID + " = " + taskId;

                if((selection != null) && (selection.length()>0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ExerciseEntry.getContract().TABLE_NAME, values, selectionCriteria, selectionArgs);
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
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/ExerciseEntries
        matcher.addURI(CONTENT_AUTHORITY, ExerciseEntry.getContract().TABLE_NAME, EXERCISE_ENTRIES);
        //  eg. content://com.bateman.rich.exercisetrack.datamodel.provider/ExerciseEntries/8 (8 representing an arbitrary ID number)
        matcher.addURI(CONTENT_AUTHORITY, ContentProviderHelper.buildUriPathForId(ExerciseEntry.getContract().TABLE_NAME), EXERCISE_ENTRIES_ID);

        return matcher;
    }

}
