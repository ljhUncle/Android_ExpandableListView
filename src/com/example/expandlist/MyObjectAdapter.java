package com.example.expandlist;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class MyObjectAdapter extends BaseExpandableListAdapter {

	private MyObjectGroup myObjectGroup;
	private Context mContext;
	
	public MyObjectAdapter(Context context, MyObjectGroup myObjcetgGroup) {
		super();
		this.myObjectGroup = myObjcetgGroup;
		mContext = context;
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ItemExpandListViewChild view = null;
		if(convertView == null) {
			view = new ItemExpandListViewChild(mContext);
		} else {
			view = (ItemExpandListViewChild)convertView;
		}
		MyObject mo = myObjectGroup.getItem(groupPosition, childPosition);
		view.setTag(mo);
		view.setIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_non_image_bg));
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if(myObjectGroup.getGroup(groupPosition).equals("*")) {
			return 0;
		} else {
			return myObjectGroup.getChildCount(groupPosition);
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return myObjectGroup.getGroup(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return myObjectGroup.getGroupCount();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ItemExpandListViewGroup view;
		if(convertView == null) {
			view = new ItemExpandListViewGroup(mContext);
		} else {
			view = (ItemExpandListViewGroup)convertView;
		}
		view.setData(myObjectGroup.getGroup(groupPosition));
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public MyObjectGroup getMyObjectGroup() {
		return myObjectGroup;
	}

}
