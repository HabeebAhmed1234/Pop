package com.stupidfungames.pop.physics;

import androidx.core.util.Preconditions;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.util.math.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

/**
 * Connects the position and roation of a sprite to a body in the physics world. Body must be
 * STATIC.
 */
public class ReversePhysicsConnectorImpl implements IPhysicsConnector {

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

  public ReversePhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody) {
    this(pAreaShape, pBody, true, true);
  }

  public ReversePhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody,
      final float pPixelToMeterRatio) {
    this(pAreaShape, pBody, true, true, pPixelToMeterRatio);
  }

  public ReversePhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody,
      final boolean pUdatePosition, final boolean pUpdateRotation) {
    this(pAreaShape, pBody, pUdatePosition, pUpdateRotation, PIXEL_TO_METER_RATIO_DEFAULT);
  }

  public ReversePhysicsConnectorImpl(final IAreaShape pAreaShape, final Body pBody,
      final boolean pUdatePosition, final boolean pUpdateRotation, final float pPixelToMeterRatio) {

    Preconditions.checkArgument(pBody.getType() == BodyType.STATIC);

    this.mShape = pAreaShape;
    this.mBody = pBody;

    this.mUpdatePosition = pUdatePosition;
    this.mUpdateRotation = pUpdateRotation;
    this.mPixelToMeterRatio = pPixelToMeterRatio;

    this.mShapeHalfBaseWidth = pAreaShape.getWidth() * 0.5f;
    this.mShapeHalfBaseHeight = pAreaShape.getHeight() * 0.5f;
  }

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

  public IShape getShape() {
    return this.mShape;
  }

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

    Vec2 position = body.getPosition();
    if (this.mUpdatePosition) {
      final float pixelToMeterRatio = this.mPixelToMeterRatio;
      position = Vec2Pool.obtain((shape.getX() + this.mShapeHalfBaseWidth) / pixelToMeterRatio,
          (shape.getY() + this.mShapeHalfBaseHeight) / pixelToMeterRatio);
    }

    float rotationRads = body.getAngle();
    if (this.mUpdateRotation) {
      rotationRads = MathUtils.degToRad(shape.getRotation());
    }
    body.setTransform(position, rotationRads);
    body.setActive(shape.isVisible());
  }

  @Override
  public void reset() {

  }
}
