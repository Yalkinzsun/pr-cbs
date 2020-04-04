package com.example.pr_cbs.RecordStorage;

import java.io.IOException;
import java.util.ArrayList;

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
            storage = new TakenBookStorage(new ArrayList<TakenBookRecord>() {});

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
                int[] found = connection.search("\"T=" + query + "$\"");

                if (found.length != 0) {
                    for (int mfn : found) {
                        localRecords.add(downloadTakenBookRecord(mfn, connection));
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

    private TakenBookRecord downloadTakenBookRecord(int mfn, IrbisConnection connection) throws
            IOException, IrbisException {

        MarcRecord record = connection.readRecord(mfn);

        TakenBookRecord takenBookRecord = new TakenBookRecord();

        takenBookRecord.title = record.fm(200, 'a');
        takenBookRecord.ISBN = record.fm(10, 'A');
        takenBookRecord.author = record.fm(700, 'A') + ' ' + record.fm(700, 'B');
        takenBookRecord.year = record.fm(210, 'D');
        takenBookRecord.publish = record.fm(210, 'A') + record.fm(210, 'C');
        takenBookRecord.size = record.fm(215, 'A');

        return takenBookRecord;
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
