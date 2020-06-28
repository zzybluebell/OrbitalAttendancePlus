package com.example.mas;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private getReqIdTask gTask = null;
    private studentLoginTask sTask = null;
    private teacherLoginTask tTask = null;

    private String userId;
    private String password;
    private EditText mUserIDText;
    private EditText mPasswordText;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSignInButton;
    private ImageView facebookLogo;
    private ImageView twitterLogo;
    private ImageView instagramLogo;
    private ImageView youtubeLogo;

    private String loginURL = "";
    private String requestID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initControl();
        init();

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = mUserIDText.getText().toString();
                password = mPasswordText.getText().toString();
                attemptLogin();
            }
        });

        facebookLogo = (ImageView) findViewById(R.id.facebook_logo);
        facebookLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = openFacebook(LoginActivity.this);
                startActivity(facebookIntent);
            }
        });

        twitterLogo = (ImageView) findViewById(R.id.twitter_logo);
        twitterLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twitterIntent = openTwitter(LoginActivity.this);
                startActivity(twitterIntent);
            }
        });

        instagramLogo = (ImageView) findViewById(R.id.instagram_logo);
        instagramLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instagramIntent = openInstagram(LoginActivity.this);
                startActivity(instagramIntent);
            }
        });

        youtubeLogo = (ImageView) findViewById(R.id.youtube_logo);
        youtubeLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent youtubeIntent = openYoutube(LoginActivity.this);
                startActivity(youtubeIntent);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        mUserIDText = (EditText) findViewById(R.id.userID);
        mPasswordText = (EditText) findViewById(R.id.password);
        mProgressView = (View) findViewById(R.id.login_progress);
        mLoginFormView = (View) findViewById(R.id.login_form);
    }

    /**
     * 初始化账号ID
     */
    private void init() {
        try {
            mUserIDText.setText(GetSetSharedPreferences.getDefaults("userId", getApplicationContext()));
        }
        catch(Exception exc) {
            System.out.println("SharedPreferences Error: " + exc);
        }
    }

    /**
     * 登录验证
     */
    private void attemptLogin() {
        if (gTask != null || sTask != null || tTask != null) {
            return;
        }

        // Reset errors.
        mUserIDText.setError(null);
        mPasswordText.setError(null);

        // Store values at the time of the login attempt.
        String userid = mUserIDText.getText().toString();
        String password = mPasswordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordText;
            cancel = true;
        }

        // Check for a valid user ID.
        if (TextUtils.isEmpty(userid)) {
            mUserIDText.setError(getString(R.string.error_field_required));
            focusView = mUserIDText;
            cancel = true;
        } else if (!isUserIdValid(userid)) {
            mUserIDText.setError("Please enter correct User ID");
            focusView = mUserIDText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            if(mUserIDText.getText().toString().trim().length() == 7) {
                // gTask = new getReqIdTask();
                // gTask.execute();
                sTask = new studentLoginTask();
                sTask.execute((Void) null);
            }
            else {
                tTask = new teacherLoginTask();
                tTask.execute();
            }

        }
    }

    private boolean isUserIdValid(String userid) {
        //TODO: Replace this with your own logic
        if(userid.length() == 7) {
            String regex = "^[0-9]{6}[a-zA-Z]{1}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userid);
            return matcher.matches();
        }
        else if (userid.length() == 9) {
            String regex = "^[a-zA-Z]{1}[0-9]{7}[a-zA-Z]{1}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userid);
            return matcher.matches();
        }
        else {
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 获取Request ID
     */
    private class getReqIdTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/studentLogin.php?status=login";
                URL requestIdUrl = new URL(path);
                conn = (HttpURLConnection) requestIdUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                int code = conn.getResponseCode();
                if (code == 200) {
                    is = conn.getInputStream();
                    resultData = readStream(is);
                    return resultData;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultData;
        }

        @Override
        protected void onPostExecute(String result) {
            gTask = null;

            if(result.equals("error")) {

            }
            else {
                String[] result_arr = result.split("_split_");
                requestID = result_arr[0];
                loginURL = result_arr[1];
                sTask = new studentLoginTask();
                sTask.execute((Void) null);
            }
        }

        @Override
        protected void onCancelled() {
            gTask = null;
            showProgress(false);
        }
    }

    public class studentLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            if(loginURL == "" || requestID == "") {
                return false;
            }
            else {
                final String uid = mUserIDText.getText().toString().trim();
                final String pas = mPasswordText.getText().toString().trim();
                try {
                    String path = "http://192.168.1.114/studentLogin.php";
                    String data = "username=" + URLEncoder.encode(uid) + "&password=" + URLEncoder.encode(pas);
                    URL loginUrl = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) loginUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 设置发送的数据为表单类型，会被添加到http body当中
                    conn.setRequestProperty("Content-Length", String.valueOf(data.length()));

                    conn.setDoOutput(true);// 运行当前的应用程序给服务器写数据
                    conn.setDoInput(true);
                    conn.getOutputStream().write(data.getBytes()); // post的请求是把数据以流的方式写给了服务器
                    conn.setInstanceFollowRedirects(true);
                    conn.getResponseCode();// trigger server redirect

                    int code = conn.getResponseCode();
                    if (code == 200) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            sTask = null;
            showProgress(false);

            if (success) {
//                GetSetSharedPreferences.setDefaults("userType", "student", getApplicationContext());
//                GetSetSharedPreferences.setDefaults("userId", userId, getApplicationContext());
//                GetSetSharedPreferences.setDefaults("password", password, getApplicationContext());
//
//                Intent intent = new Intent(LoginActivity.this, StudentModeActivity.class);
//                String AdminNoTxt = mUserIDText.getText().toString().trim();
//                intent.putExtra("AdminNo", AdminNoTxt);
//                startActivity(intent);
                Log.i("LOG", "6666");
            } else {
                mPasswordText.setError(getString(R.string.error_incorrect_password));
                mPasswordText.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            sTask = null;
            showProgress(false);
        }
    }

    private class teacherLoginTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            String resultData = "error";
            final String uid = mUserIDText.getText().toString().trim();
            final String pas = mPasswordText.getText().toString().trim();
            try {
                String path = "http://192.168.1.114/MAS/App/teacherLogin.php";
                String data = "TeacherIC=" + URLEncoder.encode(uid) + "&Password=" + URLEncoder.encode(pas);
                URL loginUrl = new URL(path);
                conn = (HttpURLConnection) loginUrl.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                // 设置发送的数据为表单类型，会被添加到http body当中
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(data.length()));

                // 运行当前的应用程序给服务器写数据
                conn.setDoOutput(true);
                conn.setDoInput(true);
                // post的请求是把数据以流的方式写给了服务器
                conn.getOutputStream().write(data.getBytes());
                conn.setInstanceFollowRedirects(true);
                // trigger server redirect
                conn.getResponseCode();

                int code = conn.getResponseCode();
                if (code == 200) {
                    is = conn.getInputStream();
                    resultData = readStream(is);
                    return resultData;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultData;
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            tTask = null;
            showProgress(false);
            System.out.println("TError: "+ result);

            if (result.equals("succeeded")) {
                GetSetSharedPreferences.setDefaults("userType", "teacher", getApplicationContext());
                GetSetSharedPreferences.setDefaults("userId", userId, getApplicationContext());
                GetSetSharedPreferences.setDefaults("password", password, getApplicationContext());

                Intent intent = new Intent(LoginActivity.this, TeacherModeActivity.class);
                String TeacherICTxt = mUserIDText.getText().toString().trim();
                intent.putExtra("TeacherIC", TeacherICTxt);
                startActivity(intent);
            } else {
                mPasswordText.setError(getString(R.string.error_incorrect_password));
                mPasswordText.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            tTask = null;
            showProgress(false);
        }
    }

    private String readStream(InputStream is){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while(( len = is.read(buffer))!=-1){
                baos.write(buffer, 0, len);
            }
            is.close();
            String result = baos.toString();

            if(result.contains("gb2312")){
                return baos.toString("gb2312");
            }else{
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Intent openFacebook(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/nus.singapore"));
    }

    private static Intent openTwitter(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/nusingapore"));
    }

    private static Intent openInstagram(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/nus_singapore/"));
    }

    private static Intent openYoutube(Context context) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=9ZhNn4oKq58"));
    }
}
