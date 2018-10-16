package com.isys3400.usyd.handwritingcapturing;

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
import java.util.HashSet;
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

    private ArrayList<ShapeData> chosenShape = new ArrayList<>();
    private ArrayList<ShapeData> cache = new ArrayList<>();
    private ArrayList<ShapeData> validPoints = new ArrayList<>();

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
        shapeMaker(MainActivity.chosenShape);
        playing(chosenShape);
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

        float x = event.getX();
        float y = event.getY();
        float pressure = event.getPressure();

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
                saveInputCache(x, y, pressure, true);
                drawTouchDown(x, y);

                return true;

            //touch move
            case MotionEvent.ACTION_MOVE:
                if (lockInput != fingerOrPen) {
                    makeToast();
                    return true;
                }
                saveInputCache(x, y, pressure, false);
                drawTouchMove(x, y, pressure);
                addValidPoints(x, y);
                inputAnalysis(chosenShape, validPoints);

                String info = "Similarity:\t\t\t\t" + String.format(Locale.ENGLISH, "%.2f", simi) +
                        "%\nCompleteness:\t" + String.format(Locale.ENGLISH, "%.2f", completeness) + "%";
                mDrawActivity.tv.setText(info);

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

    public void addValidPoints(float x, float y) {
        boolean add = true;
        for (int i = 0; i < validPoints.size(); i++) {
            ShapeData tmp = validPoints.get(i);
            if (Math.abs(x - tmp.x) <= 20 && Math.abs(y - tmp.y) <= 20) {
                add = false;
                break;
            }
        }
        if (add) {
            System.out.println();
            validPoints.add(new ShapeData(x, y));
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

        //reset cache & validPoints
        cache.clear();
        validPoints.clear();

        //reset assisted boolean
        fingerOrPen = null;
        endDraw = true;

        //reset analysis
        mDrawActivity.tv.setText("Analysis Not Available!");

        //reset canvas & icon color
        mDrawActivity.changeIcon("save_gray");
        mDrawActivity.changeIcon("reset_gray");
        mDrawActivity.changeIcon("play_gray");
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
        instantDraw(chosenShape);
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
        File file = new File(Environment.getExternalStorageDirectory(), "/HWOutput/" + MainActivity.fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file, true);

            StringBuilder allCache = new StringBuilder();

            //write header
            String index = "#" + MainActivity.indexNum + ",";
            allCache.append(index);
            allCache.append(lockInput ? "Finger," : "Pen,");
            allCache.append(MainActivity.chosenShape);
            String time = "," + day + "/" + month + "/" + year + "\n";
            allCache.append(time);
            String attrs = lockInput ? "X,Y,Time\n" : "X,Y,Pressure,Time\n";
            allCache.append(attrs);

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
        instantDraw(chosenShape);
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

    public void shapeMaker(String shapeType) {
        float x = 0;
        float y = 0;
        switch (shapeType) {
            case "Circle":
                for (int i = 0; i <= 200; i++) {
                    //x=a+rcost y=b+rsint    math.sin from 0 to 2pi
                    x = (float) (this.getWidth() / 2 + this.getWidth() / 5 * Math.cos(2 * Math.PI / 200 * i - Math.PI / 2));
                    y = (float) (this.getHeight() / 2 + this.getWidth() / 5 * Math.sin(2 * Math.PI / 200 * i - Math.PI / 2));
                    addToShape(i, x, y, 8);
                }
                break;

            case "Square":
                float half = (float) this.getWidth() / 5;
                for (int i = 0; i <= 200; i++) {
                    if (i >= 0 && i < 50) {
                        x = this.getWidth() / 2 - half + half * 2 / 50 * i;
                        y = this.getHeight() / 2 - half;
                    } else if (i >= 50 && i < 100) {
                        x = this.getWidth() / 2 + half;
                        y = this.getHeight() / 2 - half + half * 2 / 50 * (i - 50);
                    } else if (i >= 100 && i < 150) {
                        x = this.getWidth() / 2 + half - half * 2 / 50 * (i - 100);
                        y = this.getHeight() / 2 + half;
                    } else if (i >= 150 && i <= 200) {
                        x = this.getWidth() / 2 - half;
                        y = this.getHeight() / 2 + half - half * 2 / 50 * (i - 150);
                    }
                    addToShape(i, x, y, 8);
                }
                break;

            case "Triangle":
                float centreDist = (float) (this.getWidth() / 4.5);
                float sideLength = (float) (centreDist * Math.sin(Math.PI / 3 * 2) / Math.sin(Math.PI / 6));
                for (int i = 0; i <= 180; i++) {
                    if (i >= 0 && i < 60) {
                        x = (float) (this.getWidth() / 2 + sideLength * Math.sin(Math.PI / 6) / 60 * i);
                        y = (float) (this.getHeight() / 1.75 - centreDist + sideLength * Math.cos(Math.PI / 6) / 60 * i);
                    } else if (i >= 60 && i < 120) {
                        x = this.getWidth() / 2 + (float) (Math.cos(Math.PI / 6) * centreDist) - sideLength / 60 * (i - 60);
                        y = (float) (this.getHeight() / 1.75) + (float) (Math.sin(Math.PI / 6) * centreDist);
                    } else if (i >= 120 && i <= 180) {
                        x = (float) (this.getWidth() / 2 - (Math.cos(Math.PI / 6) * centreDist) + sideLength * Math.sin(Math.PI / 6) / 60 * (i - 120));
                        y = (float) (this.getHeight() / 1.75 + (Math.sin(Math.PI / 6) * centreDist) - sideLength * Math.cos(Math.PI / 6) / 60 * (i - 120));
                    }
                    addToShape(i, x, y, 8);
                }
                break;

            case "Spiral":
                float radius = (float) this.getWidth() / 40;
                for (int i = 0; i <= 200; i++) {
                    //x=a+rcost y=b+rsint    math.sin from 0 to 2pi
                    x = (float) (this.getWidth() / 2 + radius * Math.cos(6.25 * Math.PI / 200 * i - Math.PI / 2));
                    y = (float) (this.getHeight() / 2 + radius * Math.sin(6.25 * Math.PI / 200 * i - Math.PI / 2));
                    radius += 1;
                    addToShape(i, x, y, 8);
                }
                break;

            default:
                break;
        }

        System.out.println(chosenShape.get(0).x + "." + chosenShape.get(0).y);
        System.out.println(chosenShape.get(chosenShape.size() - 1).x + "." + chosenShape.get(chosenShape.size() - 1).y);

    }

    public void addToShape(int index, float x, float y, int speed) {
        if (index == 0) {
            chosenShape.add(new ShapeData(Float.parseFloat(to4f(x)), Float.parseFloat(to4f(y)), 1, true));
        } else {
            chosenShape.add(new ShapeData(Float.parseFloat(to4f(x)), Float.parseFloat(to4f(y)), index * speed, false));
        }
    }

    int pointSize = -1;
    double simi = -1;
    double completeness = -1;

    public void inputAnalysis(ArrayList<ShapeData> chosenShape, ArrayList<ShapeData> validPoints) {

        if (pointSize != -1) {
            if (validPoints.size() == pointSize) {
                return;
            }
        }

        boolean spiral = false;
        if (MainActivity.chosenShape.equals("Spiral")) {
            spiral = true;
        }

        double totalDist = 0;
        double validSize = validPoints.size();
        HashSet<Integer> completedPoints = new HashSet<>();

        for (int i = 0; i < validPoints.size(); i++) {

            ShapeData testPoint = validPoints.get(i);
            double minDist = 99999999;
            int chosenPoint = -1;
            for (int j = 0; j < chosenShape.size(); j += 5) {
                ShapeData shapePoint = chosenShape.get(j);
                double dist = Math.sqrt(Math.pow(testPoint.x - shapePoint.x, 2) +
                        Math.pow(testPoint.y - shapePoint.y, 2));
                if (dist < minDist) {
                    minDist = dist;
                    chosenPoint = j;
                }
            }
            if (minDist > 100) {
                validSize--;
                continue;
            }
            totalDist += minDist;
            if (minDist < 40) {
                completedPoints.add(chosenPoint);
            }
        }

        if (validSize == 0) {
            simi = 100;
        } else {
            simi = totalDist / validSize;
        }

        if (simi < (spiral ? 15 : 10)) {
            simi = 100;
        } else if (simi > (spiral ? 30 : 20)) {
            simi = 0;
        } else {
            simi = ((double) (-1) / (double) (spiral ? 3375 : 1000) * Math.pow(simi - (spiral ? 15 : 10), 3) + 1) * 100;
        }

        completeness = ((double) completedPoints.size() /
                (double) (chosenShape.size() / 5 + (spiral ? 1 : 0))) * 100;

        pointSize = validPoints.size();
    }
}
