package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class DrawActivity extends Activity {

    private ImageView img;

    private View.OnClickListener tweakedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbtn_back:
                    startActivity(new Intent(DrawActivity.this, MainActivity.class));
                    break;
                case R.id.imgbtn_reset:
                    ((DrawCanvas) findViewById(R.id.myCanvas)).clearCanvas();
                    break;

                default:
                    throw new RuntimeException("Unknown button ID");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        img = findViewById(R.id.imgdraw);
        img.setImageResource(MainActivity.chosen_Shape);

        ImageButton imgbtn_back = findViewById(R.id.imgbtn_back);
        ImageButton imgbtn_reset = findViewById(R.id.imgbtn_reset);

        imgbtn_back.setOnClickListener(tweakedOnClickListener);
        imgbtn_reset.setOnClickListener(tweakedOnClickListener);

    }
}
