//package com.bateman.rich.exercisetrack.datamodel;
//
//import android.content.ContentUris;
//import android.net.Uri;
//import android.support.annotation.NonNull;
//
//import com.bateman.rich.rmblibrary.persistence.ContentProviderHelper;

/**
 * Reusable class meant to facilitate creating contracts for data objects meant to be
 * used with a content provider.
 *
 *
 *  I had tried creating a "DatabaseContractBase" which would handle creating the CONTENT URI, CONTENT_ITEM_TYPE, etc.
 *  UNFORTUNATELY, those require that the ExerciseAppProvider already be setup.  The ExerciseAppProvider, when it
 *  is initializing its static data... DEPENDS on all these Contract classes.  So there is an insidious cycle of dependencies.
 *  To eliminate this problem, Contracts will ALWAYS be purely static.
 *
 */

//
//public class DatabaseContractBase {
//    final String TABLE_NAME;
//    public final Uri CONTENT_URI;
//    final String CONTENT_TYPE;
//    final String CONTENT_ITEM_TYPE;
//
//    DatabaseContractBase(@NonNull Uri contentAuthorityUri, @NonNull String contentAuthority, @NonNull String tableName) {
//        if(contentAuthorityUri == null) throw new IllegalArgumentException("null contentAuthorityUri passed.");
//        if(contentAuthority == null) throw new IllegalArgumentException("null contentAuthority passed.");
//        if(tableName == null) throw new IllegalArgumentException("null tableName passed.");
//
//        TABLE_NAME = tableName;
//        CONTENT_URI = ContentProviderHelper.buildContentUri(contentAuthorityUri, TABLE_NAME);
//        CONTENT_TYPE = ContentProviderHelper.buildContentTypeString(contentAuthority, TABLE_NAME);
//        CONTENT_ITEM_TYPE = ContentProviderHelper.buildContentItemTypeString(contentAuthority, TABLE_NAME);
//    }
//
//
//}
