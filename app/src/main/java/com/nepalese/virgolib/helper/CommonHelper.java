package com.nepalese.virgolib.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.nepalese.virgolib.config.Constans;
import com.nepalese.virgolib.data.bean.AudioItem;
import com.nepalese.virgolib.data.dbhelper.DBHelper;
import com.nepalese.virgolib.data.dbhelper.DBManager;
import com.nepalese.virgosdk.Util.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.WorkerThread;

/**
 * Created by Administrator on 2022/3/9.
 * Usage: 临时通用工具类，后面与sdk合并
 */

public class CommonHelper {
    private static final String TAG = "CommonHelper";
    /**
     * activity 跳转到设置页请求某权限
     * @param activity
     * @param action 权限
     * @param code 请求码
     */
    public static void jump4Permission(Activity activity, String action, int code){
        Intent intent = new Intent();
        intent.setAction(action);
//        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, code);
    }

    /**
     * 跳转到设置应用详情页
     * @param activity
     */
    public static void jump2AppDetail(Activity activity) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    /**
     * 获取当前可用内存
     * @param context
     * @return
     */
    public static long getFreeMem(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return info.availMem;
    }

    /**
     * 当前内存详细信息
     * @param context
     * @return
     */
    public static String getMemInfo(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        return JsonUtil.toJson(info);
    }

    /**
     * 同步本地指定路径下音频文件： Music, .mp3
     * @param dir "Music"
     * @return
     */
    @WorkerThread
    public static List<AudioItem> synLocalMusic(String dir) {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), dir);
        if(root.exists()){
            File[] files = root.listFiles();
            if(files==null || files.length<1){
                return null;
            }

            List<AudioItem> list = new ArrayList<>();
            for(File f: files){
                if(f.isFile() && f.getName().endsWith(".mp3")){
                    Log.d(TAG, "synLocalMusic: " + f.getName());
                    list.add(new AudioItem(f.getPath()));
                }
            }

            return list;
        }
        return null;
    }

    public static List<AudioItem> getAudioItems(Context context){
        List<AudioItem> list;
        DBManager dbManager = DBHelper.getInstance(context).getDbManager();
        list = dbManager.getAllAudioItem();
        if(list==null || list.isEmpty()){
            list = synLocalMusic(Constans.DEFAULT_SYN_MUSIC_DIR);
            if(list!=null && !list.isEmpty()){
                dbManager.addAudioItems(list);
            }
        }
        return list;
    }
}
