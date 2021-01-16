package com.stupidfungames.pop.auth;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;

/**
 * NOOP impl of {@link LoginListener}
 */
public abstract class LoginListenerImpl implements LoginListener {

  @Override
  public void onLoginStart() {

  }

  @Override
  public void onLoggedIn(GoogleSignInAccount account) {

  }

  @Override
  public void onLoggedOut() {

  }

  @Override
  public void onLoginCanceled() {

  }

  @Override
  public void onLoginFailed(Exception e) {

  }
}
