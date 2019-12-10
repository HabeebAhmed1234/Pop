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
class BallAndChainHandleEntity extends BaseEntity implements BallAndChainStateMachine.Listener, IOnSceneTouchListener {

    public static final Vec2 OFF_SCREEN_HANDLE_POSITION =
            CoordinateConversionUtil.sceneToPhysicsWorld(
                    new Vec2(ScreenUtils.getSreenSize().width,
                            ScreenUtils.getSreenSize().height * 1.1f));

    @Nullable private MouseJoint handleJoint;
    private BallAndChainStateMachine stateMachine;

    public BallAndChainHandleEntity(
            BallAndChainStateMachine stateMachine,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
    }

    public void setHandleJoint(MouseJoint handleJoint) {
        if (this.handleJoint != null) throw new IllegalStateException("Ball and chain handle has already been set");

        this.handleJoint = handleJoint;
        // start listening to state when the handle is set
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onCreateScene() {}

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
                releaseHandle();
                return false;
            case TouchEvent.ACTION_DOWN:
            case TouchEvent.ACTION_MOVE:
                setHandlePositionTarget(touchEvent);
                return true;
        }
        return false;
    }

    private void releaseHandle() {
        if (BallAndChainStateClassifier.isBallAndChainInUse(stateMachine.getCurrentState())) {
            stateMachine.transitionState(
                    stateMachine.getCurrentState() == BallAndChainStateMachine.State.IN_USE_CHARGED
                            ? BallAndChainStateMachine.State.UNLOCKED_CHARGED
                            : BallAndChainStateMachine.State.UNLOCKED_DISCHARGED);
            stopTouchTracking();
        }
    }

    private void setHandlePositionTarget(TouchEvent touchEvent) {
        if (BallAndChainStateClassifier.isChainHandleDraggingPermitted(stateMachine.getCurrentState()))
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
