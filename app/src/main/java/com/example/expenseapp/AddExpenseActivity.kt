package com.example.expenseapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        dbHelper = DBHelper(this)

        val etDate = findViewById<EditText>(R.id.etDate)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etNote = findViewById<EditText>(R.id.etNote)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(dateFormat.format(today.time))
        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        val categories = listOf("Food", "Transport", "Shopping", "Bills", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // If editing existing expense, populate fields
        if (intent.hasExtra("expense_id")) {
            editingId = intent.getLongExtra("expense_id", 0L)
            val expense = dbHelper.getExpenseById(editingId ?: 0L)
            expense?.let {
                etDate.setText(it.date)
                val categoryIndex = categories.indexOf(it.category)
                if (categoryIndex >= 0) {
                    spinnerCategory.setSelection(categoryIndex)
                }
                etAmount.setText(it.amount.toString())
                etNote.setText(it.note ?: "")
            }
        }

        btnSave.setOnClickListener {
            val date = etDate.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val amount = etAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
            val note = etNote.text.toString().trim().ifEmpty { null }
            
            if (editingId != null && editingId != 0L) {
                val updated = Expense(id = editingId!!, date = date, category = category, amount = amount, note = note)
                dbHelper.updateExpense(updated)
            } else {
                val newExpense = Expense(date = date, category = category, amount = amount, note = note)
                dbHelper.addExpense(newExpense)
            }
            finish()
        }
    }

    private fun showDatePicker(etDate: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            etDate.setText(dateFormat.format(calendar.time))
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }
}
