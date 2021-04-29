package com.ku.runner;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.ku.runner.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "activity-detection-intent-service";

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Get the most probable activity
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            // Get the probability that this activity is the  the user's actual activity
            int confidence = mostProbableActivity.getConfidence();
            //Get an integer describing the type of activity
            int activityType = mostProbableActivity.getType();
            if (activityType == DetectedActivity.ON_FOOT) {
                DetectedActivity betterActivity = walkingOrRunning(result.getProbableActivities());
                if (null != betterActivity)
                    mostProbableActivity = betterActivity;
            }
             activityType = mostProbableActivity.getType();
            String activityName = getNameFromType(activityType);

            /*
             * At this point, you have retrieved all the information
             * for the current update. You can display this
             * information to the user in a notification, or
             * send it to an Activity or Service in a broadcast
             * Intent.
             */
            showNotification(confidence, activityName);
          
            Log.i(TAG, 
                    "CURRENT ACTIVITY"+ getNameFromType(mostProbableActivity.getType()) + " " + mostProbableActivity.getConfidence() + "%"
            );
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

            // Get the list of the probable activities associated with the current state of the
            // device. Each activity is associated with a confidence level, which is an int between
            // 0 and 100.
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            // Log each activity.
            Log.i(TAG, "activities detected");
            for (DetectedActivity da: detectedActivities) {
                Log.i(TAG, Constants.getActivityString(
                                getApplicationContext(),
                                da.getType()) + " " + da.getConfidence() + "%"
                );
            }

            // Broadcast the list of detected activities.
            localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
            localIntent.putExtra(Constants.CURRENT_ACTIVITY, activityName);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        } else {
            /*
             * This implementation ignores intents that don't contain
             * an activity update. If you wish, you can report them as
             * errors.
             */
        }
    }
   

    private DetectedActivity walkingOrRunning(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 0;
        for (DetectedActivity activity : probableActivities) {
            if (activity.getType() != DetectedActivity.RUNNING && activity.getType() != DetectedActivity.WALKING)
                continue;

            if (activity.getConfidence() > confidence)
                myActivity = activity;
        }

        return myActivity;
    }
    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    private String getNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }

    @SuppressLint("NewApi")
	private void showNotification(int confidence, String activityName) {

        // Create an explicit content Intent that starts the main Activity
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        // Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the main Activity to the task stack as the parent
        stackBuilder.addParentStack(MainActivity.class);
        // Push the content Intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set the notification contents
        builder.setSmallIcon(R.drawable.abc_ab_share_pack_mtrl_alpha) //
                .setWhen(System.currentTimeMillis()) //
                .setContentTitle("Activity Recognition") // Transition Type
                .setContentText("" + confidence + "% of " + activityName) // Zone Name
                .setContentIntent(notificationPendingIntent);

        // BigView
        NotificationCompat.InboxStyle inBoxStyle = new NotificationCompat.InboxStyle();
        inBoxStyle.setBigContentTitle("Activity Recognition");
        inBoxStyle.addLine("" + confidence + "% of " + activityName);
        builder.setStyle(inBoxStyle);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
    
}

