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
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private Paint mPaint = new Paint();

    public DrawCanvas(Context c) {
        super(c);
    }

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);

    }

    private float mX, mY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        //System.out.println(event.getPressure());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - mX) >= 4 || Math.abs(y - mY) >= 4) {
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(mX, mY);
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                invalidate();
                break;
        }
        return true;
    }

    public void clearCanvas() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}