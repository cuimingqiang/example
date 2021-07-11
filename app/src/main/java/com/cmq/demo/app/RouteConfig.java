package com.cmq.demo.app;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cmq.base.RouteMapping;

import kotlin.annotation.Retention;

public interface RouteConfig {

    @RouteMapping("/dex/main")
    void startTestDexActivity();

    @RouteMapping("/skin/main")
    void startTestSkinActivity();

    @RouteMapping("/test/av")
    void startTestAvActivity();
}
