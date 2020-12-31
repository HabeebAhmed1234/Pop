package com.stupidfungames.pop.hudentities;

import static com.stupidfungames.pop.GameConstants.STREAK_2X_POPPED_BUBBLES_THRESHOLD;
import static com.stupidfungames.pop.GameConstants.STREAK_4X_POPPED_BUBBLES_THRESHOLD;
import static com.stupidfungames.pop.GameConstants.STREAK_8X_POPPED_BUBBLES_THRESHOLD;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;

public class ScoreStreakModel {

  public BubbleType currentStreakBubbleType = null;
  public int numBubblesPoppedOfStreak = 0;

  public int getScoreMultiplier() {
    if (numBubblesPoppedOfStreak >= STREAK_8X_POPPED_BUBBLES_THRESHOLD) {
      return 8;
    } else if (numBubblesPoppedOfStreak >= STREAK_4X_POPPED_BUBBLES_THRESHOLD) {
      return 4;
    } else if (numBubblesPoppedOfStreak >= STREAK_2X_POPPED_BUBBLES_THRESHOLD) {
      return 2;
    }
    return 1;
  }

  public String getScoreMultiplierString() {
    int mult = getScoreMultiplier();
    if (mult == 1) {
      return "";
    }
    switch (getScoreMultiplier()) {
      case 2:
        return "  X2!";
      case 4:
        return "  X4!!";
      case 8:
        return "  X8!!";
    }
    return "";
  }
}
