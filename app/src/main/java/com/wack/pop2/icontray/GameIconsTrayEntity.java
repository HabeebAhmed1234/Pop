package com.wack.pop2.icontray;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Single entity used to manage the icons for different tools in the game.
 */
public class GameIconsTrayEntity extends BaseEntity implements TrayCallback {

    public enum IconId {
        BALL_AND_CHAIN_ICON,
        TURRETS_ICON,
        WALLS_ICON,
        NUKE_ICON,
    }

    private static final int ICONS_SIZE_DP = 24;
    private int verticalAnchorDp;
    private int horizontalAnchorDp;

    private static final int PADDING_HORIZONTAL_DP = 0;
    private static final int PADDING_VERTICAL_DP = 8;
    private static final int PADDING_BETWEEN_DP = 16;
    private static final int MARGIN_RIGHT_DP = 8;

    private final List<Pair<IconId, Sprite>> icons = new ArrayList<>();

    private Rectangle iconsTray;
    private TrayAnimationManager trayAnimationManager;
    private GameIconsTrayOpenCloseButton gameIconsTrayOpenCloseButton;
    private GameIconsTrayStateMachine stateMachine;

    public GameIconsTrayEntity(
            GameAreaTouchListenerEntity areaTouchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(gameResources);
        verticalAnchorDp = ScreenUtils.getSreenSize().heightDp / 2;
        horizontalAnchorDp = ScreenUtils.getSreenSize().widthDp / 2;

        stateMachine = new GameIconsTrayStateMachine();
        gameIconsTrayOpenCloseButton = new GameIconsTrayOpenCloseButton(this, areaTouchListenerEntity, stateMachine, gameTexturesManager, gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        iconsTray = new Rectangle(getTrayXPostitionAtRest(), getTrayXPostitionAtRest(),getTrayWidthPx(),getTrayHeightPx(), vertexBufferObjectManager);
        iconsTray.setColor(AndengineColor.TRANSPARENT);
        scene.attachChild(iconsTray);

        trayAnimationManager = new TrayAnimationManager(this, stateMachine, iconsTray);
        gameIconsTrayOpenCloseButton.onIconsTrayCreated(iconsTray);

        refreshDimensions(null);
    }

    public void addIcon(IconId iconId, Sprite iconSprite) {
        addToSceneWithTouch(iconsTray, iconSprite);
        icons.add(Pair.create(iconId, iconSprite));
        refreshDimensions(iconSprite);
    }

    @Nullable
    public Sprite getIcon(IconId id) {
        for (Pair<IconId, Sprite> pair : icons) {
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
        return new int[] {ScreenUtils.getSreenSize().widthPx - getTrayWidthPx() - getMarginRightPx(), getTrayYPostitionPx()};
    }

    @Override
    public int[] getClosedPositionPx() {
        return new int[] {ScreenUtils.getSreenSize().widthPx, getTrayYPostitionPx()};
    }

    private void refreshDimensions(@Nullable Sprite newIcon){
        if (newIcon != null) {
            applyIconSize(newIcon);
        }
        refreshTrayDimensions();
        refreshIconPostitions();
        refreshOpenCloseIconPosition();
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
            Pair<IconId, Sprite> iconPair = icons.get(i);
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
        gameIconsTrayOpenCloseButton.onIconsTrayPositionChanged(iconsTray);
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
        return (int)(ICONS_SIZE_DP * ScreenUtils.getSreenSize().widthPx);
    }

    private int getPaddingHorizontalPx() {
        return ScreenUtils.dpToPx(PADDING_HORIZONTAL_DP, hostActivity.getActivityContext());
    }

    private int getPaddingVerticalPx() {
        return ScreenUtils.dpToPx(PADDING_VERTICAL_DP, hostActivity.getActivityContext());
    }

    private int getPaddingBetweenPx() {
        return ScreenUtils.dpToPx(PADDING_BETWEEN_DP, hostActivity.getActivityContext());
    }

    private int getMarginRightPx() {
        return ScreenUtils.dpToPx(MARGIN_RIGHT_DP, hostActivity.getActivityContext());
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
        int trayCenterY = ScreenUtils.dpToPx(verticalAnchorDp, hostActivity.getActivityContext());
        return trayCenterY - getTrayHeightPx() / 2;
    }

    private int getTrayXPostitionAtRest() {
        GameIconsTrayStateMachine.State state = stateMachine.getCurrentState();
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
