package com.cmq.skin;

import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SkinResource extends Resources {

    private static final boolean DEBUG = true;
    private boolean useApkResource = true;
    private Resources skinResource;
    private String packageName;

    public SkinResource(AssetManager empty, Resources apkResources) {
        super(empty, apkResources.getDisplayMetrics(), apkResources.getConfiguration());
        try {
            Field mResourcesImpl = Resources.class.getDeclaredField("mResourcesImpl");
            mResourcesImpl.setAccessible(true);
            Object apkResourcesImp = mResourcesImpl.get(apkResources);
            mResourcesImpl.set(this, apkResourcesImp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isUseSkin(Resources resources) {
        return skinResource == resources;
    }

    public void useSkin(Resources resources, String packageName) {
        this.skinResource = resources;
        this.packageName = packageName;
        useApkResource = skinResource == null;
    }

    /**
     * 获取换肤资源中的Id
     *
     * @param apkResId app 资源Id
     * @return 如果使用换肤 返回换肤资源的Id，如果Id = 0 说明换肤资源没有该资源，请自行判断
     */
    public int getId(int apkResId) {
        if (!useApkResource) {
            String resName = getResourceEntryName(apkResId);
            String resType = getResourceTypeName(apkResId);
            int id = skinResource.getIdentifier(resName, resType, packageName);
            if (id > 0) return id;
        }

        return apkResId;
    }

    @Override
    public int getColor(int id, @Nullable Theme theme) throws NotFoundException {
        if (!useApkResource && skinResource != null) {
            int skinId = getId(id);
            if (skinId > 0) try {
                return skinResource.getColor(skinId, theme);
            } catch (Exception e) {
                if (DEBUG)
                    e.printStackTrace();
            }
        }
        return super.getColor(id, theme);
    }

    @NonNull
    @Override
    public ColorStateList getColorStateList(int id, @Nullable Theme theme) throws NotFoundException {
        if (!useApkResource && skinResource != null) {
            int skinId = getId(id);
            if (skinId > 0) try {
                return skinResource.getColorStateList(skinId, theme);
            } catch (Exception e) {
                if (DEBUG)
                    e.printStackTrace();
            }
        }
        return super.getColorStateList(id, theme);
    }

    @Override
    public Drawable getDrawable(int id, @Nullable Theme theme) throws NotFoundException {
        if (!useApkResource && skinResource != null) {
            int skinId = getId(id);
            if (skinId > 0) try {
                return skinResource.getDrawable(skinId, theme);
            } catch (Exception e) {
                if (DEBUG)
                    e.printStackTrace();
            }

        }
        return super.getDrawable(id, theme);
    }

    @NonNull
    @Override
    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        if (!useApkResource && skinResource != null) {
            int skinId = getId(id);
            if (skinId > 0) try {
                return skinResource.getAnimation(skinId);
            } catch (Exception e) {
                if (DEBUG)
                    e.printStackTrace();
            }

        }
        return super.getAnimation(id);
    }
}
