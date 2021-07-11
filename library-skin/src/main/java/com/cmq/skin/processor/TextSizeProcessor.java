package com.cmq.skin.processor;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.cmq.skin.AttrProcessor;

public class TextSizeProcessor implements AttrProcessor {
    @Override
    public void applyValue(Resources resources, View view, int resId) {
        int pixelSize = resources.getDimensionPixelSize(resId);
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelSize);
        }
    }
}
