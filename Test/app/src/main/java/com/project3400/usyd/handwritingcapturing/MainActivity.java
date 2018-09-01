package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.Random;

public class MainActivity extends Activity {

    public static int chosenShape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int shapes[] = {R.drawable.triangle,R.drawable.circle,R.drawable.square};

        ImageButton imgbtn1 = (ImageButton) findViewById(R.id.imgbtn1);
        imgbtn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chosenShape = shapes[0];
                startActivity(new Intent(MainActivity.this, DrawActivity.class));
            }
        });

        ImageButton imgbtn2 = (ImageButton) findViewById(R.id.imgbtn2);
        imgbtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chosenShape = shapes[1];
                startActivity(new Intent(MainActivity.this, DrawActivity.class));
            }
        });

        ImageButton imgbtn3 = (ImageButton) findViewById(R.id.imgbtn3);
        imgbtn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chosenShape = shapes[2];
                startActivity(new Intent(MainActivity.this, DrawActivity.class));
            }
        });


        ImageButton imgbtnRd = (ImageButton) findViewById(R.id.imgbtnRd);
        imgbtnRd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chosenShape = shapes[new Random().nextInt(shapes.length)];
                startActivity(new Intent(MainActivity.this, DrawActivity.class));
            }
        });




    }
}
