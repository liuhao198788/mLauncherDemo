package com.kingberry.liuhao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.kingberry.liuhao.MyParamsCls.appPkgs;

/**
 * Created by Administrator on 2017/7/19.
 */

public class AppUtils {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor ed;
    public static final String mDATA_NAME="mSaveData";
    public static final String strFirstFlag="isFirstLoad";
    public static final String strPkgs="PKGS";

    //根据包名取得应用全部信息ResolveInfo
    public  static ResolveInfo findAppByPackageName(Context context, String mPackageName)
    {
        ResolveInfo newAppInfo = null;
        // 用于存放临时应用程序
        List<ResolveInfo> tmpInfos=new ArrayList<ResolveInfo>();

        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(mPackageName);
        tmpInfos = pm.queryIntentActivities(mainIntent, 0);
        newAppInfo = tmpInfos.get(0);

        return newAppInfo;
    }

    /**
     * @Title: getAllApps @Description: 加载所有app @param 参数 @return void
     * 返回类型 @throws
     */
     public static List<ResolveInfo> getAllApps(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allApps = new ArrayList<ResolveInfo>();
        allApps=context.getPackageManager().queryIntentActivities(intent, 0);
        // 调用系统排序，根据name排序
        // 该排序很重要，否则只能显示系统应用，不能显示第三方应用
        // 否则，输出的顺序容易乱掉
        Collections.sort(allApps, new ResolveInfo.DisplayNameComparator(context.getPackageManager()));

        return allApps;
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

    public static void saveData(Context context){

        //保存顺序
        int appCount=MyParamsCls.mAppList.size();
        appPkgs="";

        for(int i=0;i<appCount;i++){
            AppItem item=MyParamsCls.mAppList.get(i);
            // TODO
            appPkgs+=item.getPkgName();
            appPkgs+=";";
        }

        //保存记录，是否为第一次登陆
        //add by liuhao 0718 start *******************************
        sp = context.getSharedPreferences(mDATA_NAME, MODE_PRIVATE);
        ed = sp.edit();

        ed.clear();

        ed.putBoolean(strFirstFlag, false);
        ed.putString(strPkgs, appPkgs);

        ed.commit();

        Log.e(TAG,"onPause appCount="+appCount);
    }

}
