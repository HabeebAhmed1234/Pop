package com.stupidfungames.pop;

import android.view.Gravity;
import android.view.View;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;

public class GoogleAuthPopupView {

  public GoogleAuthPopupView(final View rootView, HostActivity hostActivity) {

    hostActivity.getAuthManager()
        .addListener(new LoginListener() {
          @Override
          public void onLoggedIn(GoogleSignInAccount account) {
            showPopup(rootView, account);
          }

          @Override
          public void onLoggedOut() { }

          @Override
          public void onLoginFailed(Exception e) {}

          @Override
          public void onLoginCanceled() {}
        });
      }

  private void showPopup(View rootView, GoogleSignInAccount account) {
    GamesClient gamesClient= Games.getGamesClient(rootView.getContext(), account);
    gamesClient.setViewForPopups(rootView);
    gamesClient.setGravityForPopups(Gravity.TOP);
  }
}
