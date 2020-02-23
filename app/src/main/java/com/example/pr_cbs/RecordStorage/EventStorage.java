package com.example.pr_cbs.RecordStorage;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.pr_cbs.Database.DBHelper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EventStorage {
    //  private String link;
    private ArrayList<EventRecord> localEventRecords;
    private ArrayList<Integer> localMFNs;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private static EventStorage storage = null;
    DBHelper dbHelper;
    SharedPreferences sPref;
    SharedPreferences.Editor editor;
    String APP_PREFERENCES = "pref_settings";
    String APP_PREFERENCES_EVENT_UPDATE_DATE = "event_update_date";


    private EventStorage(ArrayList<EventRecord> localEventRecords, ArrayList<Integer> localMFNs) {
        this.localEventRecords = localEventRecords;
        this.localMFNs = localMFNs;
    }

    public EventRecord test (int position) {
        return localEventRecords.get(position);
    }

    public static EventStorage Instance() {
        if (storage == null)
            storage = new EventStorage(new ArrayList<EventRecord>() {
            }, new ArrayList<Integer>() {
            });
        return storage;
    }

    public int getAvailableRecordsCount() {
        return localEventRecords.size();
    }

    public void clear() {
        localEventRecords.clear();
        localMFNs.clear();
        currentPage = 0;
    }

    public boolean fetchMFNsByQuery(String query) {

        clear();

        try {
            IrbisConnection connection = getIrbisConnection();
            int[] found = connection.search("\"EVENTNAME=" + query + "$\"");

            connection.disconnect();

            if (found.length != 0) {
                for (int mfn : found) {
                    localMFNs.add(mfn);

                }
            } else return false;


        } catch (Exception e) {
            //Log.e(...)
            return false;
        }


        return true;
    }


    public boolean canLoadMore() {
        return localMFNs.size() > localEventRecords.size();
    }


    public boolean loadAllActualEvents(Context mContext, Boolean reload) {
        clear();

        SQLiteDatabase database = DBHelper.getInstance(mContext).getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null);

        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime());
        boolean isTheDateRelevant = false;
        String savedDate;
        Date current_date = null;
        Date saved_date = null;


        if (!reload) {
            sPref = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            if (sPref.contains(APP_PREFERENCES_EVENT_UPDATE_DATE)) {
                savedDate = sPref.getString(APP_PREFERENCES_EVENT_UPDATE_DATE, "");


                try {
                    current_date = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(currentDate);
                    saved_date = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(savedDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            if (current_date != null && saved_date != null) {
                long diff = current_date.getTime() - saved_date.getTime();
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                isTheDateRelevant = hours < 10;
            }

            if (isTheDateRelevant) {

                if (cursor != null && cursor.getCount() > 0) {
                    downloadEventFromDatabase(mContext);
                } else downloadActualEventRecord(currentDate, mContext);

            } else downloadActualEventRecord(currentDate, mContext);

        } else downloadActualEventRecord(currentDate, mContext);

        if (cursor != null) cursor.close();
        database.close();
        return true;

    }

    private void downloadActualEventRecord(String currentDate, Context mContext) {
        sPref = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sPref.edit();
        editor.putString(APP_PREFERENCES_EVENT_UPDATE_DATE, currentDate);
        editor.apply();

        SQLiteDatabase database = DBHelper.getInstance(mContext).getWritableDatabase();
        //Очистка содержимого таблицы events
        database.execSQL("delete from events");

        // класс для добавления новых строк в таблицу БД
        ContentValues contentValues = new ContentValues();

        try {
            IrbisConnection connection = getIrbisConnection();

            int[] found = connection.search("\"EVENTNAME=$\"*\"MONTHLY=2020$\"");
            found = Arrays.copyOf(found, 100);
            if (found.length != 0) {

                for (int mfn : found) {

                    String d = connection.formatRecord("@brief", mfn);
                    String eventDate = d.substring(d.indexOf("[") + 2, d.indexOf("]") - 1);


                    Date event_date = new SimpleDateFormat("dd.MM.yyyy").parse(eventDate);
                    Date date_2 = new SimpleDateFormat("dd.MM.yyyy").parse(currentDate);

                    if (date_2 != null && event_date != null && event_date.getTime() >= date_2.getTime())
                        localMFNs.add(mfn);
                }

                Log.v("Tag", Integer.toString(localMFNs.size()) );

            }

            connection.disconnect();

            for (int i = 0; i < localMFNs.size(); i++) {
                int mfn = localMFNs.get(i);

                //localEventRecords.add(downloadEventRecord(mfn, connection, mContext));
                IrbisConnection connect = getIrbisConnection();
                EventRecord eventRecord = new EventRecord();
                MarcRecord record = connect.readRecord(mfn);

                String link = record.fm(107, 'P') + record.fm(107, 'F') ;
                eventRecord.link = link;
                contentValues.put(DBHelper.KEY_LINK, link);

                String description2 = connect.formatRecord("@", mfn);

                Document html = Jsoup.parse(description2);
                Element table = html.select("table").get(0);
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    Elements cols = row.select("td");

                    for (Element col : cols) {
                        Elements spans = col.select("span");
                        for (int m = 0; m < spans.size(); m++) {

                            if (spans.get(m).text().equals("Дата начала:")) {
                                String start_date = spans.get(m + 1).text();
                                eventRecord.start_date = start_date;
                                contentValues.put(DBHelper.KEY_START_DATE, start_date);
                            }

                            if (spans.get(m).text().equals("Дата окончания:")) {
                                String end_date = spans.get(m + 1).text();
                                eventRecord.end_date = end_date;
                                contentValues.put(DBHelper.KEY_END_DATE, end_date);
                            }

                            if (spans.get(m).text().equals("Время начала:")) {
                                String start_time = spans.get(m + 1).text();
                                eventRecord.start_time = start_time;
                                contentValues.put(DBHelper.KEY_START_TIME, start_time);
                            }

                            if (spans.get(m).text().equals("Время окончания:")) {
                                String end_time = spans.get(m + 1).text();
                                eventRecord.end_time = end_time;
                                contentValues.put(DBHelper.KEY_END_TIME, end_time);
                            }

                            if (spans.get(m).text().equals("Возрастная категория:")) {
                                String age_category = spans.get(m + 1).text();
                                eventRecord.age_category = age_category;
                                contentValues.put(DBHelper.KEY_AGE_CATEGORY, age_category);
                            }

                            if (spans.get(m).text().equals("Наименование мероприятия:")) {
                                String name = spans.get(m + 1).text();
                                eventRecord.name = name;
                                contentValues.put(DBHelper.KEY_NAME, name);
                            }

                            if (spans.get(m).text().equals("Дополнительные сведения:")) {
                                String additional_information = spans.get(m + 1).text();
                                eventRecord.additional_information = additional_information;
                                contentValues.put(DBHelper.KEY_ADDITIONAL_INFORMATION, additional_information);

                            }

                            if (spans.get(m).text().equals("Аннотация к мероприятию:")) {
                                String annotation = spans.get(m + 1).text();
                                eventRecord.annotation = annotation;
                                contentValues.put(DBHelper.KEY_ANNOTATION, annotation);
                            }

                            if (spans.get(m).text().equals("Библиотека-организатор:")) {
                                String library = spans.get(m + 1).text();
                                eventRecord.library = library;
                                contentValues.put(DBHelper.KEY_LIBRARY, library);
                            }

                            if (spans.get(m).text().equals("Адрес проведения мероприятия:")) {
                                String address = spans.get(m + 1).text();
                                eventRecord.address = address;
                                contentValues.put(DBHelper.KEY_ADDRESS, address);
                            }

                            if (spans.get(m).text().equals("Ответственное лицо:")) {
                                String responsible_person = spans.get(m + 1).text();
                                eventRecord.responsible_person = responsible_person;
                                contentValues.put(DBHelper.KEY_RESPONSIBLE_PERSON, responsible_person);
                            }

                            if (spans.get(m).text().equals("Телефоны для справок:")) {
                                String phone = spans.get(m + 1).text();
                                eventRecord.phone_number = phone;
                                contentValues.put(DBHelper.KEY_PHONE_NUMBER, phone);
                            }

                        }
                    }
                }


                database.insert(DBHelper.TABLE_EVENTS, null, contentValues);
                localEventRecords.add(eventRecord);

            }

            connection.disconnect();


        } catch (Exception e) {
            Log.e("eTag", e.getMessage());

        }

        database.close();

    }

    public void loadNextPage() {
        try {
            IrbisConnection connection = getIrbisConnection();

            for (int i = currentPage * PAGE_SIZE; i < currentPage * PAGE_SIZE + PAGE_SIZE; i++) {
                int mfn = localMFNs.get(i);
                localEventRecords.add(downloadEventRecord(mfn, connection));
            }
            connection.disconnect();

        } catch (Exception e) {
            return;
        }

        currentPage++;
    }

    public EventRecord getRecordById(int id) {
        return localEventRecords.get(id);
    }

    private EventRecord downloadEventRecord(int mfn, IrbisConnection connection) throws
            IOException, IrbisException {

        EventRecord eventRecord = new EventRecord();

        MarcRecord record = connection.readRecord(mfn);

        String eventDescription = connection.formatRecord("@", mfn);

        Document html = Jsoup.parse(eventDescription);
        Element table = html.select("table").get(0);
        Elements rows = table.select("tr");

        for (Element row : rows) {
            Elements cols = row.select("td");

            for (Element col : cols) {
                Elements spans = col.select("span");
                for (int m = 0; m < spans.size(); m++) {

                    if (spans.get(m).text().equals("Дата начала:")) {
                        eventRecord.start_date = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Дата окончания:")) {
                        eventRecord.end_date = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Время начала:")) {
                        eventRecord.start_time = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Время окончания:")) {
                        eventRecord.end_time = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Возрастная категория:")) {
                        eventRecord.age_category = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Наименование мероприятия:")) {
                        eventRecord.name = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Дополнительные сведения:")) {
                        eventRecord.additional_information = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Аннотация к мероприятию:")) {
                        eventRecord.annotation = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Библиотека-организатор:")) {
                        eventRecord.library = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Адрес проведения мероприятия:")) {
                        eventRecord.address = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Ответственное лицо:")) {
                        eventRecord.responsible_person = spans.get(m + 1).text();
                    }

                    if (spans.get(m).text().equals("Телефоны для справок:")) {
                        eventRecord.phone_number = spans.get(m + 1).text();
                    }

                }
            }

            eventRecord.link = record.fm(107, 'p') + record.fm(107, 'f') ;
        }
        return eventRecord;
    }

    private void downloadEventFromDatabase(Context mContext) {
        dbHelper = new DBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int startDateIndex = cursor.getColumnIndex(DBHelper.KEY_START_DATE);
            int endDateIndex = cursor.getColumnIndex(DBHelper.KEY_END_DATE);
            int startTimeIndex = cursor.getColumnIndex(DBHelper.KEY_START_TIME);
            int endTimeIndex = cursor.getColumnIndex(DBHelper.KEY_END_TIME);
            int ageCategoryIndex = cursor.getColumnIndex(DBHelper.KEY_AGE_CATEGORY);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int additionalInfoIndex = cursor.getColumnIndex(DBHelper.KEY_ADDITIONAL_INFORMATION);
            int annotationIndex = cursor.getColumnIndex(DBHelper.KEY_ANNOTATION);
            int libraryIndex = cursor.getColumnIndex(DBHelper.KEY_LIBRARY);
            int addressIndex = cursor.getColumnIndex(DBHelper.KEY_ADDRESS);
            int responsiblePersonIndex = cursor.getColumnIndex(DBHelper.KEY_RESPONSIBLE_PERSON);
            int phoneIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE_NUMBER);
            int linkIndex = cursor.getColumnIndex(DBHelper.KEY_LINK);

            do {
                EventRecord eventRecord = new EventRecord();
                eventRecord.start_date = cursor.getString(startDateIndex);
                eventRecord.end_date = cursor.getString(endDateIndex);
                eventRecord.start_time = cursor.getString(startTimeIndex);
                eventRecord.end_time = cursor.getString(endTimeIndex);
                eventRecord.age_category = cursor.getString(ageCategoryIndex);
                eventRecord.name = cursor.getString(nameIndex);
                eventRecord.additional_information = cursor.getString(additionalInfoIndex);
                eventRecord.annotation = cursor.getString(annotationIndex);
                eventRecord.library = cursor.getString(libraryIndex);
                eventRecord.address = cursor.getString(addressIndex);
                eventRecord.responsible_person = cursor.getString(responsiblePersonIndex);
                eventRecord.phone_number = cursor.getString(phoneIndex);
                eventRecord.link = cursor.getString(linkIndex);

                localEventRecords.add(eventRecord);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }


    private IrbisConnection getIrbisConnection() throws IrbisException, IOException {
        IrbisConnection connection = new IrbisConnection();
        connection.parseConnectionString("host=194.186.155.14;" +
                "port=1192;database=events;" +
                "user=12_AGENT_MOB;password=agentmob;"
        );


        connection.connect();
        return connection;
    }

}
