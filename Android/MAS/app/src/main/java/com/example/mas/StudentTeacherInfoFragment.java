package com.example.mas;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class StudentTeacherInfoFragment extends Fragment implements SearchView.OnQueryTextListener {

    private String adminNo;
    private String searchText;
    private teacherInfoTask tTask = null;
    private searchTeacherInfoTask sTask = null;
    private StudentTeacherInfoListView adapter;
    private List<StudentTeacherInfo> mStudentTeacherInfoList;

    ListView studentTeacherInfoListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //引用创建好的xml布局
        View view = inflater.inflate(R.layout.student_teacher_info,container,false);
        Bundle bundle = getArguments();
        adminNo = bundle.getString("AdminNo");
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Teachers Info");

        studentTeacherInfoListView = (ListView) getActivity().findViewById(R.id.studentTeacherInfoListView);

        tTask = new teacherInfoTask();
        tTask.execute();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.studentTeacherInfoSwipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tTask = null;
                        tTask = new teacherInfoTask();
                        tTask.execute();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.student_teacher_info, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search by name...");
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchText = newText;
        sTask = null;
        sTask = new searchTeacherInfoTask();
        sTask.execute();
        return true;
    }

    private class searchTeacherInfoTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/teacherInfo.php";
                String data = "AdminNo=" + URLEncoder.encode(adminNo) + "&SearchText=" + URLEncoder.encode(searchText);
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
                    mStudentTeacherInfoList = new ArrayList<>();
                    for (int i=1; i<jsonObject.length(); i++) {
                        JSONObject subJsonObject = new JSONObject(jsonObject.getString(Integer.toString(i-1)));
                        mStudentTeacherInfoList.add(
                                new StudentTeacherInfo(i-1,
                                        subJsonObject.getString("ModuleName").toString(),
                                        subJsonObject.getString("TeacherName").toString(),
                                        subJsonObject.getString("Phone").toString(),
                                        subJsonObject.getString("Email").toString(),
                                        subJsonObject.getString("TeacherImage").toString())
                        );
                    }
                    adapter = new StudentTeacherInfoListView(getActivity().getApplicationContext(), mStudentTeacherInfoList);
                    studentTeacherInfoListView.setAdapter(adapter);
                }
                else {
                    // No record;
                }
            }
            catch (JSONException e) {
                // No record;
            }
        }
    }

    private class teacherInfoTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/teacherInfo.php";
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

            try {
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("OK")) {
                    mStudentTeacherInfoList = new ArrayList<>();
                    for (int i=1; i<jsonObject.length(); i++) {
                        JSONObject subJsonObject = new JSONObject(jsonObject.getString(Integer.toString(i-1)));
                        mStudentTeacherInfoList.add(
                                new StudentTeacherInfo(i-1,
                                        subJsonObject.getString("ModuleName").toString(),
                                        subJsonObject.getString("TeacherName").toString(),
                                        subJsonObject.getString("Phone").toString(),
                                        subJsonObject.getString("Email").toString(),
                                        subJsonObject.getString("TeacherImage").toString())
                        );
                    }
                    adapter = new StudentTeacherInfoListView(getActivity().getApplicationContext(), mStudentTeacherInfoList);
                    studentTeacherInfoListView.setAdapter(adapter);
                }
                else {
                    // No record;
                }
            }
            catch (JSONException e) {
                // No record;
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
