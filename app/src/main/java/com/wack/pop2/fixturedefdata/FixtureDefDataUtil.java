package com.wack.pop2.fixturedefdata;

import org.jbox2d.dynamics.Fixture;

public class FixtureDefDataUtil {

    public static boolean isBubbleFixtureDefData(Fixture fixture) {
        return fixture.m_userData != null && fixture.m_userData instanceof BubbleEntityUserData;
    }


    public static boolean isFloorFixtureDefData(Fixture fixture) {
        return fixture.m_userData != null && fixture.m_userData instanceof FloorEntityUserData;
    }
}
