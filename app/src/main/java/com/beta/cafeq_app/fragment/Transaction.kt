package com.beta.cafeq_app.fragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.cafeq_app.DAO
import com.beta.cafeq_app.ReservationActivity
import com.beta.cafeq_app.adapter.ClosedCafeAdapter
import com.beta.cafeq_app.adapter.TransactionListAdapter
import com.beta.cafeq_app.data.CafeDTO
import com.beta.cafeq_app.data.Reservation
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.ClosedCafeBinding
import com.beta.cafeq_app.databinding.TransactListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class Transaction : Fragment(){
    private lateinit var binding: TransactListBinding
    private lateinit var adapter: TransactionListAdapter
    private lateinit var fragContext: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TransactListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragContext = context as Activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTransaction.setHasFixedSize(true)
        binding.rvTransaction.layoutManager = LinearLayoutManager(context)
        adapter = TransactionListAdapter()
        binding.rvTransaction.adapter = adapter
        loadReservationFirebase()
    }

    private fun loadReservationFirebase() {
        val currUser = Firebase.auth.currentUser
        if (currUser != null) {
            DAO.getReservation(currUser.uid).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val transactionList = ArrayList<Reservation>()

                    snapshot.children.forEach {
                        val transaction = it.getValue<Reservation>()
                        transactionList.add(transaction!!)
                    }
                    adapter.setItems(transactionList)
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
