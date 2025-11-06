package com.example.expenseapp

data class Expense(
    val id: Long = 0,
    val date: String,
    val category: String,
    val amount: Double,
    val note: String?
)