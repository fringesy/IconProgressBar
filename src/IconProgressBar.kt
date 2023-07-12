package com.meow.fringe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs


class IconProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()

    private var viewWidth = 0
    private var viewHeight = 0

    private var radius = 0f
    private var indicatorRadius = 0f
    private var layerRadius = 0f

    private var max = 100f
    private val min = 0f

    private var progress = 0f
    private var backgroundColor = Color.parseColor("#4AD49D")
    private var progressColor = Color.parseColor("#4EA3FC")
    private var indicatorColor = Color.parseColor("#FC624E")
    private var layerColor = Color.parseColor("#4AD49D")
    private var leftIcon = Bitmap.createBitmap(BitmapFactory.decodeResource(resources, R.mipmap.icon_read_view_brightness_sub))
    private var rightIcon = Bitmap.createBitmap(BitmapFactory.decodeResource(resources, R.mipmap.icon_read_view_brightness_add))

    private var indicatorX = 0f

    private var onProgress: ((Float) -> Unit)? = null
    private var onStop: ((Float) -> Unit)? = null
    private var touchType = TouchType.DOWN
    private var gestureDetectorCompat: GestureDetectorCompat

    constructor(context: Context) : this(context, null)

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconProgressBar)
            max = typedArray.getFloat(R.styleable.IconProgressBar_ipb_max_value, 100f)
            progress = typedArray.getFloat(R.styleable.IconProgressBar_ipb_progress_value, 0f)
            backgroundColor = typedArray.getColor(R.styleable.IconProgressBar_ipb_background_color, backgroundColor)
            progressColor = typedArray.getColor(R.styleable.IconProgressBar_ipb_progress_color, progressColor)
            layerColor = typedArray.getColor(R.styleable.IconProgressBar_ipb_layer_color, layerColor)
            indicatorColor = typedArray.getColor(R.styleable.IconProgressBar_ipb_indicator_color, indicatorColor)
            leftIcon = typedArray.getDrawable(R.styleable.IconProgressBar_ipb_left_icon)?.toBitmap() ?: leftIcon
            rightIcon = typedArray.getDrawable(R.styleable.IconProgressBar_ipb_right_icon)?.toBitmap() ?: rightIcon
            typedArray.recycle()
        }
        gestureDetectorCompat = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                touchType = TouchType.DOWN
                return isEnabled
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                when (touchType) {
                    TouchType.DOWN -> {
                        touchType = if (abs(distanceX) > abs(distanceY)) {
                            TouchType.HORIZONTAL
                        } else {
                            TouchType.VERTICAL
                        }
                    }

                    TouchType.HORIZONTAL -> {
                        progress -= distanceX / (viewWidth - indicatorRadius * 2) * (max - min)
                        progress = min.coerceAtLeast(max.coerceAtMost(progress))
                        onProgress?.invoke(progress)
                        invalidate()
                    }

                    TouchType.VERTICAL -> {

                    }
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        layerRadius = viewHeight / 8f
        radius = viewHeight / 2f - layerRadius
        indicatorRadius = viewHeight / 2f
        indicatorX = indicatorRadius
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        resetPaint(backgroundColor)
        canvas?.drawRoundRect(layerRadius, layerRadius, viewWidth.toFloat() - layerRadius, viewHeight.toFloat() - layerRadius, radius, radius, paint)
        resetPaint(progressColor)
        indicatorX = (viewWidth.toFloat() - indicatorRadius * 2) * progress / max + indicatorRadius
        canvas?.drawRoundRect(
            layerRadius,
            layerRadius,
            indicatorX + indicatorRadius - layerRadius,
            viewHeight.toFloat() - layerRadius,
            radius, radius,
            paint
        )

        resetPaint(indicatorColor)
        canvas?.drawBitmap(leftIcon, radius, (viewHeight - leftIcon.height) / 2f, paint)
        canvas?.drawBitmap(rightIcon, viewWidth - radius - rightIcon.width, (viewHeight - rightIcon.height) / 2f, paint)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.setShadowLayer(10f, 0f, 0f, layerColor)
        canvas?.drawCircle(indicatorX, viewHeight / 2f, indicatorRadius - layerRadius, paint)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val ret = event?.let { gestureDetectorCompat.onTouchEvent(it) } ?: true
        if (touchType == TouchType.HORIZONTAL) {
            parent.requestDisallowInterceptTouchEvent(true)
            if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
                onStop?.invoke(progress)
            }
        }
        return super.dispatchTouchEvent(event) || ret
    }

    private fun resetPaint(color: Int) {
        paint.reset()
        paint.isAntiAlias = true
        paint.color = color
    }

    fun setOnProgress(onProgress: (Float) -> Unit) = apply {
        this.onProgress = onProgress
    }

    fun setOnStop(onStop: (Float) -> Unit) = apply {
        this.onStop = onStop
    }

    fun setMax(max: Float) = apply {
        this.max = max
    }

    fun setProgress(progress: Float) = apply {
        this.progress = progress
        invalidate()
    }

    enum class TouchType {
        DOWN, HORIZONTAL, VERTICAL
    }
}