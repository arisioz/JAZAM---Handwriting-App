package com.isys3400.usyd.handwritingcapturing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;

import java.util.Random;
import java.io.File;
import java.util.Calendar;

public class MainActivity extends Activity {

    private View view = null;

    private EditText userName, age;
    private RadioButton male, female;
    private String gender_value;
    public static String chosenShape;
    private Button button_Upload;
    private ImageButton image_Button_1;
    private ImageButton image_Button_2;
    private ImageButton image_Button_3;
    private ImageButton image_Button_4;
    private ImageButton image_Button_Random;
    private final String shapes[] = {"Triangle", "Circle", "Square", "Spiral"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        image_Button_1 = findViewById(R.id.imgbtn1);
        image_Button_2 = findViewById(R.id.imgbtn2);
        image_Button_3 = findViewById(R.id.imgbtn3);
        image_Button_4 = findViewById(R.id.imgbtn4);
        image_Button_Random = findViewById(R.id.imgbtnRd);
        // HERE
        button_Upload = findViewById(R.id.button);

        image_Button_1.setOnClickListener(tweakedOnClickListener);
        image_Button_2.setOnClickListener(tweakedOnClickListener);
        image_Button_3.setOnClickListener(tweakedOnClickListener);
        image_Button_4.setOnClickListener(tweakedOnClickListener);
        image_Button_Random.setOnClickListener(tweakedOnClickListener);
        // HERE
        button_Upload.setOnClickListener(tweakedOnClickListener);

        view = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog, null);
        userName = view.findViewById(R.id.username);
        age = view.findViewById(R.id.Age);
        male = view.findViewById(R.id.radio_male);
        female = view.findViewById(R.id.radio_female);
    }

    private View.OnClickListener tweakedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgbtn1:
                    chosenShape = shapes[0];
                    break;
                case R.id.imgbtn2:
                    chosenShape = shapes[1];
                    break;
                case R.id.imgbtn3:
                    chosenShape = shapes[2];
                    break;
                case R.id.imgbtn4:
                    chosenShape = shapes[3];
                    break;
                case R.id.imgbtnRd:
                    int rndNum = new Random().nextInt(shapes.length);
                    chosenShape = shapes[rndNum];
                    break;
                case R.id.button:
                    uploadDialog();
                    return;
                default:
                    throw new RuntimeException("Unknown button ID");
            }
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
                break;
            case R.id.radio_female:
                if (is_checked) {
                    gender_value = "Female";
                } else {
                    female.setChecked(false);
                }
                break;
        }
    }


    private void uploadDialog() {

        // Create the Dialog with two buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        final android.view.ViewParent parent = view.getParent();
        if (parent instanceof android.view.ViewManager) {
            final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
            viewManager.removeView(view);
        }

        builder.setView(view
        ).setPositiveButton(getString(R.string.Sendstring), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                sendCsvViaEmail();
            }
        }).setNegativeButton(getString(R.string.Cancelstring), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.dismiss();
            }
        }).show();


    }

    private void sendCsvViaEmail() {

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
        String bodyText = "User: " + userName.getText().toString() +
                "\nGender: " + gender_value +
                "\nAge: " + age.getText().toString() +
                "\nSent time: " + TimeNow;
        String mail_Contents = "mailto:mhti.lab@sydney.edu.au" +
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
