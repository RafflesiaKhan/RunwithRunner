package com.ku.runner.utils;

import java.util.ArrayList;
import java.util.List;

import com.ku.runner.R;
import com.ku.runner.menu.EntryItem;
import com.ku.runner.menu.Item;
import com.ku.runner.menu.SectionItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EntryAdapter extends ArrayAdapter<Item> {
	private Context context;
	 private ArrayList<Item> items;
	 private LayoutInflater vi;
	public EntryAdapter(Context context, int resource, List<Item> objects) {
		super(context, 0, objects);
		
		this.context =context;
		this.items =(ArrayList<Item>) objects;
		 vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View v = convertView;
		 
		  final Item i = items.get(position);
		  if (i != null) {
		   if(i.isSection()){
		    SectionItem si = (SectionItem)i;
		    v = vi.inflate(R.layout.about_section, null);
		 
		    v.setOnClickListener(null);
		    v.setOnLongClickListener(null);
		    v.setLongClickable(false);
		     
		    final TextView sectionView = (TextView) v.findViewById(R.id.list_section);
		    sectionView.setText(si.getTitle());
		     
		   }else{
		    EntryItem ei = (EntryItem)i;
		    v = vi.inflate(R.layout.about_section_item, null);
		    final TextView title = (TextView)v.findViewById(R.id.firstLine);
		    final TextView subtitle = (TextView)v.findViewById(R.id.secondLine);
		     
		     
		    if (title != null) 
		     title.setText(ei.title);
		    if(subtitle != null)
		     subtitle.setText(ei.subtitle);
		   }
		  }
		  return v;
	}

	
	
}
