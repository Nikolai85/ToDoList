package com.example.todolist;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

public class ToDoContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri
            .parse("content://com.example.todoprovider/todoitems");

    // Колонки для таблицы
    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task";
    public static final String KEY_CREATION_DATE = "creation_date";

    private MySQLiteOpenHelper myOpenHelper;

    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final UriMatcher uriMatcher;
    // Если URI заканчивается todoitems, то запрос направлен ко всем элементам
    // todoitems/[rowID] представляет одиночную строку
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.example.todoprovider", "todoitems",
                ALLROWS);
        uriMatcher.addURI("com.example.todoprovider", "todoitems/#",
                SINGLE_ROW);
    }

    @Override
    public boolean onCreate() {
        myOpenHelper = new MySQLiteOpenHelper(getContext(),
                MySQLiteOpenHelper.DATABASE_NAME, null,
                MySQLiteOpenHelper.DATABASE_VERSION);

        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {
            case ALLROWS:
                return "vnd.android.cursor.dir/vnd.example.todos";
            case SINGLE_ROW:
                return "vnd.android.cursor.item/vnd.example.todos";
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Открытие базы данных для чтения
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);

        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" + rowID);

            default:
                break;
        }
        return queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE, null,
                values);

        if (id > -1) {
            Uri insertedID = ContentUris.withAppendedId(CONTENT_URI, id);

            getContext().getContentResolver().notifyChange(insertedID, null);

            return insertedID;
        } else
            return null;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID
                        + "="
                        + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : "");
            default:
                break;
        }

        if (selection == null)
            selection = "1";

        int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE,
                selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = myOpenHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");

            default:
                break;
        }


        int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE, values, selection, selectionArgs);


        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "tododatabase.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "todoItemTable";

        // SQL-выражение для создания новой базы данных
        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + " (" + KEY_ID
                + " integer primary key autoincrement, " + KEY_TASK
                + " text not null, " + KEY_CREATION_DATE + " long);";

        public MySQLiteOpenHelper(Context context, String name,
                                  CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
}