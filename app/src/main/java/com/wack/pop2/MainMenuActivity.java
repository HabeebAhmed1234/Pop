package com.wack.pop2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    private ValueAnimator logoAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitGame();
            }
        });

        animateLogo();
    }

    private void startNewGame() {
        startActivity(GameActivity.newIntent(this));
        this.finish();
    }

    private void  quitGame() {
        this.finish();
    }

    private void animateLogo() {
        final View logo = findViewById(R.id.logo);
        logoAnimator = ObjectAnimator.ofFloat(1,0.5f, 1,1,1,1,1,1,1,1,0.7f,1);
        logoAnimator.setDuration(2000);
        logoAnimator.start();
        logoAnimator.setRepeatMode(ValueAnimator.RESTART);

        logoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                logo.setAlpha((float) animation.getAnimatedValue());
            }
        });
        logoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoAnimator.end();
    }
}