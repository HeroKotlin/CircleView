package com.github.herokotlin.circleview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class CircleView : View {

    companion object {

        var DEFAULT_TRACK_VALUE = 0f

        var DEFAULT_CENTER_RADIUS = 36

        var DEFAULT_CENTER_COLOR = Color.argb(255, 255, 255, 255)

        var DEFAULT_RING_WIDTH = 7

        var DEFAULT_RING_COLOR = Color.argb(255, 221, 221, 221)

        var DEFAULT_TRACK_WIDTH = 7

        var DEFAULT_TRACK_COLOR = Color.argb(255, 80, 80, 80)

        var DEFAULT_TRACK_OFFSET = 0

    }

    /**
     * 内圆
     */
    var centerRadius = DEFAULT_CENTER_RADIUS

        set(value) {
            field = value
            updateCircleBitmap()
        }

    var centerColor = DEFAULT_CENTER_COLOR

        set(value) {
            field = value
            updateCircleBitmap()
        }

    /**
     * 图片 id
     */
    var centerImage = 0

        set(value) {
            field = value
            rawBitmap = if (value > 0) BitmapFactory.decodeResource(resources, value) else null
            updateCircleBitmap()
        }

    /**
     * 圆环
     */
    var ringWidth = DEFAULT_RING_WIDTH
    var ringColor = DEFAULT_RING_COLOR

    /**
     * 高亮轨道
     */
    var trackWidth = DEFAULT_TRACK_WIDTH
    var trackColor = DEFAULT_TRACK_COLOR

    /**
     * 轨道默认贴着圆环的外边，给正值可以往内部来点，当然负值就能出去点...
     */
    var trackOffset = DEFAULT_TRACK_OFFSET

    /**
     * 轨道的值 0.0 - 1.0，影响轨道圆弧的大小
     */
    var trackValue = DEFAULT_TRACK_VALUE

        set(value) {
            field = when {
                value > 1 -> { 1f }
                value < 0 -> { 0f }
                else -> { value }
            }
        }

    /**
     * 回调函数
     */
    lateinit var callback: CircleViewCallback

    /**
     * 图片
     */
    private var rawBitmap: Bitmap? = null

    private var circleBitmap: Bitmap? = null

    /**
     * 是否正在触摸
     */
    private var isTouching = false

    /**
     * 是否触摸在圆内部
     */
    private var isTouchingInside = false

    /**
     * 是否正在长按
     */
    private var isLongPressing = false

    /**
     * 是否正在等待长按触发
     */
    private var isLongPressWaiting = false

    /**
     * 按下之后多少秒触发长按回调
     */
    private var longPressInterval = 1L

    /**
     * 半径 = 内圆 + 轨道
     */
    private var radius = 0

        get() {
            return centerRadius + ringWidth
        }

    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val trackRect = RectF()

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.CircleView, defStyle, 0)

        centerRadius = typedArray.getDimensionPixelSize(
            R.styleable.CircleView_circle_view_center_radius,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CENTER_RADIUS.toFloat(), resources.displayMetrics).toInt()
        )

        centerColor = typedArray.getColor(R.styleable.CircleView_circle_view_center_color, DEFAULT_CENTER_COLOR)

        centerImage = typedArray.getResourceId(R.styleable.CircleView_circle_view_center_image, 0)

        ringWidth = typedArray.getDimensionPixelSize(
            R.styleable.CircleView_circle_view_ring_width,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RING_WIDTH.toFloat(), resources.displayMetrics).toInt()
        )

        ringColor = typedArray.getColor(R.styleable.CircleView_circle_view_ring_color, DEFAULT_RING_COLOR)

        trackWidth = typedArray.getDimensionPixelSize(
            R.styleable.CircleView_circle_view_track_width,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TRACK_WIDTH.toFloat(), resources.displayMetrics).toInt()
        )

        trackColor = typedArray.getColor(R.styleable.CircleView_circle_view_track_color, DEFAULT_TRACK_COLOR)

        trackOffset = typedArray.getDimensionPixelSize(
            R.styleable.CircleView_circle_view_track_offset,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TRACK_OFFSET.toFloat(), resources.displayMetrics).toInt()
        )

        trackValue = typedArray.getFloat(R.styleable.CircleView_circle_view_track_value, DEFAULT_TRACK_VALUE)



        // 获取完 TypedArray 的值后，
        // 一般要调用 recycle 方法来避免重新创建的时候出错
        typedArray.recycle()

        setOnTouchListener { _, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    if (isPointInside(event.x, event.y)) {
                        touchDown()
                    }
                }

                MotionEvent.ACTION_UP -> {
                    touchUp(isTouchingInside)
                }

                MotionEvent.ACTION_CANCEL -> {
                    touchUp(false)
                }

                MotionEvent.ACTION_MOVE -> {
                    callback.onTouchMove(this, event.x, event.y)
                    val inside = isPointInside(event.x, event.y)
                    if (inside != isTouchingInside) {
                        isTouchingInside = inside
                        if (isTouchingInside) {
                            callback.onTouchEnter(this)
                        }
                        else {
                            callback.onTouchLeave(this)
                            if (isLongPressWaiting) {
                                isLongPressWaiting = false
                            }
                        }
                    }
                }

                else -> {

                }
            }
            true
        }

    }

    /**
     * 点是否在内圆中
     * 因为这个 View 的使用方式一般是把内圆作为可点击的按钮，周围的轨道作为状态指示
     */
    private fun isPointInside(x: Float, y: Float): Boolean {
        val offsetX = x - width / 2
        val offsetY = y - height / 2
        val distance = Math.sqrt((offsetX * offsetX + offsetY * offsetY).toDouble())
        return distance <= centerRadius
    }

    /**
     * 按下
     */
    private fun touchDown() {
        if (!isTouching) {
            isTouching = true
            isTouchingInside = true
            isLongPressWaiting = true
            callback.onTouchDown(this)

            val handler = Handler()
            handler.postDelayed(
                {
                    this.longPress()
                },
                longPressInterval * 1000
            )
        }
    }

    /**
     * 松开，inside 表示是否在内圆松开
     */
    private fun touchUp(inside: Boolean) {
        if (isTouching) {
            isTouching = false
            isTouchingInside = false

            if (isLongPressing) {
                callback.onLongPressEnd(this)
            }

            callback.onTouchUp(this, inside, isLongPressing)

            isLongPressing = false
        }
    }

    private fun longPress() {
        if (isTouching && isLongPressWaiting) {
            isLongPressWaiting = false
            isLongPressing = true
            callback.onLongPressStart(this)
        }
    }

    private fun updateCircleBitmap() {

        val bitmap = rawBitmap
        if (bitmap == null) {
            circleBitmap = bitmap
            return
        }

        val radius = centerRadius.toFloat()
        val size = 2 * radius

        val output = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val saved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(0f, 0f, size, size, null)
        }
        else {
            canvas.saveLayer(0f, 0f, size, size, null, Canvas.ALL_SAVE_FLAG)
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = centerColor
        paint.style = Paint.Style.FILL

        canvas.drawCircle(radius, radius, radius, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)


        // 这里如果用 paint 会导致 png 图片的透明区域变成灰色
        // 换成 drawPaint 就没有问题，怀疑是 xfermode 影响的...
        canvas.drawBitmap(bitmap, (size - bitmap.width) / 2f, (size - bitmap.height) / 2f, paint)

        paint.xfermode = null

        canvas.restoreToCount(saved)

        circleBitmap = output

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = widthSize
        var height = heightSize

        if (widthMode != MeasureSpec.EXACTLY) {
            width = 2 * radius
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            height = 2 * radius
        }

        setMeasuredDimension(width, height)

    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        val centerX = width.toFloat() / 2
        val centerY = height.toFloat() / 2


        // 画外圆
        if (ringWidth > 0) {
            drawPaint.style = Paint.Style.FILL
            drawPaint.color = ringColor
            canvas.drawCircle(centerX, centerY, radius.toFloat(), drawPaint)

            // 在上面画高亮圆弧
            if (trackWidth > 0 && trackWidth <= ringWidth) {
                drawPaint.style = Paint.Style.STROKE
                drawPaint.color = trackColor
                drawPaint.strokeWidth = trackWidth.toFloat()

                val halfWidth = trackWidth / 2

                trackRect.left = centerX - radius + halfWidth + trackOffset
                trackRect.top = centerY - radius + halfWidth + trackOffset
                trackRect.right = centerX + radius - halfWidth - trackOffset
                trackRect.bottom = centerY + radius - halfWidth - trackOffset

                canvas.drawArc(trackRect, -90f, trackValue * 360, false, drawPaint)
            }
        }

        // 画内圆
        drawPaint.style = Paint.Style.FILL
        drawPaint.color = centerColor
        canvas.drawCircle(centerX, centerY, centerRadius.toFloat(), drawPaint)

        // 绘制图片
        val bitmap = circleBitmap
        if (bitmap != null) {
            var left = ringWidth.toFloat()
            var top = left
            if (bitmap.width / 2 < centerRadius) {
                left += centerRadius - bitmap.width / 2
            }
            if (bitmap.height / 2 < centerRadius) {
                top += centerRadius - bitmap.height / 2
            }
            canvas.drawBitmap(bitmap, left, top, drawPaint)
        }

    }

}
