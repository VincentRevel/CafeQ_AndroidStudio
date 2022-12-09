package com.beta.cafeq_app

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.data.Cafe
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.EditProfileBinding
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class EditProfileActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding : EditProfileBinding
    private val encodeMonth = arrayOf(
        "Januari","Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember")

    private var day = 0
    private var month = 0
    private var year = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0

    private var updateProfile = false
    private val currUser = Firebase.auth.currentUser!!.uid
    private lateinit var userImage: Uri
    private var email = ""
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            userImage = it
            binding.ivProfilePicture.setImageURI(it)
            updateProfile = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = EditProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.etBirthDate.isEnabled =false
        binding.ivProfilePicture.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        val context = this
        DAO.getSpecificUser(currUser).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                if(user?.img == "") {
                    binding.ivProfilePicture.setImageDrawable(resources.getDrawable(R.drawable.user))
                }else {
                    if (user != null) {
                        Glide.with(context)
                            .load(user.img)
                            .into(binding.ivProfilePicture)
                    }
                }
                binding.etName.setText(user?.name)
                binding.etCell.setText(user?.cell)
                binding.etBirthDate.setText(user?.birthdate)
                email = user?.email.toString()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnBirthdate.setOnClickListener {
            val cal = Calendar.getInstance()
            day = cal.get(Calendar.DAY_OF_MONTH)
            month = cal.get(Calendar.MONTH)
            year = cal.get(Calendar.YEAR)
            DatePickerDialog(this,this,year,month,day).show()
        }

        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etName.text.toString()
            val cell = binding.etCell.text.toString()
            val bdate = binding.etBirthDate.text.toString()
            var gender = ""

            if(name.isEmpty()){
                binding.etName.error = "Nama harus diisi"
                binding.etName.requestFocus()
                return@setOnClickListener
            }
            if(bdate.isEmpty()){
                Toast.makeText(this,"Tanggal lahir belum diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var id: Int = binding.rbGroupGender.checkedRadioButtonId
            if (id!=-1){
                val radio: RadioButton = findViewById(id)
                gender = radio.text.toString()
            }else{
                Toast.makeText(this,"Jenis kelamin belum diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(cell.isEmpty()){
                binding.etCell.error = "Nomor Handphone harus diisi"
                binding.etCell.requestFocus()
                return@setOnClickListener
            }
            userFirebase(name, cell, bdate, gender)
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        binding.etBirthDate.setText(dateFormat(p3,p2,p1))
    }

    private fun dateFormat(day: Int, month: Int, year: Int): String {
        var savedMonthText = encodeMonth[month]
        return "$day $savedMonthText $year"
    }

    private fun userFirebase(name: String, cell: String, bdate: String, gender: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_dialog)
        if (dialog.window!=null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
        var url: String
        if(updateProfile) {
            DAO.uploadImageProfile(userImage,UUID.randomUUID().toString())
                .addOnSuccessListener(this) { img ->
                    val downloadUrl: Task<Uri> = img!!.storage.downloadUrl
                    downloadUrl.addOnCompleteListener { img ->
                        url = ("https://"
                                + img.result.encodedAuthority
                                + img.result.encodedPath.toString() + "?alt=media&token="
                                + img.result.getQueryParameters("token")[0])
                        DAO.updateUser(currUser,User(url,name,email,bdate,gender,cell))
                    }
                }
            updateProfile=false
        }else {
            DAO.updateUser(currUser,User("",name,"",bdate,gender,cell))
        }
        dialog.dismiss()
        Toast.makeText(this,"Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
    }
}