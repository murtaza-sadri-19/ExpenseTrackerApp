package com.example.expenseapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val items: List<Expense>,
    private val onItemClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(v)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = items[position]
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = "₹%.2f".format(expense.amount)
        holder.tvNote.text = expense.note ?: "No note"
        holder.tvDate.text = expense.date

        holder.imgCategory.setImageResource(categoryIconResource(expense.category))

        holder.itemView.setOnClickListener { onItemClick(expense) }
        
        holder.btnDelete.setOnClickListener { onDeleteClick(expense) }
    }

    override fun getItemCount(): Int = items.size

    private fun categoryIconResource(category: String): Int {
        return when (category.lowercase()) {
            "food" -> R.drawable.ic_food
            "transport" -> R.drawable.ic_travel
            "shopping" -> R.drawable.ic_shopping
            "bills" -> R.drawable.ic_bills
            else -> R.drawable.ic_generic
        }
    }
}
