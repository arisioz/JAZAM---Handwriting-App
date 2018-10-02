package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;
import java.io.File;
import java.util.Calendar;

public class MainActivity extends Activity {

    public static int attempts;
    public static int chosen_Shape;
    private Button button_Upload;
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
                case R.id.button:
                    if (attempts == 0) {
                        Toast.makeText(MainActivity.this, "You have to first make at least one sketch.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File filelocation = new File(Environment.getExternalStorageDirectory() + "/HWOutput/output.csv");
                    Uri path_of_csv = Uri.fromFile(filelocation);
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setType("vnd.android.cursor.dir/email");
                    String subject = "Handwriting Data Capture";
                    String TimeNow = Calendar.getInstance().getTime().toString();
                    String bodyText = "Attached you will find the data capture CSV file from: " + "INSERT USER" + " on: " + TimeNow + " who attempted " + attempts + (( attempts == 1 ) ? " sketch." : " different sketches." );
                    String mail_Contents = "mailto:siozaris@outlook.com" +
                            "?cc=" + "siozaris@outlook.com" +
                            "&subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(bodyText);
                    emailIntent.setData(Uri.parse(mail_Contents));
                    emailIntent.putExtra(Intent.EXTRA_STREAM, path_of_csv);
                    try {
                        startActivity(Intent.createChooser(emailIntent, "Sending mail..."));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                default:
                    throw new RuntimeException("Unknown button ID");
            }
            // WILL ADD THAT TO THE DRAWACTIVITY
            attempts++;
            Intent activity_Main_To_Draw = new Intent(MainActivity.this, DrawActivity.class);
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
        // HERE
        button_Upload = findViewById(R.id.button);

        image_Button_1.setOnClickListener(tweakedOnClickListener);
        image_Button_2.setOnClickListener(tweakedOnClickListener);
        image_Button_3.setOnClickListener(tweakedOnClickListener);
        image_Button_Random.setOnClickListener(tweakedOnClickListener);
        // HERE
        button_Upload.setOnClickListener(tweakedOnClickListener);
    }
}
