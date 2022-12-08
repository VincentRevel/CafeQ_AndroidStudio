package com.beta.cafeq_app

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.data.CafeDTO
import com.beta.cafeq_app.data.Reservation
import com.beta.cafeq_app.databinding.AddReservationBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.*

class ReservationActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: AddReservationBinding
    private val encodeMonth = arrayOf(
        "Januari","Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember")

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = AddReservationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.etDateTime.isEnabled =false

        Glide.with(this)
            .load(EXTRA_CAFE.img)
            .into(binding.ivCafe)
        binding.tvCafeName.text = EXTRA_CAFE.name

        binding.btnDatetime.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this,this,year,month,day).show()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }

        binding.btnReservation.setOnClickListener {
            val chair = binding.etChair.text.toString()
            val dateTime = binding.etDateTime.text.toString()

            if(chair.isEmpty()){
                binding.etChair.error = "Jumlah kursi harus diisi"
                binding.etChair.requestFocus()
                return@setOnClickListener
            }
            if(chair.toInt() > EXTRA_CAFE.chair){
                binding.etChair.error = "Jumlah kursi tidak mencukupi"
                binding.etChair.requestFocus()
                return@setOnClickListener
            }
            if(dateTime.isEmpty()){
                Toast.makeText(this,"Tanggal waktu harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(LocalDate.now().toString() == LocalDate.of(savedYear,savedMonth+1,savedDay).toString()){
                cafeFirebase(EXTRA_CAFE.key,chair.toInt())
            }
            reservationFirebase(EXTRA_CAFE.key,chair.toInt(),dateTime)
        }
    }

    companion object {
        var EXTRA_CAFE = CafeDTO()
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedDay = p3
        savedMonth = p2
        savedYear = p1

        getDateTimeCalendar()
        TimePickerDialog(this,this,hour,minute,true).show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        savedHour = p1
        savedMinute = p2

        binding.etDateTime.setText(timeDateFormat(savedDay,savedMonth,savedYear,savedHour,savedMinute))
    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun timeDateFormat(day: Int, month: Int, year: Int, hour: Int, minute: Int): String {
        var savedMonthText = encodeMonth[month]
        return String.format("$day $savedMonthText $year - %02d:%02d", hour, minute)
    }

    private fun cafeFirebase(idCafe:String,reserveChair: Int) {
        DAO.updateCafe(idCafe,EXTRA_CAFE.chair - reserveChair)
    }

    private fun reservationFirebase(idCafe:String, chair: Int, dateTime: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_dialog)
        if (dialog.window!=null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
        val currUser = Firebase.auth.currentUser
        if(currUser != null){
            DAO.addReservation(Reservation(idCafe,chair,dateTime),currUser.uid)
                .addOnCompleteListener(this) {
                    if(it.isSuccessful){
                        dialog.dismiss()
                        binding.etChair.text?.clear()
                        binding.etDateTime.text?.clear()
                        Toast.makeText(this,"Reservasi Berhasil",Toast.LENGTH_SHORT).show()
                    }else{
                        dialog.dismiss()
                        Toast.makeText(this,"${it.exception?.message}",Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}