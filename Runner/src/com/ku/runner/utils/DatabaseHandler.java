package com.ku.runner.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 


import com.ku.runner.model.History;
import com.ku.runner.model.Profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "profileManager";
 
    // Profiles table name
    private static final String TABLE_ProfileS = "profile";
 
    // Profiles Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_HEIGHT= "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_Image = "imageurl";

	private static final String TABLE_OVERVIEW = "overview";

	private static final String KEY_TIME = "time";

	private static final String KEY_CALORIE = "calorie";

	private static final String KEY_DISTANCE = "distance";

	private static final String KEY_DATE = "date";

	private static final String KEY_REMAR = "remar";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ProfileS_TABLE = "CREATE TABLE " + TABLE_ProfileS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_GENDER + " TEXT,"+ KEY_AGE + " TEXT,"+ KEY_HEIGHT + " TEXT,"
                + KEY_WEIGHT + " TEXT,"+ KEY_EMAIL + " TEXT,"
                + KEY_Image + " TEXT" + ")";
        db.execSQL(CREATE_ProfileS_TABLE);
        
        String CREATE_OVERVIEW_TABLE = "CREATE TABLE " + TABLE_OVERVIEW + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE+ " DATE,"
                + KEY_TIME + " TEXT,"+ KEY_CALORIE + " TEXT,"+ KEY_DISTANCE + " TEXT,"
         
                + KEY_REMAR + " TEXT" + ")";
        db.execSQL(CREATE_OVERVIEW_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ProfileS);
     // Create tables again
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OVERVIEW);
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new Profile
    public void addProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, profile.getName()); // Profile Name
        
        values.put(KEY_GENDER, profile.getGender()); 
        values.put(KEY_AGE, profile.getAge()); // 
        
        values.put(KEY_HEIGHT, profile.getHeightFt()+":"+profile.getHeightInch()); 
        	values.put(KEY_WEIGHT, profile.getWeight()); // 
        
        values.put(KEY_EMAIL, profile.getEmail()); 
        values.put(KEY_Image, profile.getImageUrl()); 
        // Inserting Row
        db.insert(TABLE_ProfileS, null, values);
        db.close(); // Closing database connection
    }
 
    public void addHistory(History history) {
    	
    	 SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");	
        SQLiteDatabase db = this.getWritableDatabase();
        double d =history.getDistance();
        double c= history.getCalorie();
        History h = getHistory(history.getToday());
        if(h!=null)
        	{d= h.getDistance()+ history.getDistance();
        	c= history.getCalorie()+h.getCalorie();}
        
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, sdf.format(history.getToday())); 
        
        values.put(KEY_TIME, history.getTime()); 
        values.put(KEY_DISTANCE, d); // 
        
        values.put(KEY_CALORIE, c); 
        	
        // Inserting Row
        db.insert(TABLE_OVERVIEW, null, values);
        db.close(); // Closing database connection
    }
    
    
    History getHistory (Date date)
    {SQLiteDatabase db = this.getReadableDatabase();
    	 Cursor cursor = db.query(TABLE_OVERVIEW, new String[] { KEY_ID,
                 KEY_DATE, KEY_TIME, KEY_DISTANCE,KEY_CALORIE,KEY_REMAR}, KEY_DATE + "=?",
                 new String[] { date.toString() }, null, null, null, null);
    	 History history = null;
    	 if (cursor.moveToFirst())
    	  {history = new History(Integer.parseInt(cursor.getString(0)),
                 cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)),
                 Double.parseDouble(cursor.getString(4)),cursor.getString(5));
    	  }
         // return Profile
         return history;
    }
 
    // Getting single Profile
    Profile getProfile(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_ProfileS, new String[] { KEY_ID,
                KEY_NAME, KEY_GENDER, KEY_AGE,KEY_HEIGHT,KEY_WEIGHT,KEY_EMAIL,KEY_Image}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 String h  []= cursor.getString(4).split(":");
        Profile Profile = new Profile(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(h[0]),Integer.parseInt(h[1]),Integer.parseInt(cursor.getString(5)),cursor.getString(6),cursor.getString(7));
        // return Profile
        return Profile;
    }
     
    
    public Profile getProfile ()
    {
    	List<Profile> list =	getAllProfiles();
    	if(list!=null&& list.size() >0)
    		return list.get(0);
    	else
    		return null;
    }
    // Getting All Profiles
    public List<Profile> getAllProfiles() {
        List<Profile> ProfileList = new ArrayList<Profile>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ProfileS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
        	
            do { String h  []= cursor.getString(4).split(":");
                Profile profile = new Profile(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(h[0]),Integer.parseInt(h[1]),Integer.parseInt(cursor.getString(5)),cursor.getString(6),cursor.getString(7));
             
                // Adding Profile to list
                ProfileList.add(profile);
            } while (cursor.moveToNext());
        }
 
        // return Profile list
        return ProfileList;
    }
 
    // Updating single Profile
    public int updateProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, profile.getName());
        values.put(KEY_GENDER, profile.getGender()); 
        values.put(KEY_AGE, profile.getAge()); // 
        
        values.put(KEY_HEIGHT, profile.getHeightFt()+":"+profile.getHeightInch()); 
        	values.put(KEY_WEIGHT, profile.getWeight()); // 
        
        values.put(KEY_EMAIL, profile.getEmail()); 
        values.put(KEY_Image, profile.getImageUrl()); 
        // updating row
        return db.update(TABLE_ProfileS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(profile.get_id()) });
    }
 
    // Deleting single Profile
    public void deleteProfile(Profile Profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ProfileS, KEY_ID + " = ?",
                new String[] { String.valueOf(Profile.get_id()) });
        db.close();
    }
 
 
    // Getting Profiles Count
    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ProfileS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
    public List <History> getRange(Date d1, Date d2)
    {
    	 SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");	
    	List<History> historyList = new ArrayList<History>();
        // Select All Query
    	String s1 = sdf.format(d1);
    	String s2 = sdf.format(d2);
        String selectQuery = null;
		selectQuery = "SELECT  * FROM " + TABLE_OVERVIEW+ " WHERE "+ KEY_DATE +" BETWEEN \'"+ s1 + "\' AND \'"+ s2+"\'";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
        	
            do { String h  []= cursor.getString(4).split(":");
            History history = new History(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)),
                    Double.parseDouble(cursor.getString(4)),cursor.getString(5));
                // Adding Profile to list
                historyList.add(history);
            } while (cursor.moveToNext());
        }
 
        // return Profile list
        return historyList;
    }
    
}
