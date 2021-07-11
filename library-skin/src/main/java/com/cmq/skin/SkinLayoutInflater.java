package com.cmq.skin;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class SkinLayoutInflater implements LayoutInflater.Factory2 {
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };
    private Set<String> attrKeys;
    private LayoutInflater.Factory2 base;
    private LayoutInflater layoutInflater;
    private SkinActivity cache;

    public SkinLayoutInflater(LayoutInflater inflater, LayoutInflater.Factory2 base, SkinActivity cache, Map<String, AttrProcessor> attrProcessors) {
        this.base = base;
        this.cache = cache;
        this.layoutInflater = inflater;
        this.attrKeys = attrProcessors.keySet();
    }

    private View createView(String name, AttributeSet attrs) {
        for (String prefix : sClassPrefixList) {
            try {
                View view = layoutInflater.createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {

            }
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //加载兼容库的控件
        View v = null;


        if (base != null) v = base.onCreateView(parent, name, context, attrs);
        if (v == null) {
            //加载系统控件
            if (-1 == name.indexOf('.')) {
                v = createView(name, attrs);
            } else {
                //根据完整包名创建
                try {
                    v = layoutInflater.createView(name, null, attrs);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (v != null)
            cacheAttr(v, attrs, context);
        return v;
    }

    private void cacheAttr(View view, AttributeSet attrs, Context context) {
        int count = attrs.getAttributeCount();
        ViewAttr attr = new ViewAttr();
        for (int i = 0; i < count; i++) {
            String name = attrs.getAttributeName(i);
            if (attrKeys.contains(name)) {
                String value = attrs.getAttributeValue(i);
                if (value.startsWith("@")) {
                    attr.addAttr(name, Integer.parseInt(value.substring(1)));
                } else if (value.startsWith("?")) {
                    //获取系统的值
                    TypedArray array = context.obtainStyledAttributes(new int[]{Integer.parseInt(value.substring(1))});
                    attr.addAttr(name, array.getResourceId(0, 0));
                    array.recycle();
                }
            }
        }
        //将View的属性存到tag中，保证声明周期一致性
        if (attr.hasAttr()) {
            view.setTag(R.id.viewAttr,attr);
            cache.cache(view);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View v = null;
        if (base != null) v = base.onCreateView(name, context, attrs);
        if (v == null) {
            if (-1 == name.indexOf('.')) {
                v = createView(name, attrs);
            } else {
                try {
                    v = layoutInflater.createView(name, null, attrs);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (v != null)
            cacheAttr(v, attrs, context);
        return v;
    }
}
