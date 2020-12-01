package com.stupidfungames.pop;

import com.stupidfungames.pop.physics.PhysicsFactory;

import org.jbox2d.dynamics.FixtureDef;

public class GameFixtureDefs {

    public static final FixtureDef BASE_BUBBLE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
    public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
    public static final FixtureDef FLOOR_SENSOR_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 0, true);
    public static final FixtureDef BASE_CHAIN_LINK_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0f);
    public static final FixtureDef BASE_WRECKING_BALL_DEF = PhysicsFactory.createFixtureDef(1f, 1f, 1f);
    public static final FixtureDef TURRET_BULLET_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1f, 1f, 1f);
    public static final FixtureDef ICON_BOX_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
}
