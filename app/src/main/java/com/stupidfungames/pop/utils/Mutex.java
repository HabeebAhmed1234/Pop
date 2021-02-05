package com.stupidfungames.pop.utils;

/**
 * Simple entity that can be bound at any binder to provide a mutex for all its children.
 */
public class Mutex {

  private boolean isLocked;

  public synchronized boolean isLocked() {
    return isLocked;
  }

  public synchronized void setIsLocked(boolean isLocked) {
    this.isLocked = isLocked;
  }
}
