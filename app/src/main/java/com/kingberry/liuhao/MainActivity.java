package kingberry.liuhao.launcher;

import java.util.List;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DragItemAdapter extends BaseAdapter {
	
	private String TAG="DragItemAdapter";
	
	private Context context ;
	private List<ResolveInfo> mList;
	
	private int mIndex; // 页数下标，标示第几页，从0开始
    private int mPargerSize;// 每页显示的最大的数量
	
	private DragGridView gridView;
	
	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;

	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 是否删除 */
	private boolean isDelete = false;
	/** 要删除的position */
	public int remove_position = -1;
	
	/**
	 * 指定隐藏的position
	 */
	private int hidePosition = -1;
	/** 是否可见 */
	boolean isVisible = true;

	private Handler mHandler = new Handler();
	
	public DragItemAdapter(Context context, List<ResolveInfo> apps,DragGridView gridView) {
		this.context = context;
		this.mList = apps;
		this.gridView = gridView;
	}
	
	public DragItemAdapter(Context context, List<ResolveInfo> apps,
            int mIndex, int mPargerSize) {
		this.context = context;
		this.mList = apps;
		this.mIndex=mIndex;
		this.mPargerSize=mPargerSize;
	}

	/**
     * 先判断数据及的大小是否显示满本页lists.size() > (mIndex + 1)*mPagerSize
     * 如果满足，则此页就显示最大数量lists的个数
     * 如果不够显示每页的最大数量，那么剩下几个就显示几个
     */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size() > (mIndex + 1) * mPargerSize ? 
                mPargerSize : (mList.size() - mIndex*mPargerSize);
	}

	@Override
	public Object getItem(int position) {
		if (mList != null && mList.size() != 0) {
			mList.get(position + mIndex * mPargerSize);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position + mIndex * mPargerSize;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holderView = null;
		View view = null;
		if (view == null) {
			holderView = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.item_square_layout, parent,false);
			holderView.appIcon = (ImageView) view.findViewById(R.id.appIcon);
			holderView.appName = (TextView) view.findViewById(R.id.appName);
			holderView.deleteIcon = (ImageView) view.findViewById(R.id.deleteIcon);

			LayoutParams mLayoutParams = holderView.appIcon.getLayoutParams();
			mLayoutParams.width = (int) (MyParamsCls.Width / 7);
			mLayoutParams.height = (int) (MyParamsCls.Width / 7);
			holderView.appIcon.setLayoutParams(mLayoutParams);

			view.setTag(holderView);
		}
		
		holderView = (ViewHolder)view.getTag();
		
		//重新确定position因为拿到的总是数据源，数据源是分页加载到每页的GridView上的
        final int pos = position + mIndex * mPargerSize;//假设mPageSiez

        ResolveInfo appInfo=mList.get(pos);
		holderView.appIcon.setImageDrawable(appInfo.activityInfo.loadIcon(context.getPackageManager()));
		holderView.appName.setText(appInfo.activityInfo.loadLabel(context.getPackageManager()));
		holderView.deleteIcon.setOnClickListener(new ImageView.OnClickListener() {
			public void onClick(View v) {
				
				//add by liuhao 0714 start
				//gridView.isDrag=false;
				//add by liuhao 0714 end
				
				mHandler.post(new Runnable() {
					public void run() {
						//删除应用，移出图标
						if (!MyParamsCls.isAnimaEnd) {
							return;
						}
						notifyDataSetChanged();
						Log.e(TAG,"onClick postion:"+position);
					//	gridView.deleteInfo(position);
						
						/*******************
						 * 开始删除应用
						 * *****************
						 */
						
						final ResolveInfo info=mList.get(position);
						AlertDialog dlg=new AlertDialog.Builder(context)
								.setTitle("要删除 "+"\""+info.activityInfo.loadLabel(context.getPackageManager())+"\"吗?")
								.setMessage("删除此应用将同时删除其数据")
								.setCancelable(true)
								.setOnCancelListener(new OnCancelListener(){
									public void onCancel(DialogInterface dialog) {
										dialog.dismiss();
//										return;
									}
								})
								.setPositiveButton("确认",  
				                new DialogInterface.OnClickListener() {  
				                    public void onClick(DialogInterface dialog, int whichButton) {  
				                    	unstallApp(info.activityInfo.packageName);
				                    }  
				                })		
								.setNegativeButton("取消",  
				                new DialogInterface.OnClickListener() {  
				                    public void onClick(DialogInterface dialog, int whichButton) { 
										dialog.dismiss();
//										return;
				                    }  
				                })
								.show();
								
					}
				});
			}
		});
		
//		if (position == getCount()-1){
//			if (convertView == null) {
//				convertView = view;
//			}
//			convertView.setEnabled(false);
//			convertView.setFocusable(false);
//			convertView.setClickable(false);
//		}
		
		if(remove_position == position){
			deletePostion(position);
		}
		if (!isDelete) {
			holderView.deleteIcon.setVisibility(View.GONE);
		}
		else
		{
			holderView.deleteIcon.setVisibility(View.VISIBLE);
		}
		
		if (hidePosition == position) {
			view.setVisibility(View.INVISIBLE);
		}else {
			view.setVisibility(View.VISIBLE);
		}
		
		return view;
	}
	
	//卸载应用程序  
	public void unstallApp(String packageName){  
	    Intent uninstall_intent = new Intent();  
	    uninstall_intent.setAction(Intent.ACTION_DELETE);  
	    uninstall_intent.setData(Uri.parse("package:"+packageName));  
	    context.startActivity(uninstall_intent);  
	}  
	
	public void setisDelete(boolean isDelete)
	{
		this.isDelete = isDelete;
	}

	/** 设置删除的position */
	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	/** 获取是否可见 */
	public boolean isVisible() {
		return isVisible;
	}

	/** 设置是否可见 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}
	/** 显示放下的ITEM */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}

	public void setHidePosition(int position) {
		// TODO Auto-generated method stub
		this.hidePosition = position;
		notifyDataSetChanged();
	}
	
	/**
	 * 删除某个position
	 * @param position
	 */
	public void deletePostion(int position)
	{
		mList.remove(position);
		hidePosition = -1;
		notifyDataSetChanged();
	}
	
	/**  
	* @Title: exchange  
	* @Description: 拖动变更排序 
	* @param @param dragPostion
	* @param @param dropPostion    参数  
	* @return void    返回类型  
	* @throws  
	*/  
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		ResolveInfo appInfo=mList.get(dragPostion);
		Log.e(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
		if (dragPostion < dropPostion) {
			mList.add(dropPostion + 1, appInfo);
			mList.remove(dragPostion);
		} else {
			mList.add(dropPostion, appInfo);
			mList.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}

	
	class ViewHolder
	{
		private TextView appName;
		private ImageView appIcon;
		private ImageView deleteIcon;
	}

}
