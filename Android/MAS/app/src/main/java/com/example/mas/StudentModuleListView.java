package com.example.mas;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class StudentModuleListView extends BaseAdapter {

    private Context mContext;
    private List<StudentModule> mStudentModuleList;

    public StudentModuleListView(Context context, List<StudentModule> timeTableList) {
        mContext = context;
        mStudentModuleList = timeTableList;
    }

    @Override
    public int getCount() {
        return mStudentModuleList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStudentModuleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.student_module_listview, null);

        TextView moduleNameTxt = (TextView) v.findViewById(R.id.moduleNameTxt);
        TextView teacherNameTxt = (TextView) v.findViewById(R.id.teacherNameTxt);
        TextView beginTimeTxt = (TextView) v.findViewById(R.id.beginTimeTxt);
        TextView endTimeTxt = (TextView) v.findViewById(R.id.endTimeTxt);
        TableLayout moduleTableLayout = (TableLayout) v.findViewById(R.id.moduleTableLayout);

        moduleNameTxt.setText(mStudentModuleList.get(position).getModuleName());
        teacherNameTxt.setText(mStudentModuleList.get(position).getTeacherName());
        beginTimeTxt.setText(mStudentModuleList.get(position).getBeginTime());
        endTimeTxt.setText(mStudentModuleList.get(position).getEndTime());
        if(mStudentModuleList.get(position).getStatus().equals("Available")) {
            moduleTableLayout.setBackgroundResource(R.drawable.ic_table_border_green);
        }
        else {
            moduleTableLayout.setBackgroundResource(R.drawable.ic_table_border_red);
        }

        return v;
    }
}
