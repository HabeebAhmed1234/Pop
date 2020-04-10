package com.stupidfungames.pop.physics.util.hull;

import com.stupidfungames.pop.physics.util.Vec2Pool;

import org.jbox2d.common.Vec2;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 14:01:18 - 14.09.2010
 * @see http://www.iti.fh-flensburg.de/lang/algorithmen/geo/
 */
public class JarvisMarch extends BaseHullAlgorithm {
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

	@Override
	public int computeHull(final Vec2[] pVectors) {
		this.mVertices = pVectors;
		this.mVertexCount = pVectors.length;
		this.mHullVertexCount = 0;
		this.jarvisMarch();
		return this.mHullVertexCount;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void jarvisMarch() {
		final Vec2[] vertices = this.mVertices;

		int index = this.indexOfLowestVertex();
		do {
			this.swap(this.mHullVertexCount, index);
			index = this.indexOfRightmostVertexOf(vertices[this.mHullVertexCount]);
			this.mHullVertexCount++;
		} while(index > 0);
	}

	private int indexOfRightmostVertexOf(final Vec2 pVector) {
		final Vec2[] vertices = this.mVertices;
		final int vertexCount = this.mVertexCount;

		int i = 0;
		for(int j = 1; j < vertexCount; j++) {
			
			final Vec2 Vec2A = Vec2Pool.obtain().set(vertices[j]);
			final Vec2 Vec2B = Vec2Pool.obtain().set(vertices[i]);
			if(Vec2Util.isLess(Vec2A.sub(pVector), Vec2B.sub(pVector))) {
				i = j;
			}
			Vec2Pool.recycle(Vec2A);
			Vec2Pool.recycle(Vec2B);
		}
		return i;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
