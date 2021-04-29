package com.ku.runner.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class History {
	int _id;
 Date today;
	String time, remark;
 double distance, calorie;
 
 
public History(int _id, String today, String time, double distance, double calorie, String remark) {
	super();
	this._id = _id;
	SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.getDefault());
	try {
		this.today = df.parse(today);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	this.time = time;
	this.distance = distance;
	this.calorie = calorie;
	this.remark =remark;
}
public int get_id() {
	return _id;
}
public void set_id(int _id) {
	this._id = _id;
}
public History(Date today, String time, double distance, double calorie, String remark) {
	super();
	this.today = today;
	this.time = time;
	this.distance = distance;
	this.calorie = calorie;this.remark =remark;
}
public History() {
	super();
	// TODO Auto-generated constructor stub
}
public Date getToday() {
	return today;
}
public void setToday(Date today) {
	this.today = today;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
public double getDistance() {
	return distance;
}
public void setDistance(double distance) {
	this.distance = distance;
}
public double getCalorie() {
	return calorie;
}
public void setCalorie(double calorie) {
	this.calorie = calorie;
}
}
