package com.beta.cafeq_app

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.EditEmailpassBinding
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class EditEmailPassActivity : AppCompatActivity() {
    private lateinit var binding: EditEmailpassBinding
    private val currUser = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = EditEmailpassBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (currUser != null) {
            DAO.getSpecificUser(currUser.uid).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var user = snapshot.getValue<User>()
                    if (user != null) {
                        binding.etEmail.setText(user.email)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnSaveEP.setOnClickListener {
            val currEmail = currUser?.email
            var emailChanged = false
            var passwordChanged = false
            val newEmail = binding.etEmail.text.toString()
            val currPassword = binding.etCurrPass.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etPassConfirm.text.toString()

            if(newEmail.isEmpty()){
                binding.etEmail.error = "Email harus diisi"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()){
                binding.etEmail.error = "Email tidak valid"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if(currPassword.isEmpty()){
                binding.etCurrPass.error = "Password saat ini harus diisi"
                binding.etCurrPass.requestFocus()
                return@setOnClickListener
            }
            if(currPassword.length < 6){
                binding.etCurrPass.error = "Password minimal 6 karakter"
                binding.etCurrPass.requestFocus()
                return@setOnClickListener
            }

            if (currEmail != newEmail) {
                emailChanged = true
            }
            if (newPassword.isNotEmpty()) {
                if(newPassword.length < 6){
                    binding.etNewPassword.error = "Password minimal 6 karakter"
                    binding.etNewPassword.requestFocus()
                    return@setOnClickListener
                }
                if(confirmPassword.isEmpty()){
                    binding.etPassConfirm.error = "Konfirmasi password harus diisi"
                    binding.etPassConfirm.requestFocus()
                    return@setOnClickListener
                }
                if(confirmPassword != newPassword){
                    binding.etPassConfirm.error = "Password tidak sesuai"
                    binding.etPassConfirm.requestFocus()
                    return@setOnClickListener
                }
                passwordChanged = true
            }
            if (currEmail != null) {
                userFirebase(currEmail,currPassword,newEmail,emailChanged,newPassword,passwordChanged)
            }

        }
    }

    private fun userFirebase(currEmail: String,currPassword: String,newEmail: String, emailChanged: Boolean, newPassword: String, passwordChanged: Boolean) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_dialog)
        if (dialog.window!=null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(currEmail,currPassword).addOnCompleteListener(this){
            if (it.isSuccessful) {
                if(emailChanged) {
                    currUser?.updateEmail(newEmail)?.addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                            DAO.updateUser(currUser.uid,User("","",newEmail,"","",""))
                        }else{
                            Toast.makeText(this,"${task.exception?.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (passwordChanged) {
                    currUser?.updatePassword(newPassword)?.addOnCompleteListener(this) { task ->
                        if(task.isSuccessful) {
                        }else{
                            Toast.makeText(this,"${task.exception?.message}",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
                Toast.makeText(this,"Email/Password berhasil diperbarui",Toast.LENGTH_SHORT).show()
            }else{
                dialog.dismiss()
                Toast.makeText(this,"Password saat ini tidak sesuai", Toast.LENGTH_SHORT).show()
            }
        }
    }
}