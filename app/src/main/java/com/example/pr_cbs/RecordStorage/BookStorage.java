package com.example.pr_cbs.RecordStorage;


import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ru.arsmagna.IrbisConnection;
import ru.arsmagna.IrbisException;
import ru.arsmagna.MarcRecord;

public class BookStorage {
    public ArrayList<BookRecord> localRecords;
    private ArrayList<Integer> localMFNs;
    private static final int PAGE_SIZE = 10;
    private static String[] libs = new String[]{"1ф", "2ф", "3ф", "4ф", "5ф", "6ф", "do", "црб"};


    private BookStorage(ArrayList<BookRecord> localRecords, ArrayList<Integer> localMFNs) {
        this.localRecords = localRecords;
        this.localMFNs = localMFNs;
    }

    private static BookStorage storage = null;

    public static BookStorage Instance() {
        if (storage == null)
            storage = new BookStorage(new ArrayList<BookRecord>() {
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
    }

    public int fetchMFNsByQuery(String query, Boolean internetConnection) {
        if (!internetConnection) return 3;

        if (query == null || query.isEmpty())
            return 1;

        else {

            clear();

            try {
                IrbisConnection connection = getIrbisConnection();
                int[] found = connection.search("\"T=" + query + "$\"");

                connection.disconnect();

                if (found.length != 0) {
                    for (int mfn : found) {
                        localMFNs.add(mfn);

                    }
                } else return 1;


            } catch (Exception e) {

                return 2;
            }

            return 0;
        }
    }

    public int fetchMFNsByQueryAdvanced(String query, Boolean internetConnection) {
        if (!internetConnection) return 3;

        if (query == null || query.isEmpty())
            return 1;

        else {

            clear();

            try {
                IrbisConnection connection = getIrbisConnection();
                int[] found = connection.search(query);
                connection.disconnect();

                if (found.length != 0) {
                    for (int mfn : found) {
                        localMFNs.add(mfn);

                    }
                } else return 1;

            } catch (Exception e) {
                return 2;
            }

            return 0;
        }
    }

    public boolean canLoadMore() {
        return localMFNs.size() > localRecords.size();
    }

    public void loadNextPage(int position) {
        try {
            IrbisConnection connection = getIrbisConnection();

            if (localMFNs.size() <= 15) {
                for (int i = 0; i < 15; i++) {
                    int mfn = localMFNs.get(i);
                    localRecords.add(downloadBookRecord(mfn, connection));

                }
            } else {

                for (int i = position + 1; i <= position + PAGE_SIZE; i++) {
                    if (localMFNs.contains(localMFNs.get(i))) {
                        int mfn = localMFNs.get(i);
                        localRecords.add(downloadBookRecord(mfn, connection));
                    }
                }
            }
            connection.disconnect();

        } catch (Exception e) {
            Log.v("EventError", "IrbisError");
        }

    }

    public BookRecord getRecordById(int id) {
        return localRecords.get(id);
    }

    private BookRecord downloadBookRecord(int mfn, IrbisConnection connection) throws
            IOException, IrbisException {

        MarcRecord record = connection.readRecord(mfn);

        BookRecord bookRecord = new BookRecord();

        bookRecord.title = record.fm(200, 'a');
        bookRecord.ISBN = record.fm(10, 'A');
        bookRecord.author = record.fm(700, 'A') + ' ' + record.fm(700, 'B');
        bookRecord.year = record.fm(210, 'D');
        bookRecord.description = connection.formatRecord("@brief", mfn);
        bookRecord.publish = record.fm(210, 'C') + " (" + record.fm(210, 'A') + ")";
        bookRecord.subjects = record.fm(606, 'A');
        bookRecord.series = record.fm(225, 'A');
        bookRecord.size = record.fm(215, 'A');
        bookRecord.lang = record.fm(101);
        bookRecord.link = record.fm(954, 'P') + record.fm(954, 'F');

        String description = connection.formatRecord("@", mfn);
        String[] copiesInfo = getCopiesInfo(description);
        bookRecord.num_of_all_available_copies = copiesInfo[0];

        bookRecord.lib_1f_num_of_copies = copiesInfo[1];
        bookRecord.lib_2f_num_of_copies = copiesInfo[2];
        bookRecord.lib_3f_num_of_copies = copiesInfo[3];
        bookRecord.lib_4f_num_of_copies = copiesInfo[4];
        bookRecord.lib_5f_num_of_copies = copiesInfo[5];
        bookRecord.lib_6f_num_of_copies = copiesInfo[6];
        bookRecord.lib_do_num_of_copies = copiesInfo[7];
        bookRecord.lib_crb_num_of_copies = copiesInfo[8];

        return bookRecord;
    }


    private static String[] getCopiesInfo(String description) {

        int allCopies = 0;
        int copies;
        String[] info = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0"};
        if (description.contains("Свободны:")) {
            String result = description.split("Свободны:")[1];
            //result.replace("\\par", "");


            String newRes = result.replace("\\par", "").replace("}", "").replace(" ", "");

            String[] parts = newRes.split(",");


            for (String part : parts) {

                String lib = part.substring(0, part.lastIndexOf('('));


                if (Arrays.asList(libs).contains(lib)) {

                    copies = getNumOfCopies(part);
                    allCopies += copies;

                    switch (lib) {

                        case "1ф": {

//                    info[9] = "Библиотека им. В. И. Ленина";
                            info[1] = Integer.toString(copies);
                            break;
                        }
                        case "2ф": {
//                    info[10] = "Библиотека им. Б. А. Лавренева";
                            info[2] = Integer.toString(copies);
                            break;
                        }
                        case "3ф": {
//                    info[11] = "Юношеская библиотека им. А. П. Гайдара";
                            info[3] = Integer.toString(copies);
                            break;
                        }
                        case "4ф": {
//                    info[12] = "3-я районная библиотека";
                            info[4] = Integer.toString(copies);
                            break;
                        }
                        case "5ф": {
//                    info[13] = "Библиотека Кировский островов";
                            info[5] = Integer.toString(copies);
                            break;
                        }
                        case "6ф": {
//                    info[14] = "2-я детская библиотека";
                            info[6] = Integer.toString(copies);
                            break;
                        }
                        case "do": {
//                    info[15] = "Центральная районная десткая библиотека";
                            info[7] = Integer.toString(copies);
                            break;
                        }
                        case "црб": {
//                    info[16] = "Центральная районная библиотека им. А. С. Пушкина";

                            info[8] = Integer.toString(copies);
                            break;
                        }
                    }

                }

            }

        }

        info[0] = Integer.toString(allCopies);

        return info;

    }


    private static int getNumOfCopies(String part) {

        String requiredString = part.substring(part.indexOf("(") + 1, part.indexOf(")"));

        int res = 0;
        try {
            res = Integer.parseInt(requiredString);

        } catch (NumberFormatException e) {

            //TODO;
        }

        return res;

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
