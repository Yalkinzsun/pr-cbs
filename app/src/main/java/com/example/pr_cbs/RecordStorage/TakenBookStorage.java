package com.example.pr_cbs.RecordStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

public class TakenBookStorage {
    private ArrayList<TakenBookRecord> localRecords;


    private TakenBookStorage(ArrayList<TakenBookRecord> localRecords) {
        this.localRecords = localRecords;
    }

    private static TakenBookStorage storage = null;

    public static TakenBookStorage Instance() {
        if (storage == null)
            storage = new TakenBookStorage(new ArrayList<TakenBookRecord>() {
            });

        return storage;
    }


    public int getAvailableRecordsCount() {
        return localRecords.size();
    }

    public void clear() {
        localRecords.clear();
    }


    public int downloadAllTakenBooks(String query, Boolean internetConnection) {

        if (!internetConnection) return 3;


//        if (query == null || query.isEmpty())


        else {

            clear();

            try {
                IrbisConnection connection = getIrbisConnection();
                int[] found = connection.search("\"RI=" + query + "\"");

                if (found.length != 0) {
                    for (int mfn : found) {
                        downloadTakenBookRecord(mfn, connection);
                    }
                } else return 1;

                connection.disconnect();

            } catch (Exception e) {
                return 2;
            }
            return 0;
        }
    }


    public TakenBookRecord getRecordById(int id) {
        return localRecords.get(id);
    }

    private void downloadTakenBookRecord(int mfn, IrbisConnection connection) throws IOException, IrbisException {

        String description2 = connection.formatRecord("&uf('+0')", mfn);
        String[] parts = description2.split("40#");
        List<String> myList = new ArrayList<>();

        for (String part : parts) {
            if (part.contains("F******") && part.contains("[Текст]")) {
                myList.add(part);
            }
        }

        for (String part : myList) {
            TakenBookRecord takenBookRecord = new TakenBookRecord();
            String Date;
            String Description = part;
            Date = part.substring(part.indexOf("^D") + 2, part.indexOf("^E"));
            Date = Date.substring(0, 4) + "." + Date.substring(4, 6) + "." + Date.substring(6, 8);
            takenBookRecord.date_book_taken = Date;

            Description = Description.substring(Description.indexOf("^C") + 2, Description.indexOf("^V"));
            takenBookRecord.description = Description;

            localRecords.add(takenBookRecord);
        }

    }

    private IrbisConnection getIrbisConnection() throws IrbisException, IOException {
        IrbisConnection connection = new IrbisConnection();
        connection.parseConnectionString("host=194.186.155.14;" +
                "port=1192;database=RDR;" +
                "user=12_1_Zajceva;password=811;");
        connection.connect();

        return connection;
    }

}
