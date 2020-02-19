package com.wack.pop2.eventbus;

/**
 * List of all the events in the game. Any class in the app can subscribe to these events.
 */
public enum GameEvent {
    GAME_TIMEOUT_EVENT, // fired when the game has officially timed out
    GAME_OVER_ON_EXPLOSION_EVENT, // fired when the game has ended due to the user tapping on a bomb
    DECREMENT_SCORE, // Fired when score is decremented. Accompanied by score decrement amount in payload
    INCREMENT_SCORE, // Fired when score is incrementd. Accompanied by score increment amount in payload
    SCORE_CHANGED, // Fired anytime the score changes. Payload contains score
    STARTING_BUBBLE_SPAWNED, // Fired anytime a new bubble is spawned, Payload contains the bubble type
    DIFFICULTY_CHANGE, // Fired when the difficulty changes. Payload contains new difficulty
    BALL_AND_CHAIN_POPPED_BUBBLE, // Fired when the ball and chain pops a bubble
    TURRET_BULLET_POPPED_BUBBLE, // Fired bullet popped a bubble
    TURRET_DOCKED, // Fired when a turret gets docked back into the turret icon
    WALL_PLACED, // Fired when a wall gets placed
    WALL_DELETED, // Fired when a wall gets deleted
    BUBBLE_SPAWNED, // Fired when a bubble is spawned
    INTERACTION_SCORE_CHANGED, // Interaction score changed event
}
