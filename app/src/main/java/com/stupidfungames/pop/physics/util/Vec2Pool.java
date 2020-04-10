package com.stupidfungames.pop.physics.util;

import org.andengine.util.adt.pool.GenericPool;
import org.jbox2d.common.Vec2;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 16:22:23 - 14.09.2010
 */
public class Vec2Pool {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static final GenericPool<Vec2> POOL = new GenericPool<Vec2>() {
		@Override
		protected Vec2 onAllocatePoolItem() {
			return new Vec2();
		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public static Vec2 obtain() {
		return POOL.obtainPoolItem();
	}

	public static Vec2 obtain(final Vec2 pCopyFrom) {
		return POOL.obtainPoolItem().set(pCopyFrom);
	}

	public static Vec2 obtain(final float pX, final float pY) {
		return POOL.obtainPoolItem().set(pX, pY);
	}

	public static void recycle(final Vec2 pVec2) {
		POOL.recyclePoolItem(pVec2);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
