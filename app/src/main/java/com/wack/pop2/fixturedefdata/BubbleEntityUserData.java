package com.wack.pop2.fixturedefdata;

import com.wack.pop2.BubbleSpawnerEntity.BubbleType;

public class BubbleEntityUserData extends BaseEntityUserData {

    public final boolean isScoreLossBubble;
    public final boolean isGameOverWhenPopped;

    public final BubbleType bubbleType;

    /**
     * @param isScoreLossBubble If true then the user will lose points when this bubble passes
     *                          the floor
     */
    public BubbleEntityUserData(boolean isScoreLossBubble, boolean isGameOverWhenPopped, BubbleType bubbleType) {
        super();
        this.isScoreLossBubble = isScoreLossBubble;
        this.isGameOverWhenPopped = isGameOverWhenPopped;
        this.bubbleType = bubbleType;
    }
}
