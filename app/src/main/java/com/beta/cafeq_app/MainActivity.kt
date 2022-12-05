package com.beta.cafeq_app

import com.beta.cafeq_app.fragment.FragmentCC
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)
        val fragCC = FragmentCC()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentCC, fragCC)
            commit()
        }
    }
}