package com.lixd.costom.view;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    public static Context sContxt;

    @Override
    public void onCreate() {
        super.onCreate();
        sContxt = this;
    }
}
