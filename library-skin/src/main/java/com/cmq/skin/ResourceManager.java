package com.cmq.skin;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import java.lang.reflect.Method;

class ResourceManager {
    private AssetManager assetManager;
    private String packageName;
    private String skinResourcePath;
    private Resources skinResources;

    public String getPackageName() {
        return packageName;
    }

    public void applySkin(String resourcePath, String packageName) throws SkinException {
        //如果已经使用了系统的资源，再次调用应该认为换肤失败
        if (TextUtils.isEmpty(skinResourcePath) && TextUtils.isEmpty(resourcePath)) {
            throw new SkinException("该皮肤正在使用中");
        }
        //如果换肤资源为空，默认为系统资源
        if (TextUtils.isEmpty(resourcePath)) {
            skinResourcePath = null;
            this.packageName = null;
            assetManager = null;
            skinResources = null;
            return;
        }
        if (resourcePath.equals(skinResourcePath)) {
            throw new SkinException("该皮肤正在使用中");
        }
        this.skinResourcePath = resourcePath;
        try {
            assetManager = AssetManager.class.getDeclaredConstructor().newInstance();
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", new Class[]{String.class});
            addAssetPath.invoke(assetManager, resourcePath);
            addAssetPath.setAccessible(true);
            this.packageName = packageName;
        } catch (Exception e) {
            throw new SkinException("资源加载失败");
        }

    }

    public Resources newSkinResource(Resources activityResource) {
        if (skinResources == null && assetManager != null) {
            skinResources = new Resources(assetManager, activityResource.getDisplayMetrics(), activityResource.getConfiguration());
        }
        return skinResources;
    }

}
