package com.stupidfungames.pop.savegame;

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
import java.util.Arrays;

public class GooglePlayServicesAuthManager {

  public interface HostActivity {
    void startActivityForResult(Intent intent, int rc);
  }

  private static final int RC_SIGN_IN = 1;
  private static final Scope[] REQUIRED_PERMISSIONS =
      new Scope[] {Drive.SCOPE_APPFOLDER, SCOPE_GAMES_LITE};
  GoogleSignInOptions  signInOptions =
      new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
          .requestScopes(
              REQUIRED_PERMISSIONS[0],
              Arrays.copyOfRange(REQUIRED_PERMISSIONS, 1, REQUIRED_PERMISSIONS.length))
          .build();

  @Nullable private View rootView;
  private HostActivity hostActivity;
  private Context context;
  private SettableFuture<GoogleSignInAccount> signInAccountFuture;

  public GooglePlayServicesAuthManager(HostActivity hostActivity, Context context) {
    this(null, hostActivity, context);
  }

  public GooglePlayServicesAuthManager(
      @Nullable View rootView, HostActivity hostActivity, Context context) {
    this.rootView = rootView;
    this.hostActivity = hostActivity;
    this.context = context;
  }

  public ListenableFuture<GoogleSignInAccount> getAccount() {
    if (signInAccountFuture != null) {
      return signInAccountFuture;
    }

    GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(context);
    signInAccountFuture = SettableFuture.create();
    if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
      // Already signed in.
      // The signed in account is stored in the 'account' variable.
      if (account != null) {
        signInAccountFuture.set(account);
      } else {
        signInAccountFuture.setException(new IllegalStateException("Null account?"));
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
                    signInAccountFuture.set(signedInAccount);
                  }
                }
              });
    }

    addSignInListener(signInAccountFuture);

    return signInAccountFuture;
  }

  public Task<Void> logout() {
    signInAccountFuture = null;
    GoogleSignInClient signInClient = getSignInClient();
    return signInClient.signOut();
  }

  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result != null && result.isSuccess() && result.getSignInAccount() != null) {
        // The signed in account is stored in the result.
        signInAccountFuture.set(result.getSignInAccount());
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

  /**
   * This is where we add anything we need to do as soon as the user is signed in
   */
  private void addSignInListener(ListenableFuture future) {
    if (rootView == null) return;
    Futures.addCallback(future,
        new FutureCallback<GoogleSignInAccount>() {
          @Override
          public void onSuccess(GoogleSignInAccount result) {
            GamesClient gamesClient= Games.getGamesClient(context, result);
            gamesClient.setViewForPopups(rootView);
            gamesClient.setGravityForPopups(Gravity.TOP);
          }

          @Override
          public void onFailure(Throwable t) {}
        }, ContextCompat.getMainExecutor(context));
  }
}
