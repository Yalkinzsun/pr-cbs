package com.example.pr_cbs.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper mInstance = null;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "eventDB";
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_LATEST = "latest";
    public static final String TABLE_RECOMMENDED = "recommended";

    //константы заголовков таблицы
    private static final String KEY_ID = "_id";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_END_DATE = "end_date";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_END_TIME = "end_time";
    public static final String KEY_AGE_CATEGORY = "age_category";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDITIONAL_INFORMATION = "additional_information";
    public static final String KEY_ANNOTATION = "annotation";
    public static final String KEY_LIBRARY = "library";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_RESPONSIBLE_PERSON = "responsible_person";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_LINK = "link";

    public static final String KEY_TITLE = "title";
    public static final String KEY_ISBN = "isbn";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_YEAR = "year";
    public static final String KEY_PUBLISHER = "publish";
    public static final String KEY_SUBJECTS = "subjects";
    public static final String KEY_SERIES = "series";
    public static final String KEY_BOOK_LINK = "link";
    public static final String KEY_SIZE = "size";
    public static final String KEY_LANG = "lang";


    public static DBHelper getInstance(Context activityContext) {

        // Get the application context from the activityContext to prevent leak
        if (mInstance == null) {
            mInstance = new DBHelper(activityContext.getApplicationContext());
        }
        return mInstance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_EVENTS + "("
                + KEY_ID + " integer primary key,"
                + KEY_START_DATE + " text,"
                + KEY_END_DATE + " text,"
                + KEY_START_TIME + " text,"
                + KEY_END_TIME + " text,"
                + KEY_AGE_CATEGORY + " text,"
                + KEY_NAME + " text,"
                + KEY_ADDITIONAL_INFORMATION + " text,"
                + KEY_ANNOTATION + " text,"
                + KEY_LIBRARY + " text,"
                + KEY_ADDRESS + " text,"
                + KEY_RESPONSIBLE_PERSON + " text,"
                + KEY_PHONE_NUMBER + " text,"
                + KEY_LINK + " text"
                + ")"
        );

        sqLiteDatabase.execSQL("create table " + TABLE_LATEST + "("
                + KEY_ID + " integer primary key,"
                + KEY_TITLE + " text,"
                + KEY_ISBN + " text,"
                + KEY_DESCRIPTION + " text,"
                + KEY_AUTHOR + " text,"
                + KEY_YEAR + " text,"
                + KEY_PUBLISHER + " text,"
                + KEY_SUBJECTS + " text,"
                + KEY_SERIES + " text,"
                + KEY_BOOK_LINK + " text,"
                + KEY_SIZE + " text,"
                + KEY_LANG + " text"
                + ")"
        );

        sqLiteDatabase.execSQL("create table " + TABLE_RECOMMENDED + "("
                + KEY_ID + " integer primary key,"
                + KEY_TITLE + " text,"
                + KEY_ISBN + " text,"
                + KEY_DESCRIPTION + " text,"
                + KEY_AUTHOR + " text,"
                + KEY_YEAR + " text,"
                + KEY_PUBLISHER + " text,"
                + KEY_SUBJECTS + " text,"
                + KEY_SERIES + " text,"
                + KEY_BOOK_LINK + " text,"
                + KEY_SIZE + " text,"
                + KEY_LANG + " text"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_EVENTS);
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_LATEST);
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_RECOMMENDED);
        onCreate(sqLiteDatabase);
    }
}
