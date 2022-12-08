package com.beta.cafeq_app

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : RegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = RegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnBack.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tbLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener{
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confPassword = binding.etPassConfirm.text.toString()
            val cell = binding.etCell.text.toString()

            if(name.isEmpty()){
                binding.etName.error = "Nama harus diisi"
                binding.etName.requestFocus()
                return@setOnClickListener
            }
            if(email.isEmpty()){
                binding.etEmail.error = "Email harus diisi"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.etEmail.error = "Email tidak valid"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                binding.etPassword.error = "Password harus diisi"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }
            if(password.length < 6){
                binding.etPassword.error = "Password minimal 6 karakter"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }
            if(confPassword.isEmpty()){
                binding.etPassConfirm.error = "Konfirmasi password harus diisi"
                binding.etPassConfirm.requestFocus()
                return@setOnClickListener
            }
            if(confPassword != password){
                binding.etPassConfirm.error = "Password tidak sesuai"
                binding.etPassConfirm.requestFocus()
                return@setOnClickListener
            }
            if(cell.isEmpty()){
                binding.etCell.error = "Nomor Handphone harus diisi"
                binding.etCell.requestFocus()
                return@setOnClickListener
            }

            registerFirebase(name,email,password,cell)
        }
    }

    private fun registerFirebase(name: String, email: String, password: String, cell: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_dialog)
        if (dialog.window!=null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    val currUser = Firebase.auth.currentUser
                    if (currUser != null){
                        val user = User("",name,email,"-","-",cell)
                        DAO.addUser(user,currUser.uid)
                        dialog.dismiss()
                        Toast.makeText(this,"Berhasil Daftar",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,LoginActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    dialog.dismiss()
                    Toast.makeText(this,"${it.exception?.message}",Toast.LENGTH_SHORT).show()
                }
        }
    }
}
