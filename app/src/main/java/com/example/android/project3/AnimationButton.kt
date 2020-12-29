package com.example.android.project3

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class AnimationButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentWidth = 0
    private var finalWidth = 1500
    private val rectF = RectF()
    private var buttonColor = 0


    var buttonState: ButtonState by Delegates.observable(ButtonState.Clicked) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> startAnimation()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private fun startAnimation() {
        buttonColor = resources.getColor(R.color.colorPrimaryDark)

        val valueAnimator = ValueAnimator.ofInt(0, finalWidth).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 5000
            addUpdateListener { valueAnimator ->
                currentWidth = valueAnimator.animatedValue as Int
                invalidate()
            }
        }

        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = buttonColor
        canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paint)

        if (currentWidth == finalWidth) {
            refreshButton(canvas)
        }
    }

    private fun refreshButton(canvas: Canvas) {
        paint.color = resources.getColor(R.color.colorPrimary)
        canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rectF.set(0F, h.toFloat(), w.toFloat(), 0F)
    }
}
