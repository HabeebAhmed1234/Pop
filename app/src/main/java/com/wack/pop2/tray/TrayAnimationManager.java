package com.wack.pop2.tray;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.BinderEnity;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.util.modifier.IModifier;

class TrayAnimationManager extends BaseEntity {

    private SettableFuture currentAnimationFuture;
    private IEntityModifier currentAnimation;
    private final float openCloseAnimatinoDuration;

    public TrayAnimationManager(float openCloseAnimationDuration, BinderEnity parent) {
        super(parent);
        this.openCloseAnimatinoDuration = openCloseAnimationDuration;
    }

    public ListenableFuture openTray() {
        stopCurrentAnimation();
        final TrayStateMachine stateMachine = get(TrayStateMachine.class);
        stateMachine.transitionState(TrayStateMachine.State.EXPANDING);
        return startAnimation(getOpenTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                stateMachine.transitionState(TrayStateMachine.State.EXPANDED);
            }
        });
    }

    public ListenableFuture closeTray() {
        stopCurrentAnimation();
        final TrayStateMachine stateMachine = get(TrayStateMachine.class);
        stateMachine.transitionState(TrayStateMachine.State.CLOSING);
        return startAnimation(getCloseTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                stateMachine.transitionState(TrayStateMachine.State.CLOSED);
            }
        });
    }

    private ListenableFuture startAnimation(IEntityModifier entityModifier, final IModifier.IModifierListener listener) {
        currentAnimation = entityModifier;
        if (currentAnimationFuture != null) {
            currentAnimationFuture.cancel(true);
        }
        currentAnimationFuture = SettableFuture.create();
        get(HostTrayCallback.class).getTrayIconsHolderRectangle().registerEntityModifier(currentAnimation);
        currentAnimation.addModifierListener(new IModifier.IModifierListener<IEntity>() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
                listener.onModifierStarted(pModifier, pItem);
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                listener.onModifierFinished(pModifier, pItem);
                currentAnimationFuture.set(null);
            }
        });
        return currentAnimationFuture;
    }

    private void stopCurrentAnimation() {
        if (currentAnimation != null) {
            get(HostTrayCallback.class).getTrayIconsHolderRectangle().unregisterEntityModifier(currentAnimation);
        }
    }

    private int[] getOpenPositionPx() {
        HostTrayCallback hostTrayCallback = get(HostTrayCallback.class);
        int[] anchor = hostTrayCallback.getAnchorPx();
        int trayWidth = (int) hostTrayCallback.getTrayIconsHolderRectangle().getWidthScaled();
        int trayHeight = (int) hostTrayCallback.getTrayIconsHolderRectangle().getHeightScaled();
        return new int[]{anchor[0] - trayWidth, anchor[1] - trayHeight/2};
    }

    private int[] getClosedPositionPx() {
        HostTrayCallback hostTrayCallback = get(HostTrayCallback.class);
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
        HostTrayCallback hostTrayCallback = get(HostTrayCallback.class);
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
