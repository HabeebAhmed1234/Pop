package com.stupidfungames.pop;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager.LoginListener;

public class PlayerProfileView implements LoginListener {

  private Context context;

  private TextView playerUserName;
  private View signInBtn;
  private View signOutBtn;

  private HostActivity hostActivity;

  private OnClickListener signInBtnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      hostActivity.getAuthManager().initiateLogin(hostActivity);
    }
  };

  private OnClickListener signOutBtnClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      hostActivity.getAuthManager().logout();
    }
  };

  public PlayerProfileView(
      final ViewGroup viewGroup,
      final HostActivity hostActivity) {
    this.context = viewGroup.getContext();
    this.hostActivity = hostActivity;
    playerUserName = viewGroup.findViewById(R.id.username);
    signInBtn = viewGroup.findViewById(R.id.sign_in_btn);
    signOutBtn = viewGroup.findViewById(R.id.sign_out_btn);

    hostActivity.getAuthManager().addListener(this);
  }

  @Override
  public void onLoginStart() {
  }

  @Override
  public void onLoggedIn(GoogleSignInAccount account) {
    renderLoggedInState(account);
  }

  @Override
  public void onLoggedOut() {
    renderLoggedOutState();
  }

  @Override
  public void onLoginFailed(Exception e) {
    Toast.makeText(hostActivity.getContext(), R.string.signin_other_error, Toast.LENGTH_SHORT)
        .show();
    renderLoggedOutState();
  }

  @Override
  public void onLoginCanceled() {
    Toast.makeText(hostActivity.getContext(), R.string.signin_canceled, Toast.LENGTH_SHORT)
        .show();
    renderLoggedOutState();
  }

  private void renderLoggedInState(GoogleSignInAccount account) {
    // The user was logged in. Show their username and the sign out button
    PlayersClient client = Games.getPlayersClient(context, account);
    client.getCurrentPlayer().addOnCompleteListener(new OnCompleteListener<Player>() {
      @Override
      public void onComplete(@NonNull Task<Player> task) {
        if (task.isSuccessful()) {
          Player player = task.getResult();
          if (player != null) {
            playerUserName.setText(player.getDisplayName());
            playerUserName.setVisibility(View.VISIBLE);
            signInBtn.setVisibility(View.GONE);
            signOutBtn.setVisibility(View.VISIBLE);
            setClickListeners(true);
          }
        }
      }
    });
  }

  private void renderLoggedOutState() {
    playerUserName.setText("");
    playerUserName.setVisibility(View.GONE);
    signInBtn.setVisibility(View.VISIBLE);
    signOutBtn.setVisibility(View.GONE);

    setClickListeners(false);
  }

  private void setClickListeners(boolean isLoggedIn) {
    if (isLoggedIn) {
      signInBtn.setOnClickListener(null);
      signOutBtn.setOnClickListener(signOutBtnClickListener);
    } else {
      signInBtn.setOnClickListener(signInBtnClickListener);
      signOutBtn.setOnClickListener(null);
    }
  }
}
