package com.ku.runner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.ku.runner.R;
import com.ku.runner.activity.MainGamePanel;
import com.ku.runner.activity.RunnerActivity;
import com.ku.runner.model.Profile;
import com.ku.runner.utils.DatabaseHandler;
import com.ku.runner.utils.ShakeDetector;

/**
 * This sample demonstrates use of the
 * {@link com.google.android.gms.location.ActivityRecognitionApi} to recognize a user's current
 * activity, such as walking, driving, or standing still. It uses an
 * {@link android.app.IntentService} to broadcast detected activities through a
 * {@link BroadcastReceiver}. See the {@link DetectedActivity} class for a list of DetectedActivity
 * types.
 * <p/>
 * Note that this activity implements
 * {@link ResultCallback<R extends com.google.android.gms.common.api.Result>}.
 * Requesting activity detection updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}
 * and stopping updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates}
 * returns a {@link com.google.android.gms.common.api.PendingResult}, whose result
 * object is processed by the {@code onResult} callback.
 */

@SuppressLint("NewApi")
public class DectectedActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "activity-recognition";

    /**
     * A receiver for DetectedActivity objects broadcast by the
     * {@code ActivityDetectionIntentService}.
     */
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Used when requesting or removing activity detection updates.
     */
    private PendingIntent mActivityDetectionPendingIntent;


    private MainGamePanel activityGamePanel;

    /**
     * Adapter backed by a list of DetectedActivity objects.
     */
    private DetectedActivitiesAdapter mAdapter;

    /**
     * The DetectedActivities that we track in this sample. We use this for initializing the
     * {@code DetectedActivitiesAdapter}. We also use this for persisting state in
     * {@code onSaveInstanceState()} and restoring it in {@code onCreate()}. This ensures that each
     * activity is displayed with the correct confidence level upon orientation changes.
     */
    private ArrayList<DetectedActivity> mDetectedActivities;

	private ShakeDetector shakeDetector;

	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        /*if (savedInstanceState != null && savedInstanceState.containsKey(
                Constants.DETECTED_ACTIVITIES)) {
            mDetectedActivities = (ArrayList<DetectedActivity>) savedInstanceState.getSerializable(
                    Constants.DETECTED_ACTIVITIES);
        } else {
            mDetectedActivities = new ArrayList<DetectedActivity>();

            // Set the confidence level of each monitored activity to zero.
            for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
                mDetectedActivities.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], 0));
            }
        }*/

       // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
        shakeDetector = new ShakeDetector(this);
		//phone state
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		TelephonyMgr.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);
		//for no title
		activityGamePanel =new MainGamePanel(this);
		activityGamePanel.setActivityEnabled(true);
		
		setContentView(activityGamePanel);		
    }

    public class TeleListener extends PhoneStateListener 
	{
      public void onCallStateChanged(int state,String incomingNumber)
      {
        if(state==TelephonyManager.CALL_STATE_RINGING)
           {
        	
        	System.exit(0);  
           }
       } 
      
    }
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
     // TODO Auto-generated method stub
        
      
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        );
       if(mGoogleApiClient.isConnected())
    	   Toast.makeText(this, "activity recognition connected", Toast.LENGTH_SHORT).show();
       else
    	   mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    

    

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

           

            Toast.makeText(
                    this,
                    getString(requestingUpdates ? R.string.activity_updates_added :
                            R.string.activity_updates_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mActivityDetectionPendingIntent != null) {
            return mActivityDetectionPendingIntent;
        }
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    

    /**
     * Retrieves a SharedPreference object used to store or read values in this app. If a
     * preferences file passed as the first argument to {@link #getSharedPreferences}
     * does not exist, it is created when {@link SharedPreferences.Editor} is used to commit
     * data.
     */
    private SharedPreferences getSharedPreferencesInstance() {
        return getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .commit();
    }

    /**
     * Stores the list of detected activities in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(Constants.DETECTED_ACTIVITIES, mDetectedActivities);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Processes the list of freshly detected activities. Asks the adapter to update its list of
     * DetectedActivities with new {@code DetectedActivity} objects reflecting the latest detected
     * activities.
     */
  /*  protected void updateDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities) {
        mAdapter.updateActivities(detectedActivities);
    }*/

    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String currentActivity = intent.getStringExtra(Constants.CURRENT_ACTIVITY);
           
            activityGamePanel.setCurrentActivity(currentActivity);
           // updateDetectedActivitiesList(updatedActivities);
        }
    }
    public void showDialogTime(String type) {
		final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());	
	SimpleDateFormat formatFrom = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
	Date cDate = new Date();
	Date startTime=cDate, endTime = cDate;
	try {
		startTime = formatFrom.parse(mSharedPreference.getString("stTime", cDate.toString()));
		endTime=formatFrom.parse(mSharedPreference.getString("edTime", cDate.toString()));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	
	String message =""; AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// Add the buttons
	if(type.equals("STOP_ANIMATOIN"))
	{	
		 
         Long timeDiff = cDate.getTime() - startTime.getTime();
         Long timetoGo = endTime.getTime() - cDate.getTime();
         int hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff));
        int  mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
		
        
        	int mt =(int) (TimeUnit.MILLISECONDS.toMinutes(timetoGo));
       
        
        message = "You have taken total "+shakeDetector.getStepCounterValue()+" steps and total performance time is"+ hh +":"+ mm+ " minutes";
	
        if (mt >0)
        	message += "You still have to continue for "+mt +"minute. Are you sure to quite?";
        final String  totalTime = hh+":"+mm;
        final int hour =hh; final int min = mm;
	builder.setMessage(message);
	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialogf, int id) {
	        	  Dialog dialog = (Dialog) dialogf;
	        	  DatabaseHandler db = new DatabaseHandler(dialog.getContext());
	        	   Profile profile = db.getAllProfiles()!=null && db.getAllProfiles().size() >0 ?db.getAllProfiles().get(0):null;
	        	  
	        	   int w = profile!=null? profile.getWeight() : 35;
	        	   int f  = profile!=null? profile.getHeightFt(): 5;
	        	   int inch = profile!=null? profile.getHeightInch(): 5;
	        	   double heghtinCM =( f*12+inch)*2.54;
	        	   double sl =0;
	        	   // calculate stride length
	        	   if(profile.getGender().equals("Male"))
	        		   sl = heghtinCM*0.415;
	        		   else
	        			   sl = heghtinCM*0.413; 
	        	   double distance  = (shakeDetector.getStepCounterValue() * sl/100)/1000; 
	        	   //https://www.walkingwithattitude.com/articles/features/how-to-measure-stride-or-step-length-for-your-pedometer
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
	        	   
	        	   double calorie=( bmr/24)*7.5 *(hour+min/60);
	        	   Intent i = new Intent(((Dialog)dialog).getContext(), RunnerActivity.class);
	        	   i.putExtra("time", totalTime);
	        	   i.putExtra("distance", distance);
	        	   i.putExtra("calorie", calorie);
	        	   i.putExtra("status", true);
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
	}
	AlertDialog dialog = builder.create();
	
	 dialog.show();
}
}