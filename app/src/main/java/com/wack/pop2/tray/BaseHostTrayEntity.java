package com.wack.pop2.tray;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseHostTrayEntity<IconIdType> extends BaseEntity implements HostTrayCallback {

    /**
     * Contains the specifications of the tray's dimensions and layout
     */
    public static class TraySpec {

        public final int paddingHorizontalPx;
        public final int paddingVerticalPx;
        public final int paddingBetweenIconsPx;
        public final int marginRightPx;

        public final int iconsSizePx;
        public final int verticalAnchorPx;
        public final int horizontalAnchorPx;

        public final float openCloseAnimationDuration;

        public TraySpec(Context context,
                        int paddingHorizontalDp,
                        int paddingVerticalDp,
                        int paddingBetweenIconsDp,
                        int marginRightDp,
                        int iconsSizeDp,
                        int verticalAnchorDp,
                        int horizontalAnchorDp,
                        float openCloseAnimationDuration) {
            paddingHorizontalPx = ScreenUtils.dpToPx(paddingHorizontalDp, context);
            paddingVerticalPx = ScreenUtils.dpToPx(paddingVerticalDp, context);
            paddingBetweenIconsPx = ScreenUtils.dpToPx(paddingBetweenIconsDp, context);
            marginRightPx = ScreenUtils.dpToPx(marginRightDp, context);
            iconsSizePx = ScreenUtils.dpToPx(iconsSizeDp, context);
            verticalAnchorPx = ScreenUtils.dpToPx(verticalAnchorDp, context);
            horizontalAnchorPx = ScreenUtils.dpToPx(horizontalAnchorDp, context);
            this.openCloseAnimationDuration = openCloseAnimationDuration;
        }
    }

    private TraySpec traySpec;

    private final List<Pair<IconIdType, Sprite>> icons = new ArrayList<>();

    private Rectangle iconsTray;
    private TrayAnimationManager trayAnimationManager;
    private BaseTrayOpenCloseButtonEntity iconsTrayOpenCloseButton;
    private TrayStateMachine stateMachine;

    private GameAreaTouchListenerEntity areaTouchListenerEntity;
    private GameTexturesManager textureManager;

    public BaseHostTrayEntity(
            GameTexturesManager textureManager,
            GameAreaTouchListenerEntity areaTouchListenerEntity,
            GameResources gameResources) {
        super(gameResources);
        stateMachine = new TrayStateMachine();
        iconsTrayOpenCloseButton = getOpenCloseButtonEntity(gameResources);
        this.areaTouchListenerEntity = areaTouchListenerEntity;
        this.textureManager = textureManager;

    }

    protected abstract TraySpec getTraySpec();
    protected abstract BaseTrayOpenCloseButtonEntity getOpenCloseButtonEntity(GameResources gameResources);
    protected abstract AndengineColor getTrayBackgroundColor();

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        iconsTray = new Rectangle(
                getTrayXPostitionAtRest(),
                getTrayXPostitionAtRest(),
                getTrayWidthPx(),
                getTrayHeightPx(),
                vertexBufferObjectManager);
        iconsTray.setColor(getTrayBackgroundColor());
        scene.attachChild(iconsTray);

        trayAnimationManager = new TrayAnimationManager(
                getTraySpecInternal().openCloseAnimationDuration,
                this,
                stateMachine,
                iconsTray);
        iconsTrayOpenCloseButton.onIconsTrayCreated(iconsTray);

        refreshDimensions(null);
    }

    public void addIcon(IconIdType iconId, Sprite iconSprite) {
        addToSceneWithTouch(iconsTray, iconSprite);
        icons.add(Pair.create(iconId, iconSprite));
        refreshDimensions(iconSprite);
    }

    @Nullable
    public Sprite getIcon(GameIconsHostTrayEntity.IconId id) {
        for (Pair<IconIdType, Sprite> pair : icons) {
            if (pair.first == id) {
                return pair.second;
            }
        }
        return null;
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
    public int[] getOpenPositionPx() {
        return new int[] {getTraySpecInternal().horizontalAnchorPx - getTrayWidthPx() - getMarginRightPx(), getTrayYPostitionPx()};
    }

    @Override
    public int[] getClosedPositionPx() {
        return new int[] {getTraySpecInternal().horizontalAnchorPx, getTrayYPostitionPx()};
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

    private void refreshDimensions(@Nullable Sprite newIcon){
        if (newIcon != null) {
            applyIconSize(newIcon);
        }
        refreshTrayDimensions();
        refreshIconPostitions();
        refreshOpenCloseIconPosition();
    }

    private TraySpec getTraySpecInternal() {
        if (traySpec == null) {
            traySpec = getTraySpec();
        }
        return traySpec;
    }

    /**
     * Calculates the size of the tray given the icons in it and sets the dimensiosn
     */
    private void refreshTrayDimensions() {
        iconsTray.setWidth(getTrayWidthPx());
        iconsTray.setHeight(getTrayHeightPx());
        iconsTray.setX(getTrayXPostitionAtRest());
        iconsTray.setY(getTrayYPostitionPx());
    }

    private void refreshIconPostitions() {
        for (int i = 0 ; i < icons.size() ; i++) {
            Pair<IconIdType, Sprite> iconPair = icons.get(i);
            Sprite icon = iconPair.second;

            // Set the posistion of this icon
            icon.setX(getPaddingHorizontalPx());
            icon.setY(
                    getPaddingVerticalPx()
                            + (i * getIconSizePx())
                            + (i * getPaddingBetweenPx()));
        }
    }

    private void refreshOpenCloseIconPosition() {
        iconsTrayOpenCloseButton.onIconsTrayPositionChanged(iconsTray);
    }

    /**
     * Sets the given icon sprite to the correct widthPx and heightPx
     * @param iconSprite
     */
    private void applyIconSize(Sprite iconSprite) {
        int iconSizePx = getIconSizePx();
        iconSprite.setWidth(iconSizePx);
        iconSprite.setHeight(iconSizePx);
    }

    private int getIconSizePx() {
        return getTraySpecInternal().iconsSizePx;
    }

    private int getPaddingHorizontalPx() {
        return getTraySpecInternal().paddingHorizontalPx;
    }

    private int getPaddingVerticalPx() {
        return getTraySpecInternal().paddingVerticalPx;
    }

    private int getPaddingBetweenPx() {
        return getTraySpecInternal().paddingBetweenIconsPx;
    }

    private int getMarginRightPx() {
        return getTraySpecInternal().marginRightPx;
    }

    /**
     * Returns the widthPx of the tray including internal padding
     */
    private int getTrayWidthPx() {
        return getPaddingHorizontalPx() * 2 + getIconSizePx();
    }

    private int getTrayHeightPx() {
        return getPaddingVerticalPx() * 2
                + getIconSizePx() * icons.size()
                + getPaddingBetweenPx() * (icons.size() -1);
    }

    private int getTrayYPostitionPx() {
        int trayCenterY = getTraySpecInternal().verticalAnchorPx;
        return trayCenterY - getTrayHeightPx() / 2;
    }

    private int getTrayXPostitionAtRest() {
        TrayStateMachine.State state = stateMachine.getCurrentState();
        switch (state) {
            case CLOSED:
            case CLOSING:
                return getClosedPositionPx()[0];
            case EXPANDED:
            case EXPANDING:
                return getOpenPositionPx()[0];
        }
        throw new IllegalArgumentException(state + " doesn't have an x position");
    }
}
