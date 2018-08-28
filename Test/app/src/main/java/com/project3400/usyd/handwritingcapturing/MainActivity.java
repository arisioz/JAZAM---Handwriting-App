package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] buttonID = {R.id.imgbtn1, R.id.imgbtn2, R.id.imgbtn3};

        for (int btnID : buttonID) {
            ImageButton imgbtn = (ImageButton) findViewById(btnID);
            imgbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, DrawActivity.class));
                }
            });
        }

    }
}
