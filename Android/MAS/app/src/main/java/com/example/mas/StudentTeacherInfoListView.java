package com.example.mas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class StudentTeacherInfoListView extends BaseAdapter {

    private Context mContext;
    private List<StudentTeacherInfo> mStudentTeacherInfoList;

    public StudentTeacherInfoListView(Context context, List<StudentTeacherInfo> StudentTeacherInfoList) {
        mContext = context;
        mStudentTeacherInfoList = StudentTeacherInfoList;
    }

    @Override
    public int getCount() {
        return mStudentTeacherInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStudentTeacherInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.student_teacher_info_listview, null);

        ImageView teacherImage = (ImageView) v.findViewById(R.id.teacherImage);
        TextView teacherNameTxt2 = (TextView) v.findViewById(R.id.teacherNameTxt2);
        TextView moduleNameTxt2 = (TextView) v.findViewById(R.id.moduleNameTxt2);
        TextView phoneTxt = (TextView) v.findViewById(R.id.phoneTxt);
        TextView emailTxt = (TextView) v.findViewById(R.id.emailTxt);
        ImageView emailButton = (ImageView) v.findViewById(R.id.emailButton);
        ImageView contactButton = (ImageView) v.findViewById(R.id.contactButton);

        teacherImage.setImageBitmap(stringToBitmap(mStudentTeacherInfoList.get(position).getTeacherImage()));
        teacherNameTxt2.setText(mStudentTeacherInfoList.get(position).getTeacherName());
        moduleNameTxt2.setText(mStudentTeacherInfoList.get(position).getModuleName());
        phoneTxt.setText(mStudentTeacherInfoList.get(position).getPhone());
        emailTxt.setText(mStudentTeacherInfoList.get(position).getEmail());

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StudentEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String email = mStudentTeacherInfoList.get(position).getEmail();
                intent.putExtra("Email", email);
                mContext.startActivity(intent);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + mStudentTeacherInfoList.get(position).getPhone()));
                mContext.startActivity(intent);
            }
        });

        return v;
    }

    public Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
