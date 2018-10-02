package com.project3400.usyd.handwritingcapturing;

import android.graphics.*;
import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;

public class DrawCanvas extends View {

    private static Bitmap mBitmap;
    private static Canvas mCanvas;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private DrawActivity mDrawActivity = (DrawActivity) getContext();

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

    private Boolean fingerOrPen = null; //true is finger, false is pen
    private boolean endDraw = true;
    private boolean lockInput;
    private long toastTime = System.currentTimeMillis();
    private float pPres = 0.5f;

    ArrayList<String> cache = new ArrayList<>();

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float mPres = pPres + (event.getPressure() - pPres) / 4;  //smooth thickness change
        pPres = mPres;
        float mColor[] = {0, 1, 1};

        switch (event.getAction()) {

            //touch down
            case MotionEvent.ACTION_DOWN:
                fingerOrPen = event.getPressure() == 1;
                if (endDraw) {
                    cache.add(fingerOrPen ? "Finger\n" : "Pen\n");
                    lockInput = fingerOrPen;
                    endDraw = false;
                    mDrawActivity.changeIcon("save_black");
                    mDrawActivity.changeIcon("reset_black");
                }
                if (lockInput != fingerOrPen) {
                    makeToast();
                    return true;
                }
                mPath.moveTo(event.getX(), event.getY());
                return true;

            //touch move
            case MotionEvent.ACTION_MOVE:
                if (lockInput != fingerOrPen) {
                    makeToast();
                    return true;
                }

                if (lockInput) {
                    //finger input
                    cache.add(event.getX() + "," + event.getY() + "\n");
                } else {
                    //pen input
                    cache.add(event.getX() + "," + event.getY() + "," + event.getPressure() + "\n");
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

    /* This function is called inside onTouchEvent
       So make a 2 sec time check to prevent endless call
    */
    public void makeToast() {
        if (System.currentTimeMillis() - toastTime > 2000) {
            Toast.makeText(mDrawActivity, "Please use pen or finger only", Toast.LENGTH_SHORT).show();
            toastTime = System.currentTimeMillis();
        }
    }

    public void reset() {
        //check if anything drawn
        if (fingerOrPen == null) {
            Toast.makeText(mDrawActivity, "No need to reset", Toast.LENGTH_SHORT).show();
            return;
        }

        //reset cache
        cache.clear();

        //reset assisted boolean
        fingerOrPen = null;
        endDraw = true;

        //reset canvas & icon color
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mDrawActivity.changeIcon("save_gray");
        mDrawActivity.changeIcon("reset_gray");
        invalidate();
        Toast.makeText(mDrawActivity, "Reset successfully!", Toast.LENGTH_SHORT).show();
    }

    public void save() {

        //check if anything drawn
        if (fingerOrPen == null) {
            Toast.makeText(mDrawActivity, "Nothing can be saved!", Toast.LENGTH_SHORT).show();
            return;
        }

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

            StringBuilder allCache = new StringBuilder();
            for (String s : cache) {
                allCache.append(s);
            }

            fos.write((allCache.toString() + "\n").getBytes());
            fos.flush();
            fos.close();
            Toast.makeText(mDrawActivity, fileCreated ? "Your Output will be saved to /HWOutput/output.csv!" :
                    "New capture added successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}