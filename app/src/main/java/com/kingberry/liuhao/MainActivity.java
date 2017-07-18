package com.kingberry.liuhao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * description: 
 * autour: liuhao
 * date: 2017/7/17 12:10
 * update: 2017/7/17
 * version: a
 * */
public class MainActivity extends Activity implements ScrollController.OnPageChangeListener, DragController.DraggingListener, DeleteItemInterface, View.OnLongClickListener, DemoAdapter.ItemDragListener{

    public static final String TAG="MainActicity";

    RecyclerView mRecyclerView = null;
    CircleIndicator mIndicator = null;
    ArrayList<AppItem> mList = new ArrayList<AppItem>();
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
        indicatorNumber = (mList.size() / pageSize) + (mList.size() % pageSize == 0 ? 0 : 1);
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

        Log.e(TAG,"onCreate");

        setContentView(R.layout.activity_demo);

        pm = MainActivity.this.getPackageManager();

        initData();

        initDrag();

        initView();

        //btnLayout = (LinearLayout) findViewById(R.id.layout_btn);
        //btnLayout.setVisibility(View.GONE);
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

        List<ResolveInfo> apps=getAllApps();

        //List<PackageInfo> apps=getAllApps(MainActivity.this);
        for (ResolveInfo pkg : apps){

            AppItem appInfo=new AppItem();
            appInfo.setAppIcon(pkg.activityInfo.loadIcon(pm));
            appInfo.setAppName((String) pkg.activityInfo.loadLabel(pm));
            appInfo.setPkgName(pkg.activityInfo.packageName);
            appInfo.setAppMainAty(pkg.activityInfo.name);

            mList.add(appInfo);
        }
    }
        /**
     * @Title: getAllApps @Description: 加载所有app @param 参数 @return void
     * 返回类型 @throws
     */
    private List<ResolveInfo> getAllApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allApps = new ArrayList<ResolveInfo>();
        allApps=getPackageManager().queryIntentActivities(intent, 0);
        // 调用系统排序，根据name排序
        // 该排序很重要，否则只能显示系统应用，不能显示第三方应用
        // 否则，输出的顺序容易乱掉
        Collections.sort(allApps, new ResolveInfo.DisplayNameComparator(getPackageManager()));

        return allApps;
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
        mDeleteZone.setVisibility(View.VISIBLE);
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

        Log.e(TAG,"mList size = "+mList.size());

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
        SpacesItemDecoration mItemDecoration=new SpacesItemDecoration(MainActivity.this,2,R.color.gap_line);
        mRecyclerView.addItemDecoration(mItemDecoration);

        mIndicator = (CircleIndicator) findViewById(R.id.demo_indicator);
        mAdapter = new DemoAdapter(mList, this);
        mAdapter.setLongClickListener(this);
        mAdapter.setDragListener(this);
        mRecyclerView.setAdapter(mAdapter);

        horizhontalPageLayoutManager = new HorizontalPageLayoutManager(mRow, mColumn, this);
        horizhontalPageLayoutManager.setDragLayer(mDragLayer);
        indicatorNumber = (mList.size() / pageSize) + (mList.size() % pageSize == 0 ? 0 : 1);

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
            AppItem sourceItem = ((DraggableLayout) source).getItem();
            //删除操作
            if (target instanceof DeleteZone) {
                if(sourceItem == null){
                    Log.e(TAG,"sourceItem is null in delete action !!!");

                    return;
                }

                if (sourceItem.isDeletable()) {
                    if(mList.contains(sourceItem)){
                        mList.remove(sourceItem);
                        mAdapter.notifyDataSetChanged();
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
        Collections.swap(mList, sourcePos, targetPos);
        refreshItemList();
        mAdapter.notifyDataSetChanged();
    }

    private void refreshItemList(){
        for(int i = 0; i < mList.size(); i++){
            mList.get(i).itemPos = i;
        }
    }


    /**
     *  @author liuhao
     *  @time 2017/7/17  16:21
     *  @describe  得到所有应用
     */
    public static List<PackageInfo> getAllPkgs(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);

            apps.add(pak);
            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            //
            int appFlag=pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM;

            /*          <=0则为自己装的程序，否则为系统工程自带
            ************************************************************************************
            * ****************得到是否为系统预装应用 或 手动安装的应用*****************************
            * **********************************************************************************
             */
            if (appFlag <= 0) {
                // 添加自己已经安装的应用程序
                apps.add(pak);
            }else{

            }

        }
        return apps;
    }


}
