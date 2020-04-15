package com.stupidfungames.pop.auth;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.android.gms.games.Games.SCOPE_GAMES_LITE;
import static com.google.android.gms.games.Games.SCOPE_GAMES_SNAPSHOTS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.stupidfungames.pop.dialog.ToastDialogActivity;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GooglePlayServicesAuthManager {

  public interface LoginListener {
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

  private final HostActivity hostActivity;
  private final Context context;

  private boolean isLoggingIn = false;
  @Nullable private GoogleSignInAccount loggedInAccount;

  private Set<LoginListener> listeners = new HashSet<>();

  private static GooglePlayServicesAuthManager sGooglePlayServicesAuthManager;

  public static GooglePlayServicesAuthManager get(Context context, HostActivity hostActivity) {
    if (sGooglePlayServicesAuthManager == null) {
      sGooglePlayServicesAuthManager = new GooglePlayServicesAuthManager(context, hostActivity);
    }
    return sGooglePlayServicesAuthManager;
  }

  private GooglePlayServicesAuthManager(Context context, HostActivity hostActivity) {
    this.context = context;
    this.hostActivity = hostActivity;

    loggedInAccount = GoogleSignIn.getLastSignedInAccount(context);
    if (loggedInAccount != null) {
      onLogin(loggedInAccount);
    }
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
    Log.d("asdasd", "initiateLogin isLoggingIn = true");
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

  /**
   * Performs a sign in by the user via UI
   */
  private void performExplicitSignIn() {
    ActivityResultLauncher <Intent> startSignInForResult = hostActivity.prepareCall(
        new StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
          @Override
          public void onActivityResult(ActivityResult result) {
            GooglePlayServicesAuthManager.this.onActivityResult(result);
          }
        });
    startSignInForResult.launch(getSignInClient().getSignInIntent());
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
      onLogin(result.getSignInAccount());
    } else {
      ToastDialogActivity.start(R.string.signin_other_error, context);
      onLoginFailed(new RuntimeException(result.getStatus().getStatusMessage()));
    }
  }


  private GoogleSignInClient getSignInClient() {
    return GoogleSignIn.getClient(context, signInOptions);
  }

  public void addListener(LoginListener listener) {
    if (loggedInAccount != null) {
      listener.onLoggedIn(loggedInAccount);
    }
    listeners.add(listener);
  }

  private void onLogin(GoogleSignInAccount account) {
    // If the user ended up logging in then we want to auto sign them in all subsequent times they
    // Open the app
    GamePreferencesManager.set(context, PLAYER_REJECTED_LOGIN_PREFERENCE, false);
    isLoggingIn = false;
    loggedInAccount = account;
    for (LoginListener listener : listeners) {
      listener.onLoggedIn(account);
    }
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

  private void onLoginCancelled() {
    isLoggingIn = false;
    loggedInAccount = null;
    for (LoginListener listener : listeners) {
      listener.onLoginCanceled();
    }
  }

}
