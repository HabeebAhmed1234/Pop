package com.wack.pop2.physics.util.triangulation;

import org.jbox2d.common.Vec2;

import java.util.List;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 20:16:04 - 14.09.2010
 */
public interface ITriangulationAlgoritm {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * @return a {@link List} of {@link Vec2} objects where every three {@link Vec2} objects form a triangle.
	 */
	public List<Vec2> computeTriangles(final List<Vec2> pVertices);
}
