package com.udacity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // get view width and height
    private var widthSize = 0
    private var heightSize = 0

    // circle radius
    private var radius = 0.0f

    // text that will be display
    private var text = ""
    private var textSize = 0.0f
    private var circleMarginLift = 0.0f
    private var textWidth = 0.0f

    // text that will be display on loading state
    private var loadingText = "We are loading"

    // text that will be display on complete state
    private var defaultText = "Download"

    // var that will use to get textWidth
    private var textBounder = Rect()
    private var rectF = RectF()
    private var textColor = context.getColor(R.color.white)
    private var recBackgroundColor = context.getColor(R.color.colorPrimary)
    private var bgAnimationColor = context.getColor(R.color.colorPrimaryDark)

    // current animations value
    private var bgAnimationValue = 0.0f
    private var circleAnimationValue = 0.0f

    private val animationSet: AnimatorSet = AnimatorSet()

    private var bgValueAnimator: ValueAnimator? = null
    private var circleValueAnimator: ValueAnimator? = null

    private var isClick = true
    private var isLoading = false


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            is ButtonState.Loading -> {
                text = loadingText
                isLoading = true
                paint.getTextBounds(text, 0, text.length, textBounder)
                textWidth = textBounder.width().toFloat()
                invalidate()

            }
            is ButtonState.Completed -> {

            }
            is ButtonState.Clicked -> {

            }
        }
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 50.0f
        textAlign = Paint.Align.CENTER
    }

    init {
        isClickable = isClick
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultText = getString(R.styleable.LoadingButton_text) ?: defaultText
            loadingText = getString(R.styleable.LoadingButton_onLoadingText) ?: loadingText
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
            circleMarginLift = getDimension(R.styleable.LoadingButton_circleMarginLift, 20.0f)

        }
        text = defaultText

    }

    // get center point between  ascent and descent to calculate center of text (vertical)
    private fun Paint.getTextCenter() = (descent() + ascent() / 2)


    private fun drawButtonBody(canvas: Canvas?) {
        paint.color = recBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

    }

    private fun drawButtonAnimationBackground(canvas: Canvas?) {
        paint.color = bgAnimationColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas?) {
        val centerPointX = (widthSize / 2).toFloat()
        val centerPointY = (heightSize / 2).toFloat()
        paint.color = textColor
        canvas?.drawText(
            text,
            centerPointX,
            centerPointY - paint.getTextCenter(),
            paint
        )
    }

    // get start start (x,y) , end (x,y) that will use to draw arc
    private fun calculateArcRecF(): RectF {
        val endOfTextPointX = (widthSize / 2) + (((textWidth + circleMarginLift) / 2) + radius)
        val centerOfViewHeightPointY = (heightSize / 2)
        return RectF(
            endOfTextPointX - radius,
            centerOfViewHeightPointY - radius,
            endOfTextPointX + radius,
            centerOfViewHeightPointY + radius
        )
    }

    private fun drawArc(canvas: Canvas?) {
        paint.color = context.getColor(R.color.colorAccent)
        canvas?.drawArc(calculateArcRecF(), 0f, 360f, true, paint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // draw button body (rectangle)
        drawButtonBody(canvas)

        // draw button background animation(rectangle) only on Loading state
        if (isLoading)
            drawButtonAnimationBackground(canvas)

        // draw text in button
        drawText(canvas)

        // draw arc only on loading state
        if (isLoading)
            drawArc(canvas)

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
        radius = (min(w, h) / 4).toFloat()

        setMeasuredDimension(w, h)
    }


    override fun performClick(): Boolean {
        return if (!isLoading) super.performClick() else true
    }

    @JvmName("setButtonState1")
    fun setButtonState(state: ButtonState) {
        buttonState = state
    }
}

private const val TAG = "LoadingButton"

