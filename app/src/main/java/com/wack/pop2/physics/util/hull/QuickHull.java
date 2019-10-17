package com.wack.pop2.physics.util.hull;

import org.jbox2d.common.Vec2;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 14:01:34 - 14.09.2010
 * @see http://www.iti.fh-flensburg.de/lang/algorithmen/geo/
 */
public class QuickHull extends BaseHullAlgorithm {
	// ===========================================================
	// Constants
	// ===========================================================

	private final static float EPSILON = 1e-3f;

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
		this.mVertexCount = this.mVertices.length;
		this.mHullVertexCount = 0;
		this.quickHull();
		return this.mHullVertexCount;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private void quickHull() {
		this.swap(0, this.indexOfLowestVertex());
		this.mHullVertexCount++;
		final Vec2Line line = new Vec2Line(this.mVertices[0], new Vec2(this.mVertices[0]).add(new Vec2(-EPSILON, 0)));
		this.computeHullVertices(line, 1, this.mVertexCount - 1);
	}

	private void computeHullVertices(final Vec2Line pLine, final int pIndexFrom, final int pIndexTo) {
		if(pIndexFrom > pIndexTo) {
			return;
		}
		final int k = this.indexOfFurthestVertex(pLine, pIndexFrom, pIndexTo);

		final Vec2Line lineA = new Vec2Line(pLine.mVertexA, this.mVertices[k]);
		final Vec2Line lineB = new Vec2Line(this.mVertices[k], pLine.mVertexB);
		this.swap(k, pIndexTo);

		final int i = this.partition(lineA, pIndexFrom, pIndexTo - 1);
		/* All vertices from pIndexFrom to i-1 are right of lineA. */
		/* All vertices from i to pIndexTo-1 are left of lineA. */
		this.computeHullVertices(lineA, pIndexFrom, i - 1);

		/* All vertices before pIndexTo are now on the hull. */
		this.swap(pIndexTo, i);
		this.swap(i, this.mHullVertexCount);
		this.mHullVertexCount++;

		final int j = this.partition(lineB, i + 1, pIndexTo);
		/* All vertices from i+1 to j-1 are right of lineB. */
		/* All vertices from j to pToIndex are on the inside. */
		this.computeHullVertices(lineB, i + 1, j - 1);
	}

	private int indexOfFurthestVertex(final Vec2Line pLine, final int pFromIndex, final int pToIndex) {
		final Vec2[] vertices = this.mVertices;

		int f = pFromIndex;
		float mx = 0;
		for(int i = pFromIndex; i <= pToIndex; i++) {
			final float d = -Vec2Util.area2(vertices[i], pLine);
			if(d > mx || d == mx && vertices[i].x > vertices[f].y) {
				mx = d;
				f = i;
			}
		}
		return f;
	}

	private int partition(final Vec2Line pLine, final int pFromIndex, final int pToIndex) {
		final Vec2[] vertices = this.mVertices;

		int i = pFromIndex;
		int j = pToIndex;
		while(i <= j) {
			while(i <= j && Vec2Util.isRightOf(vertices[i], pLine)) {
				i++;
			}
			while(i <= j && !Vec2Util.isRightOf(vertices[j], pLine)) {
				j--;
			}
			if(i <= j) {
				this.swap(i++, j--);
			}
		}
		return i;
	}

}