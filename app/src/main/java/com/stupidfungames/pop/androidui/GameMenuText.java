package com.stupidfungames.pop.androidui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import androidx.core.content.res.ResourcesCompat;
import com.stupidfungames.pop.R;

public class GameMenuText extends TextView {

    private static final float DEFAULT_GLOW_RADIUS = 80;
    private static final int DEFAULT_COLOR = R.color.menu_button_color;

    private float shadowRadius;
    private int glowColor;

    public GameMenuText(Context context) {
        super(context);
        init(context, null);
    }

    public GameMenuText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameMenuText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public GameMenuText(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initShadow(context, attrs);
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.neon));
        setClipToOutline(false);
        setTextColor(getResources().getColor(android.R.color.white));
    }

    private void initShadow(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            setShadowLayer(DEFAULT_GLOW_RADIUS, 0,0, DEFAULT_COLOR);
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GameMenuButton,
                0, 0);
        try {
            shadowRadius = a.getFloat(R.styleable.GameMenuButton_glow_strength, DEFAULT_GLOW_RADIUS);
            glowColor = ContextCompat.getColor(
                    getContext(),
                    a.getResourceId(
                            R.styleable.GameMenuButton_glow_color,
                            DEFAULT_COLOR));
        } finally {
            a.recycle();
        }
        setShadow(glowColor);
    }

    private void setShadow(int color) {
        setShadowLayer(shadowRadius, 0,0, color);
    }
}
