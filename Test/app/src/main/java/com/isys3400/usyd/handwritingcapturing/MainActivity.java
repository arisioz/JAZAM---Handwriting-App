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

    public static String fileName;
    public static String chosenShape;
    public static int indexNum;

    private View testDialog = null;
    private View infoDialog = null;
    private View sendDialog = null;

    private EditText userName, age, testName, sendFileName;
    private RadioButton male, female;
    private String gender_value;
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
        if (actionBar != null) {
            actionBar.hide();
        }

        indexNum = 0;

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

        infoDialog = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_info, null);
        userName = infoDialog.findViewById(R.id.Username);
        age = infoDialog.findViewById(R.id.Age);
        male = infoDialog.findViewById(R.id.radio_male);
        female = infoDialog.findViewById(R.id.radio_female);
        gender_value = "Male";

        testDialog = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_start_test, null);
        testName = testDialog.findViewById(R.id.Testname);

        sendDialog = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_send, null);
        sendFileName = sendDialog.findViewById(R.id.send_file_name);

        testDialog();

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
                    sendDialog();
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

    private void testDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        final android.view.ViewParent parent = testDialog.getParent();
        if (parent instanceof android.view.ViewManager) {
            final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
            viewManager.removeView(testDialog);
        }
        builder.setView(testDialog
        ).setPositiveButton(getString(R.string.Next), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                if (testName.getText().toString().length() < 4) {
                    Toast.makeText(getApplicationContext(), "The test name must have at least 4 characters!", Toast.LENGTH_SHORT).show();
                    testDialog();
                } else {
                    infoDialog();
                }
            }
        }).show();
    }

    private void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        final android.view.ViewParent parent = infoDialog.getParent();
        if (parent instanceof android.view.ViewManager) {
            final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
            viewManager.removeView(infoDialog);
        }
        builder.setView(infoDialog
        ).setPositiveButton(getString(R.string.StartNewTest), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                if (userName.getText().toString().length() < 4) {
                    Toast.makeText(getApplicationContext(), "The user name must have at least 4 characters!", Toast.LENGTH_SHORT).show();
                    infoDialog();
                    return;
                }

                String tempAge = age.getText().toString();
                if (tempAge.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Age is required!", Toast.LENGTH_SHORT).show();
                    infoDialog();
                    return;
                }
                if (Integer.parseInt(tempAge) < 0 || Integer.parseInt(tempAge) > 100) {
                    Toast.makeText(getApplicationContext(), "The age must be in the range of 0 to 100!", Toast.LENGTH_SHORT).show();
                    age.setText("");
                    infoDialog();
                    return;
                }

                fileName = testName.getText().toString() + "_" + userName.getText().toString() + "_";
                String sdPath = Environment.getExternalStorageDirectory() + "";
                int count = 0;
                while (new File(sdPath + "/HWOutput/" + fileName + count + ".csv").exists()) {
                    count++;
                }
                fileName += (count + ".csv");
                sendFileName.setText(fileName);

                dialogInterface.dismiss();
            }
        }).show();
    }

    private void sendDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final android.view.ViewParent parent = sendDialog.getParent();
        if (parent instanceof android.view.ViewManager) {
            final android.view.ViewManager viewManager = (android.view.ViewManager) parent;
            viewManager.removeView(sendDialog);
        }

        sendFileName.setText(fileName);

        builder.setView(sendDialog
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

        File filelocation = new File(Environment.getExternalStorageDirectory() + "/HWOutput/" + sendFileName.getText().toString());
        if (!filelocation.exists()) {
            Toast.makeText(MainActivity.this, "No such CSV file found. For new test, please make one sketch first.", Toast.LENGTH_SHORT).show();
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
