package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.LongPressGesture;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

/**
 * Responsible for dragging a turret around
 */
public class TurretDraggingManager extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener, BaseStateMachine.Listener<TurretStateMachine.State> {

    private static final float DRAGGING_OFFSET_DISTANCE = 100f;

    private GameSceneTouchListenerEntity touchListenerEntity;
    private TurretStateMachine stateMachine;
    private LongPressGesture longPressGesture;
    private HostTurretCallback hostTurretCallback;

    public TurretDraggingManager(
            GameSceneTouchListenerEntity touchListenerEntity,
            TurretStateMachine stateMachine,
            HostTurretCallback hostTurretCallback,
            GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.stateMachine = stateMachine;
        this.hostTurretCallback = hostTurretCallback;
        this.longPressGesture = new LongPressGesture(engine);

        init();
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onCreateScene() {
        init();
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeSceneTouchListener(this);
    }

    private void init() {
        if (!touchListenerEntity.hasSceneTouchListener(this)) {
            touchListenerEntity.addSceneTouchListener(this);
        }
    }

    @Override
    public void onEnterState(TurretStateMachine.State newState) {
        if (newState == TurretStateMachine.State.DRAGGING) {
            longPressGesture.cancelLongPress();
        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        if (stateMachine.getCurrentState() != TurretStateMachine.State.DRAGGING) {
            if (isTouchOnTurret(touchEvent.getX(), touchEvent.getY())) {
                // We are not dragging so check for a long press on the turret body
                longPressGesture.onTouch(touchEvent);
                if (longPressGesture.isLongPressing()) {
                    stateMachine.transitionState(TurretStateMachine.State.DRAGGING);
                    trackPointerOnDrag(touchEvent.getX(), touchEvent.getY());
                }
                return true;
            } else {
                return false;
            }
        } else {
            // If we are dragging then set the position of the turret to the touch event
            trackPointerOnDrag(touchEvent.getX(), touchEvent.getY());
            if (touchEvent.getAction() == TouchEvent.ACTION_UP) {
                stateMachine.transitionState(TurretStateMachine.State.TARGETING);
                hostTurretCallback.setTurretPosition(touchEvent.getX(), touchEvent.getY());
                longPressGesture.cancelLongPress();
            }
        }
        return true;
    }

    private void trackPointerOnDrag(float x, float y) {
        hostTurretCallback.setTurretPosition(x - DRAGGING_OFFSET_DISTANCE, y - DRAGGING_OFFSET_DISTANCE);
    }

    private boolean isTouchOnTurret(float touchX, float touchY) {
        return hostTurretCallback.getTurretBodySprite().contains(touchX, touchY);
    }
}
