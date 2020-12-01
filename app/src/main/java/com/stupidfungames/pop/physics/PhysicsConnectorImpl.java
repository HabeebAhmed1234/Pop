package com.stupidfungames.pop.physics;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.util.math.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 18:51:22 - 05.07.2010
 */
public class PhysicsConnectorImpl implements IPhysicsConnector {
  // ===========================================================
  // Constants
  // ===========================================================

  // ===========================================================
  // Fields
  // ===========================================================

  protected IShape mShape;
  protected Body mBody;

  protected float mShapeHalfBaseWidth;
  protected float mShapeHalfBaseHeight;

  protected boolean mUpdatePosition;
  protected boolean mUpdateRotation;
  protected float mPixelToMeterRatio;

  // ===========================================================
  // Constructors
  // ===========================================================

  public PhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody) {
    this(pAreaShape, pBody, true, true);
  }

  public PhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody,
      final float pPixelToMeterRatio) {
    this(pAreaShape, pBody, true, true, pPixelToMeterRatio);
  }

  public PhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody,
      final boolean pUdatePosition, final boolean pUpdateRotation) {
    this(pAreaShape, pBody, pUdatePosition, pUpdateRotation, PIXEL_TO_METER_RATIO_DEFAULT);
  }

  public PhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody,
      final boolean pUdatePosition, final boolean pUpdateRotation, final float pPixelToMeterRatio) {
    this.mShape = pAreaShape;
    this.mBody = pBody;

    this.mUpdatePosition = pUdatePosition;
    this.mUpdateRotation = pUpdateRotation;
    this.mPixelToMeterRatio = pPixelToMeterRatio;

    this.mShapeHalfBaseWidth = pAreaShape.getWidth() * 0.5f;
    this.mShapeHalfBaseHeight = pAreaShape.getHeight() * 0.5f;
  }

  @Override
  public void clear() {
    mShape = null;
    mBody = null;
    mShapeHalfBaseWidth = 0;
    mShapeHalfBaseHeight = 0;
    mUpdatePosition = false;
    mUpdateRotation = false;
    mPixelToMeterRatio = 0;
  }

  // ===========================================================
  // Getter & Setter
  // ===========================================================

  @Override
  public IShape getShape() {
    return this.mShape;
  }

  @Override
  public Body getBody() {
    return this.mBody;
  }

  public boolean isUpdatePosition() {
    return this.mUpdatePosition;
  }

  public boolean isUpdateRotation() {
    return this.mUpdateRotation;
  }

  public void setUpdatePosition(final boolean pUpdatePosition) {
    this.mUpdatePosition = pUpdatePosition;
  }

  public void setUpdateRotation(final boolean pUpdateRotation) {
    this.mUpdateRotation = pUpdateRotation;
  }

  // ===========================================================
  // Methods for/from SuperClass/Interfaces
  // ===========================================================

  @Override
  public void onUpdate(final float pSecondsElapsed) {
    final IShape shape = this.mShape;
    final Body body = this.mBody;

    if (this.mUpdatePosition) {
      final Vec2 position = body.getPosition();
      final float pixelToMeterRatio = this.mPixelToMeterRatio;
      shape.setPosition(position.x * pixelToMeterRatio - this.mShapeHalfBaseWidth,
          position.y * pixelToMeterRatio - this.mShapeHalfBaseHeight);
    }

    if (this.mUpdateRotation) {
      final float angle = body.getAngle();
      shape.setRotation(MathUtils.radToDeg(angle));
    }
  }

  @Override
  public void reset() {

  }

  // ===========================================================
  // Methods
  // ===========================================================

  // ===========================================================
  // Inner and Anonymous Classes
  // ===========================================================
}
