package com.example.expandlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyObjectGroup {

	private final LinkedList<String> groupList = new LinkedList<String>();
	private final Map<String, List<MyObject>> itemList = new HashMap<String, List<MyObject>>();
	public MyObjectGroup() {
		
	}
	
	public String getGroup(int position) {
		String groupString = null;
		if(position<0 || position>=groupList.size()) {
			groupString = "";
		} else {
			groupString = groupList.get(position);
		}
		return groupString;
	}
	
	public MyObject getItem(int groupPosition, int childPostion) {
		MyObject mo = null;
		if(groupPosition>=0 && childPostion>=0 && groupPosition<groupList.size()) {
			String group = groupList.get(groupPosition);
			List<MyObject> childList = itemList.get(group);
			if(childList!=null && childPostion<childList.size()) {
				mo = childList.get(childPostion);
			}
		}
		return mo;
	}
	
	public int getGroupCount() {
		return groupList.size();
	}
	
	public int getChildCount(int groupPosition) {
		String groupName = groupList.get(groupPosition);
		List<MyObject> childList = itemList.get(groupName);
		return (childList==null) ? 0 : childList.size();
	}
	
	public void addItem(String groupName, MyObject item) {
		List<MyObject> list = itemList.get(groupName);
		if(list == null) {
			String str = groupName;
			list = new ArrayList<MyObject>();
			if(str != null) {
				groupList.add(groupName);
				itemList.put(groupName, list);
			} else {
				groupList.add("#");
				itemList.put("#", list);
			}
			Collections.sort(groupList);
		}
		list.add(item);
	}
	
	public void clear() {
		groupList.clear();
		itemList.clear();
	}
	
	public LinkedList<String> getGroupList() {
		return groupList;
	}
	
	public void addSearchGroupFlag() {
		groupList.addFirst("*");
	}
	
	public Map<String, List<MyObject>> getItemList() {
		return itemList;
	}
}










