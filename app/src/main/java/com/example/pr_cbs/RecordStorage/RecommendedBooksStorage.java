package com.example.pr_cbs.RecordStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pr_cbs.Database.DBHelper;
import com.example.pr_cbs.MainActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

public class RecommendedBooksStorage {
    private ArrayList<BookRecord> localLatestRecords;


    private RecommendedBooksStorage(ArrayList<BookRecord> localLatestRecords) {
        this.localLatestRecords = localLatestRecords;
    }

    private static RecommendedBooksStorage storage = null;


    public static RecommendedBooksStorage Instance() {
        if (storage == null)
            storage = new RecommendedBooksStorage(new ArrayList<BookRecord>() {
            });

        return storage;
    }

    public int getAvailableRecordsCount() {
        return localLatestRecords.size();
    }

    public void clear() {
        localLatestRecords.clear();
    }

    public boolean downloadLatestBooks(Boolean canDownloadFromDatabase, Context context) {
        SQLiteDatabase database = DBHelper.getInstance(context).getWritableDatabase();
        boolean emptyDatabase = true;
        if (canDownloadFromDatabase) {

            Cursor cursor = database.query(DBHelper.TABLE_LATEST, null, null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                emptyDatabase = false;
                downloadLatestBooksFromDatabase(cursor);

            }


            if (cursor != null) {
                cursor.close();
            }

        }

        if (!canDownloadFromDatabase || emptyDatabase) {

            clear();
            database.execSQL("delete from latest");


            try {
                IrbisConnection connection = getIrbisConnection();
              //  int[] found = connection.search("\"T=" + "Google" + "$\"");
                int[] found = new int[]{66480, 65892, 65995, 65919, 66198, 66233, 66299, 66381, 66311};

                if (found.length != 0) {
                    for (int mfn : found) {
                        localLatestRecords.add(downloadBookRecord(mfn, connection, database));
                    }

                    String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Calendar.getInstance().getTime());


                    MainActivity.Companion.putInSharedPreferences("latest_book_update_date", currentDate, context);

                } else return false;

                connection.disconnect();


            } catch (Exception e) {
                return false;
            }
        }

        database.close();

        return true;
    }

    private void downloadLatestBooksFromDatabase(Cursor cursor ) {

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
                BookRecord latestBookRecord = new BookRecord();
                latestBookRecord.title = cursor.getString(titleIndex);
                latestBookRecord.ISBN = cursor.getString(isbnIndex);
                latestBookRecord.description = cursor.getString(descriptionIndex);
                latestBookRecord.author = cursor.getString(authorIndex);
                latestBookRecord.year = cursor.getString(yearIndex);
                latestBookRecord.publish = cursor.getString(publisherIndex);
                latestBookRecord.subjects = cursor.getString(subjectsIndex);
                latestBookRecord.series = cursor.getString(seriesIndex);
                latestBookRecord.link = cursor.getString(bookLinkIndex);
                latestBookRecord.size = cursor.getString(sizeIndex);
                latestBookRecord.lang = cursor.getString(langIndex);

                localLatestRecords.add(latestBookRecord);

            } while (cursor.moveToNext());

        } else
            Log.d("mLog", "0 rows");
    }


    public BookRecord getRecordById(int id) {
        return localLatestRecords.get(id);
    }

    private BookRecord downloadBookRecord(int mfn, IrbisConnection connection, SQLiteDatabase database) throws
            IOException, IrbisException {

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

        String size = record.fm(215, 'A');
        bookRecord.size = size;
        contentValues.put(DBHelper.KEY_SIZE, size);

        String lang = record.fm(101);
        bookRecord.lang = lang;
        contentValues.put(DBHelper.KEY_LANG, lang);

        String link = record.fm(954, 'P') + record.fm(954, 'F');
        bookRecord.link = link;
        contentValues.put(DBHelper.KEY_LINK, link);

        database.insert(DBHelper.TABLE_LATEST, null, contentValues);

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
