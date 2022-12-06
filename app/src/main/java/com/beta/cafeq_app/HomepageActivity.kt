package com.beta.cafeq_app

import com.beta.cafeq_app.fragment.ClosedCafeActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beta.cafeq_app.fragment.TransactionListActivity

class HomepageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)
        val fragCC = ClosedCafeActivity()
        val fragTL = TransactionListActivity()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, fragCC)
            commit()
        }
    }
}