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

    public static DatePickerFragment newInstance(Date date){//��̬����
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    //ͬһ��Activity�йܵ�Fragment֮���໥�������ݵķ�ʽ �����ͷ���
    private  void sendResult(int resultCode){
        if (getTargetFragment()==null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE,mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,i);//��ȡ
        // Ŀ��Fragment���Լ���ȡĿ��Fragment��������
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
        //����Calendar����ʵ�ֶԻ�ȡ��mDate����������ã����յõ���������
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);//ͨ��XML�ļ���������

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //����ȡ��mDate����ͨ��calender������з��룬�Եõ���������
                mDate = new GregorianCalendar(year,month,day).getTime();
                //�����ݽ��б��棬����Ϊ��Ļ��ת��ԭ����ɵ����ݶ�ʧ
                getArguments().putSerializable(EXTRA_DATE,mDate);

            }
        });

        return  new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);//���÷��͵Ľ����
                    }
                })
                .create();//�����ӿڵķ�ʽ����һ��AlertDialogʵ��
    }
}