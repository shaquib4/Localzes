package com.example.localzes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.localzes.Adapters.AdapterIntroSlide
import com.example.localzes.Modals.IntroSlide
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSliderActivity : AppCompatActivity() {
    /*private val introSliderAdapter = AdapterIntroSlide(
        listOf(
            IntroSlide(R.drawable.splash_screen),
            IntroSlide(R.drawable.localze_shop),
            IntroSlide(R.drawable.lanju)
        )
    )
    lateinit var preference: SharedPreferences
    private lateinit var btnNext: Button
    val pref_show_intro = "Intro"
    private lateinit var skipIntro: TextView*//*
    private lateinit var viewPager: ViewPager
    private lateinit var introViewPagerAdapter: AdapterIntroSlide
    private lateinit var tabIndicator: TabLayout
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button
    var position = 0
    private lateinit var btnAnim: Animation*/
    private lateinit var btnNext: Button
    private lateinit var btnGetStarted: Button
    private lateinit var btnAnim: Animation
    private val introSliderAdapter = AdapterIntroSlide(
        listOf(
            IntroSlide(R.drawable.register),
            IntroSlide(R.drawable.seller),
            IntroSlide(R.drawable.customer),
            IntroSlide(R.drawable.deliver)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_intro_slider)
        btnNext = findViewById(R.id.btn_next)
        btnGetStarted = findViewById(R.id.btn_get_started)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
        if (restorePrefData()) {
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }
        view_pager2.adapter = introSliderAdapter
        btnNext.setOnClickListener {
            if (view_pager2.currentItem + 1 < introSliderAdapter.itemCount) {
                view_pager2.currentItem += 1
            } else {
                loadLastScreen()
            }
        }
        btnGetStarted.setOnClickListener {
            startActivity(Intent(this, SplashScreenActivity::class.java))
            savePrefData()
            finish()
        }

        /*viewPager = findViewById(R.id.screen_viewpager)
        tabIndicator = findViewById(R.id.tab_indicator)
        btnNext = findViewById(R.id.btn_next)
        btnGetStarted = findViewById(R.id.btn_get_started)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)

        if (restorePrefData()) {
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }
        val mList: MutableList<IntroSlide> = ArrayList()
        val fresh: Boolean = mList.add(IntroSlide(R.drawable.register))
        mList.add(IntroSlide(R.drawable.seller))
        mList.add(IntroSlide(R.drawable.customer))
        mList.add(IntroSlide(R.drawable.deliver))
        introViewPagerAdapter = AdapterIntroSlide(this, mList)
        viewPager.adapter = introViewPagerAdapter
        tabIndicator.setupWithViewPager(viewPager)
        btnGetStarted.setOnClickListener {
            startActivity(Intent(this, SplashScreenActivity::class.java))
            savePrefData()
            finish()
        }
        btnNext.setOnClickListener {
            position = viewPager.currentItem
            if (position < mList.size) {
                position++
                viewPager.currentItem = position

            }
            if (position == mList.size - 1) {
                loadLastScreen()
            }
        }
        tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == mList.size - 1) {
                    loadLastScreen()
                }
            }

        })
        *//*btnNext = findViewById(R.id.btnNext)
        preference = getSharedPreferences("IntroSlider", Context.MODE_PRIVATE)
        if (!preference.getBoolean(pref_show_intro, true)) {
            startActivity(Intent(applicationContext, SplashScreenActivity::class.java))
            finish()
        }

        introSliderViewPager.adapter = introSliderAdapter
        setUpIndicators()
        setCurrentIndicator(0)
        introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        btnNext.setOnClickListener {
            if (introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount) {
                introSliderViewPager.currentItem += 1
            } else {
                startActivity(Intent(applicationContext, SplashScreenActivity::class.java))
                finish()
                val editor = preference.edit()
                editor.putBoolean(pref_show_intro, false)
                editor.apply()
            }
        }
    }

    private fun setUpIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorsContainer.addView(indicators[i])

        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }*//*
    }

    private fun savePrefData() {
        val pref: SharedPreferences = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        val pref: SharedPreferences =
            applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isIntroActivityOpenedBefore = pref.getBoolean("isIntroOpened", false)
        return isIntroActivityOpenedBefore
    }

    private fun loadLastScreen() {
        btnNext.visibility = View.INVISIBLE
        btnGetStarted.visibility = View.VISIBLE
        tabIndicator.visibility = View.INVISIBLE
        btnGetStarted.animation = btnAnim
    }*/
    }

    private fun savePrefData() {
        val pref: SharedPreferences = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        val pref: SharedPreferences =
            applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val isIntroActivityOpenedBefore = pref.getBoolean("isIntroOpened", false)
        return isIntroActivityOpenedBefore
    }

    private fun loadLastScreen() {
        btnNext.visibility = View.INVISIBLE
        btnGetStarted.visibility = View.VISIBLE
        btnGetStarted.animation = btnAnim
    }


}