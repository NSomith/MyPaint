package com.example.paint.customview

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.BitmapCompat
import com.example.paint.modal.FingerPath
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.abs
import kotlin.random.Random

class PaintView : View {

    private var fos: OutputStream? = null
    private lateinit var imageUri: Uri

    companion object {
        const val BRUSH_SIZE = 5
        const val DEFAULT_COLOR = Color.BLACK
        const val DEFAULT_BG_COLOR = Color.WHITE
        private const val TOUCH_TOLERANCE = 0f
    }

    private var mX: Float = 0f
    private var mY: Float = 0f
    private var mPath: Path? = null
    private var mPaint: Paint? = Paint()
    private val paths: MutableList<FingerPath> = mutableListOf()
    private var currentColor = 0
    private var backgroundColors: Int = DEFAULT_BG_COLOR
    private var strokeWidth = 0
    private var blur = false
    private var mBlur: MaskFilter? = null
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mPaint?.apply {
            isAntiAlias = true
            isDither = true
            color = DEFAULT_COLOR
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            xfermode = null
            alpha = 0xff
        }
    }

    fun init(metrices: DisplayMetrics) {
        val height = metrices.heightPixels
        val width = metrices.widthPixels
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = mBitmap?.let { Canvas(it) }
        currentColor = DEFAULT_COLOR
        strokeWidth = BRUSH_SIZE
    }

    fun setColor(colorCode: String) {
        currentColor = Color.parseColor(colorCode)
    }

    fun setBrushSize(brushSize: Int) {
        strokeWidth = brushSize
    }

    fun setEraser(brushSize: Int) {
        blur = false
        strokeWidth = brushSize
        currentColor = DEFAULT_BG_COLOR
    }

    fun normal() {
        blur = false
        currentColor = DEFAULT_COLOR
        strokeWidth = BRUSH_SIZE
    }

    fun doBlur(trueFalse: Boolean) {
        blur = trueFalse
    }

    fun blurEffect(effect: BlurMaskFilter.Blur) {
        mBlur = BlurMaskFilter(strokeWidth.toFloat(), effect)
    }

    fun undo() {
        val size = paths.size
        if (size >= 1) {
            paths.removeAt(size - 1)
            invalidate()
        }
    }

    fun clear() {
        backgroundColors = DEFAULT_BG_COLOR
        paths.clear()
        normal()
        invalidate()
    }

    fun download(context: Context) {
        val randomNum = Random.nextInt().toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.also { reslover ->
                val contentValues = ContentValues().apply {
                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "myPaint$randomNum.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri =
                    reslover.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let {
                    reslover.openOutputStream(it)
                }
            }
        }else{
           val path = Environment.getExternalStorageDirectory().toString()
            val dir =File("$path/download")
            if(!dir.exists()){
                dir.mkdir()
            }
            val file = File("$path/download/", "myPaint$randomNum.jpg")
            if(!file.exists()){
                file.createNewFile()
            }
            fos = FileOutputStream(file)
        }
        fos?.use {
            mBitmap?.compress(Bitmap.CompressFormat.JPEG,100,it)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        mCanvas?.drawColor(backgroundColors)
        for(fp in paths){
            mPaint?.color = fp.color
            mPaint?.strokeWidth = fp.strokeWidth.toFloat()
            mPaint?.maskFilter = null
            if (fp.blur) {
                mPaint?.maskFilter = fp.blurEffect
            }
            mCanvas?.drawPath(fp.path, mPaint!!)
        }
        canvas?.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas?.restore()
    }

    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = FingerPath(currentColor, blur, mBlur, strokeWidth, mPath!!)
        paths.add(fp)
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath!!.lineTo(mX, mY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }
}