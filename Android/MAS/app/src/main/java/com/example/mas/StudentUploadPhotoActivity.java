package com.example.mas;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudentUploadPhotoActivity extends AppCompatActivity {

    private static final String TAG = "CameraDemo_MainActivity";
    private static final int REQUEST_OPEN_CAMERA = 1;

    private Button mUPCapturePhotoBTN;
    private Button mUPUploadPhotoBTN;
    private Button mUPRefreshBTN;
    private ImageView mUPImageView;
    private BluetoothAdapter mBluetoothAdapter;
    private ImageView mCameraBackground;

    private String adminNo;
    private String resultMsg;
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private String BTDevices = "";

    //原图像 URI
    private Uri imgUriOri;
    //原图像 路径
    private static String imgPathOri;
    private ProgressDialog progress;
    int countBTEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_upload_photo);
        initControl();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Take Photo");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adminNo = getIntent().getStringExtra("AdminNo");

        mUPCapturePhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoIntent();
                mUPUploadPhotoBTN.setClickable(true);
            }
        });

        mUPUploadPhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

        mUPRefreshBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter.isEnabled()) {
                    unregisterReceiver(mBroadcastReceiver2);
                    btnDiscover();
                    Toast.makeText(StudentUploadPhotoActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mUPUploadPhotoBTN.setClickable(false);

        btnDiscover();
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        mUPCapturePhotoBTN = (Button) findViewById(R.id.UPCapturePhotoBTN);
        mUPUploadPhotoBTN = (Button) findViewById(R.id.UPUploadPhotoBTN);
        mUPRefreshBTN = (Button) findViewById(R.id.UPRefreshBTN);
        mUPImageView = (ImageView) findViewById(R.id.UPImageView);
        mCameraBackground = (ImageView) findViewById(R.id.cameraBackground);
    }

    private void takePhotoIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
        File oriPhotoFile = null;
        try {
            oriPhotoFile = createOriImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (oriPhotoFile != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                imgUriOri = Uri.fromFile(oriPhotoFile);
            } else {
                imgUriOri = FileProvider.getUriForFile(this, getPackageName() + ".provider", oriPhotoFile);
            }
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUriOri);
            startActivityForResult(intent, REQUEST_OPEN_CAMERA);
        }

        Log.i(TAG, "openCamera_imgPathOri:" + imgPathOri);
        Log.i(TAG, "openCamera_imgUriOri:" + imgUriOri.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_CAMERA && resultCode == RESULT_OK) {
            imageViewSetPic(mUPImageView, imgPathOri);
            mUPImageView.bringToFront();
            Log.i(TAG, "openCameraResult_imgPathOri:" + imgPathOri);
            Log.i(TAG, "openCameraResult_imgUriOri:" + imgUriOri.toString());
        }
    }

    /**
     * 创建原图像保存的文件
     */
    private File createOriImageFile() throws IOException {
        String imgNameOri = "temp";
        File pictureDirOri = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
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
        File image = File.createTempFile(
                imgNameOri,         /* prefix */
                ".jpg",             /* suffix */
                pictureDirOri       /* directory */
        );
        imgPathOri = image.getAbsolutePath();
        return image;
    }

    /**
     * ImageView设置优化内存使用后的Bitmap
     * 返回一个等同于ImageView宽高的bitmap
     */
    public void imageViewSetPic(ImageView view, String imgPath) {
        // Get the dimensions of the View
        int targetW = view.getWidth();
        int targetH = view.getHeight();

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
        view.setImageBitmap(bitmap);
    }


    /**
     * Broadcast Receiver for listing devices that are not yet paired
     */
    private BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                System.out.println("onReceive: " + device.getName() + ": " + device.getAddress());
            }
        }
    };

    public void btnDiscover() {
        Log.d(TAG, "Look for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver2, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver2, discoverDevicesIntent);
        }
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != 0) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadPhoto() {
        progress = new ProgressDialog(this);
        progress.setTitle("Uploading");
        progress.setMessage("Please wait...");
        progress.show();

        BTDevices = "";
        if(!mBTDevices.isEmpty()) {
            for(int i=0; i<mBTDevices.size(); i++){
                int equalBTDevice = 0;
                if(i>=1) {
                    for (int j=0; j<i; j++) {
                        if(mBTDevices.get(i).equals(mBTDevices.get(j))) {
                            equalBTDevice++;
                        }
                    }
                    if(equalBTDevice == 0) {
                        BTDevices = BTDevices + mBTDevices.get(i) +",";
                    }
                }
                else {
                    BTDevices = BTDevices + mBTDevices.get(i) +",";
                }
            }
            System.out.print(BTDevices.length());
            BTDevices = BTDevices.substring(0, BTDevices.length()-1);
        }

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
                System.out.println("adminNo" + adminNo);
                System.out.println("device" + BTDevices);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("adminNo", adminNo)
                        .addFormDataPart("device", BTDevices)
                        .addFormDataPart("image",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                        .build();

                Request request = new Request.Builder()
                        .url("http://" + IPAddress.ipaddress + "/MAS/App/upload.php")
                        .post(request_body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    resultMsg = response.body().string();
                    System.out.println("response.body().string(): " + resultMsg);

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            //更新UI
                            if(resultMsg.equals("errora")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                builder.setMessage("Not in Attendance Time").setPositiveButton("OK", null);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                            else if(resultMsg.equals("errorb") || resultMsg.equals("errorc") || resultMsg.equals("errord") || resultMsg.equals("errore")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                builder.setMessage("Uploaded Failed").setPositiveButton("OK", null);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                            else {
                                int intIndex1 = resultMsg.indexOf("</head><body><br/><br/>");
                                int intIndex2 = resultMsg.indexOf("</body></html>");
                                if(intIndex1 == - 1){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                    builder.setMessage("Uncertain error").setPositiveButton("OK", null);

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }else{
                                    String subMsg = resultMsg.substring(intIndex1+23, intIndex2);
                                    System.out.println(subMsg);
                                    if(subMsg.equals("error1")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                        builder.setMessage("No face").setPositiveButton("OK", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else if(subMsg.equals("error2")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                        builder.setMessage("Not living body").setPositiveButton("OK", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else if(subMsg.equals("error3")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                        builder.setMessage("Not the same person").setPositiveButton("OK", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else if(subMsg.equals("error4")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                        builder.setMessage("Requested failed").setPositiveButton("OK", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else if(subMsg.equals("error5")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                        builder.setMessage("Google sheet error").setPositiveButton("OK", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else if(subMsg.equals("succeeded")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                        builder.setMessage("Succeeded").setPositiveButton("OK", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                                        builder.setMessage(subMsg).setPositiveButton("OK", null);

                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                }
                            }
                        }
                    });
                    progress.dismiss();
                }
                catch (SocketTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                            builder.setMessage("Please upload again").setPositiveButton("OK", null);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                }
                catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(StudentUploadPhotoActivity.this);
                            builder.setMessage("Uncertain error").setPositiveButton("OK", null);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                }
            }
        });
        t.start();
    }
}
