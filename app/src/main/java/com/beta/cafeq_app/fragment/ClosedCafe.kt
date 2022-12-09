package com.beta.cafeq_app.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.cafeq_app.DAO
import com.beta.cafeq_app.ReservationActivity
import com.beta.cafeq_app.adapter.ClosedCafeAdapter
import com.beta.cafeq_app.data.CafeDTO
import com.beta.cafeq_app.data.User
import com.beta.cafeq_app.databinding.ClosedCafeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ClosedCafe : Fragment() {
    private lateinit var binding: ClosedCafeBinding
    private lateinit var adapter: ClosedCafeAdapter
    private lateinit var fragContext: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ClosedCafeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragContext = context as Activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCafe.setHasFixedSize(true)
        binding.rvCafe.layoutManager = LinearLayoutManager(context)
        adapter = ClosedCafeAdapter()
        binding.rvCafe.adapter = adapter
        loadCafeFirebase()
        adapter.setOnItemCafeClickCallback(object: ClosedCafeAdapter.OnItemCafeClickCallback {
            override fun onItemCafeClicked(data: CafeDTO) {
                val currUser = Firebase.auth.currentUser
                if(currUser != null){
                    DAO.getSpecificUser(currUser.uid).addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue<User>()
                            val email = user?.email
                            if (email != null) if(!email.contains("@admin.com")){
                                ReservationActivity.EXTRA_CAFE = data
                                val intent = Intent(fragContext, ReservationActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
        })
    }

    private fun loadCafeFirebase() {
        DAO.getCafe().addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                val cafeList = ArrayList<CafeDTO>()

                children.forEach {
                    var cafeObj = it.value as HashMap<*, *>
                    var cafe = CafeDTO()
                    with(cafe) {
                        key = it.key.toString()
                        img = cafeObj["img"].toString()
                        name = cafeObj["name"].toString()
                        address = cafeObj["address"].toString()
                        chair = cafeObj["chair"].toString().toInt()
                        distance = cafeObj["distance"].toString()
                        rating = cafeObj["rating"].toString()
                    }
                    cafeList.add(cafe!!)
                }
                adapter.setItems(cafeList)
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}