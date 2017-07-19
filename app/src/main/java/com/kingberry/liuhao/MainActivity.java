package com.kingberry.liuhao;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.kingberry.liuhao.drag.DeleteItemInterface;
import com.kingberry.liuhao.drag.DeleteZone;
import com.kingberry.liuhao.drag.DragController;
import com.kingberry.liuhao.drag.DragLayer;
import com.kingberry.liuhao.drag.DragSource;
import com.kingberry.liuhao.drag.DraggableLayout;
import com.kingberry.liuhao.drag.ScrollController;

import java.util.Collections;
import java.util.List;

import static com.kingberry.liuhao.AppUtils.mDATA_NAME;
import static com.kingberry.liuhao.AppUtils.strFirstFlag;
import static com.kingberry.liuhao.AppUtils.strPkgs;

/**
 * description: 
 * autour: liuhao
 * date: 2017/7/17 12:10
 * update: 2017/7/17
 * version: a
 * */
public class MainActivity extends Activity implements ScrollController.OnPageChangeListener, DragController.DraggingListener, DeleteItemInterface, View.OnLongClickListener, DemoAdapter.ItemDragListener{

    public static final String TAG="MainActicity";

    /*判断是否为第一次登陆*/
    private boolean isFirstLoad=true;

    RecyclerView mRecyclerView = null;
    CircleIndicator mIndicator = null;

    //ArrayList<ResolveInfo> mList = new ArrayList<>();

    PackageManager pm=null;

    private HorizontalPageLayoutManager horizhontalPageLayoutManager;
    ScrollController mScrollController = new ScrollController();

    private DemoAdapter mAdapter = null;
    private int indicatorNumber;

    private DragLayer mDragLayer;
    private DeleteZone mDeleteZone;
    private DragController mDragController;
    //private LinearLayout btnLayout = null;

    //行
    public static int mRow = 0;
    //列
    public static int mColumn = 0;

    //每页显示的最大条目总数
    public int pageSize = 0;

    //是否可拖拽
    private boolean isEnableDrag = true;

    private Handler mHandler=new Handler();

    /**
     * @Title: getDpiInfo
     * @Description: 获取手机的屏幕密度DPI
     * @param
     * @return void
     */
    private void getDpiInfo() {
        // TODO Auto-generated method stub
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        MyParamsCls.Width = metrics.widthPixels;
        MyParamsCls.Height = metrics.heightPixels;
    }


    private void updateIncatorNum() {
        int oldNum = indicatorNumber;

        int endPageIndex = oldNum -1;
        boolean isEnd = mScrollController.getCurrentPageIndex() == endPageIndex ? true : false;

        //refresh indicatorNumber
        indicatorNumber = (MyParamsCls.mAppList.size() / pageSize) + (MyParamsCls.mAppList.size() % pageSize == 0 ? 0 : 1);
        mIndicator.setNumber(indicatorNumber);

        if(indicatorNumber == oldNum + 1 && isEnd){
            mScrollController.arrowScroll(false);
        }else if(indicatorNumber == oldNum - 1 && isEnd){
            mScrollController.arrowScroll(true);
        }
    }

    /***********************************************************************************
    public void clickRemove(View view){
        mList.remove(mList.size() -1);
        mAdapter.notifyDataSetChanged();

        updateIncatorNum();
    }

        public void clickAdd(View view){
        AppItem item = new AppItem("debug", R.mipmap.launcher2);
        item.itemPos = mList.size();
        mList.add(mList.size(), item);
        mAdapter.notifyDataSetChanged();

        updateIncatorNum();
    }
     ************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_demo);

        pm = MainActivity.this.getPackageManager();

        //btnLayout = (LinearLayout) findViewById(R.id.layout_btn);
        //btnLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();

        initDrag();

        initView();

    }



    @Override
    protected void onPause() {
        super.onPause();


        //Log.e(TAG,"onPause get pks="+sp.getString(strPkgs,""));
        AppUtils.saveData(MainActivity.this);

        //add by liuhao 0718 end ********************************
    }

    //    private void initDebug() {
//        int size = 50;
//        for (int i = 1; i <= size; i++) {
//            int resId = getResources().getIdentifier("face_" + i,
//                    "mipmap", getPackageName());
//            AppItem item = new AppItem("item"+i, resId);
//            item.itemPos = i -1;
//            mList.add(item);
//        }
//    }

    private void initData(){

        SharedPreferences sp=getSharedPreferences(mDATA_NAME, Activity.MODE_PRIVATE);
        isFirstLoad=sp.getBoolean(strFirstFlag,true);

        if(isFirstLoad){
            List<ResolveInfo> apps=AppUtils.getAllApps(MainActivity.this);
            //List<PackageInfo> apps=getAllApps(MainActivity.this);
            int  i=0;
            for (ResolveInfo pkg : apps){

                AppItem appInfo=new AppItem();
                appInfo.setAppIcon(pkg.activityInfo.loadIcon(pm));
                appInfo.setAppName((String) pkg.activityInfo.loadLabel(pm));
                appInfo.setPkgName(pkg.activityInfo.packageName);
                appInfo.setAppMainAty(pkg.activityInfo.name);
                appInfo.itemPos=i;
                MyParamsCls.mAppList.add(appInfo);
                i++;
            }
        }
        else{

            //Log.e(TAG,"*********NO FIRST*********");
            MyParamsCls.mAppList.clear();
            //MyParamsCls.mAppList.removeAll(MyParamsCls.mAppList);

            MyParamsCls.appPkgs=sp.getString(strPkgs,"");

            //Log.e(TAG,"initData get pks="+sp.getString(strPkgs,""));

            String[] pksArray=MyParamsCls.appPkgs.split(";");

            for (int i = 0; i < pksArray.length; i++) {

//                Log.e(TAG,"pksArray["+i+"] = "+pksArray[i]);
                //根据包名取得应用全部信息ResolveInfo
                ResolveInfo resolveInfo = AppUtils.findAppByPackageName(MainActivity.this,pksArray[i]);

                AppItem appInfo=new AppItem();
                appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
                appInfo.setAppName((String) resolveInfo.activityInfo.loadLabel(pm));
                appInfo.setPkgName(resolveInfo.activityInfo.packageName);
                appInfo.setAppMainAty(resolveInfo.activityInfo.name);
                appInfo.itemPos=i;
                MyParamsCls.mAppList.add(i,appInfo);
            }
        }

    }



    public void onPageChange(int index) {
        mIndicator.setOffset(index);
    }

    private void initDrag() {
        mDragLayer = (DragLayer) findViewById(R.id.demo_draglayer);
        mDeleteZone = (DeleteZone) findViewById(R.id.demo_del_zone);
        mDragController = new DragController(this);

        //是为了把dragLayer里面的触摸、拦截事件传给dragController
        //把很多能力交给dragController处理
        mDragLayer.setDragController(mDragController);
        //设置监听
        mDragLayer.setDraggingListener(MainActivity.this);

        if (mDeleteZone != null) {
            mDeleteZone.setOnItemDeleted(MainActivity.this);
            mDeleteZone.setEnabled(true);
            mDragLayer.setDeleteZoneId(mDeleteZone.getId());
        }

        mDragController.setDraggingListener(mDragLayer);
        mDragController.setScrollController(mScrollController);
    }

    @Override
    public boolean onLongClick(View view) {
        if (!view.isInTouchMode() && isEnableDrag) {
            return false;
        }

        Lg.d("onLongClick ********************* Drag started");
        DragSource dragSource = (DragSource) view;
        mDragController.startDrag(view, dragSource, dragSource, DragController.DRAG_ACTION_MOVE);

        return true;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
        AppItem sourceItem=((DraggableLayout)source).getItem();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(sourceItem.getPkgName(), PackageManager.MATCH_UNINSTALLED_PACKAGES);
            Log.e(TAG, "appInfo.flags :" + appInfo.flags);
            //系统应用
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                mDeleteZone.setVisibility(View.GONE);
//                Toast.makeText(MainActivity.this, "系统应用，不能卸载 ！", Toast.LENGTH_SHORT).show();
//                return;
            } else {
                if (appInfo.packageName.contains("com.kingberry.liuhao")) {
                    mDeleteZone.setVisibility(View.GONE);
//                    Toast.makeText(MainActivity.this, "应用 ：" + appInfo.loadLabel(pm) + " 不能被卸载！", Toast.LENGTH_SHORT).show();
//                    return;
                }else {
                    mDeleteZone.setVisibility(View.VISIBLE);
                }
            }
        }catch (PackageManager.NameNotFoundException e) {
           // Toast.makeText(MainActivity.this,"找不到该应用~",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
      //  mDeleteZone.setVisibility(View.VISIBLE);
        //btnLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDragEnd() {
        mDeleteZone.setVisibility(View.GONE);
       // btnLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void itemDeleted(DragSource source) {

    }

    private void initView() {

        Log.e(TAG,"mList size = "+MyParamsCls.mAppList.size());

        /*
        *************************************************************************************
        ****************************动态设置recyleView的列数 START****************************
         *************************************************************************************/
        //获取屏幕长宽像素信息
        getDpiInfo();

        mColumn = (int) Math.floor(MyParamsCls.Width / 200);
        mRow = (int) Math.floor(MyParamsCls.Height / 200);

        pageSize = mRow * mColumn ;

        Log.e(TAG,"initView -> mColumn ="+mColumn+"  mRow = "+mRow +"  pageSize = "+pageSize);
         /*
        *************************************************************************************
        ****************************动态设置recyleView的列数  END ****************************
         *************************************************************************************/

        mRecyclerView = (RecyclerView) findViewById(R.id.demo_listview);

        //为recyclerView添加间距
        SpacesItemDecoration mItemDecoration=new SpacesItemDecoration(MainActivity.this,2,R.color.color_777572);
        mRecyclerView.addItemDecoration(mItemDecoration);

        mIndicator = (CircleIndicator) findViewById(R.id.demo_indicator);
        mAdapter = new DemoAdapter(MyParamsCls.mAppList, this);
        mAdapter.setLongClickListener(this);
        mAdapter.setDragListener(this);
        mRecyclerView.setAdapter(mAdapter);

        horizhontalPageLayoutManager = new HorizontalPageLayoutManager(mRow, mColumn, this);
        horizhontalPageLayoutManager.setDragLayer(mDragLayer);
        indicatorNumber = (MyParamsCls.mAppList.size() / pageSize) + (MyParamsCls.mAppList.size() % pageSize == 0 ? 0 : 1);

        mRecyclerView.setLayoutManager(horizhontalPageLayoutManager);

        //添加分页
        mScrollController.setUpRecycleView(mRecyclerView);
        mScrollController.setOnPageChangeListener(this);

        //添加分页指示器--圆形
        mIndicator.setNumber(indicatorNumber);

        mDragLayer.setDragView(mRecyclerView);
    }

    @Override
    public void onDragStarted(View source) {
        Log.e( TAG,"onDragStarted soure="+source);
    }

    @Override
    public void onDropCompleted(View source, View target, boolean success) {
        Log.e(TAG,"========onDropCompleted success : " + success);

        if (success && (source != target)) {
            final AppItem sourceItem = ((DraggableLayout) source).getItem();
            //删除操作
            if (target instanceof DeleteZone) {
                if(sourceItem == null){
                    Log.e(TAG,"sourceItem is null in delete action !!!");

                    return;
                }

                if (sourceItem.isDeletable()) {
                    if(MyParamsCls.mAppList.contains(sourceItem)){

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        unstallApp(sourceItem.getPkgName());
                                        MyParamsCls.mAppList.remove(sourceItem);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).start();

                    }
                } else {
                    Toast.makeText(MainActivity.this, "不能删除这个条目！", Toast.LENGTH_SHORT).show();
                }
            }
            //item之间的替换操作
            else {
                if(sourceItem == null){
                    Lg.e("sourceItem is null in replace action !!!");
                    return;
                }

                AppItem targetItem = ((DraggableLayout) target).getItem();
                if(targetItem == null){
                    Lg.e("targetItem is null in replace action !!!");
                    return;
                }

                executeItemReplaceAction(sourceItem, targetItem);
            }
        }

        if(mDragLayer.getDraggingListener() != null){
            mDragLayer.getDraggingListener().onDragEnd();
        }
    }

    private void executeItemReplaceAction(AppItem sourceItem, AppItem targetItem) {

        //来源item信息
        int sourcePos = sourceItem.itemPos;

        //目标item位置
        int targetPos = targetItem.itemPos;

        Lg.d("sourcePos: " + sourcePos + " targetPos: " + targetPos);
        //位置交换
        Collections.swap(MyParamsCls.mAppList, sourcePos, targetPos);
        refreshItemList();
        mAdapter.notifyDataSetChanged();
    }

    private void refreshItemList(){
        for(int i = 0; i < MyParamsCls.mAppList.size(); i++){
            MyParamsCls.mAppList.get(i).itemPos = i;
        }
    }

    //卸载应用程序
    public void unstallApp(String packageName){
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:"+packageName));
        MainActivity.this.startActivity(uninstall_intent);
    }

}
