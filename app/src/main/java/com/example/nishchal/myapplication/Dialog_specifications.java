package com.example.nishchal.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Dialog_specifications extends AppCompatActivity {
    private EditText ed;
    private EditText ed1;
    private EditText ed2;
    String dat, st;
    String am_pm = "";
    String cbText;
    String tim;
    Context ctx = this;

    DatabaseOperations mydb;
    private static final int av = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_specifications);
        ed = (EditText) findViewById(R.id.editText);

        ed1 = (EditText) findViewById(R.id.editText2);
        ed2 = (EditText) findViewById(R.id.editText3);


        ImageButton im = (ImageButton) findViewById(R.id.imageButton);
        im.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                promptspeech();
            }
        });


        ImageButton im2 = (ImageButton) findViewById(R.id.imageButton2);

        im2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                st = ed.getText().toString();
                new DatePickerDialog(Dialog_specifications.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();


            }
        });


        ImageButton im3 = (ImageButton) findViewById(R.id.imageButton3);
        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Dialog_specifications.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                       if (selectedHour<12)
                            am_pm = "AM";
                        else {
                           selectedHour=selectedHour-12;
                           am_pm = "PM";
                       }
                        tim = selectedHour + ":" + selectedMinute+" "+am_pm ;
                        ed2.setText(tim);

                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
        if (cb.isChecked()) {
            cbText = "yes";
        } else {
            cbText = "no";
        }


        Button b1 = (Button) findViewById(R.id.setTaskButton);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mydb = new DatabaseOperations(ctx);


                mydb.putInformation(mydb, st, dat, tim, cbText);

                Toast.makeText(Dialog_specifications.this, "Task Created", Toast.LENGTH_SHORT).show();
                Intent in2 = new Intent(Dialog_specifications.this, MainActivity.class);
                Dialog_specifications.this.startActivity(in2);


            }

        });

    }


    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    public void promptspeech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Name your task!");
        startActivityForResult(i, av);
        try {

        } catch (ActivityNotFoundException a) {
            Toast.makeText(Dialog_specifications.this, "Sorry! Your device doesnt support speech Language", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int request_code, int result_code, Intent i) {
        if (request_code == av && result_code == RESULT_OK) {
            ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            ed.setText(result.get(0));
        }
        super.onActivityResult(request_code, result_code, i);

    }


    private void updateLabel() {

        String myFormat = "EEE, dd MMM yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dat = sdf.format(myCalendar.getTime()).toString();
        ed1.setText(sdf.format(myCalendar.getTime()));

    }

}
