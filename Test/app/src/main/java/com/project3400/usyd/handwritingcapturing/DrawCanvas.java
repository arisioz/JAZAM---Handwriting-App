package com.project3400.usyd.handwritingcapturing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hmy on 9/12/2018.
 */

public class DrawCanvas extends View {

    private static Bitmap mBitmap;
    private static Canvas mCanvas;
    private Path mPath = new Path();
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    Context context;
    private Paint circlePaint = new Paint();
    private Path circlePath = new Path();
    private Paint mPaint = createPaint();

// DEFAULT_COLOR;
// BRUSH_SIZE;

    public DrawCanvas(Context c) {
        super(c);
    }

    public DrawCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
    }

    public Paint createPaint() {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        return mPaint;
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
                circlePath.reset();
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