package com.example.paint.customview

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import com.example.paint.modal.FingerPath
import java.io.OutputStream

class PaintView:View {

    private lateinit var fos: OutputStream
    private lateinit var imageUri: Uri

    companion object{
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

    }
}