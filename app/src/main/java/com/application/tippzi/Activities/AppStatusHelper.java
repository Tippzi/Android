package com.application.tippzi.Activities;

import android.util.Log;

public class AppStatusHelper {
    private static final String TAG = AppStatusHelper.class.getSimpleName();

    private static int mNumOfActivitiesInOnStarttoOnStopLifeCycle=0;

    public static void onStart()
    {
        mNumOfActivitiesInOnStarttoOnStopLifeCycle++;
        Log.d(TAG,"num->"+mNumOfActivitiesInOnStarttoOnStopLifeCycle+"\tisOnBackground->"+(mNumOfActivitiesInOnStarttoOnStopLifeCycle==0));
    }
    public static void onStop()
    {
        mNumOfActivitiesInOnStarttoOnStopLifeCycle--;
        Log.d(TAG,"num->"+mNumOfActivitiesInOnStarttoOnStopLifeCycle+"\tisOnBackground->"+(mNumOfActivitiesInOnStarttoOnStopLifeCycle==0));
    }

    public static boolean isAppInBackground()
    {
        Log.d(TAG,"num->"+mNumOfActivitiesInOnStarttoOnStopLifeCycle+"\tisOnBackground->"+(mNumOfActivitiesInOnStarttoOnStopLifeCycle==0));
        return mNumOfActivitiesInOnStarttoOnStopLifeCycle==0;
    }

}