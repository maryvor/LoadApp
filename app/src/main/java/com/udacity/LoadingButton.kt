package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var progress = 0.0
    private var valueAnimator = ValueAnimator()

    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0

    private val updateListener = ValueAnimator.AnimatorUpdateListener {
        progress = (it.animatedValue as Float).toDouble()
        //Log.i("Check", "Inside the updateListener")
        invalidate()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Completed -> {
                endAnimation()
            }
            ButtonState.Loading -> {

            }

        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Loading) {
            isClickable = false
            valueAnimator.repeatCount = Animation.INFINITE
            animation()
        }
        return true
    }

    private fun animation() {
        valueAnimator.start()
        //Log.i("Check", "Inside after animation start")
    }

    init {
        isClickable = true
        //another way of valueAminator declaration
//        valueAnimator = AnimatorInflater.loadAnimator(
//            context,
//            R.animator.loading_animation
//        ) as ValueAnimator

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0)

        buttonBackgroundColor = typedArray.getColor(R.styleable.LoadingButton_backgroundColor, 0)
        buttonTextColor = typedArray.getColor(R.styleable.LoadingButton_textColor, 0)

        valueAnimator = ValueAnimator.ofFloat(0f, 100f).apply {
            duration = 2000
            repeatCount = Animation.INFINITE
        }
            valueAnimator.addUpdateListener(updateListener)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        //Log.i("in onDraw", "$progress")

        if (buttonState == ButtonState.Loading || !progress.equals(100.0) && !progress.equals(0.0) && valueAnimator.repeatCount == 0){
            paint.color = resources.getColor(R.color.colorPrimaryDark)
            canvas?.drawRect(0f, 0f, widthSize*progress.toFloat()/100, heightSize.toFloat(), paint )

            paint.color = Color.YELLOW
            val left_circle = width*0.75f
            val right_circle = left_circle + heightSize*0.6f
            canvas?.drawArc(left_circle, heightSize*0.2f, right_circle, heightSize*0.8f,-90f, 360*progress.toFloat()/100, true, paint)

            paint.textAlign = Paint.Align.CENTER
            paint.color = buttonTextColor
            canvas?.drawText(resources.getString(R.string.loading), width/2.toFloat(), ((height + 30) / 2).toFloat(), paint)
        }
        if (buttonState == ButtonState.Completed && (progress.equals(0.0) || progress.equals(100.0) && valueAnimator.repeatCount == 0)) {
            paint.textAlign = Paint.Align.CENTER
            paint.color = buttonTextColor
            canvas?.drawText(resources.getString(R.string.download), width/2.toFloat(), ((height + 30) / 2).toFloat(), paint)
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

    fun downloadCompleted(){
        buttonState = ButtonState.Completed
    }

    fun setLoadingState(){
        buttonState = ButtonState.Loading
    }

    fun endAnimation(){
        valueAnimator.repeatCount = 0
        invalidate()
        requestLayout()
        isClickable = true
    }

}