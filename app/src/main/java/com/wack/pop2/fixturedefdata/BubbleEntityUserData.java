package com.wack.pop2.fixturedefdata;

import com.wack.pop2.BubbleSpawnerEntity;
import com.wack.pop2.BubbleSpawnerEntity.BubbleType;

import org.andengine.entity.IEntity;
import org.andengine.entity.shape.IShape;

public class BubbleEntityUserData extends BaseEntityUserData {

    public final boolean isScoreLossBubble;
    public final boolean isGameOverWhenPopped;
    public final BubbleSpawnerEntity.BubbleSize size;
    public final BubbleType bubbleType;
    public final IShape bubbleSprite;

    /**
     * @param isScoreLossBubble If true then the user will lose points when this bubble passes
     *                          the floor
     */
    public BubbleEntityUserData(
            boolean isScoreLossBubble,
            boolean isGameOverWhenPopped,
            BubbleSpawnerEntity.BubbleSize size,
            BubbleType bubbleType,
            IShape bubbleSprite) {
        super();
        this.isScoreLossBubble = isScoreLossBubble;
        this.isGameOverWhenPopped = isGameOverWhenPopped;
        this.size = size;
        this.bubbleType = bubbleType;
        this.bubbleSprite = bubbleSprite;
    }

    public boolean isPoppable() {
        return bubbleType == BubbleType.RED
                || bubbleType == BubbleType.BLUE
                || bubbleType == BubbleType.GREEN;
    }
}
