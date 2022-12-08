package com.beta.cafeq_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beta.cafeq_app.databinding.AdminHomepageBinding
import com.beta.cafeq_app.fragment.AddCafe
import com.beta.cafeq_app.fragment.ClosedCafe

class AdminHomepageActivity : AppCompatActivity() {
    lateinit var binding: AdminHomepageBinding
    private var homeClicked = true
    private var transactClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = AdminHomepageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val fragClosedCafe = ClosedCafe()
        val fragAddCafe = AddCafe()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, fragClosedCafe)
            commit()
        }

        binding.vctHome.setOnClickListener {
            if(!homeClicked){
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment, fragClosedCafe)
                    commit()
                }
                binding.vctHome.background = resources.getDrawable(R.drawable.home_terracotta)
                binding.tvHome.setTextColor(resources.getColor(R.color.terracotta))
                binding.vctAddCafe.background = resources.getDrawable(R.drawable.add)
                binding.tvAddCafe.setTextColor(resources.getColor(R.color.light_grey))
                transactClicked = false
            }
        }
        binding.vctAddCafe.setOnClickListener {
            if (!transactClicked){
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment, fragAddCafe)
                    commit()
                }
                binding.vctHome.background = resources.getDrawable(R.drawable.home)
                binding.tvHome.setTextColor(resources.getColor(R.color.light_grey))
                binding.vctAddCafe.background = resources.getDrawable(R.drawable.add_terracotta)
                binding.tvAddCafe.setTextColor(resources.getColor(R.color.terracotta))
                homeClicked = false
            }
        }
        binding.vctLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}