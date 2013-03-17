package com.example.expandlist;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ExpandableListView.OnGroupClickListener;

public class ItemExpandListView extends FrameLayout {
	
	protected ExpandableListView mExpandableListView;
	//类别分组
	private ItemExpandListViewGroup mItemExpandListViewGroup;
	//类别分组的高度
	private int mItemExpandListViewGroupHight = 0;
	private int mCurrentShowGroupId;
	private MyObjectAdapter myObjectAdapter;
	
	private int mFirstVisibleItem = -1;
	private int mVisibleItemCount = -1;
	private int mLastVisibleItem = -1;
	private int mLastVisibleItemCount = -1;
	private boolean isScrolling = false;
	private ItemCategoryExpandListViewListener mItemCategoryExpandListViewListener;
	private Options iconOptions;
	
	public ItemExpandListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupView();
	}
	public ItemExpandListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}
	public ItemExpandListView(Context context) {
		super(context);
		setupView();
	}

	private void setupView() {
		mItemExpandListViewGroupHight = (int)getDimenById(R.dimen.item_expand_listview_group_height);
		LayoutInflater inFlater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inFlater.inflate(R.layout.item_expand_listview, this);
		mItemExpandListViewGroup = (ItemExpandListViewGroup) findViewById(R.id.item_category_expand_listview_group);
		mExpandableListView = (ExpandableListView) findViewById(R.id.item_category_expand_listview);
		iconOptions = new Options();
		iconOptions.height = (int)getDimenById(R.dimen.item_expand_listview_child_icon_height);
		iconOptions.width = (int)getDimenById(R.dimen.item_expand_listview_child_icon_width);
		setupListener();
	}
	
	private float getDimenById(int dimenId) {
		return getContext().getResources().getDimension(dimenId);
	}
	
	private void setupListener() {
		mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				// 防止group可以点击
				return true;
			}
		});
		
		mExpandableListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int ptp = view.pointToPosition(0, 0);
				if(ptp != AdapterView.INVALID_POSITION) {
					long pos = mExpandableListView.getExpandableListPosition(ptp);
					int groupPos = ExpandableListView.getPackedPositionGroup(pos);
					int childPos = ExpandableListView.getPackedPositionChild(pos);
					int childCount = mExpandableListView.getExpandableListAdapter().getChildrenCount(groupPos);
					String groupName = (String)mExpandableListView.getExpandableListAdapter().getGroup(groupPos);
					loadImage(mFirstVisibleItem, mVisibleItemCount);
					if(groupName.equals("*")) {
						mItemExpandListViewGroup.setVisibility(View.GONE);
					} else {
						Rect r = new Rect();
						View topChildview = mExpandableListView.getChildAt(0);
						topChildview.getHitRect(r);
						if(childPos == (childCount-1)) {
							if(r.bottom <= mItemExpandListViewGroupHight) {
								mItemExpandListViewGroup.scrollTo(0, mItemExpandListViewGroupHight-r.bottom);
							} else {
								mItemExpandListViewGroup.scrollTo(0, 0);
							}
						} else if(childPos==0 || childPos==(childCount-2) || (childPos==-1 && mItemExpandListViewGroup instanceof ItemExpandListViewGroup)) {
							mItemExpandListViewGroup.scrollTo(0, 0);
						}
						mItemExpandListViewGroup.setVisibility(View.VISIBLE);
						mItemExpandListViewGroup.setData((String)mExpandableListView.getExpandableListAdapter().getGroup(groupPos));
					}
				}
			}
		});
		testData();
	}
	
	public ExpandableListView getExpandableListView() {
		return mExpandableListView;
	}
	
	private void loadImage(final int firstItem, final int visibleCount) {
		mLastVisibleItem = firstItem;
		mLastVisibleItemCount = visibleCount;
		if(mLastVisibleItem==mFirstVisibleItem && mLastVisibleItemCount==mVisibleItemCount) {
			return;
		}
		if(!isScrolling) {
			isScrolling = true;
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... arg0) {
					while(isScrolling) {
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(mLastVisibleItem!=mFirstVisibleItem || mLastVisibleItemCount!=mVisibleItemCount) {
							mFirstVisibleItem = mLastVisibleItem;
							mVisibleItemCount = mLastVisibleItemCount;
						} else {
							break;
						}
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					setListViewChildIcon(true);
					super.onPostExecute(result);
				}
				
			}.execute();
		}
	}
	
	private void setListViewChildIcon(boolean setlistener) {
		for(int i=0; i<mVisibleItemCount; i++) {
			if(mExpandableListView.getChildAt(i) instanceof ItemExpandListViewChild) {
				final ItemExpandListViewChild item = (ItemExpandListViewChild) mExpandableListView.getChildAt(i);
				item.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_non_image_bg));
				
				/*if(item.getTag() instanceof Object) {
					final Object obj = item.getTag();
					item.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_non_image_bg));
				}*/
			}
		}
		isScrolling = false;
	}
	
	private void setListData(MyObjectGroup myObjectGroup) {
		mFirstVisibleItem = -1;
		mVisibleItemCount = -1;
		mLastVisibleItem = -1;
		mLastVisibleItemCount = -1;
		myObjectAdapter = new MyObjectAdapter(getContext(), myObjectGroup);
		mExpandableListView.setAdapter(myObjectAdapter);
		
		if(myObjectGroup.getGroupCount()==0) {
			mItemExpandListViewGroup.setVisibility(View.INVISIBLE);
		} else {
		}
		for(int i=0; i<myObjectAdapter.getGroupCount(); i++) {
			mExpandableListView.expandGroup(i);
		}
	}
	
	private void testData() {
		MyObjectGroup mog = new MyObjectGroup();
		//mog.addItem("*", new MyObject());
		for(int i=0; i<=5; i++) {
			mog.addItem("adf", new MyObject());
		}
		for(int i=0; i<=5; i++) {
			mog.addItem("bdf", new MyObject());
		}
		for(int i=0; i<=5; i++) {
			mog.addItem("cdf", new MyObject());
		}
		for(int i=0; i<=5; i++) {
			mog.addItem("ddf", new MyObject());
		}
		setListData(mog);
	}
	
	public interface ItemCategoryExpandListViewListener {
		public void onChildClick(int groupPostion, int childPosition, Object obj);
	}
	public void setItemCategoryExpandListViewListener(ItemCategoryExpandListViewListener itemCategoryExpandListViewListener) {
		mItemCategoryExpandListViewListener = itemCategoryExpandListViewListener;
	}
	
	public static class Options {
		public float width = -1;	// pixel
		public float height = -1;	// pixel
		public int ratio = -1;		// 缩放比例

		@Override
		public String toString() {
			return "Options --> witdh:" + width + " height:" + height + " ratio:" + ratio;
		}

	}
}
