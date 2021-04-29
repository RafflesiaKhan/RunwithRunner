package com.ku.runner.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.ku.runner.R;
import com.ku.runner.R.id;
import com.ku.runner.R.layout;
import com.ku.runner.R.menu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class OverviewActivity extends Activity implements OnClickListener {

	//UI References
	private EditText fromDateEtxt;
	private EditText toDateEtxt;
	private Button btn; 
	private DatePickerDialog fromDatePickerDialog;
	private DatePickerDialog toDatePickerDialog;
	Calendar from, to;
	private SimpleDateFormat dateFormatter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datepicer_activity);
		
		dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		
		findViewsById();
		
		setDateTimeField();
	}

	private void findViewsById() {
		fromDateEtxt = (EditText) findViewById(R.id.etxt_fromdate);	
		fromDateEtxt.setInputType(InputType.TYPE_NULL);
		fromDateEtxt.requestFocus();
		btn = (Button) findViewById(R.id.overview);
		toDateEtxt = (EditText) findViewById(R.id.etxt_todate);
		toDateEtxt.setInputType(InputType.TYPE_NULL);
	}

	private void setDateTimeField() {
		fromDateEtxt.setOnClickListener(this);
		toDateEtxt.setOnClickListener(this);
		
		Calendar newCalendar = Calendar.getInstance();
		fromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

	        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	            Calendar newDate = Calendar.getInstance();
	            newDate.set(year, monthOfYear, dayOfMonth);
	            from= newDate;
	            fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
	        }

	    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
		
		toDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

	        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	            Calendar newDate = Calendar.getInstance();
	            newDate.set(year, monthOfYear, dayOfMonth);
	            to = newDate;
	            toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
	        }

	    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		if(view == fromDateEtxt) {
			fromDatePickerDialog.show();
		} else if(view == toDateEtxt) {
			toDatePickerDialog.show();
		}
		else if(view == btn)
		{
			Intent i = new Intent(view.getContext(), GraphActivity.class);
			i.putExtra("from", from.toString());
			i.putExtra("to", to.toString());
			startActivity(i);
		}
	}
	
	public void Graph(View v) {
		Intent i = new Intent(v.getContext(), GraphActivity.class);
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
		i.putExtra("from",  fmt.format(from.getTime()));
		i.putExtra("to",  fmt.format(to.getTime()));
		startActivity(i);
	}
	
}