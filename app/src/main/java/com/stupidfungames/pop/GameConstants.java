package com.stupidfungames.pop;

import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import java.util.concurrent.TimeUnit;

public class GameConstants {

  // Unlock all icons by the time we are halfway through to our max difficulty
  private static final float ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE = 0.5f;

  public static final float BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD =
      1 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float TURRETS_DIFFICULTY_UNLOCK_THRESHOLD =
      2 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float WALLS_DIFFICULTY_UNLOCK_THRESHOLD =
      3 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float NUKE_UNLOCK_THRESHOLD =
      4 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float MULTI_POP_UNLOCK_THRESHOLD =
      5 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);

  // Limits
  public static final int MAX_BUBBLES_ON_SCREEN = 70;

  // Difficulty
  public static final float MAX_BUBBLES_PER_SPAWN = 3;
  public static final float TIME_TO_MAX_DIFFICULTY_SECONDS = 3;//TimeUnit.MINUTES.toSeconds(7);
  // Once all icons are unlocked open up upgrades
  public static final float MIN_UPGRADE_SPAWN_START_THRESHOLD_DIFFICULTY = 0.5f;
  public static final float MAX_SPAWN_INTERVAL_SECONDS = 3.5f;
  public static final float MIN_SPAWN_INTERVAL_SECONDS = 0.5f;

  // The amount of time a bomb bubble is green/red
  public static final float BOMB_STATES_DURATION_SECONDS = 1f;
  // The amount of time before a bomb bubble explodes and ends the game
  public static final float BOMB_BUBBLE_LIFESPAN_SECONDS = 7f;

}
