package com.kingberry.liuhao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import com.kingberry.liuhao.AppItem;
import com.kingberry.liuhao.AppUtils;
import com.kingberry.liuhao.MyParamsCls;

/**
 * Created by Administrator on 2017/7/19.
 */

public class AppInstallStateReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {

        PackageManager pm = context.getPackageManager();

        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();

            ResolveInfo resolveInfo = AppUtils.findAppByPackageName(context,packageName);

            AppItem appInfo=new AppItem();
            appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
            appInfo.setAppName((String) resolveInfo.activityInfo.loadLabel(pm));
            appInfo.setPkgName(resolveInfo.activityInfo.packageName);
            appInfo.setAppMainAty(resolveInfo.activityInfo.name);
            appInfo.itemPos=MyParamsCls.mAppList.size();
            MyParamsCls.mAppList.add(appInfo);

            AppUtils.saveData(context);

            Log.e(TAG, packageName +"--------安装成功 itemPos" +appInfo.itemPos+ " count :"+MyParamsCls.mAppList.size());
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();

            ResolveInfo resolveInfo = AppUtils.findAppByPackageName(context,packageName);

            AppItem appInfo=new AppItem();
            appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
            appInfo.setAppName((String) resolveInfo.activityInfo.loadLabel(pm));
            appInfo.setPkgName(resolveInfo.activityInfo.packageName);
            appInfo.setAppMainAty(resolveInfo.activityInfo.name);
            appInfo.itemPos=MyParamsCls.mAppList.size();
            MyParamsCls.mAppList.add(appInfo);

            AppUtils.saveData(context);

            Log.e(TAG, "--------替换成功" + packageName);

        }
        else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();

//            ResolveInfo resolveInfo = AppUtils.findAppByPackageName(context,packageName);
//
//            AppItem appInfo=new AppItem();
//            appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
//            appInfo.setAppName((String) resolveInfo.activityInfo.loadLabel(pm));
//            appInfo.setPkgName(resolveInfo.activityInfo.packageName);
//            appInfo.setAppMainAty(resolveInfo.activityInfo.name);
//            appInfo.itemPos=MyParamsCls.mAppList.size();
//            MyParamsCls.mAppList.remove(appInfo);

            Log.e(TAG, packageName+"--------卸载成功");
        }
    }
}
