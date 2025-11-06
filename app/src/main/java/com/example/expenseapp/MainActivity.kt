package com.example.expenseapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private val expenses = mutableListOf<Expense>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExpenseAdapter(
            expenses,
            { expense ->
                val intent = Intent(this, AddExpenseActivity::class.java)
                intent.putExtra("expense_id", expense.id)
                startActivity(intent)
            },
            { expense ->
                showDeleteConfirmDialog(expense)
            }
        )
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        expenses.clear()
        expenses.addAll(dbHelper.getAllExpenses())
        adapter.notifyDataSetChanged()
        updateSummary()
    }

    private fun updateSummary() {
        val currentMonthExpenses = getCurrentMonthExpenses()
        val totalAmount = currentMonthExpenses.sumOf { it.amount }
        val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)

        findViewById<TextView>(R.id.tvMonthTitle).text = currentMonth
        findViewById<TextView>(R.id.tvSummaryAmount).text = String.format("₹%.2f", totalAmount)
    }

    private fun getCurrentMonthExpenses(): List<Expense> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return expenses.filter { expense ->
            val expenseCalendar = Calendar.getInstance()
            expenseCalendar.time = dateFormat.parse(expense.date) ?: return@filter false
            expenseCalendar.get(Calendar.MONTH) == currentMonth && expenseCalendar.get(Calendar.YEAR) == currentYear
        }
    }

    private fun showDeleteConfirmDialog(expense: Expense) {
        AlertDialog.Builder(this)
            .setTitle("Delete Expense?")
            .setMessage("Are you sure you want to delete this ${expense.category} expense of ₹${String.format("%.2f", expense.amount)}?")
            .setPositiveButton("Delete") { _, _ ->
                dbHelper.deleteExpense(expense.id)
                expenses.remove(expense)
                adapter.notifyDataSetChanged()
                updateSummary()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun getMonthlyExpenses(): Map<String, Double> {
        val monthlyTotals = mutableMapOf<String, Double>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

        for (expense in expenses) {
            val date = sdf.parse(expense.date)
            if (date != null) {
                val month = monthFormat.format(date)
                monthlyTotals[month] = (monthlyTotals[month] ?: 0.0) + expense.amount
            }
        }
        return monthlyTotals
    }
}
