package com.example.android.project3
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingCircle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rectF = RectF()
    private var currentSweepAngle = 0

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                startAnimation()
            }
            else -> Log.i("TEST", new.toString())
        }
    }

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    private fun startAnimation() {
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(1, 360).apply {
            interpolator = AccelerateInterpolator()
            duration = 5000
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }

        valueAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rectF.set(0F, 0F, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = resources.getColor(R.color.colorAccent)
        canvas.drawArc(rectF, 270F, currentSweepAngle.toFloat(), true, paint);

        if (currentSweepAngle == 360) {
            refreshCircle(canvas)
        }
    }

    private fun refreshCircle(canvas: Canvas){
        paint.color = resources.getColor(R.color.colorPrimary)
        canvas.drawArc(rectF, 270F, currentSweepAngle.toFloat(), true, paint);
    }
}
