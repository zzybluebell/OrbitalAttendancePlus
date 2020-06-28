package com.example.mas;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TeacherModeActivity extends AppCompatActivity {

    private String TeacherIC;
    private getModuleTask gTask = null;
    private beginAttendanceTask bTask = null;
    private closeAttendanceTask cTask = null;
    private TeacherModuleListView adapter;
    private List<TeacherModule> mTeacherModuleList;
    private String [] moduleStatus = null;
    private String [] moduleId = null;
    private String macAddress;
    private String moduleid;
    private String status;
    private String mid;
    private int listviewposition;
    private boolean beginCloseAttendanceStatus = false;

    ListView teacherModuleListView;
    Button BTOnOff;
    BluetoothAdapter mBluetoothAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_mode);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Current Modules");
        setSupportActionBar(toolbar);

        TeacherIC = getIntent().getStringExtra("TeacherIC");
        Toast.makeText(getApplicationContext(), "Welcome to come back, " + TeacherIC + "!", Toast.LENGTH_SHORT).show();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.teacherModuleSwipeLayout);

        teacherModuleListView = (ListView) findViewById(R.id.teacherModuleListView);
        BTOnOff = (Button)findViewById(R.id.BTOnOff);

        macAddress = android.provider.Settings.Secure.getString(getContentResolver(), "bluetooth_address");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        gTask = new getModuleTask();
        gTask.execute();

        BTOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enable = false;
                if(moduleStatus != null) {
                    for (int i=0; i<moduleStatus.length; i++) {
                        if(moduleStatus[i].equals("Available")) {
                            mid = moduleId[i];
                            enable = true;
                            break;
                        }
                    }
                }

                if (enable == true) {
                    System.out.print("onClick: enabling/disabling bluetooth.");
                    enableDisableBT();
                }
                else {
                    Toast.makeText(TeacherModeActivity.this, "The modules are unavailable, please turn on later", Toast.LENGTH_LONG).show();
                }
            }
        });

        teacherModuleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    status = mTeacherModuleList.get(position).getStatus();
                    moduleid = mTeacherModuleList.get(position).getModuleId();
                    listviewposition = position;
                    System.out.println("status" + status);
                    if (status.equals("Available")) {
                        if(beginCloseAttendanceStatus == false) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherModeActivity.this);
                            builder.setMessage("Do you want to start?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.out.print("onClick: enabling/disabling bluetooth.");
                                            mSwipeRefreshLayout.setEnabled(false);
                                            beginCloseAttendanceStatus = true;
                                            bTask = null;
                                            bTask = new beginAttendanceTask();
                                            bTask.execute();
                                        }
                                    })
                                    .setNegativeButton("Cancel", null);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherModeActivity.this);
                            builder.setMessage("Do you want to close?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.out.print("onClick: enabling/disabling bluetooth.");
                                            mSwipeRefreshLayout.setEnabled(true);
                                            beginCloseAttendanceStatus = false;
                                            cTask = null;
                                            cTask = new closeAttendanceTask();
                                            cTask.execute();
                                        }
                                    })
                                    .setNegativeButton("Cancel", null);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }

                    } else {
                        Toast.makeText(TeacherModeActivity.this, "This module is unavailable", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(TeacherModeActivity.this, "Please turn on BlueTooth", Toast.LENGTH_LONG).show();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gTask = null;
                        gTask = new getModuleTask();
                        gTask.execute();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.teacher_mode_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.teacherLogout) {
            GetSetSharedPreferences.removeDefaults("password", getApplicationContext());
            Intent intent = new Intent(this, LoginActivity.class);// New activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class getModuleTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/moduleInfo.php";
                String data = "TeacherIC=" + URLEncoder.encode(TeacherIC);
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
                mTeacherModuleList = new ArrayList<>();
                mTeacherModuleList.add(new TeacherModule(0,"0","0", "None", "00:00:00", "00:00:00","Unavailable","Stop"));
                adapter = new TeacherModuleListView(getApplicationContext(), mTeacherModuleList);
                teacherModuleListView.setAdapter(adapter);
            }
            else {
                String[] result_arr = result.split("<br>");

                String[] ModuleId_arr = result_arr[0].split(",");
                String[] ModuleCode_arr = result_arr[1].split(",");
                String[] ModuleName_arr = result_arr[2].split(",");
                String[] BeginTime_arr = result_arr[3].split(",");
                String[] EndTime_arr = result_arr[4].split(",");
                String[] Status_arr = result_arr[5].split(",");
                moduleStatus = Status_arr;
                moduleId = ModuleId_arr;

                mTeacherModuleList = new ArrayList<>();
                for (int i=0; i<ModuleId_arr.length; i++) {
                    mTeacherModuleList.add(new TeacherModule(i, ModuleId_arr[i], ModuleCode_arr[i], ModuleName_arr[i], BeginTime_arr[i], EndTime_arr[i], Status_arr[i], "Stop"));
                }

                adapter = new TeacherModuleListView(getApplicationContext(), mTeacherModuleList);
                teacherModuleListView.setAdapter(adapter);
            }
        }
    }

    private class beginAttendanceTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/beginAttendance.php";
                System.out.println("moduleid: "+ moduleid);
                System.out.println("MacAddress: "+ macAddress);
                String data = "ModuleId=" + URLEncoder.encode(moduleid) + "&MacAddress=" + URLEncoder.encode(macAddress);
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
            if(result.equals("succeeded")) {
                TeacherModule selectedmodule = mTeacherModuleList.get(listviewposition);
                mTeacherModuleList.remove(listviewposition);
                mTeacherModuleList.add(listviewposition, new TeacherModule(
                        listviewposition,
                        selectedmodule.getModuleId(),
                        selectedmodule.getModuleCode(),
                        selectedmodule.getModuleName(),
                        selectedmodule.getBeginTime(),
                        selectedmodule.getEndTime(),
                        selectedmodule.getStatus(),
                        "Start"));
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(TeacherModeActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class closeAttendanceTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/closeAttendance.php";
                System.out.println("moduleid: "+ moduleid);
                System.out.println("MacAddress: "+ macAddress);
                String data = "ModuleId=" + URLEncoder.encode(moduleid) + "&MacAddress=" + URLEncoder.encode(macAddress);
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
            if(result.equals("succeeded")) {
                TeacherModule selectedmodule = mTeacherModuleList.get(listviewposition);
                mTeacherModuleList.remove(listviewposition);
                mTeacherModuleList.add(listviewposition, new TeacherModule(
                        listviewposition,
                        selectedmodule.getModuleId(),
                        selectedmodule.getModuleCode(),
                        selectedmodule.getModuleName(),
                        selectedmodule.getBeginTime(),
                        selectedmodule.getEndTime(),
                        selectedmodule.getStatus(),
                        "Stop"));
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(TeacherModeActivity.this, result, Toast.LENGTH_LONG).show();
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

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(timer != null) {
                            timer.cancel();
                            timer.purge();
                        }
                        System.out.print("onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        System.out.print("onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        callBluetoothRegularly();
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
     * Broadcast Receiver for changes made to bluetooth states such as:
     * Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, mBluetoothAdapter.ERROR);

                switch(mode) {
                    // Device in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        System.out.print("mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        System.out.print("mBroadcastReceiver2: Discoverability Enabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        System.out.print("mBroadcastReceiver2: Connecting......");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        System.out.print("mBroadcastReceiver2: Connected");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        System.out.print("onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
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
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()) {
            System.out.print("enableDisableBT: disabling BT");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(mBluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    private void btnEnableDisable_Discoverable() {
        System.out.print("btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2, intentFilter);
    }

    private void callBluetoothRegularly() {
        final long period = 300000;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                btnEnableDisable_Discoverable();
            }
        }, 0, period);
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
}

