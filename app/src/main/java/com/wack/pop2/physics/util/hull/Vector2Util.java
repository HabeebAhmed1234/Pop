package com.wack.pop2.physics.util.hull;

import org.jbox2d.common.Vec2;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 15:05:33 - 14.09.2010
 */
class Vec2Util {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static boolean isLess(final Vec2 pVertexA, final Vec2 pVertexB) {
		final float f = Vec2.cross(pVertexA, pVertexB);
		return f > 0 || f == 0 && Vec2Util.isLonger(pVertexA, pVertexB);
	}

	public static boolean isLonger(final Vec2 pVertexA, final Vec2 pVertexB) {
		return pVertexA.length() > pVertexB.length();
	}

	public static float getManhattanDistance(final Vec2 pVertexA, final Vec2 pVertexB) {
		return Math.abs(pVertexA.x - pVertexB.x) + Math.abs(pVertexA.y - pVertexB.y);
	}

	public static boolean isConvex(final Vec2 pVertexTest, final Vec2 pVertexA, final Vec2 pVertexB) {
		final float f = Vec2Util.area2(pVertexTest, pVertexA, pVertexB);
		return f < 0 || f == 0 && !Vec2Util.isBetween(pVertexTest, pVertexA, pVertexB);
	}

	public static float area2(final Vec2 pVertexTest, final Vec2 pVertexA, final Vec2 pVertexB) {
		return (pVertexA.x - pVertexTest.x) * (pVertexB.y - pVertexTest.y) - (pVertexB.x - pVertexTest.x) * (pVertexA.y - pVertexTest.y);
	}

	public static float area2(final Vec2 pVertexTest, final Vec2Line pLine) {
		return Vec2Util.area2(pVertexTest, pLine.mVertexA, pLine.mVertexB);
	}

	public static boolean isBetween(final Vec2 pVertexTest, final Vec2 pVertexA, final Vec2 pVertexB) {
		return Vec2Util.getManhattanDistance(pVertexA, pVertexB) >= Vec2Util.getManhattanDistance(pVertexTest, pVertexA) + Vec2Util.getManhattanDistance(pVertexTest, pVertexB);
	}

	public static boolean isRightOf(final Vec2 pVertexTest, final Vec2Line pLine) {
		return Vec2Util.area2(pVertexTest, pLine) < 0;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
