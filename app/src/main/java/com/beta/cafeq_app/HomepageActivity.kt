package com.beta.cafeq_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.databinding.HomepageBinding
import com.beta.cafeq_app.fragment.ClosedCafe
import com.beta.cafeq_app.fragment.Transaction
import com.google.firebase.auth.FirebaseAuth

class HomepageActivity : AppCompatActivity(){
    lateinit var binding: HomepageBinding
    private var homeClicked = true
    private var transactClicked = false
    private val fragClosedCafe = ClosedCafe()
    private val fragTransactList = Transaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = HomepageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, fragClosedCafe)
            commit()
        }
        setContentView(binding.root)

        binding.vctHome.setOnClickListener {
            if(!homeClicked){
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment, fragClosedCafe)
                    commit()
                }
                binding.vctHome.background = resources.getDrawable(R.drawable.home_terracotta)
                binding.tvHome.setTextColor(resources.getColor(R.color.terracotta))
                binding.vctTransact.background = resources.getDrawable(R.drawable.transaction)
                binding.tvTransact.setTextColor(resources.getColor(R.color.light_grey))
                transactClicked = false
            }
        }
        binding.vctTransact.setOnClickListener {
            if (!transactClicked){
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment, fragTransactList)
                    commit()
                }
                binding.vctHome.background = resources.getDrawable(R.drawable.home)
                binding.tvHome.setTextColor(resources.getColor(R.color.light_grey))
                binding.vctTransact.background = resources.getDrawable(R.drawable.transaction_terractotta)
                binding.tvTransact.setTextColor(resources.getColor(R.color.terracotta))
                homeClicked = false
            }
        }
        binding.vctProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.vctLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}