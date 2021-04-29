package com.ku.runner.utils;

import java.util.Calendar;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;

public class SetTime implements OnFocusChangeListener, OnTimeSetListener {   

    private EditText editText;
    private Calendar myCalendar;
    private Context ctx;
    public SetTime(EditText editText, Context ctx){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.myCalendar = Calendar.getInstance();
        this.ctx= ctx;
    }

     @Override
     public void onFocusChange(View v, boolean hasFocus) {
         // TODO Auto-generated method stub
         if(hasFocus){
             int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
             int minute = myCalendar.get(Calendar.MINUTE);
             new TimePickerDialog(ctx, this, hour, minute, true).show();
         }
     }

     @Override
     public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
         // TODO Auto-generated method stub
         this.editText.setText( hourOfDay + ":" + minute);
     }

 }