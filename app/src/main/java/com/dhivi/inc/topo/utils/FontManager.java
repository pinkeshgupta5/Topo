package com.dhivi.inc.topo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dhivi.inc.topo.R;

import java.util.HashMap;


/**
 * Created by Manoj on 9/9/2017.
 */

public class FontManager {

    private static FontManager sInstance;
    private HashMap<String, Typeface> mFontCache = new HashMap<>();

    /**
     * Gets the FontManager singleton
     * @return the FontManager singleton
     */
    public static FontManager getInstance() {
        if (sInstance == null) {
            sInstance = new FontManager();
        }
        return sInstance;
    }


    public Typeface getTypeface(@NonNull Context context, @NonNull String path) throws RuntimeException {
        Typeface typeface = mFontCache.get(path);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), path);
            mFontCache.put(path, typeface);
        }
        return typeface;
    }


    public Typeface getTypeface(@NonNull Context context, int pathResId) throws RuntimeException {
        try {
            String path = context.getResources().getString(pathResId);
            return getTypeface(context, path);
        } catch (Resources.NotFoundException exception) {
            String message = "String resource id " + pathResId + " not found";
            throw new RuntimeException(message);
        }
    }


    public void applyFont(@NonNull TextView textView, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            Context context = textView.getContext();
            TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextView, 0, 0);
            String fontPath = styledAttributes.getString(R.styleable.TextView_font_text);
            if (!TextUtils.isEmpty(fontPath)) {
                Typeface typeface = getTypeface(context, fontPath);
                if (typeface != null) {
                       textView.setTypeface(typeface);
                }
            }
            styledAttributes.recycle();
        }
    }
}
