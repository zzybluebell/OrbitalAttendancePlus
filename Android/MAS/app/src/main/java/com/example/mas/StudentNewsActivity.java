package com.example.mas;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

public class StudentNewsActivity extends AppCompatActivity {

    private String AdminNo;
    private List<StudentNews> mStudentNewsList;
    private latestNewsTask lTask = null;
    private StudentNewsListView adapter;
    private ListView studentNewsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_news);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Latest News");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdminNo = getIntent().getStringExtra("AdminNo");

        studentNewsListView = (ListView) findViewById(R.id.studentNewsListView);

        lTask = new latestNewsTask();
        lTask.execute();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.studentNewsSwipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lTask = null;
                        lTask = new latestNewsTask();
                        lTask.execute();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });
    }

    private class latestNewsTask extends AsyncTask<String, Integer, String> {
        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conn = null; //连接对象
            InputStream is = null;
            String resultData = "error";
            try {
                String path = "http://" + IPAddress.ipaddress + "/MAS/App/latestNews.php";
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
                    mStudentNewsList = new ArrayList<>();
                    for (int i=1; i<jsonObject.length(); i++) {
                        JSONObject subJsonObject = new JSONObject(jsonObject.getString(Integer.toString(i-1)));
                        mStudentNewsList.add(
                                new StudentNews(i-1,
                                        subJsonObject.getString("ModuleName").toString(),
                                        subJsonObject.getString("Content").toString(),
                                        subJsonObject.getString("TimeStamp").toString()
                                )
                        );
                    }
                    adapter = new StudentNewsListView(getApplicationContext(), mStudentNewsList);
                    studentNewsListView.setAdapter(adapter);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
