package com.bignerdranch.android.criminalintent;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;


public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "criminalintent.CRIME_ID";
    private static final String DIALOG_DATE = "date";//唯一可识别DatePickerFragment
    private static final int REQUEST_DATE =0;//设置请求码
    private static final int REQUEST_PHOTO = 1;//设置另外的一个请求码
    private static final int REQUEST_CONTACT = 2;//设置另外的一个请求码
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_IMAGE ="image";

    Crime mCrime;
    EditText mTitleField;
    Button mDateButton;
    CheckBox mSolvedCheckBox;
    ImageView mPhotoView;
    Button mSuspectButton;
    Callbacks mCallbacks;


    private ImageButton mPhontoButton;

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    public static CrimeFragment newInstance(UUID crimeId){//静态方法
        Bundle args = new Bundle();//创建Bundle对象
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();//创建CrimeFragment实例
        fragment.setArguments(args);//将Bundle对象实例付给fragment

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);//获取argument
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);
    }

    @TargetApi(11)//添加注解阻止Android Lint报告兼容性问题
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if (NavUtils.getParentActivityName(getActivity())!=null){//显示向左的箭头，添加层级式导航
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                mCallbacks.onCrimeUpdated(mCrime);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
//        mDateButton.setText(mCrime.getDate().toString());
//        mDateButton.setEnabled(false);
        update();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());//将CrimeFragment
                // 的数据传递给DatePickerFragemnt
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);//将CrimeFragement设置
                // 为目标Fragement
                dialog.show(fm, DIALOG_DATE);//通过show方法将DatePickerFragment添加
                // 给FragmengManager管理并放置到屏幕中

            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                mCallbacks.onCrimeUpdated(mCrime);
            }
        });

        mPhontoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhontoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),CrimeCameraActivity.class);
                //startActivity(i);
                startActivityForResult(i,REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p==null)
                    return;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm,DIALOG_IMAGE);
            }
        });
        //检查设备是否带有相机
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)||
                Build.VERSION.SDK_INT<Build.VERSION_CODES.GINGERBREAD|| Camera.getNumberOfCameras()>0;
        if (!hasACamera){
            mPhontoButton.setEnabled(false);//如果设备上没有相机，将按钮设置为不可用状态
        }

        Button reportButton = (Button) v.findViewById(R.id.crime_reportButton);
        //设置监听器
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_SEND);//执行发送
                i.setType("text/plain");//指定数据类型
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());//添加数据
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));//添加数据
                i = Intent.createChooser(i,getString(R.string.send_report));//设置选择器的标题
                startActivity(i);
            }
        });

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i,REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect()!=null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

            return  v;//将生成的View返回给托管的Activity
    }

    private void showPhoto(){
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p!=null){
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaleDrawable(getActivity(),path);
        }
        mPhotoView.setImageDrawable(b);
    }
    @Override
    //同一个Activity托管的两个Fragment之间相互传递数据的方式，接收方法
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode!= Activity.RESULT_OK)//验证结果码
            return;
        if (requestCode==REQUEST_DATE){//验证请求码
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCallbacks.onCrimeUpdated(mCrime);
            update();
        }else if (requestCode ==REQUEST_PHOTO){
            //创建一个照片实例并将它添加到crime中
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);//获取照片的名字
            if (filename!=null){
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                mCallbacks.onCrimeUpdated(mCrime);
                showPhoto();
               // Log.i(TAG,"Crime: "+mCrime.getTitle()+" has a photo");
                Log.i(TAG, "filename:" + filename);
            }
        }else if (requestCode ==REQUEST_CONTACT){
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            //查询全部联系人
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            if (c.getCount()==0){
                c.close();
                return;
            }
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mCallbacks.onCrimeUpdated(mCrime);
            mSuspectButton.setText(suspect);
            c.close();
        }
    }

    private void update() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EE,MM,dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect==null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_subject,suspect);
        }

        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);

        return report;
    }


    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity())!=null){//检查元数据中是否
                // 指定了父Activity
                    NavUtils.navigateUpFromSameTask(getActivity());//导航至父Activity界面
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }
}
