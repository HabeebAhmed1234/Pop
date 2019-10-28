package com.wack.pop2.fixturedefdata;

public class BubbleFixtureDefData extends BaseFixtureDefData {

    public final boolean isScoreLossBubble;

    /**
     * @param isScoreLossBubble If true then the user will lose points when this bubble passes
     *                          the floor
     */
    public BubbleFixtureDefData(boolean isScoreLossBubble) {
        super();
        this.isScoreLossBubble = isScoreLossBubble;
    }
}
