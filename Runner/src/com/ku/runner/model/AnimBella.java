package com.ku.runner.model;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.ku.runner.R;
import com.ku.runner.activity.MainGamePanel;

public class AnimBella{
	private Bitmap bitmap;		// the animation sequence
	private Rect initialstage, sourceRect;	// the rectangle to be drawn from the animation bitmap
	private int frameNr;		// number of frames in animation
	private int currentFrame, startFrame, endFrame, row=0;	// the current frame
	private long frameTicker;	// the time of the last frame update
	private int framePeriod;	// milliseconds between each frame (1000/fps)
	private int framePerRow =4;
	private int spriteWidth;	// the width of the sprite to calculate the cut out rectangle
	private int spriteHeight;	// the height of the sprite

	private int startX,x, delay =0, jumped=-1;				// the X coordinate of the object (top left of the image)
	boolean doJump = false;
	private int startY, y;
	private String TAG = AnimBella.class.getSimpleName() ;	
	int containerWidth =0, containerheight =0; 
	String currentactivity, prevActivity;
	 Resources resources;
	
	
	public AnimBella(Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount, int startFrame,Resources resources) {
		this.resources = resources;
		this.bitmap = bitmap;
		this.startX =this.x = x;
		this.startY =this.y = y;
		currentFrame = -1;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth()/4;
		spriteHeight = bitmap.getHeight()/4;
	
		initialstage =sourceRect = new Rect(0, 0, width, height);
		framePeriod = 1000 / fps;
		frameTicker = 0l;
		
		Log.d(TAG, "Initalization"+ spriteHeight+":"+ spriteWidth+ "source"+sourceRect.left+ ":"+ sourceRect.top+ ":"+ sourceRect.right+ ":"+ sourceRect.bottom);

	}
	
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public Rect getSourceRect() {
		return sourceRect;
	}
	public void setSourceRect(Rect sourceRect) {
		this.sourceRect = sourceRect;
	}
	public int getFrameNr() {
		return frameNr;
	}
	public void setFrameNr(int frameNr) {
		this.frameNr = frameNr;
	}
	public int getCurrentFrame() {
		return currentFrame;
	}
	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}
	public int getFramePeriod() {
		return framePeriod;
	}
	public void setFramePeriod(int framePeriod) {
		this.framePeriod = framePeriod;
	}
	public int getSpriteWidth() {
		return spriteWidth;
	}
	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidth = spriteWidth;
	}
	public int getSpriteHeight() {
		return spriteHeight;
	}
	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void update(long gameTime) {
		if(!MainGamePanel.drawRock)
			{jumped =-1;}
		
		else if(MainGamePanel.drawRock&& jumped ==-1)
			jumped =0;
		int prevCurrent =0;
		
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			// increment the frame
			row = (int) Math.floor(currentFrame % getFramePerRow());
			currentFrame++;
			if (currentFrame >= frameNr) {
				currentFrame = 0;
			}
		}
		
		if(MainGamePanel.isActivityEnabled())
			updateActivity(gameTime,prevCurrent, currentFrame);
		else
		{	
			if(prevCurrent != currentFrame && jumped !=0  ){
				//row = (int) Math.floor(currentFrame % getFramePerRow());
				this.sourceRect.left = row * spriteWidth;
				this.sourceRect.right = this.sourceRect.left + spriteWidth;
				this.sourceRect.top= row*spriteHeight;
				this.sourceRect.bottom =this.sourceRect.top +spriteHeight;
			}
		if(MainGamePanel.drawRock && jumped == 0 && !MainGamePanel.jumpNow)
		{
			
				x=x;
		}
		else if(MainGamePanel.isJumpNow())
		{
			y = startY - 120;x=x+120;
			sourceRect = new Rect(0, 125, spriteWidth,125+ spriteHeight);
			MainGamePanel.setJumpNow(false);
			setJumped(1);
			
			Log.d(TAG, "Jumping now ?????"+MainGamePanel.jumpNow+":"+jumped); 
			// y = startY;
		}
		
		
		else
		{
			y = startY;
		 if(x>=containerWidth-10)
			 x=startX;
		else
			x+=2;
		 
			 
		}
	
		}
		 prevCurrent = currentFrame;
	}
	

	private void updateActivity(long gameTime, int prevCurrent, int currentFrame) {
		
	if(	getCurrentactivity()!=null)
	{
		if(getCurrentactivity().equals(resources.getString(R.string.walking))
				||getCurrentactivity().equals(resources.getString(R.string.on_foot)))
		{
			if(x%2 ==0)
				sourceRect = initialstage;
			else
				sourceRect = new Rect(375, 375, 375+spriteWidth, 375+spriteHeight);
		}
		
		if((getCurrentactivity().equals(resources.getString(R.string.running))||
				getCurrentactivity().equals(resources.getString(R.string.on_bicycle))||
				getCurrentactivity().equals(resources.getString(R.string.in_vehicle))
				) && prevCurrent != currentFrame){
			//row = (int) Math.floor(currentFrame % getFramePerRow());
			this.sourceRect.left = row * spriteWidth;
			this.sourceRect.right = this.sourceRect.left + spriteWidth;
			this.sourceRect.top= row*spriteHeight;
			this.sourceRect.bottom =this.sourceRect.top +spriteHeight;
		}
		
		if(getCurrentactivity().equals(resources.getString(R.string.tilting))|| 
				getCurrentactivity().equals(resources.getString(R.string.unknown)))
		{
			y = startY - 60; x=x+10;
			sourceRect = new Rect(0, 125, spriteWidth,125+ spriteHeight);
			MainGamePanel.jumpNow=false;jumped =1; 
		}
		
		
		else
		{	x+=2;
		 if(x>=containerWidth-10)
			 x=startX;
			 y = startY;
		}
	}
	}


	public void draw(Canvas canvas) {
		Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
	    canvas.drawBitmap(bitmap, sourceRect, destRect, null);
	    
	
  }
	public int getFramePerRow() {
		return framePerRow;
	}


	public void setFramePerRow(int framePerRow) {
		this.framePerRow = framePerRow;
	}


	public void DrawJump(Canvas canvas) {
		Rect jump = new Rect(0, 125, spriteWidth,125+ spriteHeight);
     	canvas.drawBitmap(bitmap, jump, new Rect (160,200,160+spriteWidth,200+ spriteHeight), null);
    	try {
			// send the thread to sleep for a short period
			// very useful for battery saving
			Thread.sleep(2000);	
		} catch (InterruptedException e) {}
     	canvas.drawBitmap(bitmap, jump, new Rect (200,getY(),200+spriteWidth,getY()+ spriteHeight), null);
     	//draw(canvas);
	}


	public int getJumped() {
		return jumped;
	}


	public void setJumped(int jumped) {
		this.jumped = jumped;
	}


	public int getContainerWidth() {
		return containerWidth;
	}


	public void setContainerWidth(int containerWidth) {
		this.containerWidth = containerWidth;
	}


	public int getContainerheight() {
		return containerheight;
	}


	public void setContainerheight(int containerheight) {
		this.containerheight = containerheight;
	}

	public String getCurrentactivity() {
		return currentactivity;
	}


	public void setCurrentactivity(String currentactivity) {
		prevActivity = this.currentactivity;
		this.currentactivity = currentactivity;
	}

	
	
}
