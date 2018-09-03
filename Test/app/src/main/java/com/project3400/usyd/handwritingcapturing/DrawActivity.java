package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class DrawActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        //Intent activity_Main_To_Draw = getIntent();
        //int source = activity_Main_To_Draw.getIntExtra("chosen_Shape", 2130968584);

        ImageView img = (ImageView) findViewById(R.id.imgdraw);
        img.setImageResource(MainActivity.chosen_Shape);
    }
}
