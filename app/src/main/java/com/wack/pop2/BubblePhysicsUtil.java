package com.wack.pop2;

import com.wack.pop2.physics.util.Vec2Pool;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class BubblePhysicsUtil {


    public static void applyVelocity(final Body bubbleBody, float xspeed, float yspeed) {
        final Vec2 velocity = Vec2Pool.obtain(xspeed, yspeed);
        bubbleBody.setLinearVelocity(velocity);
        Vec2Pool.recycle(velocity);
    }
}
