package com.example.pr_cbs.RecordStorage;

import com.example.pr_cbs.R;

import java.io.IOException;
import java.util.ArrayList;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

public class RecordStorageFake {
    String link;
    private ArrayList<BookRecord> localRecords;
    private ArrayList<Integer> localMFNs;
    private ArrayList<BookRecord> fakeCloudRecords;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;

    public RecordStorageFake(ArrayList<BookRecord> localRecords, ArrayList<Integer> localMFNs, ArrayList<BookRecord> fakeCloudRecords) {
        this.localRecords = localRecords;
        this.localMFNs = localMFNs;
        this.fakeCloudRecords = fakeCloudRecords;
    }

    private static RecordStorageFake storage = null;

    public static RecordStorageFake Instance() {
        if (storage == null) {
            storage = new RecordStorageFake(new ArrayList<BookRecord>() {
            }, new ArrayList<Integer>() {
            }, new ArrayList<BookRecord>() {
            });
        }

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
        fakeCloudRecords.clear();
        currentPage = 0;
    }

    public boolean fetchMFNsByQuery(String query) {
        if (query.isEmpty())
            return false;

        clear();

        for (int i = 0; i < 200; i++) {
            localMFNs.add(i);

            fakeCloudRecords.add(
                    new BookRecord(
                            "Book " + Integer.toString(i),
                            "asd", "asd", "asd", "asd", "asd", "asd", "asd"
                    )
            );
        }

        return true;
    }

    public boolean canLoadMore() {
        return localMFNs.size() > localRecords.size();
    }

    public void loadNextPage() {
        try {
            for (int i = currentPage * PAGE_SIZE; i < currentPage * PAGE_SIZE + PAGE_SIZE; i++) {
                int mfn = localMFNs.get(i);
                localRecords.add(downloadBookRecord(mfn));
            }
        } catch (Exception e) {
            return;
        }

        currentPage++;
    }

    public BookRecord getRecordById(int id) {
        return localRecords.get(id);
    }

    private BookRecord downloadBookRecord(int mfn) {
        return fakeCloudRecords.get(mfn);
    }
}
