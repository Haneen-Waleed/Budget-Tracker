package com.example.budgettracker.fragment

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgettracker.data.UserManager
import com.example.budgettracker.databinding.FragmentMoneyOperationBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

data class PrioritySimple(
    val id: String = "",
    val title: String = "",
    val amount: Int = 0
)

class MoneyOperationFragment : Fragment() {

    private var _binding: FragmentMoneyOperationBinding? = null
    private val binding get() = _binding!!

    private var isAdd = true
    private var priorityList: List<PrioritySimple> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAdd = arguments?.getBoolean("isAdd") ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyOperationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleMoney.text = if (isAdd) "Add Money" else "Withdraw Money"

        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadPriorities()

        binding.btnDone.setOnClickListener {
            doOperation()
        }
    }

    private fun loadPriorities() {
        val uid = UserManager.getUid(requireContext())

        FirebaseFirestore.getInstance()
            .collection("priorities")
            .whereEqualTo("userId", uid) // filter
            .get()
            .addOnSuccessListener { snap ->

                if (!isAdded) return@addOnSuccessListener

                priorityList = snap.documents.map {
                    PrioritySimple(
                        id = it.id,
                        title = it.getString("title") ?: "",
                        amount = it.getLong("amount")?.toInt() ?: 0
                    )
                }

                val names = priorityList.map { it.title }

                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_spinner_dropdown_item,
                    names
                )

                binding.spinnerPriority.adapter = adapter
            }
    }

    private fun doOperation() {

        val enteredAmount = binding.inputAmount.text.toString().toIntOrNull()

        if (enteredAmount == null || enteredAmount <= 0) {
            binding.inputAmount.error = "Enter valid amount"
            return
        }

        if (priorityList.isEmpty()) {
            Toast.makeText(requireContext(), "No priorities available", Toast.LENGTH_SHORT).show()
            return
        }

        val selected = priorityList[binding.spinnerPriority.selectedItemPosition]

        val newRemaining = if (isAdd) {
            selected.amount - enteredAmount
        } else {
            selected.amount + enteredAmount
        }

        if (newRemaining < 0) {
            Toast.makeText(requireContext(), "Amount exceeds goal needed", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnDone.isEnabled = false

        val db = FirebaseFirestore.getInstance()
        val uid = UserManager.getUid(requireContext())

        db.collection("priorities").document(selected.id)
            .update("amount", newRemaining)
            .addOnSuccessListener {

                val data = hashMapOf(
                    "priorityId" to selected.id,
                    "itemTitle" to selected.title,
                    "amount" to enteredAmount,
                    "type" to if (isAdd) "add" else "withdraw",
                    "timestamp" to Timestamp.now(),
                    "userId" to uid // NEW
                )

                db.collection("transactions")
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        binding.btnDone.isEnabled = true
                        Toast.makeText(requireContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                binding.btnDone.isEnabled = true
                Toast.makeText(requireContext(), "Failed to update priority", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
