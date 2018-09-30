package com.project3400.usyd.handwritingcapturing;

import android.graphics.*;
import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    boolean endDraw = true;
    boolean lockInput;
    long toastTime = System.currentTimeMillis();
    float pPres = 0.5f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float mPres = pPres + (event.getPressure() - pPres) / 4;  //smooth thickness change
        pPres = mPres;
        float mColor[] = {0, 1, 1};

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //touch down
                fingerOrPen = event.getPressure() == 1;
                if (endDraw) {
                    lockInput = fingerOrPen;
                    endDraw = false;
                }
                if (lockInput != fingerOrPen) {
                    makeToast();
                    return true;
                }
                mPath.moveTo(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:  //touch move
                if (lockInput != fingerOrPen) {
                    makeToast();
                    return true;
                }
                mPath.lineTo(event.getX(), event.getY());
                mPaint.setStrokeWidth(fingerOrPen ? 10 : 5 + mPres * 25);
                mColor[0] = fingerOrPen ? 120 : mPres * 80 + 80;    //hue
                mColor[1] = fingerOrPen ? 1 : mPres * 0.5f + 0.5f;  //saturation
                mPaint.setColor(Color.HSVToColor(mColor));
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                invalidate();
                return true;
            default:
                return true;
        }
    }

    public void makeToast() {
        if (System.currentTimeMillis() - toastTime > 2000) {
            Toast.makeText(getContext(), "Please use pen or finger only", Toast.LENGTH_SHORT).show();
            toastTime = System.currentTimeMillis();
        }
    }

    public void reset() {
        endDraw = true;
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void save() {

        //create folder
        String externalDataPath = Environment.getExternalStorageDirectory() + "/HWOutput";
        File f = new File(externalDataPath);
        boolean fileCreated = false;
        if (!f.exists()) {
            fileCreated = f.mkdir();
        }

        //save file
        File file = new File(Environment.getExternalStorageDirectory(), "/HWOutput/output.csv");
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write("test-hello\n".getBytes());
            fos.flush();
            fos.close();
            Toast.makeText(getContext(), fileCreated ? "Your Output will be saved to /HWOutput/output.csv" :
                    "New capture added successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}