package com.stupidfungames.pop.touchlisteners;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

/**
 * This is a touch listener for buttons. It makes sure that the user pressed down on the button
 * before letting go. If the user cancels the touch in between the down and up action then it
 * doesn't count as a button press.
 */
public abstract class ButtonUpTouchListener implements IOnAreaTouchListener {

    private boolean pressedDown = false;

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        switch (pSceneTouchEvent.getAction()) {
            case ACTION_CANCEL:
            case ACTION_OUTSIDE:
                setUnPressed();
                break;
            case ACTION_DOWN:
                setPressed();
                return true;
            case ACTION_MOVE:
                if (isPressedDown()) {
                    if (!pTouchArea.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
                        setUnPressed();
                        return true;
                    }
                    return true;
                }
                return false;
            case ACTION_UP:
                if (isPressedDown()) {
                    setUnPressed();
                    return onButtonPressed(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX, pTouchAreaLocalY);
                }
                setUnPressed();
                return false;

        }
        return false;
    }

    private boolean isPressedDown() {
        return pressedDown;
    }

    private void setPressed() {
        pressedDown = true;
        onPressedStateChanged(pressedDown);
    }

    private void setUnPressed() {
        pressedDown = false;
        onPressedStateChanged(pressedDown);
    }

    protected void onPressedStateChanged(boolean isPressed) {
        /** NOOP **/
    }

    protected abstract boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY);
}
