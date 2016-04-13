package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Luwei on 2015/10/17.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "criminalintent.DATE";

    private Date mDate;

    public static DatePickerFragment newInstance(Date date){//静态方法
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    //同一个Activity托管的Fragment之间相互传递数据的方式 ，发送方法
    private  void sendResult(int resultCode){
        if (getTargetFragment()==null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE,mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);//获取
        // 目标Fragment，以及获取目标Fragment的请求码
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
        //创建Calendar对象，实现对获取的mDate对象进行配置，最终得到整数数据
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);//通过XML文件创建布局

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //将获取的mDate数据通过calender对象进行翻译，以得到整数数据
                mDate = new GregorianCalendar(year,month,day).getTime();
                //将数据进行保存，防因为屏幕旋转等原因造成的数据丢失
                getArguments().putSerializable(EXTRA_DATE,mDate);

            }
        });

        return  new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);//设置发送的结果码
                    }
                })
                .create();//以流接口的方式创建一个AlertDialog实例
    }
}
