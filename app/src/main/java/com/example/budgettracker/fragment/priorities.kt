package com.example.budgettracker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.adapter.PriorityAdapter
import com.example.budgettracker.model.PriorityModel
import com.example.budgettracker.R
import com.example.budgettracker.data.UserManager
import com.example.budgettracker.main.MainActivity
import com.google.firebase.firestore.FirebaseFirestore

class priorities : Fragment() {

    private lateinit var recycler: RecyclerView
    private val list = arrayListOf<PriorityModel>()
    private lateinit var adapter: PriorityAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_priorities, container, false)

        (requireActivity() as MainActivity).hideMainFab()

        recycler = view.findViewById(R.id.priorityRecycler)
        adapter = PriorityAdapter(list)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        val bottomPadding = resources.getDimensionPixelSize(R.dimen.fab_bottom_padding)
        recycler.setPadding(
            recycler.paddingLeft,
            recycler.paddingTop,
            recycler.paddingRight,
            bottomPadding
        )
        recycler.clipToPadding = false

        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                override fun onMove(
                    rv: RecyclerView,
                    vh: RecyclerView.ViewHolder,
                    t: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val item = list[pos]

                    // Show confirmation dialog before deletion
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Priority")
                        .setMessage("Are you sure you want to delete \"${item.title}\"?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            dialog.dismiss()
                            deletePriority(item, pos)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                            adapter.notifyItemChanged(pos) // Reset swipe if cancelled
                        }
                        .setCancelable(false)
                        .show()
                }
            })

        itemTouchHelper.attachToRecyclerView(recycler)

        fetchData()

        return view
    }

    private fun fetchData() {
        val uid = UserManager.getUid(requireContext())

        FirebaseFirestore.getInstance()
            .collection("priorities")
            .whereEqualTo("userId", uid) // only this user's priorities
            .get()
            .addOnSuccessListener { result ->

                list.clear()

                for (doc in result) {
                    val item = PriorityModel(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        amount = doc.getLong("amount")?.toInt(),
                        description = doc.getString("description") ?: ""
                    )
                    list.add(item)
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun deletePriority(item: PriorityModel, pos: Int) {
        val db = FirebaseFirestore.getInstance()
        val uid = UserManager.getUid(requireContext())

        // Delete related transactions first
        db.collection("transactions")
            .whereEqualTo("priorityId", item.id)
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snap ->
                for (doc in snap.documents) doc.reference.delete()

                // Delete priority itself
                db.collection("priorities")
                    .document(item.id)
                    .delete()
                    .addOnSuccessListener {
                        list.removeAt(pos)
                        adapter.notifyItemRemoved(pos)
                    }
                    .addOnFailureListener {
                        adapter.notifyItemChanged(pos)
                    }
            }
            .addOnFailureListener {
                adapter.notifyItemChanged(pos)
            }
    }
}