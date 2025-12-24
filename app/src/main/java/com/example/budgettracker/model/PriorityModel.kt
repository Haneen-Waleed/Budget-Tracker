package com.example.budgettracker.model

//data class of priorities
data class PriorityModel(
    val id: String = "",
    val title: String = "",
    val amount: Int? = 0,
    val description: String = ""
)