package com.example.localzes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.IntroSlide
import com.example.localzes.R

class AdapterIntroSlide(private val introSlides: List<IntroSlide>) :
    RecyclerView.Adapter<AdapterIntroSlide.IntroSlideViewHolder>() {
    inner class IntroSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageSplash = view.findViewById<ImageView>(R.id.imgSplash)
        fun bind(introSlide: IntroSlide) {
            imageSplash.setImageResource(introSlide.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slide_layout_container, parent, false)
        return IntroSlideViewHolder(view)
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }
}