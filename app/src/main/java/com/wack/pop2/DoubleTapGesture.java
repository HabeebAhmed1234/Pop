package com.wack.pop2;

import com.wack.pop2.areatouch.GameAreaTouchListenerEntity;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.WallEntityUserData;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.shape.IShape;
import org.andengine.input.touch.TouchEvent;

public class DoubleTapGesture extends BaseEntity implements GameAreaTouchListenerEntity.AreaTouchListener{

    public interface DoubleTapCallback {
        void onDoubleTap(IShape wallSprite, WallEntityUserData userData);
    }

    private static final float DOUBLE_TAP_MAX_TIME_SECONDS = 0.5f;

    private final DoubleTapCallback callback;
    private Class<? extends BaseEntityUserData> userDataType;
    private GameAreaTouchListenerEntity touchListenerEntity;
    private int tappedWallId = -1;

    private TimerHandler doubleTapTimedOutCallback = new TimerHandler(DOUBLE_TAP_MAX_TIME_SECONDS, new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
            resetDoubleTap();
        }
    });

    public DoubleTapGesture(
            DoubleTapCallback callback,
            Class<? extends BaseEntityUserData> userDataType,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(gameResources);
        this.callback = callback;
        this.userDataType = userDataType;
        this.touchListenerEntity = touchListenerEntity;
    }

    @Override
    public void onCreateScene() {
        touchListenerEntity.addAreaTouchListener(userDataType, this);
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeAreaTouchListener(userDataType, this);
        resetDoubleTap();
    }

    @Override
    public boolean onTouch(TouchEvent touchEvent, ITouchArea touchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (touchEvent.isActionUp()) {
            IEntity wallEntity = (IEntity) touchArea;
            WallEntityUserData wallEntityUserData = (WallEntityUserData) wallEntity.getUserData();

            final int currentWallTappedId = wallEntityUserData.getId();

            if (tappedWallId == -1) {
                startDoubleTapDetection(currentWallTappedId);
            } else {
                if (tappedWallId == currentWallTappedId) {
                    resetDoubleTap();
                    callback.onDoubleTap((IShape) touchArea, wallEntityUserData);
                }
            }
        }
        return false;
    }

    private void startDoubleTapDetection(int currentWallTappedId) {
        tappedWallId = currentWallTappedId;
        engine.registerUpdateHandler(doubleTapTimedOutCallback);
    }

    private void resetDoubleTap() {
        tappedWallId = -1;
        engine.unregisterUpdateHandler(doubleTapTimedOutCallback);
    }

}
