package com.cmq.skin;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import com.cmq.skin.processor.BackgroundProcessor;
import com.cmq.skin.processor.DrawableBottomProcessor;
import com.cmq.skin.processor.DrawableLeftProcessor;
import com.cmq.skin.processor.DrawableRightProcessor;
import com.cmq.skin.processor.DrawableTopProcessor;
import com.cmq.skin.processor.ForegroundProcessor;
import com.cmq.skin.processor.TextColorProcessor;
import com.cmq.skin.processor.TextSizeProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;

public class SkinManager {
    private static final Map<Activity, SkinActivity> activityViewCache = new HashMap<>();
    private static final Map<String, AttrProcessor> attrProcessors = new HashMap<>();
    private static final ResourceManager resourceManager = new ResourceManager();

    @MainThread
    public static void applySkin(String resourcePath) throws SkinException {
        resourceManager.applySkin(resourcePath, "com.cmq.skin");
        for (Map.Entry<Activity, SkinActivity> entry : activityViewCache.entrySet()) {
            Activity activity = entry.getKey();
            if (activity instanceof AppCompatActivity) {
                Lifecycle.State state = ((AppCompatActivity) activity).getLifecycle().getCurrentState();
                //更新生命周期处于resume和pause状态的
                if (state == Lifecycle.State.STARTED || state == Lifecycle.State.RESUMED) {
                    entry.getValue().applySkin(resourceManager.newSkinResource(activity.getResources()), resourceManager.getPackageName());
                }
            }
        }
    }

    public static void reset() {
        try {
            resourceManager.applySkin(null, null);
            for (Map.Entry<Activity, SkinActivity> entry : activityViewCache.entrySet()) {
                Activity activity = entry.getKey();
                if (activity instanceof AppCompatActivity) {
                    Lifecycle.State state = ((AppCompatActivity) activity).getLifecycle().getCurrentState();
                    //更新生命周期处于resume和pause状态的
                    if (state == Lifecycle.State.STARTED || state == Lifecycle.State.RESUMED) {
                        entry.getValue().applySkin(null, null);
                    }
                }
            }
        } catch (SkinException e) {
            e.printStackTrace();
        }
    }


    //可以根据 SkinResourceImp 实现自己的扩展功能
    public static void init(Application application, ProcessorCallback callback) {
        attrProcessors.put("background", new BackgroundProcessor());
        attrProcessors.put("foreground", new ForegroundProcessor());
        attrProcessors.put("textColor", new TextColorProcessor());
        attrProcessors.put("textSize", new TextSizeProcessor());
        attrProcessors.put("drawableTop", new DrawableTopProcessor());
        attrProcessors.put("drawableBottom", new DrawableBottomProcessor());
        DrawableLeftProcessor drawableLeftProcessor = new DrawableLeftProcessor();
        attrProcessors.put("drawableLeft", drawableLeftProcessor);
        attrProcessors.put("drawableStart", drawableLeftProcessor);
        DrawableRightProcessor drawableRightProcessor = new DrawableRightProcessor();
        attrProcessors.put("drawableRight", drawableRightProcessor);
        attrProcessors.put("drawableEnd", drawableRightProcessor);
        if (callback != null) callback.addProcessor(attrProcessors);
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                //获取Theme
                int themeId = 0;
                SkinResource resources = null;
                try {
                    Method getThemeResId = ContextThemeWrapper.class.getDeclaredMethod("getThemeResId");
                    themeId = (int) getThemeResId.invoke(activity, new Object[]{});
                    Resources activityResources = activity.getResources();
                    AssetManager empty = AssetManager.class.getDeclaredConstructor().newInstance();

                    resources = new SkinResource(empty, activityResources);
                    Field mResources = ContextThemeWrapper.class.getDeclaredField("mResources");
                    mResources.setAccessible(true);
                    mResources.set(activity, resources);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                SkinActivity cache = new SkinActivity(attrProcessors, themeId, resources);
                LayoutInflater layoutInflater = LayoutInflater.from(activity);
                if (activity instanceof AppCompatActivity) {
                    AppCompatDelegate delegate = ((AppCompatActivity) activity).getDelegate();
                    try {
                        Field sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
                        sLayoutInflaterFactory2Field.setAccessible(true);
                        sLayoutInflaterFactory2Field.set(layoutInflater, new SkinLayoutInflater(layoutInflater, (LayoutInflater.Factory2) delegate, cache, attrProcessors));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Field sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
                        sLayoutInflaterFactory2Field.setAccessible(true);
                        sLayoutInflaterFactory2Field.set(layoutInflater, new SkinLayoutInflater(layoutInflater, null, cache, attrProcessors));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                activityViewCache.put(activity, cache);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                activityViewCache.get(activity).applySkin(resourceManager.newSkinResource(activity.getResources()), resourceManager.getPackageName());
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                activityViewCache.remove(activity);
            }
        });
    }

    private SkinManager() {
    }

    ;

    public interface ProcessorCallback {
        void addProcessor(Map<String, AttrProcessor> map);
    }
}
