package com.beta.cafeq_app.fragment
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.cafeq_app.DAO
import com.beta.cafeq_app.adapter.TransactionListAdapter
import com.beta.cafeq_app.data.ReservationDTO
import com.beta.cafeq_app.databinding.CardTransactionBinding
import com.beta.cafeq_app.databinding.TransactListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import androidx.annotation.RequiresApi
import com.beta.cafeq_app.CancelReservationActivity
import com.beta.cafeq_app.ReservationActivity
import com.beta.cafeq_app.data.Cafe
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.getValue
import java.time.LocalDate

class Transaction : Fragment(){
    private lateinit var binding: TransactListBinding
    private lateinit var adapter: TransactionListAdapter

    private lateinit var fragContext: Activity
    private val currUser = Firebase.auth.currentUser?.uid

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
        adapter.setOnItemTransactionClickCallback(object: TransactionListAdapter.OnItemTransactionClickCallback{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemTransactionClicked(data: ReservationDTO) {
                CancelReservationActivity.EXTRA_RESERVATION = data
                val intent = Intent(fragContext, CancelReservationActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun loadReservationFirebase() {
        if (currUser != null) {
            DAO.getReservation(currUser).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children
                    val transactionList = ArrayList<ReservationDTO>()

                    children.forEach {
                        var cafeObj = it.value as HashMap<*, *>
                        var reservation = ReservationDTO()
                        with(reservation) {
                            key = it.key.toString()
                            idCafe = cafeObj["idCafe"].toString()
                            chair = cafeObj["chair"].toString().toInt()
                            dateTime = cafeObj["dateTime"].toString()
                        }
                        transactionList.add(reservation!!)
                    }

                    adapter.setItems(transactionList)
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
