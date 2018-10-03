package com.project3400.usyd.handwritingcapturing;

public class Output {

    float x;
    float y;
    float pressure;
    long time;

    Output(float x, float y, long time) {
        this.x = x;
        this.y = y;
        this.time = time;
    }

    Output(float x, float y, float pressure, long time) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.time = time;
    }
}
