package com.example.pr_cbs.RecordStorage;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pr_cbs.Database.DBHelper;
import com.example.pr_cbs.MainActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EventStorage {

    public ArrayList<EventRecord> localEventRecords;
    private ArrayList<Integer> localMFNs;
    private static final int PAGE_SIZE = 10;
    private static EventStorage storage = null;

    String APP_PREFERENCES = "pref_settings";
    String APP_PREFERENCES_EVENT_UPDATE_DATE = "event_update_date";


    private EventStorage(ArrayList<EventRecord> localEventRecords, ArrayList<Integer> localMFNs) {
        this.localEventRecords = localEventRecords;
        this.localMFNs = localMFNs;
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
    }

    public int fetchMFNsByQuery(String query, Boolean internetConnection) {

        if (!internetConnection) return 3;

        clear();

        try {

            IrbisConnection connection = getIrbisConnection();
            int[] found = connection.search("\"EVENTNAME=" + query + "$\"");

            connection.disconnect();

            if (found.length != 0) {
                //  MarcRecord record = connection.readRecord(found[0]);
                for (int mfn : found)
                    localMFNs.add(mfn);

            } else return 1;

        } catch (Exception e) {
            return 2;
        }
        return 0;
    }


    public boolean canLoadMore() {
        return localMFNs.size() > localEventRecords.size();
    }


    public int loadAllActualEvents(Context context, Boolean reload, Boolean internetConnection) {

        if (!internetConnection) return 3;

        clear();

        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime());
        boolean isTheDateRelevant = false;
        String savedDate;
        Date current_date = null;
        Date saved_date = null;

        if (!reload) {
            if (MainActivity.Companion.checkSharedPreferenceAvailability(APP_PREFERENCES_EVENT_UPDATE_DATE, context)) {
                savedDate = MainActivity.Companion.getFromSharedPreferences(APP_PREFERENCES_EVENT_UPDATE_DATE, context);

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
                isTheDateRelevant = hours < 24;
            }

            if (isTheDateRelevant) {


                SQLiteDatabase database = DBHelper.getInstance(context).getWritableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    downloadEventFromDatabase(context);
                } else {
                    if (cursor != null) cursor.close();
                    database.close();
                    return downloadActualEventRecord(currentDate, context);
                }

                cursor.close();
                database.close();

            } else return downloadActualEventRecord(currentDate, context);

        } else return downloadActualEventRecord(currentDate, context);

        return 0;
    }

    private int downloadActualEventRecord(String currentDate, Context context) {

        try {
            IrbisConnection connection = getIrbisConnection();

            int[] found = connection.search("\"EVENTNAME=$\"*\"MONTHLY=2020$\"");
            found = Arrays.copyOf(found, 110);
            if (found.length != 0) {


                for (int mfn : found) {

                    String d = connection.formatRecord("@brief", mfn);
                    String eventDate = d.substring(d.indexOf("[") + 2, d.indexOf("]") - 1);


                    Date event_date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(eventDate);
                    Date date_2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(currentDate);

                    if (date_2 != null && event_date != null && event_date.getTime() >= date_2.getTime())
                        localMFNs.add(mfn);
                }
            } else {
                connection.disconnect();
                return 1;
            }


        } catch (Exception e) {
            return 2;
        }

        try {
            IrbisConnection connection = getIrbisConnection();
            SQLiteDatabase database = DBHelper.getInstance(context).getWritableDatabase();
            database.execSQL("delete from events");

            for (int i = 0; i < localMFNs.size(); i++) {
                int mfn = localMFNs.get(i);

                ContentValues contentValues = new ContentValues();
                //localEventRecords.add(downloadEventRecord(mfn, connection, mContext));

                EventRecord eventRecord = new EventRecord();
                //     MarcRecord record = connection.readRecord(mfn);

                //      String link = record.fm(107, 'P') + record.fm(107, 'F');
                String link = "nullnull";
                eventRecord.link = link;
                contentValues.put(DBHelper.KEY_LINK, link);

                String description2 = connection.formatRecord("@", mfn);

                Document html = Jsoup.parse(description2);
                Element table = html.select("table").get(0);
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    Elements cols = row.select("td");

                    for (Element col : cols) {
                        Elements spans = col.select("span");
                        for (int m = 0; m < spans.size(); m++) {

                            if (spans.get(m).text().equals("Дата начала:")) {
                                String startDate = spans.get(m + 1).text();
                                // eventRecord.startDate = startDate;

                                //   Date startDateInNewFormat = new SimpleDateFormat("YYYY-MM-DD", Locale.getDefault()).parse(startDate);

                                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                Date oldDate = format.parse(startDate);

                                format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                                String newDate = null;
                                if (oldDate != null) {
                                    newDate = format.format(oldDate);
                                }


                                contentValues.put(DBHelper.KEY_START_DATE, newDate);
                            }

                            if (spans.get(m).text().equals("Дата окончания:")) {
                                String end_date = spans.get(m + 1).text();
                                // eventRecord.end_date = end_date;
                                contentValues.put(DBHelper.KEY_END_DATE, end_date);
                            }

                            if (spans.get(m).text().equals("Время начала:")) {
                                String start_time = spans.get(m + 1).text();
                                // eventRecord.start_time = start_time;
                                contentValues.put(DBHelper.KEY_START_TIME, start_time);
                            }

                            if (spans.get(m).text().equals("Время окончания:")) {
                                String end_time = spans.get(m + 1).text();
                                //  eventRecord.end_time = end_time;
                                contentValues.put(DBHelper.KEY_END_TIME, end_time);
                            }

                            if (spans.get(m).text().equals("Возрастная категория:")) {
                                String age_category = spans.get(m + 1).text();
                                //   eventRecord.age_category = age_category;
                                contentValues.put(DBHelper.KEY_AGE_CATEGORY, age_category);
                            }

                            if (spans.get(m).text().equals("Наименование мероприятия:")) {
                                String name = spans.get(m + 1).text();
                                //  eventRecord.name = name;
                                contentValues.put(DBHelper.KEY_NAME, name);
                            }

                            if (spans.get(m).text().equals("Дополнительные сведения:")) {
                                String additional_information = spans.get(m + 1).text();
                                //   eventRecord.additional_information = additional_information;
                                contentValues.put(DBHelper.KEY_ADDITIONAL_INFORMATION, additional_information);

                            }

                            if (spans.get(m).text().equals("Аннотация к мероприятию:")) {
                                String annotation = spans.get(m + 1).text();
                                //   eventRecord.annotation = annotation;
                                contentValues.put(DBHelper.KEY_ANNOTATION, annotation);
                            }

                            if (spans.get(m).text().equals("Библиотека-организатор:")) {
                                String library = spans.get(m + 1).text();
                                //   eventRecord.library = library;
                                contentValues.put(DBHelper.KEY_LIBRARY, library);
                            }

                            if (spans.get(m).text().equals("Адрес проведения мероприятия:")) {
                                String address = spans.get(m + 1).text();
                                //    eventRecord.address = address;
                                contentValues.put(DBHelper.KEY_ADDRESS, address);
                            }

                            if (spans.get(m).text().equals("Ответственное лицо:")) {
                                String responsible_person = spans.get(m + 1).text();
                                //   eventRecord.responsible_person = responsible_person;
                                contentValues.put(DBHelper.KEY_RESPONSIBLE_PERSON, responsible_person);
                            }

                            if (spans.get(m).text().equals("Телефоны для справок:")) {
                                String phone = spans.get(m + 1).text();
                                //    eventRecord.phone_number = phone;
                                contentValues.put(DBHelper.KEY_PHONE_NUMBER, phone);
                            }

                        }
                    }
                }


                database.insert(DBHelper.TABLE_EVENTS, null, contentValues);
                // localEventRecords.add(eventRecord);

            }

            connection.disconnect();
            database.close();
        } catch (Exception e) {
            return 2;
        }

        MainActivity.Companion.putInSharedPreferences(APP_PREFERENCES_EVENT_UPDATE_DATE, currentDate, context);

        downloadEventFromDatabase(context);

        return 0;
    }


    public void loadNextPage(int position) {

        try {
            IrbisConnection connection = getIrbisConnection();

            if (localMFNs.size() <= 15) {
                for (int i = 0; i < 15; i++) {
                    int mfn = localMFNs.get(i);
                    localEventRecords.add(downloadEventRecord(mfn, connection));

                }
            } else {

                for (int i = position + 1; i <= position + PAGE_SIZE; i++) {
                    if (localMFNs.contains(localMFNs.get(i))) {
                        int mfn = localMFNs.get(i);
                        localEventRecords.add(downloadEventRecord(mfn, connection));
                    }
                }
            }
            connection.disconnect();

        } catch (Exception e) {
            Log.v("EventError", "IrbisError");
        }

    }

    public EventRecord getRecordById(int id) {
        return localEventRecords.get(id);
    }

    private EventRecord downloadEventRecord(int mfn, IrbisConnection connection) throws
            IOException, IrbisException {

        EventRecord eventRecord = new EventRecord();

        // MarcRecord record = connection.readRecord(mfn);

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

            //   eventRecord.link = record.fm(107, 'p') + record.fm(107, 'f');
            eventRecord.link = "null";
        }
        return eventRecord;
    }

    private void downloadEventFromDatabase(Context mContext) {

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_EVENTS, null, null, null, null, null, "start_date" + " ASC", null);

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
                "user=12_AGENT_MOB;password=agentmob;");


        connection.connect();
        return connection;
    }

}
