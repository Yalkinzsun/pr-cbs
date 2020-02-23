package com.example.pr_cbs.RecordStorage;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pr_cbs.Database.DBHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

public class RecommendedBookStorage {
    private ArrayList<BookRecord> localRecords;


    private RecommendedBookStorage(ArrayList<BookRecord> localRecords) {
        this.localRecords = localRecords;
    }

    private static RecommendedBookStorage storage = null;

    String APP_PREFERENCES = "pref_settings";
    String APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE = "recommended_book_update_date";
    DBHelper dbHelper;

    public static RecommendedBookStorage Instance() {
        if (storage == null)
            storage = new RecommendedBookStorage(new ArrayList<BookRecord>() {});

        return storage;
    }

    public int getAvailableRecordsCount() {
        return localRecords.size();
    }

    public void clear() {
        localRecords.clear();
    }

    public boolean downloadRecommendedBooks(Boolean canDownloadFromDatabase, Context context) {

        boolean emptyDatabase = true;
        if (canDownloadFromDatabase) {
            SQLiteDatabase database = DBHelper.getInstance(context).getWritableDatabase();
            Cursor cursor = database.query(DBHelper.TABLE_RECOMMENDED, null, null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                emptyDatabase = false;
                downloadRecommendedBooksFromDatabase(context);
                cursor.close();
            }

        }

        if (!canDownloadFromDatabase || emptyDatabase){
            clear();
            SQLiteDatabase database = DBHelper.getInstance(context).getWritableDatabase();
            //Очистка содержимого таблицы events
            database.execSQL("delete from latest");

            try {
                IrbisConnection connection = getIrbisConnection();
                int[] found = connection.search("\"T=" + "Microsoft" + "$\"");


                if (found.length != 0) {
                    for (int mfn : found) {
                        localRecords.add(downloadBookRecord(mfn, connection, context));
                    }
                } else return false;

                connection.disconnect();

            } catch (Exception e) {
                //Log.e(...)
                return false;
            }
        }
        return true;
    }

    public void downloadRecommendedBooksFromDatabase(Context context) {
        dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_RECOMMENDED, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
            int isbnIndex = cursor.getColumnIndex(DBHelper.KEY_ISBN);
            int descriptionIndex = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION);
            int authorIndex = cursor.getColumnIndex(DBHelper.KEY_AUTHOR);
            int yearIndex = cursor.getColumnIndex(DBHelper.KEY_YEAR);
            int publisherIndex = cursor.getColumnIndex(DBHelper.KEY_PUBLISHER);
            int subjectsIndex = cursor.getColumnIndex(DBHelper.KEY_SUBJECTS);
            int seriesIndex = cursor.getColumnIndex(DBHelper.KEY_SERIES);
            int bookLinkIndex = cursor.getColumnIndex(DBHelper.KEY_BOOK_LINK);
            int sizeIndex = cursor.getColumnIndex(DBHelper.KEY_SIZE);
            int langIndex = cursor.getColumnIndex(DBHelper.KEY_LANG);

            do {
                BookRecord recommendedBookRecord = new BookRecord();
                recommendedBookRecord.title = cursor.getString(titleIndex);
                recommendedBookRecord.ISBN = cursor.getString(isbnIndex);
                recommendedBookRecord.description = cursor.getString(descriptionIndex);
                recommendedBookRecord.author = cursor.getString(authorIndex);
                recommendedBookRecord.year = cursor.getString(yearIndex);
                recommendedBookRecord.publish = cursor.getString(publisherIndex);
                recommendedBookRecord.subjects = cursor.getString(subjectsIndex);
                recommendedBookRecord.series = cursor.getString(seriesIndex);
                recommendedBookRecord.link= cursor.getString(bookLinkIndex);
                recommendedBookRecord.size = cursor.getString(sizeIndex);
                recommendedBookRecord.lang = cursor.getString(langIndex);

                localRecords.add(recommendedBookRecord);

            } while (cursor.moveToNext());

        } else
            Log.d("mLog", "0 rows");

        cursor.close();
    }


    public BookRecord getRecordById(int id) {
        return localRecords.get(id);
    }

    private BookRecord downloadBookRecord(int mfn, IrbisConnection connection, Context context) throws
            IOException, IrbisException {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime());

        SharedPreferences sPref = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(APP_PREFERENCES_RECOMMENDED_BOOK_UPDATE_DATE, currentDate);
        editor.apply();


        SQLiteDatabase database = DBHelper.getInstance(context).getWritableDatabase();

        // класс для добавления новых строк в таблицу БД
        ContentValues contentValues = new ContentValues();

        MarcRecord record = connection.readRecord(mfn);
        BookRecord bookRecord = new BookRecord();

        String title = record.fm(200, 'a');
        bookRecord.title = title;
        contentValues.put(DBHelper.KEY_TITLE, title);

        String ISBN = record.fm(10, 'A');
        bookRecord.ISBN = ISBN;
        contentValues.put(DBHelper.KEY_ISBN, ISBN);

        String author = record.fm(700, 'A') + ' ' + record.fm(700, 'B');
        bookRecord.author = author;
        contentValues.put(DBHelper.KEY_AUTHOR, author);

        String year = record.fm(210, 'D');
        bookRecord.year = year;
        contentValues.put(DBHelper.KEY_YEAR, year);

        String description = connection.formatRecord("@brief", mfn);
        bookRecord.description = description;
        contentValues.put(DBHelper.KEY_DESCRIPTION, description);

        String publish = record.fm(210, 'A') + record.fm(210, 'C');
        bookRecord.publish = publish;
        contentValues.put(DBHelper.KEY_PUBLISHER, publish);

        String subjects = record.fm(606, 'A');
        bookRecord.subjects = subjects;
        contentValues.put(DBHelper.KEY_SUBJECTS, subjects);

        String series = record.fm(225, 'A');
        bookRecord.series = series;
        contentValues.put(DBHelper.KEY_SERIES, series);

        String size = record.fm(225, 'A');
        bookRecord.size = size;
        contentValues.put(DBHelper.KEY_SIZE, size);

        String lang = record.fm(101);
        bookRecord.lang = lang;
        contentValues.put(DBHelper.KEY_LANG, lang);

        String link = record.fm(954, 'P') + record.fm(954, 'F');
        bookRecord.link = link;
        contentValues.put(DBHelper.KEY_LINK, link);

        database.insert(DBHelper.TABLE_RECOMMENDED, null, contentValues);

        return bookRecord;
    }

    private IrbisConnection getIrbisConnection() throws IrbisException, IOException {
        IrbisConnection connection = new IrbisConnection();
        connection.parseConnectionString("host=194.186.155.14;" +
                "port=1192;database=BDP%SERV12%;" +
                "user=12_AGENT_MOB;password=agentmob;");
        connection.connect();

        return connection;
    }

}
