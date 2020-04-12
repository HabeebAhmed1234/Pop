package com.stupidfungames.pop.savegame;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_REQUIRED;
import static com.google.android.gms.games.Games.SCOPE_GAMES_LITE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.MainMenuActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GooglePlayServicesAuthManager {

  public interface LoginListener {
    void onLoggedIn(GoogleSignInAccount account);
    void onLoggedOut();
    void onLoginFailed(Exception e);
  }

  public interface HostActivity {
    void startActivityForResult(Intent intent, int rc);
  }

  private static final String PLAYER_REJECTED_LOGIN_PREFERENCE = "player_rejected_login";

  private static final int RC_SIGN_IN = 1;
  private static final Scope[] REQUIRED_PERMISSIONS =
      new Scope[] {Drive.SCOPE_APPFOLDER, SCOPE_GAMES_LITE};

  GoogleSignInOptions  signInOptions =
      new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
          .requestScopes(
              REQUIRED_PERMISSIONS[0],
              Arrays.copyOfRange(REQUIRED_PERMISSIONS, 1, REQUIRED_PERMISSIONS.length))
          .build();

  @Nullable private final View rootView;
  private final HostActivity hostActivity;
  private final Context context;

  private boolean isLoggingIn = false;
  @Nullable private GoogleSignInAccount loggedInAccount;

  private Set<LoginListener> listeners = new HashSet<>();

  public GooglePlayServicesAuthManager(HostActivity hostActivity, Context context) {
    this(null, hostActivity, context);
  }

  public GooglePlayServicesAuthManager(
      @Nullable View rootView, HostActivity hostActivity, Context context) {
    this.rootView = rootView;
    this.hostActivity = hostActivity;
    this.context = context;
  }

  @Nullable
  public GoogleSignInAccount getLoggedInAccount() {
    return loggedInAccount;
  }

  public boolean isLoggedIn() {
    return loggedInAccount != null;
  }

  // Starts a login on app launch if the user has not already declined to login
  public void maybeLoginOnAppStart() {
    if (!GamePreferencesManager.getBoolean(context, PLAYER_REJECTED_LOGIN_PREFERENCE)) {
      initiateLogin();
    }
  }

  public void initiateLogin() {
    initiateLogin(null);
  }

  /**
   * Starts the login flow
   * @param listener the listener to add to the set of listeners
   */
  public void initiateLogin(@Nullable LoginListener listener) {
    if (isLoggingIn) return;
    if (loggedInAccount != null && listener != null) {
      listener.onLoggedIn(loggedInAccount);
      return;
    }
    isLoggingIn = true;

    if (listener != null) {
      listeners.add(listener);
    }

    GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(context);
    if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
      // Already signed in.
      // The signed in account is stored in the 'account' variable.
      if (account != null) {
        onLogin(account);
      } else {
        onLoginFailed(new IllegalStateException("Null account?"));
      }
    } else {
      // Haven't been signed-in before. Try the silent sign-in first.
      GoogleSignInClient signInClient = getSignInClient();
      signInClient
          .silentSignIn()
          .addOnCompleteListener(
              ContextCompat.getMainExecutor(context),
              new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                  GoogleSignInAccount signedInAccount = null;
                  try {
                    signedInAccount = task.getResult(ApiException.class);
                  } catch (ApiException e) {
                    Log.e("AuthManager", "error logging in", e);
                  }
                  if (signedInAccount == null) {
                    performExplicitSignIn();
                  } else {
                    onLogin(signedInAccount);
                  }
                }
              });
    }
  }

  public void logout() {
    getSignInClient().signOut();
    onLoggedOut();
  }

  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == RC_SIGN_IN) {
      if (resultCode == RESULT_CANCELED) {
        GamePreferencesManager.set(context, PLAYER_REJECTED_LOGIN_PREFERENCE, true);
       return;
      }
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result != null && result.isSuccess() && result.getSignInAccount() != null) {
        // The signed in account is stored in the result.
        onLogin(result.getSignInAccount());
      } else {
        String message = result.getStatus().getStatusMessage();
        if (message == null || message.isEmpty()) {
          message = context.getString(R.string.signin_other_error);
        }
        // TODO make this our own UI
        new AlertDialog.Builder(context).setMessage(message)
            .setNeutralButton(android.R.string.ok, null).show();
      }
    }
  }

  /**
   * Performs a sign in by the user via UI
   */
  private void performExplicitSignIn() {
    GoogleSignInClient signInClient = getSignInClient();
    Intent intent = signInClient.getSignInIntent();
    hostActivity.startActivityForResult(intent, RC_SIGN_IN);
  }

  private GoogleSignInClient getSignInClient() {
    return GoogleSignIn.getClient(context, signInOptions);
  }

  private void showPopup(GoogleSignInAccount account) {
    if (rootView == null) return;
    GamesClient gamesClient= Games.getGamesClient(context, account);
    gamesClient.setViewForPopups(rootView);
    gamesClient.setGravityForPopups(Gravity.TOP);
  }

  public void addListener(LoginListener listener) {
    if (loggedInAccount != null) {
      listener.onLoggedIn(loggedInAccount);
    }
    listeners.add(listener);
  }

  private void onLogin(GoogleSignInAccount account) {
    isLoggingIn = false;
    loggedInAccount = account;
    for (LoginListener listener : listeners) {
      listener.onLoggedIn(account);
    }
    showPopup(account);
  }

  private void onLoggedOut() {
    isLoggingIn = false;
    loggedInAccount = null;
    for (LoginListener listener : listeners) {
      listener.onLoggedOut();
    }
  }

  private void onLoginFailed(Exception e) {
    isLoggingIn = false;
    loggedInAccount = null;
    for (LoginListener listener : listeners) {
      listener.onLoginFailed(e);
    }
  }

}
