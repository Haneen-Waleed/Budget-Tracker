package com.example.budgettracker.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.main.SignIn
import com.example.budgettracker.adapter.TransactionHistoryAdapter
import com.example.budgettracker.model.TransactionModel
import com.example.budgettracker.data.UserManager
import com.example.budgettracker.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class settings : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadHistory()
        binding.logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), SignIn::class.java))
            requireActivity().finish()
        }

    }

    private fun loadHistory() {
        val db = FirebaseFirestore.getInstance()
        val uid = UserManager.getUid(requireContext())

        db.collection("transactions")
            .whereEqualTo("userId", uid)   // نجيب بس معاملات اليوزر
            .get()
            .addOnSuccessListener { snap ->

                val sdf = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())

                // نحول الداتا لـ model
                val list = snap.documents.map { doc ->

                    val ts = doc.getTimestamp("timestamp")?.toDate()
                    val formattedDate = if (ts != null) sdf.format(ts) else ""

                    TransactionModel(
                        amount = doc.getLong("amount")?.toInt() ?: 0,
                        type = doc.getString("type") ?: "",
                        priorityName = doc.getString("priorityName")
                            ?: doc.getString("itemTitle")
                            ?: "Unknown",
                        date = formattedDate
                    )
                }.sortedByDescending { item ->
                    // نرتّب من الأحدث للأقدم بناء على التاريخ
                    sdf.parse(item.date)
                }

                // عرض البيانات
                binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
                binding.rvHistory.adapter = TransactionHistoryAdapter(list)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}