package com.example.chanst.camera2x;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chanst on 16-3-20.
 */
public class lines extends View {
    private Paint  mPaint;
    public lines(Context context) {
        this(context, null);
    }

    public lines(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public lines(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.parseColor("#ADFF2F"));
        float w =(float)getMeasuredWidth();
        float h = (float)getMeasuredHeight();
        canvas.drawLine(w / 2, 0, w / 2, h, mPaint);
        canvas.drawCircle(w/2,h/2, 150,mPaint);
    }
}
