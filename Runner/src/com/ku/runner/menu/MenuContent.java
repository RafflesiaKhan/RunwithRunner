package com.ku.runner.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R;
import android.app.Activity;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MenuContent {
	public static final String MENUITEM_EXIT_ID = "-1";
	public static String MENUITEM_OVERVIEW_ID="1";
	public static String MENUITEM_RUNNER_ID="2";
	public static String MENUITEM_DETECTOR_ID="3";
	public static String MENUITEM_PROFILE_ID="4";
	public static String MENUITEM_ABOUT_ID="5";
	public static String MENUITEM_ACT_RECOG_ID="6";
	//public static String MENUITEM_PLAY_ID="7";
    /**
     * An array of sample (dummy) items.
     */
    public static List<MenuItem> ITEMS = new ArrayList<MenuItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, MenuItem> ITEM_MAP = new HashMap<String, MenuItem>();

    static {
        // Add all menu items. this (key, value) pari
        addItem(new MenuItem("1", "Overview"));
        addItem(new MenuItem("2", "Runner"));
        addItem(new MenuItem("3", "Detector"));
        addItem(new MenuItem("4", "Profile"));
        addItem(new MenuItem("5", "About"));
        addItem(new MenuItem("6", "Activity Recognition"));
        //addItem(new MenuItem(MENUITEM_PLAY_ID, "Run/Walk"));
        addItem(new MenuItem(MENUITEM_EXIT_ID, "Exit"));
    }

    private static void addItem(MenuItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MenuItem {
        public String id;
        public String content;
   

        public MenuItem(String id, String content) {
            this.id = id;
            this.content = content;
        }
        
   

        @Override
        public String toString() {
            return content;
        }
    }
}
