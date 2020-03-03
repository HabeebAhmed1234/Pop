package com.wack.pop2.androidbuttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wack.pop2.R;

public class GameMenuButton extends TextView {

    private static final float DEFAULT_GLOW_RADIUS = 50;
    private static final int DEFAULT_COLOR = Color.WHITE;

    public GameMenuButton(Context context) {
        super(context);
        applyShadow(context, null);
    }

    public GameMenuButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyShadow(context, attrs);
    }

    public GameMenuButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyShadow(context, attrs);
    }

    public GameMenuButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyShadow(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        applyShadow(context, attrs);
        setClipToOutline(false);
    }

    private void applyShadow(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            setShadowLayer(DEFAULT_GLOW_RADIUS, 0,0, DEFAULT_COLOR);
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GameMenuButton,
                0, 0);
        float radius = DEFAULT_GLOW_RADIUS;
        int color = DEFAULT_COLOR;
        try {
            radius = a.getFloat(R.styleable.GameMenuButton_glow_strength, DEFAULT_GLOW_RADIUS);
            color = a.getInteger(R.styleable.GameMenuButton_glow_color, DEFAULT_COLOR);
        } finally {
            a.recycle();
        }
        setShadowLayer(radius, 0,0, color);
    }
}
