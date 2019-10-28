package com.wack.pop2.fixturedefdata;

public class BubbleEntityUserData extends BaseEntityUserData {

    public final boolean isScoreLossBubble;
    public final boolean isGameOverWhenPopped;

    /**
     * @param isScoreLossBubble If true then the user will lose points when this bubble passes
     *                          the floor
     */
    public BubbleEntityUserData(boolean isScoreLossBubble, boolean isGameOverWhenPopped) {
        super();
        this.isScoreLossBubble = isScoreLossBubble;
        this.isGameOverWhenPopped = isGameOverWhenPopped;
    }
}
