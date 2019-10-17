package com.wack.pop2.physics.util.hull;

import org.jbox2d.common.Vec2;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 14:05:51 - 14.09.2010
 */
public abstract class BaseHullAlgorithm implements IHullAlgorithm {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected Vec2[] mVertices;
	protected int mVertexCount;
	protected int mHullVertexCount;

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

	protected int indexOfLowestVertex() {
		final Vec2[] vertices = this.mVertices;
		final int vertexCount = this.mVertexCount;

		int min = 0;
		for(int i = 1; i < vertexCount; i++) {
			final float dY = vertices[i].y - vertices[min].y;
			final float dX = vertices[i].x - vertices[min].x;
			if(dY < 0 || dY == 0 && dX < 0) {
				min = i;
			}
		}
		return min;
	}

	protected void swap(final int pIndexA, final int pIndexB) {
		final Vec2[] vertices = this.mVertices;
		
		final Vec2 tmp = vertices[pIndexA];
		vertices[pIndexA] = vertices[pIndexB];
		vertices[pIndexB] = tmp;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
