package com.example.pr_cbs.RecordStorage;

import java.util.ArrayList;
import java.util.Arrays;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.MarcRecord;

public class RecordStorage {

    private ArrayList<BookRecord> localRecords;

    public RecordStorage(ArrayList<BookRecord> local_records) {
        this.localRecords = local_records;
    }

    private static RecordStorage storage = null;

    public static RecordStorage Instance() {
        if (storage == null)
            storage = new RecordStorage(new ArrayList<BookRecord>() {
            });

        return storage;
    }

    public int getStorageSize() {
        return localRecords.size();
    }

    public void clear() {
        localRecords.clear();
    }

    public boolean fetchRecordsByQuery(String query) {
        try {
            IrbisConnection connection = new IrbisConnection();
            connection.parseConnectionString("host=194.186.155.14;" +
                    "port=1192;database=BDP%SERV12%;" +
                    "user=12_AGENT_MOB;password=agentmob;");
            connection.connect();

            if (query == null || query.isEmpty())
                return false;

            int[] found = connection.search("\"T=" + query + "$\"");

            if (found.length > 1) {
                found = Arrays.copyOf(found, 10);
            }

            for (int i = 0; i < found.length; i++) {

                int mfn = found[i];

                MarcRecord record = connection.readRecord(mfn);

                BookRecord bookRecord = new BookRecord();

                bookRecord.title = record.fm(200, 'a');
                bookRecord.ISBN = record.fm(10, 'A');
                bookRecord.author = record.fm(700, 'A') + record.fm(700, 'B');
                bookRecord.year = record.fm(210, 'D');
                bookRecord.description = connection.formatRecord("@brief", mfn);
                bookRecord.publish = record.fm(210, 'A') + record.fm(210, 'C');
                bookRecord.subjects = record.fm(606, 'A');
                bookRecord.series = record.fm(225, 'A');

                localRecords.add(bookRecord);
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    public BookRecord getRecordById(int id) {
        return localRecords.get(id);
    }
}
