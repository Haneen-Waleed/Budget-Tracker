package com.example.budgettracker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgettracker.data.UserManager
import com.example.budgettracker.databinding.FragmentAddPriorityBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddPriorityFragment : Fragment() {

    private var _binding: FragmentAddPriorityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPriorityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButtonPriority.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.doneButtonPriority.setOnClickListener {
            val title = binding.priorityTitle.text.toString().trim()
            val amount = binding.priorityAmount.text.toString().toIntOrNull()
            val description = binding.priorityDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.priorityTitle.error = "Enter title"
                return@setOnClickListener
            }

            binding.doneButtonPriority.isEnabled = false

            val uid = UserManager.getUid(requireContext())

            val priorityInfo = hashMapOf(
                "title" to title,
                "amount" to amount,
                "description" to description,
                "createdAt" to Timestamp.now(),
                "userId" to uid // NEW: local uid
            )

            val db = FirebaseFirestore.getInstance()

            db.collection("priorities")
                .add(priorityInfo)
                .addOnSuccessListener { docRef ->
                    val transaction = hashMapOf(
                        "type" to "add_priority",
                        "priorityId" to docRef.id,
                        "priorityName" to title,
                        "amount" to amount,
                        "timestamp" to FieldValue.serverTimestamp(),
                        "userId" to uid // NEW
                    )

                    db.collection("transactions").add(transaction)

                    Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed!", Toast.LENGTH_SHORT).show()
                    binding.doneButtonPriority.isEnabled = true
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
