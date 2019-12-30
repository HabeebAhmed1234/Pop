package com.wack.pop2;

import com.wack.pop2.utils.ScreenUtils;

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
    }

    private static final int RIGHT_PADDING = 50;
    private static final int PADDING_BETWEEN = 30;
    private static final int PADDING_BOTTOM = 30;

    private final Map<ICON_ID, Sprite> icons = new HashMap<>();

    private float currentRightEdge = 0;

    public GameIconsTrayEntity(GameResources gameResources) {
        super(gameResources);
        currentRightEdge += ScreenUtils.getSreenSize().width - RIGHT_PADDING;
    }

    public void addIcon(ICON_ID iconId, Sprite iconSprite) {
        icons.put(iconId, iconSprite);
        iconSprite.setX(currentRightEdge - iconSprite.getWidthScaled());
        iconSprite.setY(ScreenUtils.getSreenSize().height - PADDING_BOTTOM - iconSprite.getHeightScaled());
        currentRightEdge = iconSprite.getX() - PADDING_BETWEEN;
    }

    public Sprite getIcon(ICON_ID id) {
        return icons.get(id);
    }
}
