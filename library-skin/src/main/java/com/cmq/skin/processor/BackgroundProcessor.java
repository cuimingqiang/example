package com.cmq.skin.processor;

import android.content.res.Resources;
import android.view.View;

import com.cmq.skin.AttrProcessor;

import androidx.core.content.res.ResourcesCompat;

public class BackgroundProcessor implements AttrProcessor{
    @Override
    public void applyValue(Resources resources, View view, int resId) {
        view.setBackground(resources.getDrawable(resId,null));
    }
}
