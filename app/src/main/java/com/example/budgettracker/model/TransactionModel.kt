package com.example.budgettracker.model

data class TransactionModel(
    val id: String = "",
    val priorityId: String? = null,
    val priorityName: String? = null,   // ← اضف ده
    val amount: Int = 0,
    val date: String = "",
    val type: String = "" // add_priority , add_saving...
)