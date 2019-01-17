package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Reeusable helper class when working with ContentProviders and Uris.
 * https://developer.android.com/guide/topics/providers/content-provider-basics
 */
public class ContentProviderHelper {
    private static final String TAG = "ContentProviderHelper";
    private static final String CONTENT_AUTHORITY_URI_PREFIX = "content://";
    private static final String PATH_ID_SEPARATOR = "/#";
    private static final String CONTENT_TYPE_PREFIX = "vnd.android.cursor.dir/vnd.";
    private static final String CONTENT_ITEM_TYPE_PREFIX = "vnd.android.cursor.item/vnd.";
    private static final String CONTENT_TYPE_TABLE_PREFIX = ".";

    /**
     * Given a content authority string (which seems like a value that represent your app, to distinguish it
     * from other apps, so your package name seems valid, like "com.bateman.rich.exercisetrack".
     * @param contentAuthority A string representing the Content Authority name.
     * @return A Uri representing the content authority.
     */
    public static Uri buildContentAuthorityUri(@NonNull String contentAuthority) {
        Log.d(TAG, "buildContentAuthorityUri: start");
        if(contentAuthority == null) throw new IllegalArgumentException("null contentAuthority passed.");
        String uriStr = CONTENT_AUTHORITY_URI_PREFIX + contentAuthority;
        Log.d(TAG, "buildContentAuthorityUri: Parsing: " + uriStr);
        Uri contentAuthorityUri = Uri.parse(uriStr);
        if(contentAuthorityUri == null) throw new IllegalStateException("A null contentAuthorityUri was returned from Uri.parse.");
        return contentAuthorityUri;
    }

    /**
     * builds a content uri.
     * @param contentAuthorityUri
     * @param tableName
     * @return
     */
    public static Uri buildContentUri(@NonNull Uri contentAuthorityUri, @NonNull String tableName) {
        if(contentAuthorityUri == null) throw new IllegalArgumentException("null contentAuthorityUri passed.");
        if(tableName == null) throw new IllegalArgumentException("null tableName passed.");

        return Uri.withAppendedPath(contentAuthorityUri, tableName);
    }

    public static String buildContentTypeString(@NonNull String contentAuthority, @NonNull String tableName) {
        return CONTENT_TYPE_PREFIX + contentAuthority + CONTENT_TYPE_TABLE_PREFIX + tableName;
    }

    public static String buildContentItemTypeString(String contentAuthority, String tableName) {
        return CONTENT_ITEM_TYPE_PREFIX + contentAuthority + CONTENT_TYPE_TABLE_PREFIX + tableName;
    }

    /**
     * Creates a URI that represents some table and will be used in conjunction with an id.
     * This function basically just appends a "/#" to the table name supplied.
     * @param tableName
     * @return
     */
    public static String buildUriPathForId(String tableName) {
        return tableName + PATH_ID_SEPARATOR;
    }

    /**
     * Given a URI, returns the id.
     * @param uri
     * @return
     */
    public static long getId(Uri uri) {return ContentUris.parseId(uri);}

    /**
     * Builds a URI given a Content URI and an id.
     * @param contentUri
     * @param id
     * @return
     */
    public static Uri buildUriFromId(Uri contentUri, long id) {return ContentUris.withAppendedId(contentUri, id);}
}
