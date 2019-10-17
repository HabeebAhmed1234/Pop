package com.wack.pop2.physics.util.iterators;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.Iterator;

public class BodyIterator implements Iterator<Body> {

    private Body mCurrentBody = null;

    private BodyIterator(Body firstBody) {
        mCurrentBody = firstBody;
    }

    /**
     * Given a {@link World} returns an iterator for all the bodies in it
     */
    public static Iterator<Body> instanceOf(World world) {
        return new BodyIterator(world.getBodyList());
    }

    @Override
    public boolean hasNext() {
        return mCurrentBody != null;
    }

    @Override
    public Body next() {
        Body toReturn = mCurrentBody;
        mCurrentBody = mCurrentBody.getNext();
        return toReturn;
    }
}
