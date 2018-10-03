package com.project3400.usyd.handwritingcapturing;

public class Output {

    float x;
    float y;
    float pressure;
    long time;
    boolean isStartPoint;

    Output(float x, float y, long time, boolean isStartPoint) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.isStartPoint = isStartPoint;
    }

    Output(float x, float y, float pressure, long time, boolean isStartPoint) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.time = time;
        this.isStartPoint = isStartPoint;
    }
}
