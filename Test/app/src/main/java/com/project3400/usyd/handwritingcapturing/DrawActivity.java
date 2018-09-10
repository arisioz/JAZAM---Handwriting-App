package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

public class DrawActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        //Here
        Intent activity_Main_To_Draw = getIntent();
        int source = activity_Main_To_Draw.getIntExtra("$chosen_Shape", 0);
        System.out.println("----------"+source);
        System.out.println("----------"+MainActivity.chosen_Shape);
        //Here
        //Button button = findViewById(R.id.button)
        ImageView img = (ImageView) findViewById(R.id.imgdraw);
        img.setImageResource(MainActivity.chosen_Shape);
    }
}
