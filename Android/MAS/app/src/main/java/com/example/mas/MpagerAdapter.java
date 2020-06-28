package com.example.mas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class MpagerAdapter extends PagerAdapter {

    private int[] layouts;
    private LayoutInflater layoutInflater;
    private Context context;
    private ImageView phoneBTN3;

    public MpagerAdapter(int[] layouts, Context context) {
        this.layouts = layouts;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        if(layouts[position] == R.layout.student_about_page5) {
            ImageView phoneBTN3 =(ImageView) view.findViewById(R.id.phoneBTN3);
            ImageView emailBTN3 =(ImageView) view.findViewById(R.id.emailBTN3);
            ImageView companyBTN3 =(ImageView) view.findViewById(R.id.companyBTN3);

            final TextView phoneTxt3 =(TextView) view.findViewById(R.id.phoneTxt3);
            final TextView emailTxt3 =(TextView) view.findViewById(R.id.emailTxt3);
            final TextView companyTxt3 =(TextView) view.findViewById(R.id.companyTxt3);

            phoneBTN3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("tel:" + phoneTxt3.getText().toString()));
                    context.startActivity(intent);
                }
            });

            emailBTN3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String recipientList = emailTxt3.getText().toString();
                    String[] recipients = recipientList.split(",");

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_TEXT, "Hello developer!");
                    intent.setType("message/rfc822");
                    context.startActivity(Intent.createChooser(intent, "Choose an email client"));
                }
            });

            companyBTN3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(companyTxt3.getText().toString())));
                }
            });
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
