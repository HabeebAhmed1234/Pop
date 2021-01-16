package com.stupidfungames.pop.difficulty;

import android.util.Pair;

/**
 * Contains the function that maps game progress to game difficulty
 */
class GameDifficultyFunction {

  // linear difficulty progression
  static float getDifficultyLinear(float progress) {
    return progress;
  }

  // Adjustable values
  private static Pair<Float, Float> SET_POINTS_ORIGIN = new Pair<>(0.0f, 0.0f);
  private static Pair<Float, Float>[] SET_POINTS = new Pair[]{
      new Pair(0.03125f, 0.02f),
      new Pair(0.125f, 0.2f),
      new Pair(1.0f, 1.0f),
  };

  /**
   * Uses the set points above to return a difficulty assuming actual difficulty is linearly scaled
   * between each point
   */
  static float getDifficultyFromSetPoints(float progress) {
    int nextSetPointIndex = getNextSetPointIndex(progress);
    Pair<Float, Float> nextSetPoint = SET_POINTS[nextSetPointIndex];
    Pair<Float, Float> previousSetPoint =
        (nextSetPointIndex - 1) >= 0 ? SET_POINTS[nextSetPointIndex - 1] : SET_POINTS_ORIGIN;

    float slope = (nextSetPoint.second - previousSetPoint.second)
        / (nextSetPoint.first - previousSetPoint.first);

    return previousSetPoint.second + (progress - previousSetPoint.first) * slope;
  }

  private static int getNextSetPointIndex(float progress) {
    int nextSetPointIndex = 0;
    for (int i = 0; i < SET_POINTS.length; i++) {
      Pair<Float, Float> setPoint = SET_POINTS[i];
      if (progress < setPoint.first) {
        nextSetPointIndex = i;
        break;
      } else if (progress == setPoint.first) {
        return i;
      }
    }
    return nextSetPointIndex;
  }
}
