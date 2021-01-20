package com.localzes.android.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Modals.IntroSlide
import com.localzes.android.R


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
/*
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
*/
class AdapterIntroSlide(private var images: List<IntroSlide>) :
    RecyclerView.Adapter<AdapterIntroSlide.Pager2ViewHolder>() {
    inner class Pager2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageSplash: ImageView = view.findViewById(R.id.imgSplash)
        fun bind(introSlide: IntroSlide) {
            imageSplash.setImageResource(introSlide.image)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slide_layout_container, parent, false)
        return Pager2ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.bind(images[position])
    }
}
