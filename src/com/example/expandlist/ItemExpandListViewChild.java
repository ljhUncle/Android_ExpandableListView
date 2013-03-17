package com.example.expandlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ItemExpandListViewChild extends RelativeLayout {

	private static final String TAG = "ItemListViewChild";
	private ImageView icon;

	public ItemExpandListViewChild(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupView();
	}

	public ItemExpandListViewChild(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}

	public ItemExpandListViewChild(Context context) {
		super(context);
		setupView();
	}

	private void setupView() {
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.item_expand_listview_child, this);
		icon = (ImageView) findViewById(R.id.item_list_view_child_image);
	}

	public void setIcon(Bitmap bitmap) {
		if (bitmap == null) {
			setDefaultIcon();
		} else {
			icon.setImageBitmap(bitmap);
		}
	}

	public void setDefaultIcon() {
		icon.setImageResource(R.drawable.ic_launcher);
	}

}
