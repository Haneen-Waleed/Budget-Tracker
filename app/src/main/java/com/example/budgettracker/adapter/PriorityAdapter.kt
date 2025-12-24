package com.example.budgettracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.model.PriorityModel
import com.example.budgettracker.R

class PriorityAdapter(private val list: ArrayList<PriorityModel>) : RecyclerView.Adapter<PriorityAdapter.MyViewHolder>() {
//getting the recycleView of the priorities list
    //to use it in the priority_item fragment
    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val amount: TextView = view.findViewById(R.id.itemAmount)
        val desc: TextView = view.findViewById(R.id.itemDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.priority_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.amount.text = item.amount?.toString() ?: "0"
        holder.desc.text = item.description ?: ""
    }
}