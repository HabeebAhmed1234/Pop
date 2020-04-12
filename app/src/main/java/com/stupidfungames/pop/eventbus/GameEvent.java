package com.stupidfungames.pop.eventbus;

/**
 * List of all the events in the game. Any class in the app can subscribe to these events.
 */
public enum GameEvent {
    GAME_OVER_ON_EXPLOSION_EVENT, // fired when the game has ended due to the user tapping on a bomb
    DECREMENT_SCORE, // Fired when score is decremented. Accompanied by score decrement amount in payload
    INCREMENT_SCORE, // Fired when score is incrementd. Accompanied by score increment amount in payload
    SCORE_CHANGED, // Fired anytime the score changes. Payload contains score
    STARTING_BUBBLE_SPAWNED, // Fired anytime a new bubble is spawned, Payload contains the bubble type
    BUBBLE_SPAWNED, // Fired when a bubble is spawned
    BUBBLE_TOUCHED, // Fired when a bubble is touched by a user's finger
    SPAWN_INTERVAL_CHANGED, // Fired when the bubble spawn interval changes as determined by GameDifficultyEntity
    GAME_PROGRESS_CHANGED, // Fired when the user's current game progress has changed (percentage)
    BALL_AND_CHAIN_POPPED_BUBBLE, // Fired when the ball and chain pops a bubble
    TURRET_BULLET_POPPED_BUBBLE, // Fired bullet popped a bubble
    TURRET_DOCKED, // Fired when a turret gets docked back into the turret icon
    WALL_PLACED, // Fired when a wall gets placed
    WALL_DELETED, // Fired when a wall gets deleted
    INTERACTION_SCORE_CHANGED, // Interaction score changed event
    SETTING_CHANGED, // Called when a setting is changed
    OPEN_GAME_ICONS_TRAY, // Called when we want to open the game icons tray
    GAME_ICONS_TRAY_CLOSED, // Called when the game icons tray is closing or closed
    GAME_ICONS_TRAY_OPENED, // Called when the game icons tray is opening or opened
    QUICK_SETTINGS_TRAY_OPEN, // Called when the quick settings tray is opening
    QUICK_SETTINGS_TRAY_CLOSE, // Called when the quick settings tray is closing
}