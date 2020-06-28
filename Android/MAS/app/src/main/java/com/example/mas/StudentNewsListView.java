package com.example.mas;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class StudentNewsListView extends BaseAdapter {

    private Context mContext;
    private List<StudentNews> mStudentNewsList;

    public StudentNewsListView(Context context, List<StudentNews> studentNewsList) {
        mContext = context;
        mStudentNewsList = studentNewsList;
    }

    @Override
    public int getCount() {
        return mStudentNewsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStudentNewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.student_news_listview, null);

        TextView moduleName = (TextView) v.findViewById(R.id.moduleName4);
        TextView content = (TextView) v.findViewById(R.id.content4);
        TextView timeStamp = (TextView) v.findViewById(R.id.timeStamp4);

        moduleName.setText(mStudentNewsList.get(position).getModuleName());
        content.setText(mStudentNewsList.get(position).getContent());
        timeStamp.setText(mStudentNewsList.get(position).getTimeStamp());

        return v;
    }
}
