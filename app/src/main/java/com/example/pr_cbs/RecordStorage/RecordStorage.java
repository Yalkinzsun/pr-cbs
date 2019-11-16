package com.example.pr_cbs.RecordStorage;

import com.example.pr_cbs.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

public class RecordStorage {
    String link;
    private ArrayList<BookRecord> localRecords;
    private ArrayList<Integer> localMFNs;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;

    public RecordStorage(ArrayList<BookRecord> localRecords, ArrayList<Integer> localMFNs) {
        this.localRecords = localRecords;
        this.localMFNs = localMFNs;
    }

    private static RecordStorage storage = null;

    public static RecordStorage Instance() {
        if (storage == null)
            storage = new RecordStorage(new ArrayList<BookRecord>() {
            }, new ArrayList<Integer>() {
            });

        return storage;
    }

    public int getMFNsCount() {
        return localMFNs.size();
    }

    public int getAvailableRecordsCount() {
        return localRecords.size();
    }

    public void clear() {
        localRecords.clear();
        localMFNs.clear();
        currentPage = 0;
    }

    public boolean fetchMFNsByQuery(String query) {
        if (query == null || query.isEmpty())
            return false;

        clear();

        try {
            IrbisConnection connection = getIrbisConnection();

            for (int mfn : connection.search("\"T=" + query + "$\"")) {
                localMFNs.add(mfn);
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean canLoadMore() {
        return localMFNs.size() > localRecords.size();
    }

    public void loadNextPage() {
        try {
            IrbisConnection connection = getIrbisConnection();

            for (int i = currentPage * PAGE_SIZE; i < currentPage * PAGE_SIZE + PAGE_SIZE; i++) {
                int mfn = localMFNs.get(i);
                localRecords.add(downloadBookRecord(mfn, connection));
            }
        } catch (Exception e) {
            return;
        }

        currentPage++;
    }

    public BookRecord getRecordById(int id) {
        return localRecords.get(id);
    }

    private BookRecord downloadBookRecord(int mfn, IrbisConnection connection) throws IOException, IrbisException {
        MarcRecord record = connection.readRecord(mfn);

        BookRecord bookRecord = new BookRecord();

        bookRecord.title = record.fm(200, 'a');
        bookRecord.ISBN = record.fm(10, 'A');
        bookRecord.author = record.fm(700, 'A') + ' ' + record.fm(700, 'B');
        bookRecord.year = record.fm(210, 'D');
        bookRecord.description = connection.formatRecord("@brief", mfn);
        bookRecord.publish = record.fm(210, 'A') + record.fm(210, 'C');
        bookRecord.subjects = record.fm(606, 'A');
        bookRecord.series = record.fm(225, 'A');

        link = record.fm(954, 'P') + record.fm(954, 'F');
        if (link == "nullnull") {

            bookRecord.link = R.drawable.book_cover_1;
        } else {
            // bookRecord.link = link;
        }

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
