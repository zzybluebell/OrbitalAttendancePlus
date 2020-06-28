package com.example.mas;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class StudentModeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private String AdminNo;

    private NavigationView navView;
    private TextView adminNoTxt;
    private TextView emailTxt;
    private ImageView personalIcon;
    private String firebaseToken;
    private Bitmap bitmap;
    private String imgPathOri = "";

    private firebaseTask fTask = null;
    private studentInfoTask sTask = null;

    StudentModuleFragment mStudentModuleFragment;
    StudentMyProfileFragment mStudentMyProfileFragment;
    StudentTimeTableFragment mStudentTimeTableFragment;
    StudentTeacherInfoFragment mStudentTeacherInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_mode);
        AdminNo = getIntent().getStringExtra("AdminNo");
        initControl();

        DBAdapter dbAdaptor = new DBAdapter(getApplicationContext());
        Cursor cursor = null;
        try {
            dbAdaptor.open();
            cursor = dbAdaptor.getMyProfileByAdminNo(AdminNo);
            cursor.moveToFirst();
            final String myName = cursor.getString(1);
            final String myProfileUrl = cursor.getString(2);
            final int myProfileHeight = cursor.getInt(3);
            final int myProfileWidth = cursor.getInt(4);
            if(myName != null) {
                Toast.makeText(getApplicationContext(), "Welcome to come back, " + myName + "!", Toast.LENGTH_SHORT).show();
            }
            if(myName == null) {
                Toast.makeText(getApplicationContext(), "Welcome to come back, " + AdminNo + "!", Toast.LENGTH_SHORT).show();
            }
            if(myProfileUrl != null) {
                imageViewSetPic(personalIcon, myProfileHeight, myProfileWidth, myProfileUrl);
            }
        }
        catch (android.database.CursorIndexOutOfBoundsException e){
            dbAdaptor.insertAdminNo(AdminNo);
            firebaseToken = FirebaseInstanceId.getInstance().getToken();
            fTask = new firebaseTask();
            fTask.execute();
            Toast.makeText(getApplicationContext(), "Welcome to come back, " + AdminNo + "!", Toast.LENGTH_SHORT).show();
        }
        catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "Welcome to come back, " + AdminNo + "!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "System error!", Toast.LENGTH_SHORT).show();
            finish();
        }
        finally{
            if (cursor != null) {
                cursor.close();
            }
            if (dbAdaptor != null) {
                dbAdaptor.close();
            }
        }

        adminNoTxt.setText(AdminNo);
        emailTxt.setText(AdminNo+"@mymail.nyp.edu.sg");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mStudentModuleFragment = new StudentModuleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("AdminNo", AdminNo);
        mStudentModuleFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.contentLayout, mStudentModuleFragment);
        fragmentTransaction.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Copyright © 2018 HuJiaJun, All Rights Reserved.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        navView = (NavigationView)findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        adminNoTxt = (TextView)headerView.findViewById(R.id.AdminNoTxt);
        emailTxt = (TextView)headerView.findViewById(R.id.EmailTxt);
        personalIcon = (ImageView)headerView.findViewById(R.id.PersonalIcon);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_mode_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_news) {
            Intent intent = new Intent(this, StudentNewsActivity.class);
            intent.putExtra("AdminNo", AdminNo);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menuCurrentModule) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mStudentModuleFragment = new StudentModuleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AdminNo", AdminNo);
            mStudentModuleFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.contentLayout, mStudentModuleFragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.menuTimeTable) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mStudentTimeTableFragment = new StudentTimeTableFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AdminNo", AdminNo);
            mStudentTimeTableFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.contentLayout, mStudentTimeTableFragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.menuMyProfile) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mStudentMyProfileFragment = new StudentMyProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AdminNo", AdminNo);
            mStudentMyProfileFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.contentLayout, mStudentMyProfileFragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.menuTeacherInfo) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mStudentTeacherInfoFragment = new StudentTeacherInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AdminNo", AdminNo);
            mStudentTeacherInfoFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.contentLayout, mStudentTeacherInfoFragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.menuAbout) {
            Intent intent = new Intent(this, StudentAboutActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.menuLogout) {
            GetSetSharedPreferences.removeDefaults("password", getApplicationContext());
            Intent intent = new Intent(this, LoginActivity.class);// New activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i= new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ImageView设置优化内存使用后的Bitmap
     * 返回一个等同于ImageView宽高的bitmap
     */
    public void imageViewSetPic(ImageView view, int viewHeight, int viewWidth, String imgPath) {
        // Get the dimensions of the View
        int targetW = viewWidth;
        int targetH = viewHeight;

        System.out.println("targetW: " + targetW);
        System.out.println("targetH: " + targetH);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
        view.setImageBitmap(getRoundedCornerBitmap(bitmap, 1000));
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获取Request ID
     */
    private class firebaseTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/Firebase.php";
                String data = "FirebaseToken=" + URLEncoder.encode(firebaseToken) + "&AdminNo=" + URLEncoder.encode(AdminNo);
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

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            if(result.equals("error")) {
                Toast.makeText(getApplicationContext(), "There is an error in pushing notification!", Toast.LENGTH_SHORT).show();
            }
            else {
                sTask = new studentInfoTask();
                sTask.execute();
            }
        }
    }

    private class studentInfoTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/studentInfo.php";
                String data = "AdminNo=" + URLEncoder.encode(AdminNo);
                URL url = new URL(path);
                conn = (HttpURLConnection) url.openConnection();
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

            System.out.println("result_arr: "+ result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("OK")) {
                    JSONObject subJsonObject = new JSONObject(jsonObject.getString(Integer.toString(0)));
                    String getAdminNo = subJsonObject.getString("AdminNo").toString();
                    String getName = subJsonObject.getString("Name").toString();
                    String getSCG = subJsonObject.getString("SCG").toString();
                    String getImage = subJsonObject.getString("Image").toString();
                    DBAdapter dbAdaptor = new DBAdapter(StudentModeActivity.this);
                    try {
                        dbAdaptor.open();
                        if(!getName.equals("null")) {
                            dbAdaptor.updateNameByAdminNo(getAdminNo, getName);
                        }
                        if(!getSCG.equals("null")) {
                            dbAdaptor.updateSCGByAdminNo(getAdminNo, getSCG);
                        }

                        File pictureDirOri = new File(StudentModeActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+ "/MyProfile");

                        if (!pictureDirOri.exists()) {
                            pictureDirOri.mkdirs();
                        }
                        else {
                            String[] children = pictureDirOri.list();
                            for (int i = 0; i < children.length; i++)  {
                                new File(pictureDirOri, children[i]).delete();
                            }
                            pictureDirOri.mkdirs();
                        }

                        FileOutputStream fos = null;
                        File image = null;
                        try {
                            image = File.createTempFile(
                                    "temp",         /* prefix */
                                    ".jpg",             /* suffix */
                                    pictureDirOri       /* directory */
                            );

                            fos = new FileOutputStream(image);
                            bitmap = stringToBitmap(getImage.substring(getImage.indexOf(",")  + 1));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            dbAdaptor.updateMyProfileByAdminNo(getAdminNo, image.getAbsolutePath(), bitmap.getHeight(), bitmap.getWidth());
                            imageViewSetPic(personalIcon, bitmap.getHeight(), bitmap.getWidth(), image.getAbsolutePath());
                        }
                        catch (Exception e) {
                            System.out.println(e);
                        }
                        finally {
                            try {
                                fos.flush();
                                fos.close();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println(e);
                    }
                    finally {
                        if (dbAdaptor != null) {
                            dbAdaptor.close();
                        }
                    }
                }
            }
            catch (JSONException e) {
                System.out.println(e);
            }
            catch (Exception e) {
                System.out.println(e);
            }
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

    public Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

