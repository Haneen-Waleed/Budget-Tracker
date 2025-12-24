package com.example.budgettracker.data

import android.content.Context
import java.util.UUID

object UserManager {
    private const val PREFS = "bt_prefs"
    private const val KEY_UID = "local_uid"

    fun getUid(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        var uid = prefs.getString(KEY_UID, null)
        if (uid == null) {
            uid = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_UID, uid).apply()
        }
        return uid
    }
}