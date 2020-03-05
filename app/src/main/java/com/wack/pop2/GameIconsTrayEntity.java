package com.wack.pop2;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Single entity used to manage the icons for different tools in the game.
 */
public class GameIconsTrayEntity extends BaseEntity{

    public enum IconId {
        BALL_AND_CHAIN_ICON,
        TURRETS_ICON,
        WALLS_ICON,
        NUKE_ICON,
    }

    public enum TrayState {
        EXPANDED,
        EXPANDING,
        CLOSED,
        CLOSING,
    }

    private static final AndengineColor TRAY_BORDER_COLOR = AndengineColor.WHITE;
    private static final AndengineColor TRAY_INNER_COLOR = AndengineColor.BLACK;
    private static final int TRAY_BORDER_SIZE_DP = 4;

    private static final float ICONS_SIZE_AS_PERCENT_OF_SCREEN_WIDTH = 0.15f;
    private static final float ICONS_TRAY_VERTICAL_CENTER_POINT_AS_PERCENT_OF_SCREEN_HEIGHT = 0.5f;
    private static final int PADDING_HORIZONTAL_DP = 8;
    private static final int PADDING_VERTICAL_DP = 8;
    private static final int PADDING_BETWEEN_DP = 16;

    private final List<Pair<IconId, Sprite>> icons = new ArrayList<>();

    private Rectangle iconsTray;
    private Rectangle iconsTrayInner;
    private TrayState currentTrayState = TrayState.EXPANDED;

    public GameIconsTrayEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        iconsTray = new Rectangle(0,0,0,0, vertexBufferObjectManager);
        iconsTray.setColor(TRAY_BORDER_COLOR);
        iconsTrayInner = new Rectangle(0,0,0,0, vertexBufferObjectManager);
        iconsTrayInner.setColor(TRAY_INNER_COLOR);
        scene.attachChild(iconsTray);
        scene.attachChild(iconsTrayInner);
        refreshDimensions(null);
    }

    public void addIcon(IconId iconId, Sprite iconSprite) {
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

    private void refreshDimensions(@Nullable Sprite newIcon){
        if (newIcon != null) {
            applyIconSize(newIcon);
        }
        refreshTrayDimensions();
        refreshIconPostitions();
    }

    /**
     * Calculates the size of the tray given the icons in it and sets the dimensiosn
     */
    private void refreshTrayDimensions() {
        iconsTray.setWidth(getTrayWidthPx());
        iconsTray.setHeight(getTrayHeightPx());
        iconsTray.setX(getTrayXPostition(currentTrayState));
        iconsTray.setY(getTrayYPostition());

        int trayBorderSizePx = getTrayBorderSizePx();
        iconsTrayInner.setWidth(iconsTray.getWidthScaled() - trayBorderSizePx * 2);
        iconsTrayInner.setHeight(iconsTray.getHeightScaled() - trayBorderSizePx * 2);
        iconsTrayInner.setX(iconsTray.getX() + trayBorderSizePx);
        iconsTrayInner.setY(iconsTray.getY() + trayBorderSizePx);
    }

    private void refreshIconPostitions() {
        for (int i = 0 ; i < icons.size() ; i++) {
            Pair<IconId, Sprite> iconPair = icons.get(i);
            Sprite icon = iconPair.second;
            float trayLeftEdge = iconsTray.getX();
            float trayTopEdge = iconsTray.getY();

            // Set the posistion of this icon
            icon.setX(trayLeftEdge + getPaddingHorizontalPx());
            icon.setY(
                    trayTopEdge
                    + getPaddingVerticalPx()
                    + (i * getIconSizePx())
                    + (i * getPaddingBetweenPx()));
        }
    }

    /**
     * Sets the given icon sprite to the correct width and height
     * @param iconSprite
     */
    private void applyIconSize(Sprite iconSprite) {
        int iconSizePx = getIconSizePx();
        iconSprite.setWidth(iconSizePx);
        iconSprite.setHeight(iconSizePx);
    }

    private int getTrayBorderSizePx() {
        return ScreenUtils.dpToPx(TRAY_BORDER_SIZE_DP, hostActivity.getActivityContext());
    }

    private int getIconSizePx() {
        return (int)(ICONS_SIZE_AS_PERCENT_OF_SCREEN_WIDTH * ScreenUtils.getSreenSize().width);
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

    /**
     * Returns the width of the tray including internal padding
     */
    private int getTrayWidthPx() {
        return getPaddingHorizontalPx() * 2 + getIconSizePx();
    }

    private int getTrayHeightPx() {
        return getPaddingVerticalPx() * 2
                + getIconSizePx() * icons.size()
                + getPaddingBetweenPx() * (icons.size() -1);
    }

    private int getTrayYPostition() {
        int trayCenterY = (int) (ScreenUtils.getSreenSize().height * ICONS_TRAY_VERTICAL_CENTER_POINT_AS_PERCENT_OF_SCREEN_HEIGHT);
        return trayCenterY - getTrayHeightPx() / 2;
    }

    private int getTrayXPostition(TrayState state) {
        switch (state) {
            case CLOSED:
            case CLOSING:
                return ScreenUtils.getSreenSize().width;
            case EXPANDED:
            case EXPANDING:
                return ScreenUtils.getSreenSize().width - getTrayWidthPx();
        }
        throw new IllegalArgumentException(state + " doesn't have an x position");
    }
}
