package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var text = ""
    private var textSize = 0.0f
    private var textColor = context.getColor(R.color.white)
    private var recBackgroundColor = context.getColor(R.color.colorPrimary)


    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 50.0f
        textAlign = Paint.Align.CENTER
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            text = getString(R.styleable.LoadingButton_text).toString()
            textColor =
                getColor(R.styleable.LoadingButton_textColor, context.getColor(R.color.white))
            recBackgroundColor = getColor(
                R.styleable.LoadingButton_backgroundColor,
                context.getColor(R.color.colorPrimary)
            )
            textSize = getDimension(R.styleable.LoadingButton_textSize,21.0f)
            paint.textSize = textSize
        }
    }

    private fun Paint.getTextCenter() = (descent() + ascent() / 2)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = recBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        paint.color = textColor
        canvas?.drawText(
            text,
            (widthSize / 2).toFloat(),
            (heightSize / 2).toFloat() - paint.getTextCenter(),
            paint
        )

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