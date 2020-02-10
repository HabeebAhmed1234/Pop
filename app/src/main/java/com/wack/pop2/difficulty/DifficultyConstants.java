package com.wack.pop2.difficulty;

import android.text.format.Time;

import java.util.concurrent.TimeUnit;

public class DifficultyConstants {

    // Static
    public static final float TIME_TO_MIN_SPAWN_INTERVAL_SECONDS = TimeUnit.MINUTES.toSeconds(5);
    public static final float STARTING_SPAWN_INTERVAL = 10;
    public static final float MIN_UPDATE_INTERVAL = 0.2f;
    public static final float SPAWN_INTERVAL_UPDATE_SECONDS = 2;
    public static final float SPAWN_INTERVAL_DECREASE_SPEED = (STARTING_SPAWN_INTERVAL - MIN_UPDATE_INTERVAL) / (TIME_TO_MIN_SPAWN_INTERVAL_SECONDS / SPAWN_INTERVAL_UPDATE_SECONDS);

    // Dynamic
    public static final int NUM_DATA_POINTS_FOR_SMA = 10;
    public static final int ACCELERATION_UPDATE_INTERVAL_SECONDS = 5;
    public static final float BASE_SPAWN_INTERVAL_DIFF = 1;
    public static final float TARGET_SCORE_ACCELERATION = 1;
}
