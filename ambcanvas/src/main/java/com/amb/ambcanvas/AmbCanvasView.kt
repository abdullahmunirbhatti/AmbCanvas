package com.amb.ambcanvas

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.view.MotionEvent
import android.util.AttributeSet
import android.view.View
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class AmbCanvasView : View {

    private var canvas: Canvas? = null
    private val pathLists: MutableList<Path> = ArrayList()
    private val paintLists: MutableList<Paint> = ArrayList()
    private var historyPointer = 0
    var mode = PEN
    var drawer = PEN
    var paintStyle = Paint.Style.STROKE
    var paintStrokeColor = Color.BLACK
    private var paintStrokeWidth = 10f
    private var opacity = 255
    private var blur = 2f
    var drawPathEffect: PathEffect? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setup()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setup()
    }

    constructor(context: Context?) : super(context) {
        setup()
    }

    private fun setup() {
        pathLists.add(path)
        paintLists.add(createPaint())
        historyPointer++
    }


    private fun createPaint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = paintStyle
        paint.strokeWidth = paintStrokeWidth
        paint.strokeCap = Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND

        if (mode == ERASER) {
//            setLayerType(LAYER_TYPE_SOFTWARE, null)
            setLayerType(LAYER_TYPE_HARDWARE, null)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            paint.setARGB(0, 0, 0, 0)
        } else {
            paint.color = paintStrokeColor
//            paint.setShadowLayer(blur, 0f, 0f, paintStrokeColor)
            paint.alpha = opacity
//            paint.pathEffect = drawPathEffect
        }

        return paint
    }

    private fun createPath(): Path {
        /*val*/ path = Path()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
        return path
    }

    private val currentPath: Path
        get() = pathLists[historyPointer - 1]

//    var paint = Paint()
    private var path = Path()
    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private fun touchDown() {
        path = createPath()
        if (historyPointer == pathLists.size) {
            pathLists.add(path)
            paintLists.add(createPaint())
            historyPointer++
        } else {
            if (historyPointer >= 0) {
                pathLists[historyPointer] = path
                paintLists[historyPointer] = createPaint()
                historyPointer++
                var i = historyPointer
                val size = paintLists.size
                while (i < size) {
                    pathLists.removeAt(historyPointer)
                    paintLists.removeAt(historyPointer)
                    i++
                }
            }
        }
    }

    private fun touchMove(event: MotionEvent) {
        val x = event.x
        val y = event.y

        when (drawer) {
            PEN -> {
                path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
                currentX = motionTouchEventX
                currentY = motionTouchEventY
            }
            LINE -> {
                path.reset()
                path.moveTo(currentX, currentY)
                path.lineTo(x, y)
            }
            RECTANGLE -> {
                path.reset()
                val left = currentX.coerceAtMost(x)
                val right = currentX.coerceAtLeast(x)
                val top = currentY.coerceAtMost(y)
                val bottom = currentY.coerceAtLeast(y)
                path.addRect(left, top, right, bottom, Path.Direction.CCW)
            }
            CIRCLE -> {
                val distanceX = abs((currentX - x).toDouble())
                val distanceY = abs((currentY - y).toDouble())
                val radius = sqrt(distanceX.pow(2.0) + distanceY.pow(2.0))
                path.reset()
                path.addCircle(currentX, currentY, radius.toFloat(), Path.Direction.CCW)
            }
            ELLIPSE -> {
                val rect = RectF(currentX, currentY, x, y)
                path.reset()
                path.addOval(rect, Path.Direction.CCW)
            }
        }
        this.invalidate()

    }


    private fun touchUp() {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until historyPointer) {
            val path = pathLists[i]
            val paint = paintLists[i]
            canvas.drawPath(path, paint)
        }

        this.canvas = canvas
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchDown()
            MotionEvent.ACTION_MOVE -> touchMove(event)
            MotionEvent.ACTION_UP -> touchUp()
            else -> {}
        }
        return true
    }

    fun undo(): Boolean {
        return if (historyPointer > 0) {
            historyPointer--
            this.invalidate()
            true
        } else {
            false
        }
    }

    fun redo(): Boolean {
        return if (historyPointer < pathLists.size) {
            historyPointer++
            this.invalidate()
            true
        } else {
            false
        }
    }

    fun clear() {
        historyPointer = 0
        pathLists.clear()
        paintLists.clear()
        mode = PEN
        drawer = PEN
        this.invalidate()
    }

    fun enableEraser() {
        drawer = PEN
        mode = ERASER
    }

    fun startDrawing() {
        drawer = PEN
        mode = PEN
    }

    fun drawShape(shape: String) {
        when (shape) {
            "RECTANGLE" -> {
                drawer = RECTANGLE
            }
            "ELLIPSE" -> {
                drawer = ELLIPSE
            }
            "CIRCLE" -> {
                drawer = CIRCLE
            }
            "LINE" -> {
                drawer = LINE
            }
        }
    }

    fun getPaintStrokeWidth(): Float {
        return paintStrokeWidth
    }

    fun setPaintStrokeWidth(width: Float) {
        paintStrokeWidth = if (width >= 0) {
            width
        } else {
            5f
        }
    }

    fun getOpacity(): Int {
        return opacity
    }

    fun setOpacity(opacity: Int) {
        if (opacity in 0..255) {
            this.opacity = opacity
        } else {
            this.opacity = 255
        }
    }

    fun getBlur(): Float {
        return blur
    }

    fun setBlur(blur: Float) {
        if (blur >= 0) {
            this.blur = blur
        } else {
            this.blur = 0f
        }
    }

    companion object {
        const val ERASER = "ERASER"
        const val PEN = "PEN"
        const val LINE = "LINE"
        const val RECTANGLE = "RECTANGLE"
        const val CIRCLE = "CIRCLE"
        const val ELLIPSE = "ELLIPSE"
        const val TAG = "AmbLogs"
    }
}