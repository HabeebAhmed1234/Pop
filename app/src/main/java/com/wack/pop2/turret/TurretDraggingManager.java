package com.wack.pop2.turret;

import android.util.Log;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.LongPressGesture;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import static com.wack.pop2.eventbus.GameEvent.TURRET_DOCKED;

/**
 * Responsible for dragging a turret around
 */
public class TurretDraggingManager extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener, BaseStateMachine.Listener<TurretStateMachine.State>, LongPressGesture.LongPressCallback {

    private static final float DRAGGING_OFFSET_DISTANCE = 100f;

    private GameIconsHostTrayEntity iconsTrayEntity;
    private GameSceneTouchListenerEntity touchListenerEntity;
    private TurretsMutex mutex;
    private TurretStateMachine stateMachine;
    private LongPressGesture longPressGesture;
    private HostTurretCallback hostTurretCallback;

    public TurretDraggingManager(
            GameIconsHostTrayEntity iconsTrayEntity,
            GameSceneTouchListenerEntity touchListenerEntity,
            TurretsMutex mutex,
            TurretStateMachine stateMachine,
            HostTurretCallback hostTurretCallback,
            GameResources gameResources) {
        super(gameResources);
        this.iconsTrayEntity = iconsTrayEntity;
        this.touchListenerEntity = touchListenerEntity;
        this.mutex = mutex;
        this.stateMachine = stateMachine;
        this.hostTurretCallback = hostTurretCallback;
        this.longPressGesture = new LongPressGesture(this, hostTurretCallback.getTurretBodySprite(), engine);

        init();
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onCreateScene() {
        init();
    }

    private void init() {
        if (!touchListenerEntity.hasSceneTouchListener(this)) {
            touchListenerEntity.addSceneTouchListener(this);
        }
        if (!touchListenerEntity.hasSceneTouchListener(longPressGesture)) {
            touchListenerEntity.addSceneTouchListener(longPressGesture);
        }
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeSceneTouchListener(this);
        touchListenerEntity.removeSceneTouchListener(longPressGesture);
    }

    @Override
    public void onEnterState(TurretStateMachine.State newState) {
        if (newState == TurretStateMachine.State.DRAGGING) {
            mutex.setIsDragging(true);
        } else {
            mutex.setIsDragging(false);
        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        if (stateMachine.getCurrentState() == TurretStateMachine.State.DRAGGING){
            // If we are dragging then set the position of the turret to the touch event
            trackTurretToPointerOnDrag(touchEvent.getX(), touchEvent.getY());

            if (!maybeDockTurret(touchEvent) && touchEvent.getAction() == TouchEvent.ACTION_UP) {
                stateMachine.transitionState(TurretStateMachine.State.TARGETING);
                hostTurretCallback.setTurretPosition(touchEvent.getX(), touchEvent.getY());
            }
            return true;
        }
        return false;
    }

    /**
     * Force the turret to start dragging without the prerequisite long press.
     */
    public void forceStartDragging(float pointerX, float pointerY) {
        stateMachine.transitionState(TurretStateMachine.State.DRAGGING);
        trackTurretToPointerOnDrag(pointerX, pointerY);
    }

    @Override
    public void onLongPress(float touchX, float touchY) {
        if (stateMachine.getCurrentState() != TurretStateMachine.State.DRAGGING) {
            stateMachine.transitionState(TurretStateMachine.State.DRAGGING);
            trackTurretToPointerOnDrag(touchX, touchY);
        } else {
            Log.e("TurretDragginManager", "Long press registered while we are dragging the turret!");
        }
    }

    private void trackTurretToPointerOnDrag(float x, float y) {
        hostTurretCallback.setTurretPosition(x - DRAGGING_OFFSET_DISTANCE, y - DRAGGING_OFFSET_DISTANCE);
    }

    /**
     * Possibly docks the turret.
     * @param touchEvent
     * @return true if docked
     */
    private boolean maybeDockTurret(TouchEvent touchEvent) {
        // If the user is dragging over the dock and releases the turret then we must dock it back
        // Into the icon
        if (touchEvent.isActionUp() && isTouchOverTurretsIcon(touchEvent)) {
            stateMachine.transitionState(TurretStateMachine.State.DOCKED);
            EventBus.get().sendEvent(TURRET_DOCKED);
            return true;
        }
        return false;
    }

    private boolean isTouchOverTurretsIcon(TouchEvent touchEvent) {
        return iconsTrayEntity.getIcon(GameIconsHostTrayEntity.IconId.TURRETS_ICON).contains(touchEvent.getX(), touchEvent.getY());
    }
}
