package com.example.chanst.camera2x;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chanst on 16-3-20.
 */
public class lines extends View {
    private Paint mPaint;
    private Point pt1 = new Point();
    private Point pt2 = new Point();
    private Point pt3 = new Point();
    private Paint choosedPaint;
    private static int point = -1;
    private int radius = 50;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        choosedPaint = new Paint();
        choosedPaint.setColor(Color.parseColor("#FF0000"));
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ADFF2F"));
        float w = (float) getMeasuredWidth();
        float h = (float) getMeasuredHeight();
        pt1.x = (int) (w / 2);
        pt1.y = (int) (h / 5);
        pt2.x = (int) (w / 5);
        pt2.y = (int) (4 * h / 5);
        pt3.x = (int) (4 * w / 5);
        pt3.y = (int) (4 * h / 5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(pt1.x, pt1.y, radius, mPaint);
        canvas.drawCircle(pt2.x, pt2.y, radius, mPaint);
        canvas.drawCircle(pt3.x, pt3.y, radius, mPaint);
        canvas.drawLine(pt1.x, pt1.y, pt2.x, pt2.y, mPaint);
        canvas.drawLine(pt2.x, pt2.y, pt3.x, pt3.y, mPaint);
        canvas.drawLine(pt1.x, pt1.y, pt3.x, pt3.y, mPaint);
        switch (point) {
            case 1:
                canvas.drawCircle(pt1.x, pt1.y, radius, choosedPaint);
                break;
            case 2:
                canvas.drawCircle(pt2.x, pt2.y, radius, choosedPaint);
                break;
            case 3:
                canvas.drawCircle(pt3.x, pt3.y, radius, choosedPaint);
                break;

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point fingerDown = new Point((int) event.getX(), (int) event.getY());
        int distance1 = calculateDistance(pt1, fingerDown);
        int distance2 = calculateDistance(pt2, fingerDown);
        int distance3 = calculateDistance(pt3, fingerDown);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (distance1 < distance2 && distance1 < distance3 && distance1 < 100)
                    point = 1;
                else if (distance2 < distance3 && distance2 < distance1 && distance2 < 100)
                    point = 2;
                else if (distance3 < distance1 && distance3 < distance2 && distance3 < 100)
                    point = 3;
                break;
            case MotionEvent.ACTION_MOVE:
                if (point > 0) {
                    switch (point) {
                        case 1:
                            pt1 = fingerDown;
                            break;
                        case 2:
                            pt2 = fingerDown;
                            break;
                        case 3:
                            pt3 = fingerDown;
                            break;
                    }
                    this.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                point = -1;
                this.invalidate();
                break;
        }
        return true;
    }

    private int calculateDistance(Point p1, Point p2) {
        int distance = (int) Math.sqrt(Math.pow((double) (p1.x - p2.x), 2) + Math.pow((double) (p1.y - p2.y), 2));
        return distance;
    }


}
