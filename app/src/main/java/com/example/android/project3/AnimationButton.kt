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
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class AnimationButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentWidth = 0
    private var heightSize = 50
    private var finalWidth = 1500
    private val rectF = RectF()
    private var buttonColor = 0


    val circle = LoadingCircle(context)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Loading -> {
                startAnimation()
            }
            else -> Log.i("TEST", new.toString())
        }
    }

    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }



    private fun startAnimation(){
         val valueAnimator = ValueAnimator.ofInt(0, finalWidth).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 5000
            repeatCount = 0
            addUpdateListener { valueAnimator ->
                currentWidth = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator.start()
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_btnColor1, 0)
        }

        isClickable = true
    }

    override fun performClick(): Boolean {

        buttonColor = resources.getColor(R.color.colorPrimaryDark)
        circle.startAnimation()

        invalidate()
        return super.performClick()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paintButton.color = buttonColor
        canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paintButton)

        if(currentWidth == finalWidth){
            paintButton.color = resources.getColor(R.color.colorPrimary)
            canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paintButton)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rectF.set(0F, h.toFloat(), w.toFloat(), 0F)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        currentWidth = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}
