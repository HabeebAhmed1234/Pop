package com.wack.pop2.ballandchain;

import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.joints.MouseJoint;

import java.util.Set;

/**
 * Contains the components of the ball and chain
 */
class BallAndChain {

    public final MouseJoint handle;
    public final Set<Sprite> components;

    public BallAndChain(final MouseJoint handle, final Set<Sprite> components) {
        this.handle = handle;
        this.components = components;
    }

    public void setColor(AndengineColor color) {
        for (Sprite component : components) {
            component.setColor(color);
        }
    }
}
