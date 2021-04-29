package com.ku.runner.activity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.ku.runner.MenuItemListActivity;
import com.ku.runner.R;
import com.ku.runner.model.History;
import com.ku.runner.model.Profile;
import com.ku.runner.utils.DatabaseHandler;
import com.ku.runner.utils.SetTime;
import com.ku.runner.utils.ShakeDetector;


@SuppressLint("NewApi")
public class RunnerActivity extends Activity{
	SharedPreferences sharedpreferences ;
	EditText txtStopTime,txtStartTime;

	Button start, stop;
	boolean showStatus = false;
	Date startTime, endTime; String validmessage=""; 
	public static final String MyPREFERENCES = "MyPrefs" ;
	   public static final String startTimeK = "stTime";
	   public static final String endTimeK = "edTime";
	   //public static final String Email = "emailKey";
	MainGamePanel  gamePanel =null;
	public MainGamePanel getGamePanel() {
		return gamePanel;
	}

	
	private static final String TAG = RunnerActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_runner);
		sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		initializeViews();
		Intent i = this.getIntent();showStatus = i.getBooleanExtra("status", false);
		if(i!= null && showStatus){
			LinearLayout status = (LinearLayout) findViewById(R.id.statusscreeen);
			status.setVisibility(View.VISIBLE); 
			
			TextView timeV  = (TextView) findViewById(R.id.timeV);
			timeV.setText ( "Time:" +i.getStringExtra("time"));
			
			TextView v2  = (TextView) findViewById(R.id.distanceV);
			v2.setText ("Distance:" + i.getDoubleExtra("distance",0.0)+"Km");
			TextView v3  = (TextView) findViewById(R.id.calorieV);
			v3.setText ("Calorie:"+i.getDoubleExtra("calorie",0.0)+"cal");
			
		}
	}

	
	private boolean validatedate()
	{String format = "hh:mm";
	 SimpleDateFormat sdf = new SimpleDateFormat(format);
		boolean valid = true; Date d =new Date();
		try {
			
			 startTime = sdf.parse(txtStartTime.getText().toString());
			 startTime.setDate(d.getDate()); startTime.setYear(d.getYear());startTime.setMonth(d.getMonth());
			 endTime = sdf.parse(txtStopTime.getText().toString());
			 endTime.setDate(d.getDate()); endTime.setYear(d.getYear());endTime.setMonth(d.getMonth());
        } catch (ParseException e) {
            
            valid = false;
            validmessage ="Wrong Time Format";
            return valid;
        }
		
		/*if(startTime.before(d))
		 {valid = false; validmessage ="Start Time  must be after current time";}
		if(startTime.after(endTime))
			{valid = false;validmessage ="Start Time  must be before End time";}*/
		return valid;
		
	}
	private void initializeViews() {
		 
		// TODO Auto-generated method stub
		txtStopTime = (EditText) findViewById(R.id.txtStopTime);
		txtStartTime = (EditText) findViewById(R.id.txtStartTime);
		
		
		start = (Button) findViewById(R.id.btnSubmit);
		 
		
		stop = (Button) findViewById(R.id.btnStop);
		}
	
	
	public void StartGame (View view){
		boolean validDate = validatedate();
		if(!validDate)
			showAlert(this);
		StorePreference();
		PlayActivity.isActivityEnabled =false;
		Intent i=new Intent(this,PlayActivity.class);
		
		startActivity(i);
	}
	
	
	private void showAlert(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		 builder//.setView(inflater.inflate(R.layout.dialog_result, null))
	        .setTitle("Error")
	        .setMessage(validmessage)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialogf, int id) {
		        	  Dialog dialog = (Dialog) dialogf;
		        	  dialog.dismiss();
		           }
		       });
		 
		 AlertDialog dialog = builder.create();
			
		 dialog.show();
	}


	public void StartGameActivity (View v)
	{
		//boolean validDate = validatedate();
		StorePreference();
		PlayActivity.isActivityEnabled =true;
		Intent i=new Intent(this,PlayActivity.class);
		
		startActivity(i);
		
	}
	
	private void StorePreference() {
		SharedPreferences.Editor editor = sharedpreferences.edit();
        
        editor.putString(startTimeK, startTime.toString());
        editor.putString(endTimeK, endTime.toString());
     
        editor.commit();
		
	}

	public void StopGame (View view){
		Log.d(TAG , "animationstop");
		
	
		
		
		
		//stop.setVisibility(View.VISIBLE);
	}
	public void Return (View v) {
		startActivity(new Intent (this, MenuItemListActivity.class));
	}

	public void showTimePickerDialog (View v){
		if(v instanceof EditText){
	    SetTime fromTime = new SetTime((EditText) v, this);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//shakeDetector.registerListener();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//shakeDetector.unregisterListener();
	}
	
}
