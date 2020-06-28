package com.example.mas;

import android.Manifest;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */

public class StudentModuleFragment extends Fragment {

    private static final int REQUEST_PERMISSIONS = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private int permissionResult = 0;
    private boolean isClickButton = false;

    private String adminNo;
    private currentModuleTask cTask = null;
    private StudentModuleListView adapter;
    private List<StudentModule> mStudentModuleList;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private CircleButton circleMarkAttendanceButton;
    private int countBT = 0;
    private ListView studentModuleListView;

    private boolean isAvailable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //引用创建好的xml布局
        View view = inflater.inflate(R.layout.student_module,container,false);
        Bundle bundle = getArguments();
        adminNo = bundle.getString("AdminNo");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initControl();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        toolbar.setTitle("Mark Attendance");
        fab.setVisibility(View.INVISIBLE);

        cTask = new currentModuleTask();
        cTask.execute();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.studentModuleSwipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cTask = null;
                        cTask = new currentModuleTask();
                        cTask.execute();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });

        circleMarkAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAvailable == true) {
                    isClickButton = !isClickButton;
                    checkPermission();
                    enableDisableBT();
                }
                else {
                    Toast.makeText(getActivity(), "No available module!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        studentModuleListView = (ListView) getActivity().findViewById(R.id.studentModuleListView);
        circleMarkAttendanceButton = (CircleButton) getActivity().findViewById(R.id.circleMarkAttendanceButton);
    }

    private class currentModuleTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/currentModule.php";
                String data = "AdminNo=" + URLEncoder.encode(adminNo);
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

            if(result.equals("error1") || result.equals("error2")) {
                mStudentModuleList = new ArrayList<>();
                mStudentModuleList.add(new StudentModule(0,"None", "None", "00:00:00", "00:00:00", "Unavailable"));
                adapter = new StudentModuleListView(getActivity().getApplicationContext(), mStudentModuleList);
                studentModuleListView.setAdapter(adapter);
            }
            else {
                String[] result_arr = result.split("<br>");
                String[] ModuleName_arr = result_arr[0].split(",");
                String[] TeacherName_arr = result_arr[1].split(",");
                String[] BeginTime_arr = result_arr[2].split(",");
                String[] EndTime_arr = result_arr[3].split(",");
                String[] Status_arr = result_arr[4].split(",");

                mStudentModuleList = new ArrayList<>();
                for (int i=0; i<ModuleName_arr.length; i++) {
                    if(Status_arr[i].equals("Available")) {
                        isAvailable = true;
                        mStudentModuleList.add(0, new StudentModule(i, ModuleName_arr[i], TeacherName_arr[i], BeginTime_arr[i], EndTime_arr[i], Status_arr[i]));
                    }
                    else {
                        mStudentModuleList.add(new StudentModule(i, ModuleName_arr[i], TeacherName_arr[i], BeginTime_arr[i], EndTime_arr[i], Status_arr[i]));
                    }
                }

                adapter = new StudentModuleListView(getActivity().getApplicationContext(), mStudentModuleList);
                studentModuleListView.setAdapter(adapter);
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

    /**
     * 初始化相机相关权限
     * 适配6.0+手机的运行时权限
     */
    private void checkPermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionResult++;
            }
        }
    }

    private void registerPermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSIONS);
    }

    private void enableDisableBT() {
        if(mBluetoothAdapter == null) {
            System.out.print("enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()) {
            System.out.print("enableDisableBT: enabling BT");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(mBluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        System.out.print("onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        System.out.print("onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        System.out.print("onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        System.out.print("onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * 从点击运行程序到弹出蓝牙窗口：onCreate → onStart → onResume → onPause
     * 选择后 onPause → onResume
     * 第二次到达onResume才进行蓝牙判断
     */
    @Override
    public void onResume() {
        super.onResume();

        System.out.println("5555");

        if(countBT != 0) {
            permissionResult = 0;
            checkPermission();
            if(permissionResult != 0) {
                permissionResult = 0;
                isClickButton = !isClickButton;
                countBT = 0;
                Toast.makeText(getActivity(), "Request Permission failed!", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.disable();
                getActivity().unregisterReceiver(mBroadcastReceiver1);
            }
            else {
                isClickButton = !isClickButton;
                countBT = 0;
                System.out.println("6666");
                Intent intent = new Intent(getActivity(), StudentUploadPhotoActivity.class);
                intent.putExtra("AdminNo", adminNo);
                startActivity(intent);
            }
        }
        else {
            if(isClickButton && permissionResult != 0) {
                System.out.println("aaa");
                if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    System.out.println("bbb");
                    countBT++;
                    registerPermissions();
                }
                else {
                    isClickButton = !isClickButton;
                    Toast.makeText(getActivity(), "Request bluetooth failed!", Toast.LENGTH_SHORT).show();
                }
            }
            else if(isClickButton && permissionResult == 0) {
                if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    isClickButton = !isClickButton;
                    System.out.println("6666");
                    Intent intent = new Intent(getActivity(), StudentUploadPhotoActivity.class);
                    intent.putExtra("AdminNo", adminNo);
                    startActivity(intent);
                }
                else {
                    isClickButton = !isClickButton;
                    Toast.makeText(getActivity(), "Request bluetooth failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            if(mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
                getActivity().unregisterReceiver(mBroadcastReceiver1);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
