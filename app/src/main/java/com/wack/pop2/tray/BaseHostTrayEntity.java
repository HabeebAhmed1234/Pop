package com.wack.pop2.tray;

import android.content.Context;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.sprite.Sprite;

public abstract class BaseHostTrayEntity<IconIdType> extends BaseEntity implements HostTrayCallback {

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

    private TrayAnimationManager trayAnimationManager;
    private BaseTrayOpenCloseButtonEntity iconsTrayOpenCloseButton;
    private BaseTrayIconsHolderEntity trayIconsHolderEntity;
    private TrayStateMachine stateMachine;

    private GameTexturesManager textureManager;
    private GameSoundsManager soundsManager;

    public BaseHostTrayEntity(
            GameTexturesManager textureManager,
            GameSoundsManager soundsManager,
            GameResources gameResources) {
        super(gameResources);
        stateMachine = new TrayStateMachine(getIsInitiallyExpanded());
        trayIconsHolderEntity = getTrayIconsHolderEntity(gameResources);
        iconsTrayOpenCloseButton = getOpenCloseButtonEntity(gameResources);
        this.textureManager = textureManager;
        this.soundsManager = soundsManager;

    }

    protected abstract Spec getSpec();
    protected abstract boolean getIsInitiallyExpanded();
    protected abstract BaseTrayOpenCloseButtonEntity getOpenCloseButtonEntity(GameResources gameResources);
    protected abstract BaseTrayIconsHolderEntity getTrayIconsHolderEntity(GameResources gameResources);
    protected abstract SoundId getOpenSound();
    protected abstract SoundId getCloseSound();
    protected abstract GameEvent getTrayOpenEvent();
    protected abstract GameEvent getTrayCloseEvent();

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        trayAnimationManager = new TrayAnimationManager(
                getSpecInternal().openCloseAnimationDuration,
                this,
                stateMachine);
    }

    public void addIcon(IconIdType iconId, Sprite iconSprite, IOnAreaTouchListener areaTouchListener) {
        trayIconsHolderEntity.addIcon(iconId, iconSprite, areaTouchListener);
    }

    @Nullable
    public Sprite getIcon(IconIdType id) {
        return trayIconsHolderEntity.getIcon(id);
    }

    @Override
    public void openTray() {
        if (trayAnimationManager != null) {
            soundsManager.getSound(getOpenSound()).play();
            trayAnimationManager.openTray();
            EventBus.get().sendEvent(getTrayOpenEvent());
        }
    }

    @Override
    public void closeTray() {
        if (trayAnimationManager != null) {
            soundsManager.getSound(getCloseSound()).play();
            trayAnimationManager.closeTray();
            EventBus.get().sendEvent(getTrayCloseEvent());
        }
    }

    @Override
    public TrayStateMachine getStateMachine() {
        return stateMachine;
    }

    @Override
    public GameTexturesManager getTextureManager() {
        return textureManager;
    }

    private Spec getSpecInternal() {
        if (spec == null) {
            spec = getSpec();
        }
        return spec;
    }

    @Override
    public Rectangle getTrayIconsHolderRectangle() {
        return trayIconsHolderEntity.getTrayIconsHolderRectangle();
    }

    @Override
    public void onIconsTrayInitialized() {
        iconsTrayOpenCloseButton.onIconsTrayInitialized();
    }

    @Override
    public void onIconsTrayDimensionsChanged() {
        iconsTrayOpenCloseButton.onIconsTrayDimensionsChanged();
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
