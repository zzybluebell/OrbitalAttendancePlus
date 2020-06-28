package com.example.mas;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class StudentTimeTableFragment extends Fragment {

    private Button mTTSelectImageBTN;
    private ImageView mTTImageView;
    private RelativeLayout mTimeTableIconBox;
    private RelativeLayout mTimeTableBox;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private Bitmap bitmap;
    private int REQUEST_CODE = 1;
    private String adminNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //引用创建好的xml布局
        View view = inflater.inflate(R.layout.student_time_table,container,false);
        Bundle bundle = getArguments();
        adminNo = bundle.getString("AdminNo");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initControl();
        initImage();

        toolbar.setTitle("Timetable");

        mTTSelectImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
            }
        });

        mTTImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.student_timetable_zoom_in, null);
                PhotoView photoView = mView.findViewById(R.id.imageZoomInView);
                photoView.setImageBitmap(bitmap);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        mTTSelectImageBTN = (Button) getView().findViewById(R.id.TTSelectImageBTN);
        mTTImageView = (ImageView) getView().findViewById(R.id.TTImageView);
        mTimeTableIconBox = (RelativeLayout) getView().findViewById(R.id.timeTableIconBox);
        mTimeTableBox = (RelativeLayout) getView().findViewById(R.id.timeTableBox);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
    }

    /**
     * 初始化图片路径
     */
    private void initImage() {
        try {
            String[] timeTable_arr = readData().split(",");
            String timeTableUrl = timeTable_arr[0];
            int timeTableHeight = Integer.parseInt(timeTable_arr[1]);
            int timeTableWidth = Integer.parseInt(timeTable_arr[2]);
            imageViewSetPic(mTTImageView, timeTableHeight, timeTableWidth, timeTableUrl);
            mTTImageView.setVisibility(View.VISIBLE);
            mTimeTableIconBox.setVisibility(View.GONE);

            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.mipmap.ic_add_icon);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
                }
            });
        }
        catch(Exception exc) {
            fab.setVisibility(View.INVISIBLE);
            mTTImageView.setVisibility(View.GONE);
            mTimeTableIconBox.setVisibility(View.VISIBLE);
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
            cursor = dbAdaptor.getTimeTableByAdminNo(adminNo);
            cursor.moveToFirst();
            final String getAdminNo = cursor.getString(0);
            System.out.println(getAdminNo);
            final String timeTableUrl = cursor.getString(1);
            final int timeTableHeight = cursor.getInt(2);
            final int timeTableWidth = cursor.getInt(3);
            if(!timeTableUrl.equals("")) {
                result = timeTableUrl + "," + timeTableHeight + "," + timeTableWidth;
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
            dbAdaptor.updateTimeTableByAdminNo(adminNo, url, height, width);
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

        if(requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            File pictureDirOri = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+ "/TimeTable");

            if (!pictureDirOri.exists()) {
                pictureDirOri.mkdirs();
            }
            else {
                try {
                    String[] timeTable_arr = readData().split(",");
                    String timeTableUrl = timeTable_arr[0];
                    File deleteFile = new File(timeTableUrl);
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

                Uri uri = data.getData();
                fos = new FileOutputStream(image);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                mTTImageView.setVisibility(View.VISIBLE);
                mTimeTableIconBox.setVisibility(View.GONE);
                mTTImageView.setImageBitmap(bitmap);

                writeData(image.getAbsolutePath(), mTimeTableBox.getWidth(), mTimeTableBox.getHeight());

                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.mipmap.ic_add_icon);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
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
        view.setImageBitmap(bitmap);
    }
}
