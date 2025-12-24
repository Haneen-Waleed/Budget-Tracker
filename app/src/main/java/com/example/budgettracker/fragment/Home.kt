package com.example.budgettracker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgettracker.main.MainActivity
import com.example.budgettracker.data.UserManager
import com.example.budgettracker.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).showMainFab()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTotals()
    }

    private fun loadTotals() {
        val db = FirebaseFirestore.getInstance()
        val uid = UserManager.getUid(requireContext())

        // TOTAL PRIORITIES
        db.collection("priorities")
            .whereEqualTo("userId", uid) // NEW
            .get()
            .addOnSuccessListener { snap ->
                val totalSavings = snap.documents.sumOf { it.getLong("amount")?.toInt() ?: 0 }
                binding.valueSavings.text = "EGP $totalSavings"
            }

        // TOTAL INPUT (REAL MONEY)
        db.collection("transactions")
            .whereEqualTo("userId", uid) // NEW
            .get()
            .addOnSuccessListener { snap ->
                var total = 0
                for (doc in snap.documents) {
                    val amt = doc.getLong("amount")?.toInt() ?: 0
                    val type = doc.getString("type") ?: ""

                    when (type) {
                        "add" -> total += amt
                        "withdraw" -> total -= amt
                        "add_priority" -> {}
                        "delete_priority" -> {}
                    }
                }
                binding.valueInput.text = "EGP $total"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
