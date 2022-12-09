package com.beta.cafeq_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.ProfilePageBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ProfilePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ProfilePageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val context = this
        val currUser = Firebase.auth.currentUser
        if(currUser != null){
            DAO.getSpecificUser(currUser.uid).addListenerForSingleValueEvent(object: ValueEventListener {
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
                    binding.tvName.text = user?.name
                    binding.tvBdate.text = user?.birthdate
                    binding.tvEmail.text = user?.email
                    binding.tvCell.text = user?.cell
                    binding.tvGender.text = user?.gender
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnChangePass.setOnClickListener {
            val intent = Intent(this, EditEmailPassActivity::class.java)
            startActivity(intent)
        }
    }
}