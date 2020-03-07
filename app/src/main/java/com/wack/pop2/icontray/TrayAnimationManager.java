package com.wack.pop2.icontray;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.modifier.IModifier;

class TrayAnimationManager {

    private static final float FULL_OPEN_CLOSE_ANIMATION_DURATION = 1f;

    private Rectangle tray;
    private GameIconsTrayStateMachine stateMachine;
    private TrayCallback callback;

    private IEntityModifier currentAnimation;

    public TrayAnimationManager(TrayCallback callback, GameIconsTrayStateMachine stateMachine, Rectangle tray) {
        this.callback = callback;
        this.stateMachine = stateMachine;
        this.tray = tray;
    }

    public void openTray() {
        stopCurrentAnimation();
        stateMachine.transitionState(GameIconsTrayStateMachine.State.EXPANDING);
        startAnimation(getOpenTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                stateMachine.transitionState(GameIconsTrayStateMachine.State.EXPANDED);
            }
        });
    }

    public void closeTray() {
        stopCurrentAnimation();
        stateMachine.transitionState(GameIconsTrayStateMachine.State.CLOSING);
        startAnimation(getCloseTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                stateMachine.transitionState(GameIconsTrayStateMachine.State.CLOSED);
            }
        });
    }

    private void startAnimation(IEntityModifier entityModifier, IModifier.IModifierListener listener) {
        currentAnimation = entityModifier;
        tray.registerEntityModifier(currentAnimation);
        currentAnimation.addModifierListener(listener);
    }

    private void stopCurrentAnimation() {
        if (currentAnimation != null) {
            tray.unregisterEntityModifier(currentAnimation);
        }
    }

    private IEntityModifier getOpenTrayEntityModifier() {
        return getTrayEntityModifier(callback.getOpenPositionPx(), callback.getClosedPositionPx());
    }

    private IEntityModifier getCloseTrayEntityModifier() {
        return getTrayEntityModifier(callback.getClosedPositionPx(), callback.getOpenPositionPx());
    }

    private IEntityModifier getTrayEntityModifier(int[] destinationPostition, int[] oppositePosition) {
        return new MoveModifier(getAnimationDuration(destinationPostition[0], tray.getX(), destinationPostition[0] - oppositePosition[0]), tray.getX(), destinationPostition[0], tray.getY(), destinationPostition[1]);
    }

    private float getAnimationDuration(float destinationPosition, float currentPosition, float totalDelta) {
        return Math.abs(destinationPosition - currentPosition) / Math.abs(totalDelta) * FULL_OPEN_CLOSE_ANIMATION_DURATION;
    }
}
