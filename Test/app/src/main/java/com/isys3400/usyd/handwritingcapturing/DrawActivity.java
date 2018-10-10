package com.isys3400.usyd.handwritingcapturing;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class DrawActivity extends Activity {

    private ImageButton imgbtn_back;
    private ImageButton imgbtn_reset;
    private ImageButton imgbtn_save;
    private ImageButton imgbtn_play;
    private DrawCanvas dc;

    public TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        dc = findViewById(R.id.myCanvas);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        imgbtn_back = findViewById(R.id.imgbtn_back);
        imgbtn_reset = findViewById(R.id.imgbtn_reset);
        imgbtn_save = findViewById(R.id.imgbtn_save);
        imgbtn_play = findViewById(R.id.imgbtn_play);

        imgbtn_back.setOnClickListener(tweakedOnClickListener);
        imgbtn_reset.setOnClickListener(tweakedOnClickListener);
        imgbtn_save.setOnClickListener(tweakedOnClickListener);
        imgbtn_play.setOnClickListener(tweakedOnClickListener);

        tv =findViewById(R.id.tv_simi);
    }

    private View.OnClickListener tweakedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.imgbtn_back:
                    startActivity(new Intent(DrawActivity.this, MainActivity.class));
                    break;
                case R.id.imgbtn_reset:
                    dc.reset();
                    break;
                case R.id.imgbtn_save:
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    dc.save();
                    break;
                case R.id.imgbtn_play:
                    dc.play();
                    break;
                default:
                    throw new RuntimeException("Unknown button ID");
            }
        }
    };

    public void changeIcon(String cmd) {
        switch (cmd) {
            case "back_gray":
                imgbtn_back.setImageResource(R.drawable.back_gray);
                break;
            case "save_gray":
                imgbtn_save.setImageResource(R.drawable.save_gray);
                break;
            case "reset_gray":
                imgbtn_reset.setImageResource(R.drawable.reset_gray);
                break;
            case "play_gray":
                imgbtn_play.setImageResource(R.drawable.play_gray);
                break;
            case "back_black":
                imgbtn_back.setImageResource(R.drawable.back_black);
                break;
            case "save_black":
                imgbtn_save.setImageResource(R.drawable.save_black);
                break;
            case "reset_black":
                imgbtn_reset.setImageResource(R.drawable.reset_black);
                break;
            case "play_black":
                imgbtn_play.setImageResource(R.drawable.play_black);
                break;
            default:
                throw new RuntimeException("Unknown Icon change command");
        }
    }
}
