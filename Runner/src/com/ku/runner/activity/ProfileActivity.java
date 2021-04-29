package com.ku.runner.activity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.ku.runner.MenuItemDetailActivity;
import com.ku.runner.MenuItemListActivity;
import com.ku.runner.R;
import com.ku.runner.R.id;
import com.ku.runner.R.layout;
import com.ku.runner.model.Profile;
import com.ku.runner.utils.DatabaseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class ProfileActivity extends Activity {
	DatabaseHandler db;
	Profile myProfile;
	EditText nameE, ageE, emailE, weightE;
	Spinner feet, inxh;
	String imgUrl;
	private RadioGroup radioGroup;
	private final int SELECT_PHOTO = 1;
	private ImageView imageView;
	private Context t ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		db = new DatabaseHandler(this);
		myProfile = db.getProfile();
		InitializeView();
		t = this;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
        case SELECT_PHOTO:
            if(resultCode == RESULT_OK){
				try {
					final Uri imageUri = imageReturnedIntent.getData();imgUrl = imageUri.toString();
					final InputStream imageStream = getContentResolver().openInputStream(imageUri);
					final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
					imageView.setImageBitmap(selectedImage);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

            }
        }
    }

	private void InitializeView() {
		// TODO Auto-generated method stub
		nameE = (EditText) findViewById(R.id.editName);
		ageE = (EditText) findViewById(R.id.editAge);
		emailE = (EditText) findViewById(R.id.editEmail);
		weightE = (EditText) findViewById(R.id.weightEdit);
		radioGroup = (RadioGroup) findViewById(R.id.radiogender);
		imageView = (ImageView)findViewById(R.id.imageView1);

        Button pickImage = (Button) findViewById(R.id.btn_pick);
		feet=(Spinner) findViewById(R.id.feet);
		Integer[] items = new Integer[]{1,2,3,4,5,6,7,8};
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items);
		feet.setAdapter(adapter);
		inxh=(Spinner) findViewById(R.id.inch);
		Integer[] items2 = new Integer[]{1,2,3,4,5,6,7,8,9,10,11};
		ArrayAdapter<Integer>  adapter2 = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items2);
		inxh.setAdapter(adapter2);
		
		if (myProfile != null) {
			nameE.setText(myProfile.getName());
			ageE.setText(myProfile.getAge()+"");
			emailE.setText(myProfile.getEmail());
			weightE.setText(myProfile.getWeight()+"");
			feet.setSelection(adapter.getPosition(myProfile.getHeightFt()));
			inxh.setSelection(adapter.getPosition(myProfile.getHeightInch()));
		}
	}

	public void minusWeight(View v) {
		int weight = Integer.parseInt(weightE.getText().toString());
		weight = weight - 1;
		weightE.setText(weight + "");
	}

	public void plusWeight(View v) {
		int weight = Integer.parseInt(weightE.getText().toString());
		weight = weight + 1;
		weightE.setText(weight + "");
	}

	public void cancelProfile(View v) {
		startActivity(new Intent(this, MenuItemListActivity.class));
	}

	public void saveProfile(View v){
boolean add = myProfile ==null;
		RadioButton selectRadio = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
		String gender = selectRadio.getText().toString();
		myProfile = new Profile(nameE.getText().toString(), gender, 
				Integer.parseInt(ageE.getText().toString()),(feet.getSelectedItemPosition()+1),(inxh.getSelectedItemPosition()+1),
				Integer.parseInt(weightE.getText().toString()),emailE.getText().toString(),imgUrl);
		if(!add)
		db.updateProfile(myProfile);
		else
			db.addProfile(myProfile);
		String message ="Your Profile has been saved successfully!!";
		showAlert(message);
		
	}
	
	private void showAlert( String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(t);
		 builder//.setView(inflater.inflate(R.layout.dialog_result, null))
	        .setTitle("Profile Save")
	        .setMessage(message)
	        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialogf, int id) {
		        	  Dialog dialog = (Dialog) dialogf;
		        	  Intent i = new Intent(dialog.getContext(), MenuItemListActivity.class);
		        	  startActivity(i);
		           }
		       });
		 
		 AlertDialog dialog = builder.create();
			
		 dialog.show();
	}
	
	public void ChangeProfilePicture(View v)
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}
	
}
