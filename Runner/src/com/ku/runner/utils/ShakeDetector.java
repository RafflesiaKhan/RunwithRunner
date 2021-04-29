package com.ku.runner.utils;

import com.ku.runner.activity.PlayActivity;
import com.ku.runner.activity.RunnerActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor stepCounter;
	private float xAccel, yAccel, zAccel;
	private float xPreviousAccel, yPreviousAccel, zPreviousAccel;
	private boolean firstUpdate = true;
	private final float shakeThreshold = 1.5f;
	private boolean shakeInitiated = false;
	private Activity parent;
	private int stepCounterValue = 0; private int counterSteps = 0; private int stepDetector = 0; 
	public ShakeDetector(Activity runnerActivity) {
		parent = runnerActivity;
	}

	private boolean isAccelerationChanged() {
	    float deltaX = Math.abs(xPreviousAccel - xAccel);
	    float deltaY = Math.abs(yPreviousAccel - yAccel);
	    float deltaZ = Math.abs(zPreviousAccel - zAccel);
	    return (deltaX > shakeThreshold && deltaY > shakeThreshold)
	            || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
	            || (deltaY > shakeThreshold && deltaZ > shakeThreshold);
	}

	private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {
	    if (firstUpdate) {
	        xPreviousAccel = xNewAccel;
	        yPreviousAccel = yNewAccel;
	        zPreviousAccel = zNewAccel;
	        firstUpdate = false;
	    }else{
	        xPreviousAccel = xAccel;
	        yPreviousAccel = yAccel;
	        zPreviousAccel = zAccel;
	    }
	    xAccel = xNewAccel;
	    yAccel = yNewAccel;
	    zAccel = zNewAccel;
	}

	private void executeShakeAction() {
	    //this method is called when devices shakes
		((PlayActivity)parent).getGamePanel().setJumpNow(true);
	}

	public void onSensorChanged(SensorEvent se) {
		switch (se.sensor.getType()) {
		 case Sensor.TYPE_ACCELEROMETER: 
		updateAccelParameters(se.values[0], se.values[1], se.values[2]);
	    if ((!shakeInitiated) && isAccelerationChanged()) {
	        shakeInitiated = true;
	    }else if ((shakeInitiated) && isAccelerationChanged()){
	        executeShakeAction();
	    }else if((shakeInitiated) && (!isAccelerationChanged())){
	        shakeInitiated = false;
	    }
	    
	    break;
	    case Sensor.TYPE_STEP_DETECTOR: 
	    	stepDetector++; 
	    	break; 
	    case Sensor.TYPE_STEP_COUNTER: 
	    	//Since it will return the total number since we registered we need to subtract the initial amount 
	    	//for the current steps since we opened app
	    	if (counterSteps < 1) { 
	    		// initial value 
	    		counterSteps = (int)se.values [0];
	    		} 
	    		// Calculate steps taken based on first counter value received. 
	    	stepCounterValue = (int)se.values [0] - counterSteps; 
	    	((PlayActivity)parent).getGamePanel().setAvgFps(stepCounterValue+"");
	    break;
	    	} 
	    }
	    
	    
	

	public int getStepCounterValue() {
		return stepCounterValue;
	}

	public void setStepCounterValue(int stepCounterValue) {
		this.stepCounterValue = stepCounterValue;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    //setting the accuracy
		
	}
	
	
	 public void registerListener(){
		  sensorManager = (SensorManager)parent.getSystemService(Context.SENSOR_SERVICE);
		  accelerometer = sensorManager.getDefaultSensor(
		        Sensor.TYPE_ACCELEROMETER);
		     
		      
		     
		  sensorManager.registerListener(this,
				  accelerometer,
		    SensorManager.SENSOR_DELAY_NORMAL);
		  
		  
		  if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
		  {
			  stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
			  sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
		  }
		 }
	 public void unregisterListener(){
		  sensorManager.unregisterListener(this);
		 }
}
