package com.cmq.skin.processor;

import android.content.res.Resources;
import android.view.View;

import com.cmq.skin.AttrProcessor;

import androidx.appcompat.widget.Toolbar;

public class ForegroundProcessor implements AttrProcessor {
    @Override
    public void applyValue(Resources resources, View view, int resId) {
       // view.setForeground(resources.getDrawable(resId));
    }
}
