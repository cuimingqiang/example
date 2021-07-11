package com.cmq.demo.app;

import com.cmq.base.RouteMapping;

public interface RouteConfig {

    @RouteMapping("/dex/main")
    void startTestDexActivity();

    @RouteMapping("/skin/main")
    void startTestSkinActivity();
}
