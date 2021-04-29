
package com.ku.runner.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ku.runner.DectectedActivity;
import com.ku.runner.R;
import com.ku.runner.animation.MainThread;
import com.ku.runner.model.AnimBella;

/**
 * @author SSR
 * This is the main surface that handles the  events and draws
 * the image to the screen.
 */
public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {
	 
	
	private static final String TAG = MainGamePanel.class.getSimpleName();
	private static boolean isActivityEnabled = false; 
	private MainThread thread;
	Timer timer;
	String currentActivity, previousActivity;
	public static boolean drawRock =true;
	public static boolean jumpNow= false; 
	private AnimBella player;
	Bitmap spriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet2); 
	Bitmap background=BitmapFactory.decodeResource(getResources(), R.drawable.back);
	Bitmap rock  = BitmapFactory.decodeResource(getResources(), R.drawable.rock);
	Bitmap exit=BitmapFactory.decodeResource(getResources(), R.drawable.exit);
	Bitmap  pause=BitmapFactory.decodeResource(getResources(), R.drawable.pause);
	// the fps to be displayed
	private String avgFps;
	int startX =10, startY =0, inctimer=0, delay=0, show =0;
	int maxWidth=0,maxHeight=0,z=0, hurdlePos=160,  pausecount=0;
	int containerWidth , containerheight; 
	Context context ;
	
	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}

	public MainGamePanel(Context context) {
		super(context);
		this.context = context;
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		
		 maxWidth =this.getResources().getDisplayMetrics().widthPixels;
		 maxHeight =this.getResources().getDisplayMetrics().heightPixels;
		 //hurdlePos= maxWidth/2;
		 startY= maxHeight-120;
		 Log.d(TAG, "width:"+maxWidth +";Height:"+maxHeight+":startY"+startY);
		 exit=Bitmap.createScaledBitmap(exit, 25,25, true);
   	    pause=Bitmap.createScaledBitmap(pause, 25,25, true);
		background=Bitmap.createScaledBitmap(background, 2*maxWidth,maxHeight, true);
		player = new AnimBella(spriteSheet,startX, startY, 125, 125, 4, 16,0, context.getResources());
		
		setFocusable(true);

	}
	public void startAnimation() {
		 timer= new Timer ("Draw Rock");
		 TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				drawRock=!drawRock;
				inctimer++;
			}
		};
		timer.scheduleAtFixedRate(timerTask , 30, 10000);
		
		thread = new MainThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
		
		player.setContainerWidth(maxWidth);
	}

	public void stopAnimation() {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.setRunning(false);
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");	
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		startAnimation();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopAnimation();
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
    	  		show=1;
    	  		
    	  		int getx=(int) event.getX();
    	  		 int gety=(int) event.getY();
    	  		//exit
    	  		if(getx<25&&gety<25)
    	  		{
    	  			stopAnimation();
    	  			if(isActivityEnabled)
    	  			{
    	  				DectectedActivity d = (DectectedActivity) getContext();
    	  				d.showDialogTime("STO_ANIMATION");
    	  			}
    	  			else{ 
    	  			PlayActivity pl = (PlayActivity) getContext();
    	  		
    	  			
    	  			pl.showDialogTime("STOP_ANIMATION",getContext());
    	  			}
    	  			//System.exit(0);
    	  		}
    	  		// restart game
    	  		else if(getx>91&&gety<25)
    	  		{
    	  			
    	  				thread.setPause(0);
					
    	  			
    	  		}
    	  		
    	  		//pause game
    	  		else if((getx>(maxWidth-25)&&gety<25&&pausecount==0))
    	  		{
    	  			
    	  			thread.setPause(1);
    	  			pausecount=1;
    	  			
    	  		}
    	  		else if(getx>(maxWidth-25)&&gety<25&&pausecount==1)
    	  		{
    	  			thread.setPause(0);
    	  			
    	  			pausecount=0;
    	  		}
/*    	  		else {
    	  			setJumpNow(true);
    	  		}*/
    	  
		}
		return true;
	}

	public void render(Canvas canvas) {
		if(canvas!=null)
	{	canvas.drawColor(Color.TRANSPARENT);
		if(currentActivity!=null&& isActivityChanged())
			player.setCurrentactivity(currentActivity);
		/**
		 * draw moving background
		 */
		/*z=z-10;
    	if(z==-maxWidth)
    	{Log.d(TAG, "current z is"+z);
    		z=0;
    		canvas.drawBitmap(background, z, 0, null);
    		
    		
    	}
    	else
    	{*/
    		canvas.drawBitmap(background, z, 0, null);	
    		
    	//}
    	
    	/**
    	 * draw rock 
    	 */
    	
    	
 		if(drawRock){
 			
 			if(!(player.getX()> maxWidth -150))
 				
 			{	if(!jumpNow && player.getJumped()!=1)
 				hurdlePos =player.getX()+80;
 			
 			Log.d(TAG, "drawing roxk>>>>>>>>>>>"+ hurdlePos+":"+":"+player.getJumped()+":"+jumpNow+":"+player.getX()); 
 	    	    canvas.drawBitmap(rock, hurdlePos,maxHeight-120, null);
 			}
 		}
 	
 			player.draw(canvas);
 			canvas.drawBitmap(exit, 0, 0, null);
 			canvas.drawBitmap(pause, (maxWidth-100), 0, null);
		// display fps
		displayFps(canvas, avgFps);
	}
	}


	/**
	 * This is the game update method. It iterates through all the objects
	 * and calls their update method if they have one or calls specific
	 * engine's update method.
	 */
	public void update() {
		player.update(System.currentTimeMillis());
	}

	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, maxWidth/2, 20, paint);
		}
	}

	public static boolean isDrawRock() {
		return drawRock;
	}

	public static void setDrawRock(boolean drawRock) {
		MainGamePanel.drawRock = drawRock;
	}

	public static boolean isJumpNow() {
		return jumpNow;
	}

	public static void setJumpNow(boolean jumpNow) {
		MainGamePanel.jumpNow = jumpNow;
		Log.d("Shae executed" ,"no"+MainGamePanel.jumpNow);
	}

	public void setPause(int i) {
		if(thread!=null)
		thread.setPause(i);
		
	}

	public static boolean isActivityEnabled() {
		return isActivityEnabled;
	}

	public void setActivityEnabled(boolean isActivityEnabled) {
		this.isActivityEnabled = isActivityEnabled;
	}

	public String getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(String currentActivity) {
		this.currentActivity = currentActivity;
	}

 public boolean isActivityChanged ()
 {
	 if(currentActivity == null )
		 return false;
		 else 
	 return !currentActivity.equals(previousActivity);
 }

}
