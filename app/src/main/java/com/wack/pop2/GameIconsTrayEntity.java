package com.wack.pop2;

import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

import java.util.HashMap;
import java.util.Map;

/**
 * Single entity used to manage the icons for different tools in the game.
 */
public class GameIconsTrayEntity extends BaseEntity{

    public enum ICON_ID {
        BALL_AND_CHAIN_ICON,
        TURRETS_ICON,
        WALLS_ICON,
        NUKE_ICON,
    }

    private static final float ICONS_SIZE_AS_PERCENT_OF_SCREEN_WIDTH = 0.25f;
    private static final float ICONS_TRAY_VERTICAL_CENTER_POINT_AS_PERCENT_OF_SCREEN_HEIGHT = 0.5f;
    private static final int RIGHT_PADDING_DP = 50;
    private static final int PADDING_BETWEEN_DP = 30;
    private static final int PADDING_BOTTOM_DP = 30;

    private final int iconsSizePx;
    private final int iconsTrayCenterYPx;

    private final Map<ICON_ID, Sprite> icons = new HashMap<>();

    private Rectangle iconsContainer;

    private float currentRightEdge = 0;

    public GameIconsTrayEntity(GameResources gameResources) {
        super(gameResources);
        iconsSizePx = (int) (ScreenUtils.getSreenSize().width * ICONS_SIZE_AS_PERCENT_OF_SCREEN_WIDTH);
        iconsTrayCenterYPx = (int) (ScreenUtils.getSreenSize().height * ICONS_TRAY_VERTICAL_CENTER_POINT_AS_PERCENT_OF_SCREEN_HEIGHT);
        currentRightEdge += ScreenUtils.getSreenSize().width - RIGHT_PADDING_DP;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        iconsContainer = new Rectangle(0,0,0,0, vertexBufferObjectManager);
    }

    public void addIcon(ICON_ID iconId, Sprite iconSprite) {
        icons.put(iconId, iconSprite);
        iconSprite.setX(currentRightEdge - iconSprite.getWidthScaled());
        iconSprite.setY(ScreenUtils.getSreenSize().height - PADDING_BOTTOM_DP - iconSprite.getHeightScaled());
        currentRightEdge = iconSprite.getX() - PADDING_BETWEEN_DP;
    }

    public Sprite getIcon(ICON_ID id) {
        return icons.get(id);
    }
}
