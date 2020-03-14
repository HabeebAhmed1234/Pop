package com.wack.pop2.tray;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.modifier.IModifier;

class TrayAnimationManager {

    private Rectangle tray;
    private TrayStateMachine stateMachine;
    private HostTrayCallback callback;
    private IEntityModifier currentAnimation;
    private final float openCloseAnimatinoDuration;

    public TrayAnimationManager(float openCloseAnimatinoDuration,
                                HostTrayCallback callback,
                                TrayStateMachine stateMachine,
                                Rectangle tray) {
        this.callback = callback;
        this.stateMachine = stateMachine;
        this.tray = tray;
        this.openCloseAnimatinoDuration = openCloseAnimatinoDuration;
    }

    public void openTray() {
        stopCurrentAnimation();
        stateMachine.transitionState(TrayStateMachine.State.EXPANDING);
        startAnimation(getOpenTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                stateMachine.transitionState(TrayStateMachine.State.EXPANDED);
            }
        });
    }

    public void closeTray() {
        stopCurrentAnimation();
        stateMachine.transitionState(TrayStateMachine.State.CLOSING);
        startAnimation(getCloseTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                stateMachine.transitionState(TrayStateMachine.State.CLOSED);
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
        return Math.abs(destinationPosition - currentPosition) / Math.abs(totalDelta) * openCloseAnimatinoDuration;
    }
}
