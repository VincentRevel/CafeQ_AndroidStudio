package com.beta.cafeq_app.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.beta.cafeq_app.DAO
import com.beta.cafeq_app.R
import com.beta.cafeq_app.data.Cafe
import com.beta.cafeq_app.databinding.AddCafeBinding
import com.google.android.gms.tasks.Task
import java.text.DecimalFormat
import java.util.*

class AddCafe : Fragment(R.layout.add_cafe) {
    private lateinit var binding: AddCafeBinding
    private lateinit var cafeImage: Uri
    private lateinit var fragContext: Activity
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            cafeImage = it
            binding.ivAdd.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddCafeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragContext = context as Activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivAdd.setOnClickListener {
            resultLauncher.launch("image/*")
        }
        binding.btnAddCafe.setOnClickListener {
            val name = binding.etCafeName.text.toString()
            val address = binding.etCafeAddr.text.toString()
            val chair = binding.etChair.text.toString()

            if (binding.ivAdd.drawable.constantState == ContextCompat.getDrawable(requireContext(), R.drawable.large_add)?.constantState) {
                Toast.makeText(fragContext,"Gambar cafe belum di-upload", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(name.isEmpty()){
                binding.etCafeName.error = "Nama cafe harus diisi"
                binding.etCafeName.requestFocus()
                return@setOnClickListener
            }
            if(address.isEmpty()){
                binding.etCafeAddr.error = "Alamat cafe harus diisi"
                binding.etCafeAddr.requestFocus()
                return@setOnClickListener
            }
            if(chair.isEmpty()){
                binding.etChair.error = "Jumlah kursi harus diisi"
                binding.etChair.requestFocus()
                return@setOnClickListener
            }

            cafeFirebase(name,address,chair.toInt())
        }
    }

    private fun cafeFirebase(name: String, address: String, chair: Int) {
        val dialog = Dialog(fragContext)
        dialog.setContentView(R.layout.loading_dialog)
        if (dialog.window!=null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
        val distance: Double = 0.1 + Math.random() * (3 - 0.1)
        val rating: Double = 4 + Math.random() * (5 - 4)
        val df = DecimalFormat("#.##")
        var url: String
        DAO.uploadImage(cafeImage,UUID.randomUUID().toString())
            .addOnSuccessListener { img ->
                val downloadUrl: Task<Uri> = img!!.storage.downloadUrl
                downloadUrl.addOnCompleteListener { img ->
                    url = ("https://"
                            + img.result.encodedAuthority
                            + img.result.encodedPath.toString() + "?alt=media&token="
                            + img.result.getQueryParameters("token")[0])

                    DAO.addCafe(Cafe(url,name,address,chair,df.format(distance),df.format(rating)))
                    binding.ivAdd.setImageDrawable(resources.getDrawable(R.drawable.large_add))
                    binding.etCafeName.text?.clear()
                    binding.etCafeAddr.text?.clear()
                    binding.etChair.text?.clear()
                    dialog.dismiss()
                    Toast.makeText(fragContext,"Cafe berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }
    }
}