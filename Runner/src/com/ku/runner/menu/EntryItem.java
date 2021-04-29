package com.ku.runner.menu;

public class EntryItem implements Item{
	 
	 public  String title;
	 public  String subtitle;
	 public  String imagelocation ="";
	 
	 public EntryItem(String title, String subtitle) {
	  this.title = title;
	  this.subtitle = subtitle;
	 }
	 
	 
	 
	 public EntryItem(String title, String subtitle, String imagelocation) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.imagelocation = imagelocation;
	}



	@Override
	 public boolean isSection() {
	  return false;
	 }
	 
	}
