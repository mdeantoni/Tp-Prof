package com.example.matias.tprof.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Mati on 12/8/2015.
 */
public class QuotesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private QuotesDbHelper mOpenHelper;

    static final int STOCK = 100;
    static final int STOCK_QUOTE = 150;
    static final int BOND = 200;
    static final int BOND_QUOTE = 250;

    private static final SQLiteQueryBuilder sStockQuotesQueryBuilder;
    private static final SQLiteQueryBuilder sBondQuotesQueryBuilder;

    static{
        sStockQuotesQueryBuilder = new SQLiteQueryBuilder();
        sBondQuotesQueryBuilder = new SQLiteQueryBuilder();

        sStockQuotesQueryBuilder.setTables(
                QuotesContract.StockEntry.TABLE_NAME + " INNER JOIN " +
                        QuotesContract.StockQuotesEntry.TABLE_NAME +
                        " ON " + QuotesContract.StockEntry.TABLE_NAME +
                        "." + QuotesContract.StockEntry._ID +
                        " = " + QuotesContract.StockQuotesEntry.TABLE_NAME +
                        "." + QuotesContract.StockQuotesEntry.COLUMN_STOCK_ID);

        sBondQuotesQueryBuilder.setTables(
                QuotesContract.BondEntry.TABLE_NAME + " INNER JOIN " +
                        QuotesContract.BondQuotesEntry.TABLE_NAME +
                        " ON " + QuotesContract.BondEntry.TABLE_NAME +
                        "." + QuotesContract.BondEntry._ID +
                        " = " + QuotesContract.BondQuotesEntry.TABLE_NAME +
                        "." + QuotesContract.BondQuotesEntry.COLUMN_BOND_ID);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = QuotesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, QuotesContract.PATH_BONDS, BOND);
        matcher.addURI(authority, QuotesContract.PATH_STOCKS, STOCK);
        matcher.addURI(authority, QuotesContract.PATH_STOCK_QUOTES, STOCK_QUOTE);
        matcher.addURI(authority, QuotesContract.PATH_BOND_QUOTES, BOND_QUOTE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new QuotesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case STOCK: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.StockEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BOND: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        QuotesContract.BondEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case STOCK_QUOTE: {
                retCursor = sStockQuotesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case BOND_QUOTE: {
                retCursor = sBondQuotesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case STOCK:
                return QuotesContract.StockEntry.CONTENT_TYPE;
            case BOND:
                return QuotesContract.BondEntry.CONTENT_TYPE;
            case STOCK_QUOTE:
                return QuotesContract.StockQuotesEntry.CONTENT_TYPE;
            case BOND_QUOTE:
                return QuotesContract.BondQuotesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STOCK: {
                long _id = db.insert(QuotesContract.StockEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.StockEntry.buildStockUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BOND: {
                long _id = db.insert(QuotesContract.BondEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.BondEntry.buildBondUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STOCK_QUOTE: {
                long _id = db.insert(QuotesContract.StockQuotesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.StockQuotesEntry.buildStockQuoteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BOND_QUOTE: {
                long _id = db.insert(QuotesContract.BondQuotesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = QuotesContract.BondQuotesEntry.buildBondQuoteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case BOND:
                rowsDeleted = db.delete(
                        QuotesContract.BondEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK:
                rowsDeleted = db.delete(
                        QuotesContract.StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_QUOTE:
                rowsDeleted = db.delete(
                        QuotesContract.StockQuotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOND_QUOTE:
                rowsDeleted = db.delete(
                        QuotesContract.BondQuotesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case STOCK:
                rowsUpdated = db.update(QuotesContract.StockEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BOND:
                rowsUpdated = db.update(QuotesContract.BondEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STOCK_QUOTE:
                rowsUpdated = db.update(QuotesContract.StockQuotesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BOND_QUOTE:
                rowsUpdated = db.update(QuotesContract.BondQuotesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case STOCK:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.StockEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BOND:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.BondEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case STOCK_QUOTE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.StockQuotesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BOND_QUOTE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(QuotesContract.BondQuotesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}