package com.wack.pop2.tray;

import android.content.Context;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

public abstract class BaseHostTrayEntity<IconIdType> extends BaseEntity implements HostTrayCallback {

    /**
     * Contains the specifications of the tray's dimensions and layout
     */
    public static class TraySpec {

        public final int marginRightPx;
        public final int verticalAnchorPx;
        public final int horizontalAnchorPx;
        public final float openCloseAnimationDuration;

        public TraySpec(Context context,
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

    private TraySpec traySpec;

    private TrayAnimationManager trayAnimationManager;
    private BaseTrayOpenCloseButtonEntity iconsTrayOpenCloseButton;
    private BaseTrayIconsHolderEntity trayIconsHolderEntity;
    private TrayStateMachine stateMachine;

    private GameAreaTouchListenerEntity areaTouchListenerEntity;
    private GameTexturesManager textureManager;

    public BaseHostTrayEntity(
            GameTexturesManager textureManager,
            GameAreaTouchListenerEntity areaTouchListenerEntity,
            GameResources gameResources) {
        super(gameResources);
        stateMachine = new TrayStateMachine();
        trayIconsHolderEntity = getTrayIconsHolderEntity(gameResources);
        iconsTrayOpenCloseButton = getOpenCloseButtonEntity(gameResources);
        this.areaTouchListenerEntity = areaTouchListenerEntity;
        this.textureManager = textureManager;

    }

    protected abstract TraySpec getTraySpec();
    protected abstract BaseTrayOpenCloseButtonEntity getOpenCloseButtonEntity(GameResources gameResources);
    protected abstract BaseTrayIconsHolderEntity getTrayIconsHolderEntity(GameResources gameResources);

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        trayAnimationManager = new TrayAnimationManager(
                getTraySpecInternal().openCloseAnimationDuration,
                this,
                stateMachine);
        iconsTrayOpenCloseButton.onIconsTrayCreated();
        refreshDimensions(null);
    }

    public void addIcon(IconIdType iconId, Sprite iconSprite) {
        trayIconsHolderEntity.addIcon(iconId, iconSprite);
    }

    @Nullable
    public Sprite getIcon(IconIdType id) {
        return trayIconsHolderEntity.getIcon(id);
    }

    @Override
    public void openTray() {
        if (trayAnimationManager != null) {
            trayAnimationManager.openTray();
        }
    }

    @Override
    public void closeTray() {
        if (trayAnimationManager != null) {
            trayAnimationManager.closeTray();
        }
    }

    @Override
    public GameAreaTouchListenerEntity getAreaTouchListener() {
        return areaTouchListenerEntity;
    }

    @Override
    public TrayStateMachine getStateMachine() {
        return stateMachine;
    }

    @Override
    public GameTexturesManager getTextureManager() {
        return textureManager;
    }

    private TraySpec getTraySpecInternal() {
        if (traySpec == null) {
            traySpec = getTraySpec();
        }
        return traySpec;
    }

    @Override
    public Rectangle getTrayIconsHolderRectangle() {
        return trayIconsHolderEntity.getTrayIconsHolderRectangle();
    }

    @Override
    public int[] getAnchorPx() {
        return new int[] {
                getTraySpecInternal().horizontalAnchorPx - getMarginRightPx(),
                getTraySpecInternal().verticalAnchorPx};
    }

    private void refreshDimensions(@Nullable Sprite newIcon){
        trayIconsHolderEntity.refreshDimensions(newIcon);
        iconsTrayOpenCloseButton.refreshDimensions();
    }

    private int getMarginRightPx() {
        return getTraySpecInternal().marginRightPx;
    }
}
