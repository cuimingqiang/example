package com.cmq.skin;

import android.content.res.Resources;
import android.view.View;

public interface AttrProcessor{

    void applyValue(Resources resources, View view, int resId);
}
