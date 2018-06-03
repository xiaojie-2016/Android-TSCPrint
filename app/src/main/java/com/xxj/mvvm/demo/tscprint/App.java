package com.xxj.mvvm.demo.tscprint;

import android.app.Application;

import com.xxj.mvvm.demo.tscprint.utils.Utils;
import com.xxj.mvvm.demo.tscprint.utils.usb.USBUtil;

/**
 * Created by 52492 on 05/24.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        USBUtil.getInstance().init(this);
    }
}
