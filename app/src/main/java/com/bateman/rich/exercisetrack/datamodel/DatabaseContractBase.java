package com.bateman.rich.exercisetrack.datamodel;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Reusable class meant to facilitate creating contracts for data objects meant to be
 * used with a content provider.
 */
public class DatabaseContractBase {
    protected final String TABLE_NAME;
    protected final Uri CONTENT_URI;
    protected final String CONTENT_TYPE;
    protected final String CONTENT_ITEM_TYPE;

    protected DatabaseContractBase(Uri contentAuthorityUri, String contentAuthority, String tableName) {
        TABLE_NAME = tableName;
        CONTENT_URI = ContentProviderHelper.buildContentUri(contentAuthorityUri, TABLE_NAME);
        CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(contentAuthority, TABLE_NAME);
        CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(contentAuthority, TABLE_NAME);
    }

    public long getId(Uri uri) {return ContentUris.parseId(uri);}
    public Uri buildUriFromId(long id) {return ContentUris.withAppendedId(CONTENT_URI, id);}
}
