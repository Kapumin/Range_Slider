package com.abjt.rangeslider

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.slider.RangeSlider


class MainActivity : AppCompatActivity() {

    private val tag = "Slider_Experiment"

    private lateinit var seekBar: MaterialRangeSlider
    private lateinit var rangeSlider: RangeSlider

    private lateinit var rangeProgressLeft: TextView
    private lateinit var rangeProgressRight: TextView
    private lateinit var seekProgress: TextView

    private var isSeeking: Boolean = false

    private val transparentColor by lazy {
        ContextCompat.getColor(this, R.color.transparent)
    }

    private val purple by lazy {
        ContextCompat.getColor(this, R.color.purple_200)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setupRangeSeekbar()
        setUpSeekbar()
        setRangeSeekListener()
        setupSeekListener()
    }

    private fun initViews() {
        seekBar = findViewById(R.id.my_seekbar)
        rangeSlider = findViewById(R.id.range_slider)
        rangeProgressLeft = findViewById(R.id.range_progress_left)
        rangeProgressRight = findViewById(R.id.range_progress_right)
        seekProgress = findViewById(R.id.seek_progress)
    }

    private fun setupRangeSeekbar() {
        rangeSlider.setCustomThumbDrawablesForValues(R.drawable.left_thumb, R.drawable.right_thumb)
        rangeSlider.trackActiveTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange))
        rangeSlider.trackInactiveTintList = ColorStateList.valueOf(purple)
        rangeSlider.trackHeight = 0
        rangeSlider.thumbRadius = 60
        rangeSlider.haloRadius = 0
//        rangeSlider.background = ContextCompat.getDrawable(this, R.drawable.rect)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpSeekbar() {
        seekBar.thumbOffset = 0
    }

    private fun setRangeSeekListener() {
        rangeSlider.addOnChangeListener { slider, value, fromUser ->
            Log.d(tag, "activeThumb = ${slider.activeThumbIndex}")
            if (fromUser) {
                setSeekBarLimits(slider, value.toInt())
            }
        }
    }

    private fun setSeekBarLimits(slider: RangeSlider, value: Int) {
        when (slider.activeThumbIndex) {
            0 -> {
                if (value > seekBar.progress) {
                    rangeProgressLeft.text = value.toString()
                    seekBar.progress = value
                }
            }
            1 -> {
                if (value < seekBar.progress) {
                    rangeProgressRight.text = value.toString()
                    seekBar.progress = value
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSeekListener() {
        seekBar.setOnTouchListener(OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isWithinThumb(event)) {
                        isSeeking = true
                        return@OnTouchListener false
                    }
                    if (isSeeking) {
                        isSeeking = false
                        return@OnTouchListener false
                    }
                    return@OnTouchListener !isWithinThumb(event)
                }
                MotionEvent.ACTION_UP -> {
                    if (isSeeking) {
                        isSeeking = false
                        return@OnTouchListener false
                    }
                    return@OnTouchListener !isWithinThumb(event)
                }
                MotionEvent.ACTION_MOVE -> if (!isSeeking) {
                    return@OnTouchListener true
                }
            }
            onTouchEvent(event)
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                seekProgress.text = value.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(p0: SeekBar?) = Unit

        })
    }

    private fun isWithinThumb(event: MotionEvent): Boolean {
        val seekRect = seekBar.thumb.bounds
        val newSeekThumb = Rect()
        val iWidth = seekRect.width()
        val iHeight = seekRect.height()
        newSeekThumb.left = seekRect.left - iWidth
        newSeekThumb.right = seekRect.right + iWidth
        newSeekThumb.bottom = seekRect.bottom + iHeight
        return newSeekThumb.contains(event.x.toInt(), event.y.toInt())
    }
}

