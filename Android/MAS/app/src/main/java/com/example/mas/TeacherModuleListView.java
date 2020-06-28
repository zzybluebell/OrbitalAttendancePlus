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
public class TeacherModuleListView extends BaseAdapter {

    private Context mContext;
    private List<TeacherModule> mTeacherModuleList;

    public TeacherModuleListView(Context context, List<TeacherModule> teacherModuleList) {
        mContext = context;
        mTeacherModuleList = teacherModuleList;
    }

    @Override
    public int getCount() {
        return mTeacherModuleList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTeacherModuleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.teacher_module_listview, null);

        TextView startStopTxt = (TextView) v.findViewById(R.id.startStopTxt);
        TextView moduleCodeTxt = (TextView) v.findViewById(R.id.moduleCodeTxt);
        TextView moduleNameTxt = (TextView) v.findViewById(R.id.moduleNameTxt);
        TextView beginTimeTxt = (TextView) v.findViewById(R.id.beginTimeTxt);
        TextView endTimeTxt = (TextView) v.findViewById(R.id.endTimeTxt);
        TableLayout teacherModuleTableLayout = (TableLayout) v.findViewById(R.id.teacherModuleTableLayout);

        startStopTxt.setText(mTeacherModuleList.get(position).getStartStop());
        moduleCodeTxt.setText(mTeacherModuleList.get(position).getModuleCode());
        moduleNameTxt.setText(mTeacherModuleList.get(position).getModuleName());
        beginTimeTxt.setText(mTeacherModuleList.get(position).getBeginTime());
        endTimeTxt.setText(mTeacherModuleList.get(position).getEndTime());
        if(mTeacherModuleList.get(position).getStatus().equals("Available")) {
            teacherModuleTableLayout.setBackgroundResource(R.drawable.ic_table_border_green);
        }
        else {
            teacherModuleTableLayout.setBackgroundResource(R.drawable.ic_table_border_red);
        }

        return v;
    }
}
