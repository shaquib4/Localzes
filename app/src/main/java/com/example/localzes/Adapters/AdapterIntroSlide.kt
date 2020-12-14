package com.example.localzes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.localzes.Modals.IntroSlide
import com.example.localzes.R


/*
class AdapterIntroSlide(
    val context: Context, private val introSlides: List<IntroSlide>
) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen = inflater.inflate(R.layout.slide_layout_container, null)
        val imgSlide = layoutScreen.findViewById<ImageView>(R.id.imgSplash)
        imgSlide.setImageResource(introSlides[position].image)
        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return introSlides.size

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }

}*/
class AdapterIntroSlide(
    val context: Context, private val introSlides: List<IntroSlide>
) :
    PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.slide_layout_container, null)
        val imgSlide =
            layoutScreen.findViewById<ImageView>(
                R.id.imgSplash
            )
        imgSlide.setImageResource(introSlides[position].image)
        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun getCount(): Int {
        return introSlides.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

}
