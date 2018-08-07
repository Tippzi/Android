package com.application.tippzi.Service;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by E on 12/11/2017.
 */

public class MyAppClass extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }
}
