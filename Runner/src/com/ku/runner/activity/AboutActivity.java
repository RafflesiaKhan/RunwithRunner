package com.ku.runner.activity;

import java.util.ArrayList;










import com.ku.runner.MenuItemDetailActivity;
import com.ku.runner.R;
import com.ku.runner.menu.EntryItem;
import com.ku.runner.menu.Item;
import com.ku.runner.menu.SectionItem;
import com.ku.runner.utils.EntryAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class AboutActivity  extends MenuItemDetailActivity  implements OnItemClickListener{
	ArrayList<Item> items = new ArrayList<Item>();
	  ListView listview=null;
	  
	  Bitmap rafa;
	  Bitmap sonum;
	  Bitmap sapla;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		
		  // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		listview=(ListView)findViewById(R.id.about_listview);

		   rafa=BitmapFactory.decodeResource(getResources(), R.drawable.exit);
		   sonum=BitmapFactory.decodeResource(getResources(), R.drawable.exit);
		   sapla=BitmapFactory.decodeResource(getResources(), R.drawable.exit);
        
        items.add(new SectionItem("Developer"));
        items.add(new EntryItem("130233","Rafflesia Khan"));
        items.add(new EntryItem("130206","Sharima Islam"));
        items.add(new EntryItem("130229","Rahima Akter"));
      
                
        items.add(new SectionItem("Android Version"));
        
        items.add(new EntryItem("IceCream Sandwich", "android 6.0"));

         
        items.add(new SectionItem("Apllication Version"));
        items.add(new EntryItem("Runner", "1.0.0"));
    
         
        EntryAdapter adapter = new EntryAdapter(this,0, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
	}
	 @Override
	 public void onItemClick(AdapterView arg0, View arg1, int position, long arg3) {
	   
	  EntryItem item = (EntryItem)items.get(position);
	  Toast.makeText(this, "The details" + item.title , Toast.LENGTH_SHORT).show();
	 }
}
