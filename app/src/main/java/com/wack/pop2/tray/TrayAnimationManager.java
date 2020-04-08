package com.wack.pop2.tray;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.tray.TrayStateMachine.State;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.util.modifier.IModifier;

class TrayAnimationManager extends BaseEntity {

    private SettableFuture currentAnimationFuture;
    private IEntityModifier currentAnimation;
    private final float openCloseAnimationDuration;

    public TrayAnimationManager(float openCloseAnimationDuration, BinderEnity parent) {
        super(parent);
        this.openCloseAnimationDuration = openCloseAnimationDuration;
    }

    public ListenableFuture openTray(SoundId openSound) {
        final TrayStateMachine stateMachine = get(TrayStateMachine.class);
        if (stateMachine.getCurrentState() == State.EMPTY) {
            return Futures.immediateFailedFuture(new IllegalStateException("Cannot open an empty tray"));
        }
        stopCurrentAnimation();
        if (stateMachine.getCurrentState() != State.EXPANDING && stateMachine.getCurrentState() != State.EXPANDED) {
            stateMachine.transitionState(TrayStateMachine.State.EXPANDING);
            get(GameSoundsManager.class).getSound(openSound).play();
        }
        return startAnimation(getOpenTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                if (stateMachine.getCurrentState() != State.EXPANDED) {
                    stateMachine.transitionState(TrayStateMachine.State.EXPANDED);
                }
            }
        });
    }

    public ListenableFuture closeTray(SoundId closeSound) {
        final TrayStateMachine stateMachine = get(TrayStateMachine.class);
        if (stateMachine.getCurrentState() == State.EMPTY) {
            return Futures.immediateFailedFuture(new IllegalStateException("Cannot close an empty tray"));
        }
        stopCurrentAnimation();
        if (stateMachine.getCurrentState() != State.CLOSING && stateMachine.getCurrentState() != State.CLOSED) {
            stateMachine.transitionState(TrayStateMachine.State.CLOSING);
            get(GameSoundsManager.class).getSound(closeSound).play();
        }
        return startAnimation(getCloseTrayEntityModifier(), new IModifier.IModifierListener() {

            @Override
            public void onModifierStarted(IModifier pModifier, Object pItem) {}

            @Override
            public void onModifierFinished(IModifier pModifier, Object pItem) {
                if (stateMachine.getCurrentState() != State.CLOSED) {
                    stateMachine.transitionState(TrayStateMachine.State.CLOSED);
                }
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

    private IEntityModifier getOpenTrayEntityModifier() {
        HostTrayCallback hostTray = get(HostTrayCallback.class);
        return getTrayEntityModifier(hostTray.getOpenPosition(), hostTray.getClosedPosition());
    }

    private IEntityModifier getCloseTrayEntityModifier() {
        HostTrayCallback hostTray = get(HostTrayCallback.class);
        return getTrayEntityModifier(hostTray.getClosedPosition(), hostTray.getOpenPosition());
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
        return Math.abs(destinationPosition - currentPosition) / Math.abs(totalDelta) * openCloseAnimationDuration;
    }
}
