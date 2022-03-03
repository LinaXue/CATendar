package com.example.firebase;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;//

public class MyMultiDex extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context context){
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
