package com.example.mas;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.location.LocationListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class StudentMyProfileFragment extends Fragment implements LocationListener {

    private Button mMPSelectImageBTN;
    private Button mMPUploadImageBTN;
    private ImageView mMPImageView;
    private ImageView mUpdateNameBTN;
    private ImageView mSCGBTN;
    private TextView mNameTxt2;
    private EditText mNameEditTxt2;
    private TextView mAdminNoTxt2;
    private TextView mEmailTxt2;
    private TextView mSCGTxt2;
    private EditText mSCGEditTxt2;
    private FloatingActionButton fab;
    private Bitmap bitmap;
    private ImageView personalIcon;
    private Geocoder mGeocoder;
    private TextView mAddressTxt2;
    private ImageView mAddressBTN2;

    private int REQUEST_CODE = 1;
    private String resultMsg;
    //原图像 路径
    private String imgPathOri = "";
    private ProgressDialog progress;
    private String adminNo;
    private boolean isUpdateName = false;
    private boolean isUpdateSCG = false;
    private LocationManager locationManager;

    private updateProfileNameTask uPNTask = null;
    private updateProfileSCGTask uPSTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //引用创建好的xml布局
        View view = inflater.inflate(R.layout.student_my_profile, container, false);
        Bundle bundle = getArguments();
        adminNo = bundle.getString("AdminNo");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initControl();
        initImage();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("My Profile");

        fab.setVisibility(View.INVISIBLE);

        DBAdapter dbAdaptor = new DBAdapter(getActivity().getApplicationContext());
        Cursor cursor = null;
        try {
            dbAdaptor.open();
            cursor = dbAdaptor.getInfoByAdminNo(adminNo);
            cursor.moveToFirst();
            final String getAdminNo = cursor.getString(0);
            mAdminNoTxt2.setText(getAdminNo);
            mEmailTxt2.setText(getAdminNo + "@mymail.nyp.edu.sg");
            try {
                final String name = cursor.getString(1);
                if (!name.equals("")) {
                    mNameEditTxt2.setVisibility(View.GONE);
                    mNameTxt2.setText(name);
                } else {
                    mUpdateNameBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
                    mNameTxt2.setVisibility(View.GONE);
                    isUpdateName = !isUpdateName;
                }
            } catch (Exception e) {
                mUpdateNameBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
                mNameTxt2.setVisibility(View.GONE);
                isUpdateName = !isUpdateName;
            }
            try {
                final String scg = cursor.getString(2);
                if (!scg.equals("")) {
                    mSCGEditTxt2.setVisibility(View.GONE);
                    mSCGTxt2.setText(scg);
                } else {
                    mSCGBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
                    mSCGTxt2.setVisibility(View.GONE);
                    isUpdateSCG = !isUpdateSCG;
                }
            } catch (Exception e) {
                mSCGBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
                mSCGTxt2.setVisibility(View.GONE);
                isUpdateSCG = !isUpdateSCG;
            }
        } catch (Exception ex) {
            mUpdateNameBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
            mNameTxt2.setVisibility(View.GONE);
            isUpdateName = !isUpdateName;
            mSCGBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
            mSCGTxt2.setVisibility(View.GONE);
            isUpdateSCG = !isUpdateSCG;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (dbAdaptor != null) {
                dbAdaptor.close();
            }
        }

        mMPSelectImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CropIntent = null;
                try {
                    CropIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    //需要加上这两句话  ： uri 权限
                    CropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    CropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    CropIntent.putExtra("crop", "true");
                    CropIntent.putExtra("outputX", 360);
                    CropIntent.putExtra("outputY", 360);
                    CropIntent.putExtra("aspectX", 1);
                    CropIntent.putExtra("aspectY", 1);
                    CropIntent.putExtra("return-data", true);
                    startActivityForResult(CropIntent, REQUEST_CODE);
                } catch (Exception e) {
                    System.out.println(CropIntent);
                }
            }
        });

        mMPUploadImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

        mMPImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.student_my_profile_zoom_in, null);
                PhotoView photoView = mView.findViewById(R.id.imageZoomInView2);
                photoView.setImageBitmap(bitmap);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        mUpdateNameBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateName == false) {
                    String getNameTxt = mNameTxt2.getText().toString();
                    mUpdateNameBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
                    mNameTxt2.setVisibility(View.GONE);
                    mNameEditTxt2.setVisibility(View.VISIBLE);
                    mNameEditTxt2.setText(getNameTxt);
                    isUpdateName = !isUpdateName;
                } else {
                    String getNameTxt = mNameEditTxt2.getText().toString();
                    if (!TextUtils.isEmpty(getNameTxt)) {
                        uPNTask = new updateProfileNameTask();
                        uPNTask.execute();
                    } else {
                        mNameEditTxt2.setError("Please enter your name");
                        mNameEditTxt2.requestFocus();
                    }
                }
            }
        });

        mSCGBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdateSCG == false) {
                    String getSCGTxt = mSCGTxt2.getText().toString();
                    mSCGBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_tick_icon));
                    mSCGTxt2.setVisibility(View.GONE);
                    mSCGEditTxt2.setVisibility(View.VISIBLE);
                    mSCGEditTxt2.setText(getSCGTxt);
                    isUpdateSCG = !isUpdateSCG;
                } else {
                    String getSCGTxt = mSCGEditTxt2.getText().toString();
                    if (!TextUtils.isEmpty(getSCGTxt)) {
                        uPSTask = new updateProfileSCGTask();
                        uPSTask.execute();
                    } else {
                        mSCGEditTxt2.setError("Please enter your S/C/G");
                        mSCGEditTxt2.requestFocus();
                    }
                }
            }
        });

        mAddressBTN2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StudentMapActivity.class);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        mMPSelectImageBTN = (Button) getActivity().findViewById(R.id.MPSelectImageBTN);
        mMPUploadImageBTN = (Button) getActivity().findViewById(R.id.MPUploadImageBTN);
        mMPImageView = (ImageView) getActivity().findViewById(R.id.MPImageView);
        personalIcon = (ImageView) getActivity().findViewById(R.id.PersonalIcon);
        mUpdateNameBTN = (ImageView) getActivity().findViewById(R.id.updateNameBTN);
        mSCGBTN = (ImageView) getActivity().findViewById(R.id.SCGBTN);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mNameTxt2 = (TextView) getActivity().findViewById(R.id.nameTxt2);
        mNameEditTxt2 = (EditText) getActivity().findViewById(R.id.nameEditTxt2);
        mAdminNoTxt2 = (TextView) getActivity().findViewById(R.id.adminNoTxt2);
        mEmailTxt2 = (TextView) getActivity().findViewById(R.id.emailTxt2);
        mSCGTxt2 = (TextView) getActivity().findViewById(R.id.SCGTxt2);
        mSCGEditTxt2 = (EditText) getActivity().findViewById(R.id.SCGEditTxt2);
        mAddressTxt2 = (TextView) getActivity().findViewById(R.id.addressTxt2);
        mAddressBTN2 = (ImageView) getActivity().findViewById(R.id.addressBTN2);
    }

    /**
     * 初始化图片路径
     */
    private void initImage() {
        try {
            String[] myProfile_arr = readData().toString().split(",");
            imgPathOri = myProfile_arr[0];
            String myProfileUrl = myProfile_arr[0];
            int myProfileHeight = Integer.parseInt(myProfile_arr[1]);
            int myProfileWidth = Integer.parseInt(myProfile_arr[2]);
            System.out.println("hahhaha");
            System.out.println(myProfileUrl);
            System.out.println(myProfileHeight);
            System.out.println(myProfileWidth);
            imageViewSetPic(mMPImageView, myProfileHeight, myProfileWidth, myProfileUrl);
        }
        catch(Exception exc) {
            System.out.println("SharedPreferences Error: " + exc);
        }
    }

    public void uploadPhoto() {
        if(!imgPathOri.equals("")) {
            progress = new ProgressDialog(getActivity());
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    File f  = new File(imgPathOri);

                    String file_path = f.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .build();

                    RequestBody file_body = RequestBody.create(MediaType.parse("multipart/form-data"),f);

                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("adminNo", adminNo)
                            .addFormDataPart("image",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://" + IPAddress.ipaddress + "/MAS/App/myProfile.php")
                            .post(request_body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        resultMsg = response.body().string();
                        System.out.println("response.body().string(): " + resultMsg);

                        getActivity().runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                System.out.println("resultMsg"+resultMsg);
                                //更新UI
                                if(resultMsg.equals("succeeded")) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Succeeded!", Toast.LENGTH_LONG).show();
                                }
                                else if(resultMsg.equals("errorc")) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Please updated after 7 days!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getActivity().getApplicationContext(), "Please upload again!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        progress.dismiss();
                    }
                    catch (SocketTimeoutException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), "Please upload again!", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                    catch (IOException e) {
                        System.out.println("Error: " + e);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), "Uncertain error!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
            t.start();
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "Please select a picture.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 读取图片路径
     */
    private String readData() {
        DBAdapter dbAdaptor = new DBAdapter(getActivity().getApplicationContext());
        Cursor cursor = null;
        String result = null;
        try {
            dbAdaptor.open();
            cursor = dbAdaptor.getMyProfileByAdminNo(adminNo);
            cursor.moveToFirst();
            final String myProfileUrl = cursor.getString(2);
            final int myProfileHeight = cursor.getInt(3);
            final int myProfileWidth = cursor.getInt(4);
            if(!myProfileUrl.equals("")) {
                result = myProfileUrl + "," + myProfileHeight + "," + myProfileWidth;
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        finally{
            if (cursor != null) {
                cursor.close();
            }
            if (dbAdaptor != null) {
                dbAdaptor.close();
            }
        }
        return result;
    }

    /**
     * 写入图片路径
     */
    private void writeData(String url, int height, int width) {
        DBAdapter dbAdaptor = new DBAdapter(getActivity().getApplicationContext());
        try {
            dbAdaptor.open();
            dbAdaptor.updateMyProfileByAdminNo(adminNo, url, height, width);
        }
        catch (Exception e){
            System.out.println(e);
        }
        finally{
            if (dbAdaptor != null) {
                dbAdaptor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && getActivity().RESULT_OK == -1 && data != null && data.getExtras() != null) {

            File pictureDirOri = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+ "/MyProfile");

            if (!pictureDirOri.exists()) {
                pictureDirOri.mkdirs();
            }
            else {
                try {
                    String[] myProfile_arr = readData().toString().split(",");
                    String myProfileUrl = myProfile_arr[0];
                    File deleteFile = new File(myProfileUrl);
                    deleteFile.delete();
                }
                catch (Exception e) {
                    System.out.println(e);
                }
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
                Bundle extras = data.getExtras();
                bitmap = extras.getParcelable("data");
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                mMPImageView.setImageBitmap(getRoundedCornerBitmap(bitmap, 360));
                imgPathOri = image.getAbsolutePath();

                writeData(image.getAbsolutePath(), mMPImageView.getWidth(), mMPImageView.getHeight());
                imageViewSetPic(personalIcon, mMPImageView.getHeight(), mMPImageView.getWidth(), imgPathOri);
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
        else {

        }
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

        bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
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


    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        System.out.println("aaa" + lat + lng);

        mGeocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = mGeocoder.getFromLocation(lat, lng, 1);
            String address = addresses.get(0).getAddressLine(0);
            mAddressTxt2.setText(address);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private class updateProfileSCGTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            String resultData = "error";
            final String updateSCG = mSCGTxt2.getText().toString();
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/myProfile.php";
                String data = "adminNo=" + adminNo + "&SCG=" + URLEncoder.encode(updateSCG);
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
            System.out.println("TError: "+ result);

            if (result.equals("succeeded2")) {
                DBAdapter dbAdaptor = new DBAdapter(getActivity().getApplicationContext());
                try {
                    dbAdaptor.open();
                    dbAdaptor.updateSCGByAdminNo(adminNo, mSCGEditTxt2.getText().toString());
                    mSCGBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_update));
                    mSCGTxt2.setVisibility(View.VISIBLE);
                    mSCGTxt2.setText(mSCGEditTxt2.getText().toString());
                    mSCGEditTxt2.setVisibility(View.GONE);
                    isUpdateSCG = !isUpdateSCG;
                    Toast.makeText(getActivity().getApplicationContext(), "SCG Updated Successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    if (dbAdaptor != null) {
                        dbAdaptor.close();
                    }
                }
            }
            else if (result.equals("error")) {

            }
            else {

            }
        }
    }

    private class updateProfileNameTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null;
            InputStream is = null;
            String resultData = "error";
            final String updateName = mNameEditTxt2.getText().toString();
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/myProfile.php";
                String data = "adminNo=" + adminNo + "&name=" + URLEncoder.encode(updateName);
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
            System.out.println("TError: "+ result);

            if (result.equals("succeeded1")) {
                DBAdapter dbAdaptor = new DBAdapter(getActivity().getApplicationContext());
                try {
                    dbAdaptor.open();
                    dbAdaptor.updateNameByAdminNo(adminNo, mNameEditTxt2.getText().toString());
                    mUpdateNameBTN.setImageDrawable(getResources().getDrawable(R.mipmap.ic_update));
                    mNameTxt2.setVisibility(View.VISIBLE);
                    mNameTxt2.setText(mNameEditTxt2.getText().toString());
                    mNameEditTxt2.setVisibility(View.GONE);
                    isUpdateName = !isUpdateName;
                    Toast.makeText(getActivity().getApplicationContext(), "Name Updated Successfully!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    if (dbAdaptor != null) {
                        dbAdaptor.close();
                    }
                }
            }
            else if (result.equals("error")) {

            }
            else {

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
}
