package com.stupidfungames.pop.fixturedefdata;

import android.util.Log;
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

  public static boolean isWallV2Fixture(Fixture fixture) {
    return fixture.m_userData != null && fixture.m_userData instanceof WallV2EntityUserData;
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
      Log.e("FixtureDefDataUtil", "illegal state",
          new IllegalStateException("neither of the fixtures are bubbles!"));
      return null;
    }
  }

  /**
   * Returns the first wall fixture from the given two fixtures
   */
  public static Fixture getWallV2Fixture(Fixture f1, Fixture f2) {
    if (FixtureDefDataUtil.isWallV2Fixture(f1)) {
      return f1;
    } else if (FixtureDefDataUtil.isWallV2Fixture(f2)) {
      return f2;
    } else {
      Log.e("FixtureDefDataUtil", "illegal state",
          new IllegalStateException("neither of the fixtures are walls!"));
      return null;
    }
  }

  /**
   * Returns the first bullet fixture from the given two fixtures
   */
  public static Fixture getBulletFixture(Fixture f1, Fixture f2) {
    if (FixtureDefDataUtil.isBulletFixtureDefData(f1)) {
      return f1;
    } else if (FixtureDefDataUtil.isBulletFixtureDefData(f2)) {
      return f2;
    } else {
      Log.e("FixtureDefDataUtil", "illegal state",
          new IllegalStateException("neither of the fixtures are bullets!"));
      return null;
    }
  }

  /**
   * Returns the first non floor fixture from the given two fixtures
   */
  public static Fixture getNonFloorFixture(Fixture f1, Fixture f2) {
    if (!isFloorFixtureDefData(f1)) {
      return f1;
    } else if (!isFloorFixtureDefData(f2)) {
      return f2;
    } else {
      Log.e("FixtureDefDataUtil", "illegal state",
          new IllegalStateException("neither of the fixtures are non floor fixtures!"));
      return null;
    }
  }
}
