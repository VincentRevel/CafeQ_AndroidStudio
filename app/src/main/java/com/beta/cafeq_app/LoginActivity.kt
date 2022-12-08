package com.beta.cafeq_app

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = LoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.tbRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

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
            loginFirebase(email,password)
        }
    }

    private fun loginFirebase(email: String, password: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_dialog)
        if (dialog.window!=null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
        val context = this
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    val currUser = Firebase.auth.currentUser
                    if(currUser != null){
                        DAO.getSpecificUser(currUser.uid).addValueEventListener(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                dialog.dismiss()
                                if(email.contains("@admin.com")){
                                    Toast.makeText(context,"Selamat Datang Admin", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context,AdminHomepageActivity::class.java)
                                    startActivity(intent)
                                }else{
                                    val user = snapshot.getValue<User>()
                                    val name = user?.name
                                    Toast.makeText(context,"Selamat Datang ${name}", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context,HomepageActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }else{
                    dialog.dismiss()
                    Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}