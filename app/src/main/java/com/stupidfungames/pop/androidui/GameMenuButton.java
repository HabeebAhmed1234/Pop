package com.stupidfungames.pop.androidui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import androidx.core.content.res.ResourcesCompat;
import com.stupidfungames.pop.R;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class GameMenuButton extends TextView {

    private static final float MAX_SHADOW_RADIUS = 24;
    private static final float DEFAULT_GLOW_RADIUS = MAX_SHADOW_RADIUS;

    private static final int DEFAULT_COLOR = R.color.menu_button_color;
    private static final int DEFAULT_PRESSED_GLOW_COLOR =  R.color.menu_button_pressed_color;

    private float shadowRadius;
    private int glowColor;
    private int pressedGlowColor;

    public GameMenuButton(Context context) {
        super(context);
        init(context, null);
    }

    public GameMenuButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameMenuButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public GameMenuButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initResources(context, attrs);
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.neon));
        setClipToOutline(false);
        setTextColor(getResources().getColor(android.R.color.white));
    }

    private void initResources(Context context, @Nullable AttributeSet attrs) {
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
            shadowRadius = Math.min(shadowRadius, MAX_SHADOW_RADIUS);
            glowColor = ContextCompat.getColor(
                    getContext(),
                    a.getResourceId(
                            R.styleable.GameMenuButton_glow_color,
                            DEFAULT_COLOR));
            pressedGlowColor = ContextCompat.getColor(
                    getContext(),
                    a.getResourceId(
                            R.styleable.GameMenuButton_pressed_glow_color,
                            DEFAULT_PRESSED_GLOW_COLOR));
        } finally {
            a.recycle();
        }
        setShadow(glowColor);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Set correct color
        switch (event.getAction()) {
            case ACTION_DOWN:
                setShadow(pressedGlowColor);
                break;
            case ACTION_UP:
                setShadow(glowColor);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void setShadow(int color) {
        setShadowLayer(shadowRadius, 0,0, color);
    }
}
