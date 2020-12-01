package com.stupidfungames.pop.physics;

import java.util.ArrayList;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.shape.IShape;
import org.jbox2d.dynamics.Body;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 15:52:27 - 15.07.2010
 */
public class PhysicsConnectorManager extends ArrayList<IPhysicsConnector> implements
    IUpdateHandler {
  // ===========================================================
  // Constants
  // ===========================================================

  private static final long serialVersionUID = 412969510084261799L;

  // ===========================================================
  // Fields
  // ===========================================================

  // ===========================================================
  // Constructors
  // ===========================================================

  PhysicsConnectorManager() {

  }

  // ===========================================================
  // Getter & Setter
  // ===========================================================

  // ===========================================================
  // Methods for/from SuperClass/Interfaces
  // ===========================================================

  @Override
  public void onUpdate(final float pSecondsElapsed) {
    final ArrayList<IPhysicsConnector> physicsConnectors = this;
    for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
      physicsConnectors.get(i).onUpdate(pSecondsElapsed);
    }
  }

  @Override
  public void reset() {
    final ArrayList<IPhysicsConnector> physicsConnectors = this;
    for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
      physicsConnectors.get(i).reset();
    }
  }

  // ===========================================================
  // Methods
  // ===========================================================

  public Body findBodyByShape(final IShape pShape) {
    final ArrayList<IPhysicsConnector> physicsConnectors = this;
    for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
      final IPhysicsConnector physicsConnector = physicsConnectors.get(i);
      if (physicsConnector.getShape() == pShape) {
        return physicsConnector.getBody();
      }
    }
    return null;
  }

  public IPhysicsConnector findPhysicsConnectorByShape(final IShape pShape) {
    final ArrayList<IPhysicsConnector> physicsConnectors = this;
    for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
      final IPhysicsConnector physicsConnector = physicsConnectors.get(i);
      if (physicsConnector.getShape() == pShape) {
        return physicsConnector;
      }
    }
    return null;
  }

  public IPhysicsConnector findPhysicsConnectorByBody(final Body body) {
    final ArrayList<IPhysicsConnector> physicsConnectors = this;
    for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
      final IPhysicsConnector physicsConnector = physicsConnectors.get(i);
      if (physicsConnector.getBody() == body) {
        return physicsConnector;
      }
    }
    return null;
  }

  // ===========================================================
  // Inner and Anonymous Classes
  // ===========================================================
}
