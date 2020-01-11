package com.wack.pop2.collision;

import org.jbox2d.dynamics.Filter;

public class CollisionFilters {

    public static final Filter BUBBLE_FILTER = new Filter(
            CollisionBitIds.BUBBLE_MASK_BIT,
            CollisionBitIds.ALL_COLLISION_MASK);
    public static final Filter BALL_AND_CHAIN_FILTER = new Filter(
            CollisionBitIds.BALL_AND_CHAIN_MASK_BIT,
            CollisionBitIds.BUBBLE_MASK_BIT);
    public static final Filter BULLET_FILTER = new Filter(
            CollisionBitIds.BULLET_MASK_BIT,
            CollisionBitIds.BUBBLE_MASK_BIT);
    public static final Filter WALL_FILTER = new Filter(
            CollisionBitIds.WALL_MASK_BIT,
            CollisionBitIds.ALL_COLLISION_MASK);
}
