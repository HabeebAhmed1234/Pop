package com.wack.pop2.tray;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.sprite.Sprite;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class HostTrayBaseEntity<IconIdType> extends BaseEntity implements HostTrayCallback {

    /**
     * Contains the specifications of the tray's dimensions and layout
     */
    public static class Spec {

        public final int marginRightPx;
        public final int verticalAnchorPx;
        public final int horizontalAnchorPx;
        public final float openCloseAnimationDuration;

        public Spec(Context context,
                    int marginRightDp,
                    int verticalAnchorDp,
                    int horizontalAnchorDp,
                    float openCloseAnimationDuration) {
            marginRightPx = ScreenUtils.dpToPx(marginRightDp, context);
            verticalAnchorPx = ScreenUtils.dpToPx(verticalAnchorDp, context);
            horizontalAnchorPx = ScreenUtils.dpToPx(horizontalAnchorDp, context);
            this.openCloseAnimationDuration = openCloseAnimationDuration;
        }
    }

    private Spec spec;

    public HostTrayBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        binder.bind(HostTrayCallback.class, this);
        binder.bind(TrayStateMachine.class, new TrayStateMachine());
        binder.bind(TrayOpenCloseButtonBaseEntity.class, getOpenCloseButtonEntity(this));
        binder.bind(TrayIconsHolderBaseEntity.class, getTrayIconsHolderEntity(this));
        binder.bind(TrayAnimationManager.class, new TrayAnimationManager(getSpecInternal().openCloseAnimationDuration, this));
    }

    protected abstract Spec getSpec();
    protected abstract boolean shouldExpandWhenIconAdded();
    protected abstract TrayOpenCloseButtonBaseEntity getOpenCloseButtonEntity(BinderEnity parent);
    protected abstract TrayIconsHolderBaseEntity getTrayIconsHolderEntity(BinderEnity parent);
    protected abstract SoundId getOpenSound();
    protected abstract SoundId getCloseSound();
    protected abstract GameEvent getTrayOpenedEvent();
    protected abstract GameEvent getTrayClosedEvent();

    public void addIcon(IconIdType iconId, Sprite iconSprite, IOnAreaTouchListener areaTouchListener) {
        get(TrayIconsHolderBaseEntity.class).addIcon(iconId, iconSprite, areaTouchListener);

        TrayStateMachine stateMachine = get(TrayStateMachine.class);
        if (stateMachine.getCurrentState() == TrayStateMachine.State.EMPTY) {
            stateMachine.transitionState(TrayStateMachine.State.CLOSED);
        }
        if (stateMachine.canOpen() && shouldExpandWhenIconAdded()) {
            openTray();
        } else if (stateMachine.getCurrentState() == TrayStateMachine.State.EXPANDED) {
            EventBus.get().sendEvent(getTrayOpenedEvent());
        }
    }

    @Nullable
    public Sprite getIcon(IconIdType id) {
        return get(TrayIconsHolderBaseEntity.class).getIcon(id);
    }

    @Override
    public void openTray() {
        TrayStateMachine stateMachine = get(TrayStateMachine.class);
        TrayAnimationManager trayAnimationManager = get(TrayAnimationManager.class);
        if (trayAnimationManager != null && stateMachine.canOpen()) {
            get(GameSoundsManager.class).getSound(getOpenSound()).play();
            Futures.addCallback(
                    trayAnimationManager.openTray(),
                    new FutureCallback() {
                        @Override
                        public void onSuccess(@NullableDecl Object result) {
                            EventBus.get().sendEvent(getTrayOpenedEvent());
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    },
                    ContextCompat.getMainExecutor(get(Context.class)));
        }
    }

    @Override
    public void closeTray() {
        TrayAnimationManager trayAnimationManager = get(TrayAnimationManager.class);
        if (trayAnimationManager != null && get(TrayStateMachine.class).canClose()) {
            get(GameSoundsManager.class).getSound(getCloseSound()).play();
            Futures.addCallback(
                    trayAnimationManager.closeTray(),
                    new FutureCallback() {
                        @Override
                        public void onSuccess(@NullableDecl Object result) {
                            EventBus.get().sendEvent(getTrayClosedEvent());
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    },
                    ContextCompat.getMainExecutor(get(Context.class)));
        }
    }

    @Override
    public TrayStateMachine getStateMachine() {
        return get(TrayStateMachine.class);
    }

    @Override
    public GameTexturesManager getTextureManager() {
        return get(GameTexturesManager.class);
    }

    private Spec getSpecInternal() {
        if (spec == null) {
            spec = getSpec();
        }
        return spec;
    }

    @Override
    public Rectangle getTrayIconsHolderRectangle() {
        return get(TrayIconsHolderBaseEntity.class).getTrayIconsHolderRectangle();
    }

    @Override
    public void onIconsTrayInitialized() {
        get(TrayOpenCloseButtonBaseEntity.class).onIconsTrayInitialized();
    }

    @Override
    public void onIconsTrayDimensionsChanged() {
        get(TrayOpenCloseButtonBaseEntity.class).onIconsTrayDimensionsChanged();
    }

    @Override
    public int[] getAnchorPx() {
        return new int[] {
                getSpecInternal().horizontalAnchorPx - getMarginRightPx(),
                getSpecInternal().verticalAnchorPx};
    }

    private int getMarginRightPx() {
        return getSpecInternal().marginRightPx;
    }
}
