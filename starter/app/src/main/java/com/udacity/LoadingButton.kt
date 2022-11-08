package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val TAG = "LoadingButton"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var text = ""
    private var loadingText = "We are loading"
    private var idelText = ""
    private var textSize = 0.0f
    private var textColor = context.getColor(R.color.white)
    private var recBackgroundColor = context.getColor(R.color.colorPrimary)
    private var bgAnimationValue = 0.0f
    private var bgAnimationColor = context.getColor(R.color.colorPrimaryDark)
    private var isClick = true

    private var valueAnimator: ValueAnimator? = null

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            is ButtonState.Loading -> {
                text = loadingText
                startBackgroundAnimation()
                valueAnimator?.start()
            }
            is ButtonState.Completed -> {
                if (valueAnimator?.isStarted != true)
                    valueAnimator?.start()
                valueAnimator?.currentPlayTime =
                    if (bgAnimationValue < 2800) 2800 else widthSize.toLong()


            }
            is ButtonState.Clicked -> {
                isClick = false
            }
        }
    }


    @JvmName("setButtonState1")
    fun setButtonState(state: ButtonState) {
        buttonState = state
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 50.0f
        textAlign = Paint.Align.CENTER
    }

    private fun startBackgroundAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            duration = 3000
            interpolator = AccelerateInterpolator()

            addUpdateListener {
                bgAnimationValue = it.animatedValue as Float
                if (bgAnimationValue == widthSize.toFloat()) {
                    bgAnimationValue = 0f
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        valueAnimator?.cancel()
                        text = idelText
                    }

                })
                invalidate()
            }

        }

    }


    override fun performClick(): Boolean {
        if (super.performClick()) return true


        return true
    }

    init {
        startBackgroundAnimation()
        valueAnimator?.start()
        isClickable = isClick
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            idelText = getString(R.styleable.LoadingButton_text).toString()
            textColor =
                getColor(R.styleable.LoadingButton_textColor, context.getColor(R.color.white))
            recBackgroundColor = getColor(
                R.styleable.LoadingButton_backgroundColor,
                context.getColor(R.color.colorPrimary)
            )
            textSize = getDimension(R.styleable.LoadingButton_textSize, 21.0f)
            paint.textSize = textSize

            bgAnimationColor = getColor(
                R.styleable.LoadingButton_animationBackgroundColor,
                context.getColor(R.color.colorPrimaryDark)
            )
        }
        text = idelText

    }

    private fun Paint.getTextCenter() = (descent() + ascent() / 2)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // draw button body (rectangle)
        paint.color = recBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        // draw button background animation(rectangle)
        paint.color = bgAnimationColor
        canvas?.drawRect(0f, 0f, bgAnimationValue, heightSize.toFloat(), paint)
        // draw text in button
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