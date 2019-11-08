package com.example.pr_cbs.Models

data class Book(val name: String = "", val count: Int = 0)

object Supplier {
    val books = listOf<Book>(
        Book("Война и мир", 10),
        Book("Вий", 11),
        Book("Пришельцы атакуют!", 1)
    )
}