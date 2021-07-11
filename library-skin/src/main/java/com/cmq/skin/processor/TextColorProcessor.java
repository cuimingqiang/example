package com.cmq.skin.processor;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.cmq.skin.AttrProcessor;

public class TextColorProcessor implements AttrProcessor {
    @Override
    public void applyValue(Resources resources, View view, int resId) {
        if(view instanceof TextView) {
            ((TextView)view).setTextColor(resources.getColor(resId, null));
        }
        //view.setTextColor(resources.getColorStateList(resId,null));
    }
}
