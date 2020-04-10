package com.stupidfungames.pop.physics.util.iterators;

import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;

import java.util.Iterator;

public class JointIterator implements Iterator<Joint> {

    private Joint mCurrentJoint = null;

    private JointIterator(Joint firstJoin) {
        mCurrentJoint = firstJoin;
    }

    /**
     * Given a {@link World} returns an iterator for all the joints in it
     */
    public static Iterator<Joint> instanceOf(World world) {
        return new JointIterator(world.getJointList());
    }

    @Override
    public boolean hasNext() {
        return mCurrentJoint != null;
    }

    @Override
    public Joint next() {
        Joint toReturn = mCurrentJoint;
        mCurrentJoint = mCurrentJoint.getNext();
        return toReturn;
    }
}
