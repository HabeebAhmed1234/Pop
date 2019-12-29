package com.wack.pop2;

import android.util.Log;

import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.sprite.Sprite;

/**
 * Single entity used to manage the icons for different tools in the game.
 */
public class GameIconsTrayEntity extends BaseEntity{

    private static final int RIGHT_PADDING = 50;
    private static final int PADDING_BETWEEN = 30;
    private static final int PADDING_BOTTOM = 30;

    private float currentRightEdge = 0;

    public GameIconsTrayEntity(GameResources gameResources) {
        super(gameResources);
        currentRightEdge += ScreenUtils.getSreenSize().width - RIGHT_PADDING;
    }

    public void addIcon(Sprite iconSprite) {
        iconSprite.setX(currentRightEdge - iconSprite.getWidthScaled());
        iconSprite.setY(ScreenUtils.getSreenSize().height - PADDING_BOTTOM - iconSprite.getHeightScaled());
        currentRightEdge = iconSprite.getX() - PADDING_BETWEEN;
    }
}
