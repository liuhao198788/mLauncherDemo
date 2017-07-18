package com.kingberry.liuhao;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingberry.liuhao.drag.DragListener;
import com.kingberry.liuhao.drag.DraggableLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/17.
 */

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.BaseViewHolder> implements DragListener {

    public static final String TAG="DemoAdapter";

    private ArrayList<AppItem> mList = new ArrayList<>();
    private Context mContext;

    public DemoAdapter(ArrayList<AppItem> list, Context context){
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new BaseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        AppItem appItem = mList.get(position);
        holder.tv_title.setText(appItem.getAppName());
        holder.icon.setImageDrawable(appItem.getAppIcon());

        if(longClickListener != null){
            holder.layout.setOnLongClickListener(longClickListener);
        }
        if(clickListener != null){

            holder.layout.setOnClickListener(clickListener);
        }

        DraggableLayout layout = (DraggableLayout) holder.layout;
        AppItem item = mList.get(position);
        layout.setItem(item);
        layout.setImage(holder.icon);
        layout.setText(holder.tv_title);
        layout.canDelete(item.isDeletable());
        layout.setDragListener(this);

        layout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                AppItem appItem = mList.get(position);
                // info = (DragView)parent.getItemAtPosition(position);

                // 应用的包名
                String pkg = appItem.getPkgName();

                Log.e(TAG,"pkg = "+pkg);
                //应用的主Activity
                String cls = appItem.getAppMainAty();
                ComponentName componentName = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                mContext.startActivity(intent);
            }
        });

        Log.e("DemoAdapter","appName = "+holder.tv_title.getText());
}

    private ItemDragListener dragListener = null;
    public interface ItemDragListener{
        void onDragStarted(View source);
        void onDropCompleted(View source, View target, boolean success);
    }

    public void setDragListener(ItemDragListener listener){
        dragListener = listener;
    }

    @Override
    public void onDragStarted(View source) {
        if(dragListener != null){
            dragListener.onDragStarted(source);
        }
    }

    @Override
    public void onDropCompleted(View source, View target, boolean success) {
        if(dragListener != null){
            dragListener.onDropCompleted(source, target, success);
        }
    }

    private View.OnLongClickListener longClickListener = null;
    public void setLongClickListener(View.OnLongClickListener listener){
        longClickListener = listener;
    }

    private View.OnClickListener clickListener = null;
    public void setClickListener(View.OnClickListener listener){
        clickListener = listener;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tv_title;
        LinearLayout layout;

        public BaseViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
        }
    }

    //define interface
    public static interface OnAdapterItemClickListener {
        void onAdapterItemClick(View view , int position);
    }
}
