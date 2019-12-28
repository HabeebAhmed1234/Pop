package com.wack.pop2;

import org.andengine.engine.Engine;
import org.andengine.input.touch.TouchEvent;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

public class LongPressGesture {

    private static final float LONG_PRESS_THRESHOLD_SECONDS = 1f;

    private boolean isLongPressing;
    private Engine engine;
    private float lastDownEvent = -1;

    public LongPressGesture(Engine engine) {
        this.engine = engine;
    }

    public void onTouch(TouchEvent pSceneTouchEvent) {
        switch (pSceneTouchEvent.getAction()) {
            case ACTION_DOWN:
            case ACTION_MOVE:
                if (lastDownEvent == -1) {
                    lastDownEvent = engine.getSecondsElapsedTotal();
                }
                checkThreshold();
                break;
            case ACTION_CANCEL:
            case ACTION_OUTSIDE:
            case ACTION_UP:
                cancelLongPress();
                break;
        }
    }

    public boolean isLongPressing() {
        return isLongPressing;
    }

    public void cancelLongPress() {
        lastDownEvent = -1;
        isLongPressing = false;
    }

    private void checkThreshold() {
        if (lastDownEvent == -1) return;
        if (engine.getSecondsElapsedTotal() - lastDownEvent > LONG_PRESS_THRESHOLD_SECONDS) {
            isLongPressing = true;
        }
    }
}
