package com.example.expenseapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ExpenseTracker.db"
        const val TABLE_EXPENSES = "expenses"
        const val KEY_ID = "id"
        const val KEY_DATE = "date"
        const val KEY_CATEGORY = "category"
        const val KEY_AMOUNT = "amount"
        const val KEY_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_NOTE + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun addExpense(expense: Expense): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_DATE, expense.date)
            put(KEY_CATEGORY, expense.category)
            put(KEY_AMOUNT, expense.amount)
            put(KEY_NOTE, expense.note)
        }
        val success = db.insert(TABLE_EXPENSES, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllExpenses(): List<Expense> {
        val expenseList = ArrayList<Expense>()
        val selectQuery = "SELECT * FROM $TABLE_EXPENSES"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val expense = Expense(
                    id = cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                    date = cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)),
                    amount = cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT)),
                    note = cursor.getString(cursor.getColumnIndex(KEY_NOTE))
                )
                expenseList.add(expense)
            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()
        return expenseList
    }

    @SuppressLint("Range")
    fun getExpenseById(id: Long): Expense? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(KEY_ID, KEY_DATE, KEY_CATEGORY, KEY_AMOUNT, KEY_NOTE),
            "$KEY_ID=?",
            arrayOf(id.toString()),
            null, null, null
        )
        var expense: Expense? = null
        if (cursor != null && cursor.moveToFirst()) {
            expense = Expense(
                id = cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                date = cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)),
                amount = cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT)),
                note = cursor.getString(cursor.getColumnIndex(KEY_NOTE))
            )
            cursor.close()
        }
        db.close()
        return expense
    }

    fun deleteExpense(id: Long): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_EXPENSES, "$KEY_ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }

    fun updateExpense(expense: Expense): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_DATE, expense.date)
            put(KEY_CATEGORY, expense.category)
            put(KEY_AMOUNT, expense.amount)
            put(KEY_NOTE, expense.note)
        }
        val success = db.update(TABLE_EXPENSES, contentValues, "$KEY_ID=?", arrayOf(expense.id.toString()))
        db.close()
        return success
    }
}