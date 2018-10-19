package com.dhivi.inc.topo.utils;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by User on 11/27/2017.
 */

public class TransparentOutlineProvider extends ViewOutlineProvider {
    private float mAlpha;

    public TransparentOutlineProvider(float alpha) {
        mAlpha = alpha;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        ViewOutlineProvider.BACKGROUND.getOutline(view, outline);
        Drawable background = view.getBackground();
        float outlineAlpha = background == null ? 0f : background.getAlpha() / 255f;
        outline.setAlpha(mAlpha);
    }
}

