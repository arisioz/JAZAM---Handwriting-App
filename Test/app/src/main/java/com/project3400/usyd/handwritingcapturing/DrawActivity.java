package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.os.Bundle;
<<<<<<< HEAD
import android.view.MotionEvent;
import android.view.View;
=======
import android.widget.ImageButton;
>>>>>>> 7f243ed83301f910193b5d928eaa429609708eae
import android.widget.ImageView;
public class DrawActivity extends Activity {

    private ImageView img;
    private ImageView tracePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

<<<<<<< HEAD
        //Intent activity_Main_To_Draw = getIntent();
        //int source = activity_Main_To_Draw.getIntExtra("chosen_Shape", 2130968584);

        img = findViewById(R.id.imgdraw);
=======
        //Here
        Intent activity_Main_To_Draw = getIntent();
        int source = activity_Main_To_Draw.getIntExtra("$chosen_Shape", 0);
        System.out.println("----------"+source);
        System.out.println("----------"+MainActivity.chosen_Shape);
        //Here
        //Button button = findViewById(R.id.button)
        ImageView img = (ImageView) findViewById(R.id.imgdraw);
>>>>>>> 7f243ed83301f910193b5d928eaa429609708eae
        img.setImageResource(MainActivity.chosen_Shape);

        tracePad = findViewById(R.id.tracepad);
        tracePad.setOnTouchListener(handleTouch);

    }


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    System.out.println("touch down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    System.out.println( "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("touch up");
                    break;
            }

            return true;
        }


    };

}
