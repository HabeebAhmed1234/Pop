package com.wack.pop2.tray;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.util.modifier.IModifier;

class TrayAnimationManager {

    private TrayStateMachine stateMachine;
    private HostTrayCallback hostTrayCallback;
    private IEntityModifier currentAnimation;
    private final float openCloseAnimatinoDuration;

    public TrayAnimationManager(float openCloseAnimationDuration,
                                HostTrayCallback hostTrayCallback,
                                TrayStateMachine stateMachine) {
        this.hostTrayCallback = hostTrayCallback;
        this.stateMachine = stateMachine;
        this.openCloseAnimatinoDuration = openCloseAnimationDuration;
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
        hostTrayCallback.getTrayIconsHolderRectangle().registerEntityModifier(currentAnimation);
        currentAnimation.addModifierListener(listener);
    }

    private void stopCurrentAnimation() {
        if (currentAnimation != null) {
            hostTrayCallback.getTrayIconsHolderRectangle().unregisterEntityModifier(currentAnimation);
        }
    }

    private int[] getOpenPositionPx() {
        int[] anchor = hostTrayCallback.getAnchorPx();
        int trayWidth = (int) hostTrayCallback.getTrayIconsHolderRectangle().getWidthScaled();
        int trayHeight = (int) hostTrayCallback.getTrayIconsHolderRectangle().getHeightScaled();
        return new int[]{anchor[0] - trayWidth, anchor[1] - trayHeight/2};
    }

    private int[] getClosedPositionPx() {
        int[] anchor = hostTrayCallback.getAnchorPx();
        float trayHeight = hostTrayCallback.getTrayIconsHolderRectangle().getHeightScaled();
        return new int[]{anchor[0], anchor[1] - ((int)trayHeight)/2};
    }

    private IEntityModifier getOpenTrayEntityModifier() {
        return getTrayEntityModifier(getOpenPositionPx(), getClosedPositionPx());
    }

    private IEntityModifier getCloseTrayEntityModifier() {
        return getTrayEntityModifier(getClosedPositionPx(), getOpenPositionPx());
    }

    private IEntityModifier getTrayEntityModifier(int[] destinationPostition, int[] oppositePosition) {
        return new MoveModifier(
                getAnimationDuration(
                        destinationPostition[0],
                        hostTrayCallback.getTrayIconsHolderRectangle().getX(),
                        destinationPostition[0] - oppositePosition[0]),
                hostTrayCallback.getTrayIconsHolderRectangle().getX(),
                destinationPostition[0],
                hostTrayCallback.getTrayIconsHolderRectangle().getY(), destinationPostition[1]);
    }

    private float getAnimationDuration(float destinationPosition, float currentPosition, float totalDelta) {
        return Math.abs(destinationPosition - currentPosition) / Math.abs(totalDelta) * openCloseAnimatinoDuration;
    }
}
