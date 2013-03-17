package com.example.expandlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ItemExpandListViewGroup extends FrameLayout {
	
	private TextView mTitle;
	public ItemExpandListViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupView();
	}
	public ItemExpandListViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}
	public ItemExpandListViewGroup(Context context) {
		super(context);
		setupView();
	}

	private void setupView() {
		LayoutInflater inFlater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inFlater.inflate(R.layout.item_expand_listview_group, this);
		mTitle = (TextView) findViewById(R.id.item_category_group_title);
	}
	
	public String getTitle() {
		return mTitle.getText().toString();
	}
	
	public void setData(String data) {
		if(data.equals("*")) {
			mTitle.setVisibility(View.GONE);
		} else {
			mTitle.setVisibility(View.VISIBLE);
			mTitle.setText(data);
		}
	}
}
