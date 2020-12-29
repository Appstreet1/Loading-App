package com.example.android.project3.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.android.project3.utils.ButtonState
import com.example.android.project3.R
import kotlin.properties.Delegates

class AnimationButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var heightSize = 0
    private var widthSize = 0
    private var currentWidth = 0
    private var finalWidth = 1310
    private val rectF = RectF()
    private var buttonColor = 0
    private var buttonText = ""

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, old, new ->
        when (new) {
            ButtonState.Completed -> Log.i("TEST", "completed")
            ButtonState.Clicked -> startAnimation()
            ButtonState.Loading -> Log.i("TEST", "Loading")
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
        textSize = 80F
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
        this.buttonState = ButtonState.Loading
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawButton(canvas)

        if (currentWidth == finalWidth) {
            this.buttonState = ButtonState.Completed
            refreshButton(canvas)
        }
        setButtonText()

        drawText(canvas, buttonText)
    }

    private fun drawButton(canvas: Canvas){
        paint.color = buttonColor
        canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paint)
    }

    private fun setButtonText() {
        buttonText = when (this.buttonState) {
            ButtonState.Completed -> "Download"
            ButtonState.Loading -> "We are loading"
            else -> "Download"
        }
    }

    private fun drawText(canvas: Canvas, buttonText: String) {
        canvas.drawText(buttonText, (widthSize/ 2).toFloat() , (height / 2).toFloat() + 30, paintText)
    }

    private fun refreshButton(canvas: Canvas) {
        paint.color = resources.getColor(R.color.colorPrimary)
        canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paint)
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
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}
