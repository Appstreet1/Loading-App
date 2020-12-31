package com.example.android.project3.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.example.android.project3.R
import com.example.android.project3.utils.ButtonState
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentWidth = 0 //animation
    private var finalWidth = 1310 //animation

    private var buttonColor = 0
    private var heightSize = 0
    private var widthSize = 0

    private var currentSweepAngle = 0

    private var buttonText = ""

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Completed -> setButtonText()
            ButtonState.Clicked -> {
                startCircleAnimation()
                startButtonAnimation()
                setButtonText()
            }
            ButtonState.Loading -> setButtonText()
        }
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
        textSize = 80F
    }

    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintAnimationButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintCircle: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_btn_loading_color, 0)
        }
        setButtonText()
    }

    private fun startButtonAnimation() {
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

    private fun startCircleAnimation() {
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(1, 360).apply {
            interpolator = AccelerateInterpolator()
            duration = 5000
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }

        this.buttonState = ButtonState.Loading
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawButton(canvas)
        drawButtonAnimation(canvas)

        if (currentWidth == finalWidth) {
            this.buttonState = ButtonState.Completed
            refreshButton(canvas)
        }

        drawCircle(canvas)
        drawText(canvas, buttonText)

        if (currentSweepAngle == 360) {
            refreshCircle(canvas)
        }
    }

    private fun drawText(canvas: Canvas, buttonText: String) {
        canvas.drawText(
            buttonText,
            (widthSize / 2).toFloat(),
            (height / 2).toFloat() + 30,
            paintText
        )
    }

    private fun drawCircle(canvas: Canvas) {
        paintCircle.color = resources.getColor(R.color.colorAccent)
        canvas.drawArc(
            (widthSize - 200f),
            (heightSize / 2) - 50f,
            (widthSize - 100f),
            (heightSize / 2) + 50f,
            270F, currentSweepAngle.toFloat(),
            true, paintCircle
        )
    }

    private fun drawButton(canvas: Canvas) {
        paintButton.color = buttonColor
        canvas.drawRect(0F, height.toFloat(), width.toFloat(), 0F, paintButton)
    }

    private fun drawButtonAnimation(canvas: Canvas) {
        paintAnimationButton.color = resources.getColor(R.color.colorPrimaryDark)
        canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paintAnimationButton)
    }

    private fun refreshCircle(canvas: Canvas) {
        paintCircle.color = resources.getColor(R.color.colorPrimary)
        canvas.drawArc(
            (widthSize - 200f),
            (heightSize / 2) - 50f,
            (widthSize - 100f),
            (heightSize / 2) + 50f,
            270F, currentSweepAngle.toFloat(),
            true, paintCircle
        )
    }

    private fun refreshButton(canvas: Canvas) {
        paintAnimationButton.color = resources.getColor(R.color.colorPrimary)
        canvas.drawRect(0F, height.toFloat(), currentWidth.toFloat(), 0F, paintAnimationButton)
    }

    private fun setButtonText() {
        buttonText = when (this.buttonState) {
            ButtonState.Completed -> context.getString(R.string.Download)
            ButtonState.Loading -> context.getString(R.string.we_are_downloading)
            else -> context.getString(R.string.Download)
        }
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
