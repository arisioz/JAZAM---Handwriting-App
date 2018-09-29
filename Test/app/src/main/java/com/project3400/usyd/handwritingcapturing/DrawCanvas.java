package com.project3400.usyd.handwritingcapturing;

import android.graphics.*;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawCanvas extends View {

    private static Bitmap mBitmap;
    private static Canvas mCanvas;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private boolean fingerOrPen = true; //true is finger, false is pen

    public DrawCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
    }

    @Override
    protected void onSizeChanged(int width, int height, int preWidth, int preHeight) {
        super.onSizeChanged(width, height, preWidth, preHeight);
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }


    float hue[] = {0,1,1};
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        hue[0]+=1;
        hue[0]%=360;
        mPaint.setColor(Color.HSVToColor(hue));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //touch down
                fingerOrPen = event.getPressure() == 1;
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:  //touch move
                mPath.lineTo(event.getX(), event.getY());
                mPaint.setStrokeWidth(fingerOrPen ? 10 : 5 + event.getPressure() * 25);
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:  //touch up
                break;
        }
        return true;
    }

    public void clearCanvas() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}