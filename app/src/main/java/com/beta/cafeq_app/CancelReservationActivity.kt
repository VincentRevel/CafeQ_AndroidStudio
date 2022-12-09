package com.beta.cafeq_app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.data.Cafe
import com.beta.cafeq_app.data.ReservationDTO
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.DetailReservationBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class CancelReservationActivity : AppCompatActivity() {
    private lateinit var binding: DetailReservationBinding
    private val currUser = Firebase.auth.currentUser?.uid
    private val encodeMonth = arrayOf(
        "Januari","Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DetailReservationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val context = this
        DAO.getSpecificCafe(EXTRA_RESERVATION.idCafe).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cafe = snapshot.getValue<Cafe>()
                Glide.with(context)
                    .load(cafe?.img)
                    .into(binding.ivCafe)
                binding.tvCafeName.text = cafe?.name
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        if (currUser != null) {
            DAO.getSpecificUser(currUser).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue<User>()
                    if (user != null) {
                        binding.tvName.text = user.name
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
        binding.tvDate.text = EXTRA_RESERVATION.dateTime.split("-")[0]
        binding.tvTime.text = EXTRA_RESERVATION.dateTime.split("-")[1]
        binding.tvChair.text = EXTRA_RESERVATION.chair.toString()

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }

        binding.btnReservation.setOnClickListener {
            if(currDateCheck(binding.tvDate.text.toString())) {

                DAO.getSpecificCafe(EXTRA_RESERVATION.idCafe).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        DAO.updateCafe(EXTRA_RESERVATION.idCafe,EXTRA_RESERVATION.chair + snapshot.child("chair").getValue<Int>()!!)
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            }
            if (currUser != null) {
                DAO.deleteReservation(currUser, EXTRA_RESERVATION.key)
            }
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        var EXTRA_RESERVATION = ReservationDTO()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun currDateCheck(date: String): Boolean {
        val day = date.split(" ")[0].toInt()
        val month = encodeMonth.indexOf(date.split(" ")[1])+1
        val year = date.split(" ")[2]
        var reformatDate = String.format("$year-%02d-%02d",month,day)
        return LocalDate.now().toString() == reformatDate
    }
}
