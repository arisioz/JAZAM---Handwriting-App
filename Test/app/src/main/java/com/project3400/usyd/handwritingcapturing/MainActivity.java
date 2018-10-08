package com.project3400.usyd.handwritingcapturing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;
import java.io.File;
import java.util.Calendar;

public class MainActivity extends Activity {

    EditText un, age;
    RadioButton male,female;
    private String gender_value;
    public static int attempts;
    public static Pair<Integer,String> chosen_Shape;
    private Button button_Upload;
    private ImageButton image_Button_1;
    private ImageButton image_Button_2;
    private ImageButton image_Button_3;
    private ImageButton image_Button_Random;
    private final int possible_Shapes[] = {R.drawable.triangle, R.drawable.circle, R.drawable.square};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_Button_1 = findViewById(R.id.imgbtn1);
        image_Button_2 = findViewById(R.id.imgbtn2);
        image_Button_3 = findViewById(R.id.imgbtn3);
        image_Button_Random = findViewById(R.id.imgbtnRd);
        button_Upload = findViewById(R.id.button);

        image_Button_1.setOnClickListener(tweakedOnClickListener);
        image_Button_2.setOnClickListener(tweakedOnClickListener);
        image_Button_3.setOnClickListener(tweakedOnClickListener);
        image_Button_Random.setOnClickListener(tweakedOnClickListener);
        button_Upload.setOnClickListener(tweakedOnClickListener);
    }

    private View.OnClickListener tweakedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbtn1:
                    chosen_Shape = Pair.create(possible_Shapes[0],"Triangle");
                    break;
                case R.id.imgbtn2:
                    chosen_Shape = Pair.create(possible_Shapes[1],"Circle");
                    break;
                case R.id.imgbtn3:
                    chosen_Shape = Pair.create(possible_Shapes[2],"Square");
                    break;
                case R.id.imgbtnRd:
                    int rndNum = new Random().nextInt(possible_Shapes.length);
                    switch(rndNum){
                        case 0:
                            chosen_Shape = Pair.create(possible_Shapes[0],"Triangle");
                            break;
                        case 1:
                            chosen_Shape = Pair.create(possible_Shapes[1],"Circle");
                            break;
                        case 2:
                            chosen_Shape = Pair.create(possible_Shapes[2],"Square");
                            break;
                    }
                    break;
                case R.id.button:
                    uploadDialog();
                    return;
                default:
                    throw new RuntimeException("Unknown button ID");
            }
            // WILL ADD THAT TO THE DRAWACTIVITY
            attempts++;
            startActivity(new Intent(MainActivity.this, DrawActivity.class));
        }
    };

    public void RadioButtonClicked(View view) {
        boolean is_checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radio_male:
                if (is_checked) {
                    gender_value = "Male";
                } else {
                    male.setChecked(false);
                }
            case R.id.radio_female:
                if (is_checked) {
                    gender_value = "Female";
                } else {
                    female.setChecked(false);
                }
        }
    }

    private void uploadDialog () {

        // Create the Dialog with two buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog, null)).setPositiveButton(getString(R.string.Sendstring), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {

                un = (EditText) findViewById(R.id.username);
                age = (EditText) findViewById(R.id.Age);
                male = (RadioButton) findViewById(R.id.radio_male);
                female = (RadioButton) findViewById(R.id.radio_female);

                //un.getText().toString();
                /*String user_age = (String) age.getText().toString();
                String user_gender = (String) gender_value;*/

                sendCsvViaEmail();
            }
        }).setNegativeButton(getString(R.string.Cancelstring), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void sendCsvViaEmail(){

        File filelocation = new File(Environment.getExternalStorageDirectory() + "/HWOutput/output.csv");
        if (!filelocation.exists()) {
            Toast.makeText(MainActivity.this, "No CSV file found, you have to make at least one sketch", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri path_of_csv = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String subject = "Handwriting Data Capture";
        String TimeNow = Calendar.getInstance().getTime().toString();
        String bodyText = "Attached you will find the data capture CSV file from: " + "INSERT USER" + gender_value + " on: " + TimeNow + " who attempted " + attempts + ((attempts == 1) ? " sketch." : " different sketches.");
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
    }
}
