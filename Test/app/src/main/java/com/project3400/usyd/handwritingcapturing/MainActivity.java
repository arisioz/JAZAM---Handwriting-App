package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.Random;

public class MainActivity extends Activity {

    /**
     * Class variables followed by the instance variables
     */
    public static int chosen_Shape;
    private ImageButton image_Button_1;
    private ImageButton image_Button_2;
    private ImageButton image_Button_3;
    private ImageButton image_Button_Random;
    private final int possible_Shapes[] = {R.drawable.triangle,R.drawable.circle,R.drawable.square};

    private View.OnClickListener tweakedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.imgbtn1:
                    chosen_Shape = possible_Shapes[0];
                    break;
                case R.id.imgbtn2:
                    chosen_Shape = possible_Shapes[1];
                    break;
                case R.id.imgbtn3:
                    chosen_Shape = possible_Shapes[2];
                    break;
                case R.id.imgbtnRd:
                    int random_Number = new Random().nextInt(possible_Shapes.length);
                    chosen_Shape = possible_Shapes[random_Number];
                    break;
                default:
                    throw new RuntimeException("Unknown button ID");
            }
            Intent activity_Main_To_Draw = new Intent(MainActivity.this, DrawActivity.class);
            //activity_Main_To_Draw.putExtra("chosen_Shape",v.getId());
            startActivity(activity_Main_To_Draw);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_Button_1 = findViewById(R.id.imgbtn1);
        image_Button_2 = findViewById(R.id.imgbtn2);
        image_Button_3 = findViewById(R.id.imgbtn3);
        image_Button_Random = findViewById(R.id.imgbtnRd);

        image_Button_1.setOnClickListener(tweakedOnClickListener);
        image_Button_2.setOnClickListener(tweakedOnClickListener);
        image_Button_3.setOnClickListener(tweakedOnClickListener);
        image_Button_Random.setOnClickListener(tweakedOnClickListener);
    }
}
