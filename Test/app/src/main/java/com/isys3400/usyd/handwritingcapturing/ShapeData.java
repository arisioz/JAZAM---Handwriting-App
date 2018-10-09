package com.isys3400.usyd.handwritingcapturing;

public class ShapeData {

    float x;
    float y;
    float pressure;
    long time;
    boolean isStartPoint;

    ShapeData(float x, float y, long time, boolean isStartPoint) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.isStartPoint = isStartPoint;
    }

    ShapeData(float x, float y, float pressure, long time, boolean isStartPoint) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.time = time;
        this.isStartPoint = isStartPoint;
    }
}
