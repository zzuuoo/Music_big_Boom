package com.yezi.zuo.music_big_boom;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuo on 2016/10/3.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity :activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
