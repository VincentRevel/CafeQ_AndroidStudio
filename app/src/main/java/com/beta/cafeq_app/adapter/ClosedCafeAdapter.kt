package com.beta.cafeq_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beta.cafeq_app.R
import com.beta.cafeq_app.data.CafeDTO
import com.bumptech.glide.Glide
import com.beta.cafeq_app.databinding.CardCafeBinding

class ClosedCafeAdapter: RecyclerView.Adapter<ClosedCafeAdapter.CardViewViewHolder>() {
    private val cafeList = ArrayList<CafeDTO>()
    private var onItemCafeClickCallback: OnItemCafeClickCallback? = null

    inner class CardViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = CardCafeBinding.bind(itemView)
        fun bind(cafe: CafeDTO) {
            with(binding) {
                tvCafeName.text = cafe.name
                tvCafeAddr.text = cafe.address
                tvChair.text = "${cafe.chair} kursi tersedia"
                tvDistance.text = "${cafe.distance} Km"
                tvRating.text = cafe.rating
                Glide.with(itemView.context)
                    .load(cafe.img)
                    .into(ivCafe)
            }
            itemView.setOnClickListener{
                onItemCafeClickCallback?.onItemCafeClicked(cafe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_cafe, parent, false)
        return CardViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(cafeList[position])
    }

    override fun getItemCount(): Int {
        return cafeList.size
    }

    fun setOnItemCafeClickCallback(onItemCafeClickCallback: OnItemCafeClickCallback) {
        this.onItemCafeClickCallback = onItemCafeClickCallback
    }

    fun setItems(aList: ArrayList<CafeDTO>) {
        cafeList.addAll(aList)
    }

    interface OnItemCafeClickCallback {
        fun onItemCafeClicked (data: CafeDTO)
    }
}