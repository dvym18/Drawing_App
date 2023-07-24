package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet

class DrawingView(context: Context, attrs:AttributeSet): View(context,attrs) {

    private var mdrawpath:Custompath?=null
    private var mCanvasBitmap:Bitmap ?=null
    private var mDrawPaint: Paint?=null
    private var mCanvasPaint:Paint?=null
    private var mBrushSize:Float=0.toFloat()
    private var color= Color.BLACK
    private var canvas: Canvas?=null
    private val mpaths=ArrayList<Custompath>()
    private val mundopaths=ArrayList<Custompath>()


    init {
        setUpDrawing()
    }
    fun onclickundo(){
        if (mpaths.size>0){
            mundopaths.add(mpaths.removeAt(mpaths.size-1))
            invalidate()
        }
    }
    private fun setUpDrawing(){
            mDrawPaint=Paint()
            mdrawpath=Custompath(color,mBrushSize)
            mDrawPaint!!.color=color
            mDrawPaint!!.style=Paint.Style.STROKE
            mDrawPaint!!.strokeJoin=Paint.Join.ROUND
            mDrawPaint!!.strokeCap=Paint.Cap.ROUND
            mCanvasPaint= Paint(Paint.DITHER_FLAG)
            // mBrushSize=20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(mCanvasBitmap!!)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        for(path in mpaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }



        if(!mdrawpath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mdrawpath!!.brushThickness
            mDrawPaint!!.color = mdrawpath!!.color
            canvas.drawPath(mdrawpath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX=event?.x
        val touchY=event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mdrawpath!!.color=color
                mdrawpath!!.brushThickness=mBrushSize

                mdrawpath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mdrawpath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE->{
                if (touchX != null) {
                    if (touchY != null) {
                        mdrawpath!!.lineTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                mpaths.add(mdrawpath!!)
                mdrawpath=Custompath(color,mBrushSize)
            }
            else ->return false

        }
        invalidate()
        return true
    }

    fun setSizeforBrush(newSize:Float){
        mBrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        newSize,resources.displayMetrics
        )
        mDrawPaint!!.strokeWidth=mBrushSize
    }
    fun setcolor(newColor:String){
        color=Color.parseColor(newColor)
        mDrawPaint!!.color=color

    }
    internal inner class Custompath(var color:Int,var brushThickness:Float):Path() {



    }
}