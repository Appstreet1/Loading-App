package com.example.android.project3

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat


class LoadingCircle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()
    private val rectF = RectF()
    private var currentSweepAngle = 0

    init {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)

        startAnimation()
    }

    fun startAnimation(){
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(1, 360).apply {
            interpolator = LinearInterpolator()
            duration = 5000
            repeatCount = 0
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }

        valueAnimator.start()
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        startAnimation()
        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rectF.set(0F, 0F, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(rectF, 270F, currentSweepAngle.toFloat(), true, paint);
    }
}
