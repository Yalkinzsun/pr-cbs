package com.example.pr_cbs


import ru.arsmagna.DatabaseInfo
import ru.arsmagna.IrbisConnection
import ru.arsmagna.MarcRecord
import ru.arsmagna.infrastructure.IniFile

import java.util.Arrays

class IRBIS (var title:String) {



    fun main(args: Array<String>) {
        //val result:Array<String> = arrayOf()
        try {
            // Подключаемся к серверу
            val connection = IrbisConnection()
            connection.parseConnectionString("host=194.186.155.14;port=1192;" +
                                                             "database=BDP%SERV12%;" +
                                                             "user=12_AGENT_MOB;password=agentmob;")
            connection.connect()


            var found = connection.search("\"T=Google$\"")
            System.out.printf("Найдено записей: %d%n", found.size)
            if (found.size > 1) {
                // Ограничиваемся 10 первыми записями
                found = Arrays.copyOf(found, 3)
                //System.out.printf("Рез: %s%n", found);

               // result.
            }


            for (i in found.indices) {
                val mfn = found[i]
                val record = connection.readRecord(mfn)
                // System.out.printf("record: %s%n", record);

                val title = record.fm(200, 'a')
                System.out.printf("Заглавие: %s%n", title)

                val ISBN = record.fm(10, 'A')
                System.out.printf("ISBN: %s%n", ISBN)

                val author_name = record.fm(700, 'A')
                val author_initials = record.fm(700, 'B')

                System.out.printf("Автор: %s %s%n", author_name, author_initials)

                val publish1 = record.fm(210, 'A')
                val publish2 = record.fm(210, 'C')
                val year = record.fm(210, 'D')

                System.out.printf("Издательство: %s (%s)%n", publish2, publish1)
                System.out.printf("Год издания: %s%n", year)

                val subjects = record.fm(606, 'A')
                System.out.printf("Тематика: %s%n", subjects)

                val series = record.fm(225, 'A')
                System.out.printf("Серия: %s%n", series)

                val place = record.fm(910, 'D')
                System.out.printf("Место хранения: %s%n", place)

                val place2 = record.fm(902, 'A')
                System.out.printf("Организация: %s%n", place2)

                val lang = record.fm(101)
                System.out.printf("Язык: %s%n", lang)

                val page_c = record.fm(101)
                System.out.printf("Кол-во страниц: %s%n", page_c)

                val link = record.fm(945)
                System.out.printf("Ссылка на обложку: %s%n", link)


                // Объём:

                // статус
                // персоналия

                val description = connection.formatRecord("@brief", mfn)
                System.out.printf("Биб. описание: %s%n", description)

                val description2 = connection.formatRecord("@", mfn)
                System.out.printf("Биб. описание: %s%n", description2)

                println()


            }

            // Отключаемся от сервера

            connection.disconnect()


        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}
