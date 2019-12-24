package com.wack.pop2.fixturedefdata;

import org.jbox2d.dynamics.Fixture;

public class FixtureDefDataUtil {

    public static boolean isBubbleFixtureDefData(Fixture fixture) {
        return fixture.m_userData != null && fixture.m_userData instanceof BubbleEntityUserData;
    }

    public static boolean isBulletFixtureDefData(Fixture fixture) {
        return fixture.m_userData != null && fixture.m_userData instanceof TurretBulletUserData;
    }

    public static boolean isFloorFixtureDefData(Fixture fixture) {
        return fixture.m_userData != null && fixture.m_userData instanceof FloorEntityUserData;
    }

    /**
     * Returns the first bubble fixture from the given two fixtures
     */
    public static Fixture getBubbleFixture(Fixture f1, Fixture f2) {
        if (FixtureDefDataUtil.isBubbleFixtureDefData(f1)) {
            return f1;
        } else if (FixtureDefDataUtil.isBubbleFixtureDefData(f2)) {
            return f2;
        } else {
            throw new IllegalStateException("neither of the fixtures are bubbles!");
        }
    }

    /**
     * Returns the first bubble fixture from the given two fixtures
     */
    public static Fixture getBulletFixture(Fixture f1, Fixture f2) {
        if (FixtureDefDataUtil.isBulletFixtureDefData(f1)) {
            return f1;
        } else if (FixtureDefDataUtil.isBulletFixtureDefData(f2)) {
            return f2;
        } else {
            throw new IllegalStateException("neither of the fixtures are bullets!");
        }
    }
}
