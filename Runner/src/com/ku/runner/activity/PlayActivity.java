package com.ku.runner.activity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.ku.runner.MenuItemListActivity;
import com.ku.runner.R;
import com.ku.runner.model.History;
import com.ku.runner.model.Profile;
import com.ku.runner.utils.DatabaseHandler;
import com.ku.runner.utils.ShakeDetector;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

public class PlayActivity extends Activity {
 public static boolean isActivityEnabled = false; 
	 MediaPlayer mp1,jump;
	 MainGamePanel gamePanel=null; 
	 ShakeDetector shakeDetector = null;
	 protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			shakeDetector = new ShakeDetector(this);
			//phone state
			TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			TelephonyMgr.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);
			//for no title
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			gamePanel =new MainGamePanel(this);
			gamePanel.setActivityEnabled(isActivityEnabled);
			
			setContentView(gamePanel);		
		}

	 
	 
	
	public class TeleListener extends PhoneStateListener 
	{
      public void onCallStateChanged(int state,String incomingNumber)
      {
        if(state==TelephonyManager.CALL_STATE_RINGING)
           {
        	mp1.stop();
        	System.exit(0);  
           }
       } 
      
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		shakeDetector.registerListener();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		shakeDetector.unregisterListener();
	}

	public MainGamePanel getGamePanel() {
		return gamePanel;
	}

	public void setGamePanel(MainGamePanel gamePanel) {
		this.gamePanel = gamePanel;
	}
	public void showDialogTime(String type, Context context) {
			final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(context);	
		SimpleDateFormat formatFrom = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.getDefault());
		 final Date cDate = new Date();
		Date startTime=cDate, endTime = cDate;
		try {
			startTime = formatFrom.parse(mSharedPreference.getString("stTime", cDate.toString()));
			endTime=formatFrom.parse(mSharedPreference.getString("edTime", cDate.toString()));
			
			
			   Log.d( "Status>>>>>>>>>>>>>>", cDate.toString()+ startTime.toString() );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
		String message ="";
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// Add the buttons
		
			 
	         Long timeDiff = cDate.getTime() - startTime.getTime();
	         Long timetoGo = endTime.getTime() - cDate.getTime();
	         int hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff));
	        int  mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
			
	     
	        	int mt =(int) (TimeUnit.MILLISECONDS.toMinutes(timetoGo));
	       
	        	   Log.d( "Status", cDate.toString()+ startTime.toString() + timeDiff +"hh:"+hh+"mm:,,,"+mm);
	        message = "You have taken total "+shakeDetector.getStepCounterValue()+" steps and total performance time is"+ hh +" Hour "+ mm+ " minutes";
		
	        if (mt >0)
	        	message += "You still have to continue for "+mt +"minute. Are you sure to quite?";
	        final String  totalTime = hh+":"+mm;
	        final int hour =hh; final int min = mm;
	        
	        builder//.setView(inflater.inflate(R.layout.dialog_result, null))
	        .setTitle("Your Today's Performance")
		.setMessage(message)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialogf, int id) {
		        	  Dialog dialog = (Dialog) dialogf;
		        	  DatabaseHandler db = new DatabaseHandler(dialog.getContext());
		        	   Profile profile = db.getAllProfiles()!=null && db.getAllProfiles().size() >0 ?db.getAllProfiles().get(0):null;
		        	   
		        	   double distance  =0.0;double calorie=0.0;
		        	   if(profile!=null)
		        	   { int w = profile!=null? profile.getWeight() : 35;
		        	   int f  = profile!=null? profile.getHeightFt(): 5;
		        	   int inch = profile!=null? profile.getHeightInch(): 5;
		        	   double heghtinCM =( f*12+inch)*2.54;
		        	   double sl =0;
		        	   // calculate stride length
		        	   if(profile.getGender().equals("Male"))
		        		   sl = heghtinCM*0.415;
		        		   else
		        			   sl = heghtinCM*0.413; 
		        	    distance  = (shakeDetector.getStepCounterValue()==0?57000:shakeDetector.getStepCounterValue() * sl/100)/1000; //https://www.walkingwithattitude.com/articles/features/how-to-measure-stride-or-step-length-for-your-pedometer
		        	  /**
		        	   * Calorie Burn = (BMR / 24) x MET x T
							where

						For males: BMR = (13.75 x WKG) + (5 x HC) - (6.76 x age) + 66 
						For females: BMR = (9.56 x WKG) + (1.85 x HC) - (4.68 x age) + 65.5 
		        	   */
		        	   double bmr =0;
		        	   if(profile.getGender().equals("Male"))
		        	   bmr = (13.75 * w) + (5 * heghtinCM) - (6.76 * profile.getAge()) + 66;
		        	   else
		        		   bmr = (9.56 * w) + (1.85 * heghtinCM) - (4.68 * profile.getAge()) + 65.5 ;
		        	   
		        	    calorie=( bmr/24)*7.5 *(hour+min/60);
		        	   db.addHistory(new History(cDate, totalTime, distance, calorie, ""));
		        	   }
		        	   Intent i = new Intent(dialog.getContext(), RunnerActivity.class);
		        	   i.putExtra("time", totalTime);
		        	   i.putExtra("distance", distance);
		        	   i.putExtra("calorie", calorie);
		        	   i.putExtra("status", true);
		        	   /**
		        	    * save to db
		        	    */
		       
		        	
		        	   startActivity(i);// User clicked OK button
		           }
		       });
		/*builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   gamePanel.setPause (0);  // User cancelled the dialog
		           }
		       });*/
		// Set other dialog properties
		

		// Create the AlertDialog
		
		AlertDialog dialog = builder.create();
		
		 dialog.show();
	}
	
}
