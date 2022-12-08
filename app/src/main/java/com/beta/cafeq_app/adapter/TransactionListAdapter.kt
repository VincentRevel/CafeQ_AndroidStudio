package com.beta.cafeq_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beta.cafeq_app.DAO
import com.beta.cafeq_app.R
import com.beta.cafeq_app.data.Cafe
import com.beta.cafeq_app.data.CafeDTO
import com.beta.cafeq_app.data.Reservation
import com.beta.cafeq_app.databinding.CardTransactionBinding
import com.beta.cafeq_app.fragment.Transaction
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class TransactionListAdapter: RecyclerView.Adapter<TransactionListAdapter.CardViewViewHolder>() {
    private val transactionList = ArrayList<Reservation>()

    inner class CardViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = CardTransactionBinding.bind(itemView)
        fun bind(reservation: Reservation) {
            with(binding) {
                DAO.getSpecificCafe(reservation.idCafe).addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val cafe = snapshot.getValue<Cafe>()
                        tvCafeName.text = cafe?.name
                        tvCafeAddr.text = cafe?.address
                        tvChair.text = "${reservation.chair} kursi"
                        tvDatetimeReserv.text = reservation.dateTime
                        Glide.with(itemView.context)
                            .load(cafe?.img)
                            .into(ivCafe)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_transaction, parent, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    fun setItems(aList: ArrayList<Reservation>) {
        transactionList.addAll(aList)
    }
}