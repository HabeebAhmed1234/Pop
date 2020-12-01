package com.stupidfungames.pop.physics;

import com.stupidfungames.pop.physics.util.constants.PhysicsConstants;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.shape.IShape;
import org.jbox2d.dynamics.Body;

public interface IPhysicsConnector extends IUpdateHandler, PhysicsConstants {

  IShape getShape();

  Body getBody();

  void clear();
}
