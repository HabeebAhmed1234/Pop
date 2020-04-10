package com.stupidfungames.pop.fixturedefdata;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;

import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;

import androidx.annotation.Nullable;

public class BubbleEntityUserData extends BaseEntityUserData {

    public boolean isScoreLossBubble;
    public BubbleSpawnerEntity.BubbleSize size;
    public BubbleType bubbleType;
    public IShape bubbleSprite;

    public boolean isTargeted;

    /**
     * @param isScoreLossBubble If true then the user will lose points when this bubble passes
     *                          the floor
     */
    public BubbleEntityUserData(
            boolean isScoreLossBubble,
            BubbleSpawnerEntity.BubbleSize size,
            BubbleType bubbleType,
            Sprite bubbleSprite) {
        super();
        this.isScoreLossBubble = isScoreLossBubble;
        this.size = size;
        this.bubbleType = bubbleType;
        this.bubbleSprite = bubbleSprite;
    }

    public boolean isPoppable() {
        return bubbleType == BubbleType.RED
                || bubbleType == BubbleType.BLUE
                || bubbleType == BubbleType.GREEN;
    }

    public static void markTargeted(@Nullable Sprite sprite, boolean isTargeted) {
        if (sprite != null) {
            @Nullable Object userdata = sprite.getUserData();
            if (userdata != null && userdata instanceof BubbleEntityUserData) {
                ((BubbleEntityUserData) userdata).isTargeted = isTargeted;
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        isScoreLossBubble = false;
        size = null;
        bubbleType = null;
        bubbleSprite = null;
    }
}
