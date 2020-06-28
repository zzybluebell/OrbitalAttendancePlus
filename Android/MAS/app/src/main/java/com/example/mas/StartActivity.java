package com.example.mas;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private ImageView iv_start;
    private TextView mCountDownTextView;
    private CountDownTimer mCountDownTimer;
    private static final String FILENAME = "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        initTimer();
        initImage();
    }

    private void initTimer() {
        mCountDownTextView = (TextView) findViewById(R.id.start_skip_count_down);

        mCountDownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });

        mCountDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                mCountDownTextView.setText(millisUntilFinished / 1000 + "s | Click to Skip");
            }
            public void onFinish() {
                mCountDownTextView.setText("0s | Click to Skip");
            }
        }.start();
    }

    private void initImage() {
        iv_start = (ImageView) findViewById(R.id.iv_start);
        iv_start.setImageResource(R.drawable.start_activity_background);
        // 进行缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(5000);
        // 动画播放完成后保持形状
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 可以在这里先进行某些操作
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_start.startAnimation(scaleAnimation);
    }

    private void startActivity() {
        if(getUserId() != null && getPassword() != null) {
            if(getUserType().equals("student")) {
                Intent intent = new Intent(StartActivity.this, StudentModeActivity.class);
                String AdminNoTxt = getUserId();
                intent.putExtra("AdminNo", AdminNoTxt);
                startActivity(intent);
            }
            else if(getUserType().equals("teacher")) {
                Intent intent = new Intent(StartActivity.this, TeacherModeActivity.class);
                String TeacherIC = getUserId();
                intent.putExtra("TeacherIC", TeacherIC);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
        else {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private String getUserType() {
        String UserType = null;
        try {
            UserType = GetSetSharedPreferences.getDefaults("userType", getApplicationContext());
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return UserType;
    }

    private String getUserId() {
        String AdminNoTxt = null;
        try {
            AdminNoTxt = GetSetSharedPreferences.getDefaults("userId", getApplicationContext());
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return AdminNoTxt;
    }

    private String getPassword() {
        String PasswordTxt = null;
        try {
            PasswordTxt = GetSetSharedPreferences.getDefaults("password", getApplicationContext());
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return PasswordTxt;
    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }
}
