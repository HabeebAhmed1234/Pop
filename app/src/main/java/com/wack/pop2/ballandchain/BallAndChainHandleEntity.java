package com.wack.pop2.ballandchain;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.utils.CoordinateConversionUtil;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.MouseJoint;

import androidx.annotation.Nullable;

/**
 * Manages the handle of the ball and chain based off of the current state of the ball and chain
 * system
 */
public class BallAndChainHandleEntity extends BaseEntity implements BallAndChainStateMachine.Listener, IOnSceneTouchListener {

    private static final Vec2 OFF_SCREEN_HANDLE_POSITION =
            new Vec2(ScreenUtils.getSreenSize().width,
                    ScreenUtils.getSreenSize().height * 10);

    @Nullable private MouseJoint handleJoint;
    private BallAndChainStateMachine stateMachine;

    public BallAndChainHandleEntity(
            BallAndChainStateMachine stateMachine,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
    }

    public void setHandleJoint(MouseJoint handleJoint) {
        this.handleJoint = handleJoint;
    }

    @Override
    public void onCreateScene() {
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        stateMachine.removeAllStateTransitionListener(this);
    }

    @Override
    public void onEnterState(BallAndChainStateMachine.State newState) {
        switch (newState) {
            case LOCKED:
                stopTouchTracking();
                break;
            case UNLOCKED_CHARGED:
                stopTouchTracking();
                break;
            case UNLOCKED_DISCHARGED:
                stopTouchTracking();
                break;
            case IN_USE_CHARGED:
                startTouchTracking();
                break;
            case IN_USE_DISCHARGED:
                startTouchTracking();
                break;
        }
    }

    private void startTouchTracking() {
        scene.setOnSceneTouchListener(this);
    }

    private void stopTouchTracking() {
        // Snap the handle out of the screen and stop tracking touch dragging the handle
        setHandlePositionTarget(OFF_SCREEN_HANDLE_POSITION);
        scene.setOnSceneTouchListener(null);
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        switch(touchEvent.getAction()) {
            case TouchEvent.ACTION_UP:
                return false;
            case TouchEvent.ACTION_DOWN:
            case TouchEvent.ACTION_MOVE:
                setHandlePositionTarget(touchEvent);
                return true;
        }
        return false;
    }

    private void setHandlePositionTarget(TouchEvent touchEvent) {
        setHandlePositionTarget(
                CoordinateConversionUtil.sceneToPhysicsWorld(
                        Vec2Pool.obtain(
                            touchEvent.getX(),
                            touchEvent.getY())));
    }

    private void setHandlePositionTarget(Vec2 position) {
        if (handleJoint == null) return;
        handleJoint.setTarget(position);
    }
}
