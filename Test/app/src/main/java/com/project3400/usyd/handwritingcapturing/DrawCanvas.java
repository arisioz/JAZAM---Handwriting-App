package com.project3400.usyd.handwritingcapturing;

import android.graphics.*;
import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DrawCanvas extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private DrawActivity mDrawActivity = (DrawActivity) getContext();

    private boolean demo = true;
    private boolean forceStop = false;  //reset while sketch is playing

    private Boolean fingerOrPen = null; //true is finger, false is pen
    private boolean endDraw = true;
    private boolean lockInput;
    private long toastTime = System.currentTimeMillis();
    private float pPres = 0.5f;
    private int day, month, year;

    ArrayList<ShapeData> cache = new ArrayList<>();

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

        //playing tutorial when activity starts
        playing(shapeMaker("Circle"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //you cannot draw while replaying
        if (playThread.isAlive()) {
            return false;
        }

        switch (event.getAction()) {

            //touch down
            case MotionEvent.ACTION_DOWN:
                fingerOrPen = event.getPressure() == 1;
                if (endDraw) {

                    //record start time
                    Calendar now = Calendar.getInstance();
                    day = now.get(Calendar.DATE);
                    month = now.get(Calendar.MONTH) + 1;
                    year = now.get(Calendar.YEAR);

                    lockInput = fingerOrPen;
                    endDraw = false;
                    mDrawActivity.changeIcon("save_black");
                    mDrawActivity.changeIcon("reset_black");
                    mDrawActivity.changeIcon("play_black");
                }
                if (lockInput != fingerOrPen) {
                    makeToast();
                    return false;
                }
                saveInputCache(event.getX(), event.getY(), event.getPressure(), true);
                drawTouchDown(event.getX(), event.getY());
                return true;

            //touch move
            case MotionEvent.ACTION_MOVE:
                if (lockInput != fingerOrPen) {
                    makeToast();
                    return true;
                }
                saveInputCache(event.getX(), event.getY(), event.getPressure(), false);
                drawTouchMove(event.getX(), event.getY(), event.getPressure());
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
            Toast.makeText(mDrawActivity, "Please use pen or finger only!", Toast.LENGTH_SHORT).show();
            toastTime = System.currentTimeMillis();
        }
    }

    public void saveInputCache(float x, float y, float pressure, Boolean isStartPoint) {
        if (lockInput) {
            //finger input
            cache.add(new ShapeData(x, y, System.currentTimeMillis(), isStartPoint));
        } else {
            //pen input
            cache.add(new ShapeData(x, y, pressure, System.currentTimeMillis(), isStartPoint));
        }
    }

    public void drawTouchDown(float x, float y) {
        mPath.moveTo(x, y);
    }

    public void drawTouchMove(float x, float y, float pressure) {

        float mColor[] = {0, 1, 1};
        float mPres = pPres + (pressure - pPres) / 4;  //smooth thickness change
        pPres = mPres;
        mPath.lineTo(x, y);
        mPaint.setStrokeWidth(lockInput ? 10 : 5 + mPres * 25);
        mColor[0] = lockInput ? 120 : mPres * 80 + 80;    //hue
        mColor[1] = lockInput ? 1 : mPres * 0.5f + 0.5f;  //saturation

        if (demo) {
            mColor[0] = mColor[1] = mColor[2] = 0;
        }

        mPaint.setColor(Color.HSVToColor(mColor));
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
        mPath.moveTo(x, y);
        invalidate();
    }

    public void reset() {

        //stop playing sketch if it is playing
        if (playThread.isAlive()) {
            playThread.interrupt();
            forceStop = true;
        }

        //check if anything drawn
        if (cache.size() <= 0) {
            Toast.makeText(mDrawActivity, "No need to reset!", Toast.LENGTH_SHORT).show();
            return;
        }

        //reset cache
        cache.clear();

        //reset assisted boolean
        fingerOrPen = null;
        endDraw = true;

        //reset canvas & icon color
        mDrawActivity.changeIcon("save_gray");
        mDrawActivity.changeIcon("reset_gray");
        mDrawActivity.changeIcon("play_gray");
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
        instantDraw(shapeMaker("Circle"));
        Toast.makeText(mDrawActivity, "Reset successfully!", Toast.LENGTH_SHORT).show();
    }

    public void save() {

        //check if anything drawn
        if (cache.size() <= 0) {
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

            //write header
            allCache.append(lockInput ? "Finger," : "Pen,");
            allCache.append(MainActivity.chosen_Shape.second);
            String time = "," + day + "/" + month + "/" + year + "\n";
            allCache.append(time);

            //write input cache
            for (ShapeData sd : cache) {
                String line = to4f(sd.x) + "," + to4f(sd.y) + ",";
                line += lockInput ? "" : to4f(sd.pressure) + ",";
                line += new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).
                        format(new Date(sd.time)) + "\n";
                allCache.append(line);
            }

            fos.write((allCache.toString() + "\n").getBytes());
            fos.flush();
            fos.close();
            Toast.makeText(mDrawActivity, fileCreated ? "Your ShapeData will be saved to /HWOutput/output.csv!" :
                    "New capture added successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String to4f(float num) {
        return new DecimalFormat("0.0000").format(num);
    }

    private Thread playThread = new Thread();

    public void play() {

        //check if in play mode
        if (playThread.isAlive()) {
            Toast.makeText(mDrawActivity, "Sketch is playing!", Toast.LENGTH_SHORT).show();
            return;
        }

        //check if anything drawn
        if (cache.size() <= 0) {
            Toast.makeText(mDrawActivity, "No sketch can be played!", Toast.LENGTH_SHORT).show();
            return;
        }

        //clean screen first
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        instantDraw(shapeMaker("Circle"));
        invalidate();

        //change icon colour
        mDrawActivity.changeIcon("play_gray");
        playing(cache);
    }

    public void playing(final ArrayList<ShapeData> al) {
        if (demo) {
            lockInput = true;
            fingerOrPen = true;
        }
        //auto replay
        playThread = new Thread() {
            @Override
            public void run() {
                long preLineTime = al.get(0).time;
                for (int i = 0; i < al.size(); i++) {
                    final ShapeData sd = al.get(i);
                    final boolean last = i == al.size() - 1;
                    if (i == 0) {
                        drawTouchDown(sd.x, sd.y);
                    } else {
                        try {
                            synchronized (this) {
                                //no more than 2s if two draw time gap is huge
                                wait(sd.time - preLineTime > 2000 ? 2000 : sd.time - preLineTime);

                                mDrawActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (forceStop) {  //reset while playing
                                            forceStop = false;
                                            return;
                                        }
                                        if (sd.isStartPoint) {
                                            drawTouchDown(sd.x, sd.y);
                                        } else {
                                            drawTouchMove(sd.x, sd.y, sd.pressure);
                                        }
                                        if (last) {
                                            if (!demo) {
                                                mDrawActivity.changeIcon("play_black");
                                            }
                                            demo = false;
                                        }
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    preLineTime = sd.time;
                }
            }
        };
        playThread.start();
    }

    public void instantDraw(ArrayList<ShapeData> al) {
        demo = true;
        boolean tmplI = lockInput;
        Boolean tmpfOP = fingerOrPen;
        lockInput = true;
        fingerOrPen = true;
        for (int i = 0; i < al.size(); i++) {
            ShapeData sd = al.get(i);
            if (sd.isStartPoint) {
                drawTouchDown(sd.x, sd.y);
            } else {
                drawTouchMove(sd.x, sd.y, sd.pressure);
            }
        }
        lockInput = tmplI;
        fingerOrPen = tmpfOP;
        demo = false;
    }

    public ArrayList<ShapeData> shapeMaker(String shapeType) {

        ArrayList<ShapeData> shape = new ArrayList<>();

        switch (shapeType) {

            case "Circle":
                for (int i = 0; i < 200; i++) {
                    //x=a+rcost y=b+rsint    math.sin from 0 to 2pi
                    float x = (float) (this.getWidth() / 2 + this.getWidth() / 5 * Math.cos(2 * Math.PI / 200 * i - Math.PI / 2));
                    float y = (float) (this.getHeight() / 2 + this.getWidth() / 5 * Math.sin(2 * Math.PI / 200 * i - Math.PI / 2));
                    if (i == 0) {
                        shape.add(new ShapeData(x, y, 1, true));
                        continue;
                    }
                    shape.add(new ShapeData(x, y, i * 8, false));
                }
                break;

            case "Square":

                break;

            case "Triangle":

                break;
            default:
                break;
        }
        return shape;
    }
}