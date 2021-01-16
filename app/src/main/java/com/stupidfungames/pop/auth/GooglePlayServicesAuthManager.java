package com.stupidfungames.pop.auth;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.android.gms.games.Games.SCOPE_GAMES_LITE;
import static com.google.android.gms.games.Games.SCOPE_GAMES_SNAPSHOTS;
import static com.stupidfungames.pop.analytics.Events.ALREADY_LOGGED_IN;
import static com.stupidfungames.pop.analytics.Events.LOGIN_SUCCESS;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.analytics.Events;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.dialog.ToastDialogActivity;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GooglePlayServicesAuthManager {

  public interface LoginListener {
    void onLoginStart();
    void onLoggedIn(GoogleSignInAccount account);
    void onLoggedOut();
    void onLoginCanceled();
    void onLoginFailed(Exception e);
  }

  private static final String PLAYER_REJECTED_LOGIN_PREFERENCE = "player_rejected_login";
  private static final Scope[] REQUIRED_PERMISSIONS =
      new Scope[] {Drive.SCOPE_APPFOLDER, SCOPE_GAMES_LITE, SCOPE_GAMES_SNAPSHOTS};

  GoogleSignInOptions  signInOptions =
      new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
          .requestScopes(
              REQUIRED_PERMISSIONS[0],
              Arrays.copyOfRange(REQUIRED_PERMISSIONS, 1, REQUIRED_PERMISSIONS.length))
          .build();

  private final Context context;
  private final Set<LoginListener> listeners = new HashSet<>();
  private boolean isLoggingIn = false;
  private ActivityResultLauncher <Intent> startSignInForResult;

  public GooglePlayServicesAuthManager(Context context) {
    this.context = context;
  }

  @Nullable
  public GoogleSignInAccount getLoggedInAccount() {
    return GoogleSignIn.getLastSignedInAccount(context);
  }

  public boolean isLoggedIn() {
    return getLoggedInAccount() != null;
  }

  // Starts a login on app launch if the user has not already declined to login
  public void maybeLoginOnAppStart(HostActivity hostActivity) {
    if (!GamePreferencesManager.getBoolean(context, PLAYER_REJECTED_LOGIN_PREFERENCE)) {
      initiateLogin(hostActivity);
    }
  }

  public void initiateLogin(HostActivity hostActivity) {
    initiateLogin(hostActivity, null);
  }

  /**
   * Starts the login flow
   * @param hostActivity the activity from which we are calling this login
   * @param listener the listener to add to the set of listeners
   */
  public void initiateLogin(final HostActivity hostActivity, @Nullable LoginListener listener) {
    if (isLoggingIn) return;
    onLoginStart();

    if (listener != null) {
      listeners.add(listener);
    }

    GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(context);
    if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
      // Already signed in.
      // The signed in account is stored in the 'account' variable.
      if (account != null) {
        onLogin(false, account);
      } else {
        onLoginFailed(false, new IllegalStateException("Null account?"));
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
                    performExplicitSignIn(hostActivity);
                  } else {
                    onLogin(false, signedInAccount);
                  }
                }
              });
    }
  }

  public void logout() {
    getSignInClient().signOut();
    onLoggedOut();
  }

  /**
   * Performs a sign in by the user via UI
   */
  private void performExplicitSignIn(HostActivity hostActivity) {
    startSignInForResult = hostActivity.prepareCall(
        new StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
          @Override
          public void onActivityResult(ActivityResult result) {
            GooglePlayServicesAuthManager.this.onActivityResult(result);
          }
        });
    startSignInForResult.launch(getSignInClient().getSignInIntent());
    Logger.logSelect(context, Events.LOGIN_START);
  }

  private void onActivityResult(ActivityResult activityResult) {
    if (activityResult.getResultCode() == RESULT_CANCELED) {
      GamePreferencesManager.set(context, PLAYER_REJECTED_LOGIN_PREFERENCE, true);
      onLoginCancelled();
      return;
    }
    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(activityResult.getData());
    if (result != null && result.isSuccess() && result.getSignInAccount() != null) {
      // The signed in account is stored in the result.
      onLogin(true, result.getSignInAccount());
    } else {
      ToastDialogActivity.start(R.string.signin_other_error, context);
      onLoginFailed(true, new RuntimeException(result.getStatus().getStatusMessage()));
    }
  }


  private GoogleSignInClient getSignInClient() {
    return GoogleSignIn.getClient(context, signInOptions);
  }

  public void addListener(LoginListener listener) {
    if (isLoggedIn()) {
      listener.onLoggedIn(getLoggedInAccount());
    } else {
      listener.onLoggedOut();
    }
    listeners.add(listener);
  }

  public void removeListener(LoginListener listener) {
    listeners.remove(listener);
  }

  private void onLoginStart() {
    isLoggingIn = true;
    for (LoginListener listener : listeners) {
      listener.onLoginStart();
    }
  }

  private void onLogin(boolean loggedInFromFlow, GoogleSignInAccount account) {
    // If the user ended up logging in then we want to auto sign them in all subsequent times they
    // Open the app
    GamePreferencesManager.set(context, PLAYER_REJECTED_LOGIN_PREFERENCE, false);
    isLoggingIn = false;
    for (LoginListener listener : listeners) {
      listener.onLoggedIn(account);
    }
    if (loggedInFromFlow) {
      Logger.logSelect(context, LOGIN_SUCCESS);
    } else {
      Logger.logSelect(context, ALREADY_LOGGED_IN);
    }
  }

  private void onLoggedOut() {
    isLoggingIn = false;
    for (LoginListener listener : listeners) {
      listener.onLoggedOut();
    }
    Logger.logSelect(context, Events.LOGOUT);
  }

  private void onLoginFailed(boolean fromFlow,Exception e) {
    isLoggingIn = false;
    for (LoginListener listener : listeners) {
      listener.onLoginFailed(e);
    }
    if (fromFlow) {
      Logger.logSelect(context, Events.LOGIN_FAILED);
    }
  }

  private void onLoginCancelled() {
    isLoggingIn = false;
    for (LoginListener listener : listeners) {
      listener.onLoginCanceled();
    }
  }

}
