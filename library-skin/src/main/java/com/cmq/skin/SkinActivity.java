package com.cmq.skin;

import android.content.res.Resources;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SkinActivity {
    private Set<WeakReference<View>> cache = new HashSet<>();
    private Map<String, AttrProcessor> attrProcessors;
    private int themeId;
    private SkinResource skinResource;

    public SkinActivity(Map<String, AttrProcessor> attrProcessors, int themeId, SkinResource skinResource) {
        this.attrProcessors = attrProcessors;
        this.themeId = themeId;
        this.skinResource = skinResource;
    }

    /**
     * 弱引用的方式，防止内存泄漏
     * @param v
     */
    public void cache(View v) {
        cache.add(new WeakReference<>(v));
    }

    public void applySkin(Resources resources, String packageName) {
        if (skinResource.isUseSkin(resources)) return;
        skinResource.useSkin(resources, packageName);
        //更换主题

        //执行换肤
        Iterator<WeakReference<View>> iterator = cache.iterator();
        while (iterator.hasNext()){
            View view = iterator.next().get();
            //当View销毁了，同时移除该缓存
            if(view == null)iterator.remove();
            Object o = view.getTag(R.id.viewAttr);
            if(o instanceof ViewAttr){
                Map<String, Integer> attrs = ((ViewAttr) o).getAttrs();
                for(Map.Entry<String,Integer> entry:attrs.entrySet()){
                    AttrProcessor processor = attrProcessors.get(entry.getKey());
                    try {
                        //如果没有对应的拦截器，或皮肤包中没有对应的换肤资源，不换肤
                        processor.applyValue(skinResource, view, entry.getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
