package com.bateman.rich.exercisetrack.datamodel;

import android.net.Uri;

/**
 * Reeusable helper class when working with ContentProviders and Uris.
 * https://developer.android.com/guide/topics/providers/content-provider-basics
 */
public class ContentProviderHelper {
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
    public static Uri buildContentAuthorityUri(String contentAuthority) {
        Uri contentAuthorityUri = Uri.parse(CONTENT_AUTHORITY_URI_PREFIX + contentAuthority);
        return contentAuthorityUri;
    }

    /**
     * builds a content uri.
     * @param contentAuthorityUri
     * @param tableName
     * @return
     */
    public static Uri buildContentUri(Uri contentAuthorityUri, String tableName) {
        return Uri.withAppendedPath(contentAuthorityUri, tableName);
    }

    public static String buildContentTypeString(String contentAuthority, String tableName) {
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
}
