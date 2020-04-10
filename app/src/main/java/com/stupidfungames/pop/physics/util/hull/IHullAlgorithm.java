package com.stupidfungames.pop.physics.util.hull;

import org.jbox2d.common.Vec2;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 13:46:22 - 14.09.2010
 */
public interface IHullAlgorithm {
	// ===========================================================
	// Final Fields
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public int computeHull(final Vec2[] pVertices);
}
