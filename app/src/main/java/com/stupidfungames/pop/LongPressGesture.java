package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.turrets.turret.HostTurretCallback;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

/**
 * Checks for long press on a given sprite bounds. Must be delivered the touch events for the scene
 */
public class LongPressGesture extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener {

    public interface LongPressCallback {
        void onLongPress(float touchX, float touchY);
    }

    private static final float LONG_PRESS_THRESHOLD_SECONDS = 0.6f;

    private final LongPressCallback callback;

    private final TimerHandler longPressCheck = new TimerHandler(
            LONG_PRESS_THRESHOLD_SECONDS,
            new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    callback.onLongPress(lastTouchEventPosition[0], lastTouchEventPosition[1]);
                }
            });

    private float[] lastTouchEventPosition = new float[2];

    public LongPressGesture(LongPressCallback callback, BinderEnity parent) {
        super(parent);
        this.callback = callback;
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        lastTouchEventPosition[0] = touchEvent.getX();
        lastTouchEventPosition[1] = touchEvent.getY();
        switch (touchEvent.getAction()) {
            case ACTION_DOWN:
                if (isPointerInBounds(touchEvent)) {
                    startLongPressCheck();
                    return true;
                }
                break;
            case ACTION_MOVE:
                if (!isPointerInBounds(touchEvent)) {
                    cancelLongPressCheck();
                    return true;
                }
                return isCheckingLongPress();
            case ACTION_CANCEL:
            case ACTION_OUTSIDE:
            case ACTION_UP:
                if (isCheckingLongPress()) {
                    cancelLongPressCheck();
                    return true;
                }
        }
        return false;
    }

    private boolean isPointerInBounds(TouchEvent touchEvent) {
        return get(HostTurretCallback.class).getTurretBodySprite().contains(touchEvent.getX(), touchEvent.getY());
    }

    private void startLongPressCheck() {
        cancelLongPressCheck();
        longPressCheck.reset();
        engine.registerUpdateHandler(longPressCheck);
    }

    private void cancelLongPressCheck() {
        if (isCheckingLongPress()) {
            engine.unregisterUpdateHandler(longPressCheck);
        }
    }

    private boolean isCheckingLongPress() {
        return engine.containsUpdateHandler(longPressCheck);
    }
}
