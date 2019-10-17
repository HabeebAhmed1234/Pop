package com.wack.pop2.physics;

import com.wack.pop2.physics.util.constants.PhysicsConstants;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.util.Constants;
import org.andengine.util.math.MathUtils;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;

import java.util.List;

import static com.wack.pop2.physics.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 13:59:03 - 15.07.2010
 */
public class PhysicsFactory {
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

	public static FixtureDef createFixtureDef(final float pDensity, final float pElasticity, final float pFriction) {
		return PhysicsFactory.createFixtureDef(pDensity, pElasticity, pFriction, false);
	}

	public static FixtureDef createFixtureDef(final float pDensity, final float pElasticity, final float pFriction, final boolean pSensor) {
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = pDensity;
		fixtureDef.restitution = pElasticity;
		fixtureDef.friction = pFriction;
		fixtureDef.isSensor = pSensor;
		return fixtureDef;
	}

	public static FixtureDef createFixtureDef(final float pDensity, final float pElasticity, final float pFriction, final boolean pSensor, final short pCategoryBits, final short pMaskBits, final short pGroupIndex) {
		final FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = pDensity;
		fixtureDef.restitution = pElasticity;
		fixtureDef.friction = pFriction;
		fixtureDef.isSensor = pSensor;
		final Filter filter = fixtureDef.filter;
		filter.categoryBits = pCategoryBits;
		filter.maskBits = pMaskBits;
		filter.groupIndex = pGroupIndex;
		return fixtureDef;
	}

	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createBoxBody(pPhysicsWorld, pAreaShape, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final float[] sceneCenterCoordinates = pAreaShape.getSceneCenterCoordinates();
		final float centerX = sceneCenterCoordinates[Constants.VERTEX_INDEX_X];
		final float centerY = sceneCenterCoordinates[Constants.VERTEX_INDEX_Y];
		return PhysicsFactory.createBoxBody(pPhysicsWorld, centerX, centerY, pAreaShape.getWidthScaled(), pAreaShape.getHeightScaled(), pAreaShape.getRotation(), pBodyType, pFixtureDef, pPixelToMeterRatio);
	}

	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pWidth, final float pHeight, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createBoxBody(pPhysicsWorld, pCenterX, pCenterY, pWidth, pHeight, 0, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pWidth, final float pHeight, final float pRotation, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createBoxBody(pPhysicsWorld, pCenterX, pCenterY, pWidth, pHeight, pRotation, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pWidth, final float pHeight, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		return PhysicsFactory.createBoxBody(pPhysicsWorld, pCenterX, pCenterY, pWidth, pHeight, 0, pBodyType, pFixtureDef, pPixelToMeterRatio);
	}

	public static Body createBoxBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pWidth, final float pHeight, final float pRotation, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = pBodyType;

		boxBodyDef.position.x = pCenterX / pPixelToMeterRatio;
		boxBodyDef.position.y = pCenterY / pPixelToMeterRatio;

		final Body boxBody = pPhysicsWorld.createBody(boxBodyDef);

		final PolygonShape boxPoly = new PolygonShape();

		final float halfWidth = pWidth * 0.5f / pPixelToMeterRatio;
		final float halfHeight = pHeight * 0.5f / pPixelToMeterRatio;

		boxPoly.setAsBox(halfWidth, halfHeight);
		pFixtureDef.shape = boxPoly;

		boxBody.createFixture(pFixtureDef);

		// TODO: figure out why this is
		// boxPoly.dispose();

		boxBody.setTransform(boxBody.getWorldCenter(), MathUtils.degToRad(pRotation));

		return boxBody;
	}

	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createCircleBody(pPhysicsWorld, pAreaShape, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final IAreaShape pAreaShape, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final float[] sceneCenterCoordinates = pAreaShape.getSceneCenterCoordinates();
		final float centerX = sceneCenterCoordinates[Constants.VERTEX_INDEX_X];
		final float centerY = sceneCenterCoordinates[Constants.VERTEX_INDEX_Y];
		return PhysicsFactory.createCircleBody(pPhysicsWorld, centerX, centerY, pAreaShape.getWidthScaled() * 0.5f, pAreaShape.getRotation(), pBodyType, pFixtureDef, pPixelToMeterRatio);
	}

	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pRadius, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return createCircleBody(pPhysicsWorld, pCenterX, pCenterY, pRadius, 0, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pRadius, final float pRotation, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return createCircleBody(pPhysicsWorld, pCenterX, pCenterY, pRadius, pRotation, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pRadius, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		return createCircleBody(pPhysicsWorld, pCenterX, pCenterY, pRadius, 0, pBodyType, pFixtureDef, pPixelToMeterRatio);
	}

	public static Body createCircleBody(final PhysicsWorld pPhysicsWorld, final float pCenterX, final float pCenterY, final float pRadius, final float pRotation, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final BodyDef circleBodyDef = new BodyDef();
		circleBodyDef.type = pBodyType;

		circleBodyDef.position.x = pCenterX / pPixelToMeterRatio;
		circleBodyDef.position.y = pCenterY / pPixelToMeterRatio;

		circleBodyDef.angle = MathUtils.degToRad(pRotation);

		final Body circleBody = pPhysicsWorld.createBody(circleBodyDef);

		final CircleShape circlePoly = new CircleShape();
		pFixtureDef.shape = circlePoly;

		final float radius = pRadius / pPixelToMeterRatio;
		circlePoly.setRadius(radius);

		circleBody.createFixture(pFixtureDef);

		// TODO: figure out why this is
		// circlePoly.dispose();

		return circleBody;
	}

	/**
	 * @param pPhysicsWorld
	 * @param pShape
	 * @param pVertices are to be defined relative to the center of the pShape and have the {@link PhysicsConstants#PIXEL_TO_METER_RATIO_DEFAULT} applied.
	 * @param pBodyType
	 * @param pFixtureDef
	 * @return
	 */
	public static Body createPolygonBody(final PhysicsWorld pPhysicsWorld, final IShape pShape, final Vec2[] pVertices, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createPolygonBody(pPhysicsWorld, pShape, pVertices, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	/**
	 * @param pPhysicsWorld
	 * @param pShape
	 * @param pVertices are to be defined relative to the center of the pShape.
	 * @param pBodyType
	 * @param pFixtureDef
	 * @return
	 */
	public static Body createPolygonBody(final PhysicsWorld pPhysicsWorld, final IShape pShape, final Vec2[] pVertices, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = pBodyType;

		final float[] sceneCenterCoordinates = pShape.getSceneCenterCoordinates();
		boxBodyDef.position.x = sceneCenterCoordinates[Constants.VERTEX_INDEX_X] / pPixelToMeterRatio;
		boxBodyDef.position.y = sceneCenterCoordinates[Constants.VERTEX_INDEX_Y] / pPixelToMeterRatio;

		final Body boxBody = pPhysicsWorld.createBody(boxBodyDef);

		final PolygonShape boxPoly = new PolygonShape();

		boxPoly.set(pVertices, pVertices.length);
		pFixtureDef.shape = boxPoly;

		boxBody.createFixture(pFixtureDef);

		// TODO: figure out why this is
		// boxPoly.dispose();

		return boxBody;
	}


	/**
	 * @param pPhysicsWorld
	 * @param pShape
	 * @param pTriangleVertices are to be defined relative to the center of the pShape and have the {@link PhysicsConstants#PIXEL_TO_METER_RATIO_DEFAULT} applied.
	 * @param pBodyType
	 * @param pFixtureDef
	 * @return
	 */
	public static Body createTrianglulatedBody(final PhysicsWorld pPhysicsWorld, final IShape pShape, final List<Vec2> pTriangleVertices, final BodyType pBodyType, final FixtureDef pFixtureDef) {
		return PhysicsFactory.createTrianglulatedBody(pPhysicsWorld, pShape, pTriangleVertices, pBodyType, pFixtureDef, PIXEL_TO_METER_RATIO_DEFAULT);
	}

	/**
	 * @param pPhysicsWorld
	 * @param pShape
	 * @param pTriangleVertices are to be defined relative to the center of the pShape and have the {@link PhysicsConstants#PIXEL_TO_METER_RATIO_DEFAULT} applied.
	 * 					The vertices will be triangulated and for each triangle a {@link Fixture} will be created.
	 * @param pBodyType
	 * @param pFixtureDef
	 * @return
	 */
	public static Body createTrianglulatedBody(final PhysicsWorld pPhysicsWorld, final IShape pShape, final List<Vec2> pTriangleVertices, final BodyType pBodyType, final FixtureDef pFixtureDef, final float pPixelToMeterRatio) {
		final Vec2[] TMP_TRIANGLE = new Vec2[3];

		final BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = pBodyType;

		final float[] sceneCenterCoordinates = pShape.getSceneCenterCoordinates();
		boxBodyDef.position.x = sceneCenterCoordinates[Constants.VERTEX_INDEX_X] / pPixelToMeterRatio;
		boxBodyDef.position.y = sceneCenterCoordinates[Constants.VERTEX_INDEX_Y] / pPixelToMeterRatio;

		final Body boxBody = pPhysicsWorld.createBody(boxBodyDef);

		final int vertexCount = pTriangleVertices.size();
		for(int i = 0; i < vertexCount; /* */) {
			final PolygonShape boxPoly = new PolygonShape();

			TMP_TRIANGLE[2] = pTriangleVertices.get(i++);
			TMP_TRIANGLE[1] = pTriangleVertices.get(i++);
			TMP_TRIANGLE[0] = pTriangleVertices.get(i++);

			boxPoly.set(TMP_TRIANGLE, TMP_TRIANGLE.length);
			pFixtureDef.shape = boxPoly;

			boxBody.createFixture(pFixtureDef);

			// boxPoly.dispose();
		}

		return boxBody;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
