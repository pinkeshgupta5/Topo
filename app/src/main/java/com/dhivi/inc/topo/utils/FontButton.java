package com.dhivi.inc.topo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Manoj on 9/9/2017.
 */

public class FontButton  extends Button {

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontManager.getInstance().applyFont(this, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FontManager.getInstance().applyFont(this, attrs);
    }
}
