package com.example.budgettracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R
import com.example.budgettracker.model.TransactionModel

class TransactionHistoryAdapter(private val list: List<TransactionModel>) :
    RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtType: TextView = view.findViewById(R.id.txtType)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        // ⭐ اعرض اسم الـ Priority
        holder.txtTitle.text = item.priorityName ?: "Unknown"

        holder.txtAmount.text = "EGP ${item.amount}"
        holder.txtDate.text = item.date

        // اللون فقط
        holder.txtType.text = item.type

        if (item.type.contains("add"))
            holder.txtType.setTextColor(holder.itemView.resources.getColor(R.color.green))
        else
            holder.txtType.setTextColor(holder.itemView.resources.getColor(R.color.red))
    }
}