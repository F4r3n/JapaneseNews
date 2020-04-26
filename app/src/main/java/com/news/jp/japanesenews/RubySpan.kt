package com.news.jp.japanesenews

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
import android.util.Log

class RubySpan(
    private val rtText : String,
    private val visibility : VISIBILE,
    private val factor : Float = 0.6f
) : ReplacementSpan(){

    enum class VISIBILE {
        VISIBLE,
        INVISIBLE,
        GONE
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        var sizeText :Int = 0
        if(text != null) {
            if (text.isNotEmpty()) sizeText = paint.measureText(text, start, end).toInt()
        }

        return sizeText
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetrics
        val textSize = paint.textSize

        val width : Float = paint.measureText(text, start, end)

        //Save values
        paint.textSize = textSize*factor
        val widthRT :Float = paint.measureText(rtText)

        if(visibility == VISIBILE.VISIBLE) {
            val offset = (widthRT - width) / 2.0f
            if (x != 0.0f || offset < 0)
                canvas.drawText(rtText, x - offset, y + fm.top, paint)
            else
                canvas.drawText(rtText, x, y + fm.top, paint)
        }

        paint.textSize = textSize
        if (text != null) {
            canvas.drawText(text,start,end, x, y.toFloat(), paint)
        }
    }
}