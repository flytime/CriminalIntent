package com.bignerdranch.android.criminalintent;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {

    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "CrimeCameraFragment.filename";


    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private View mProgressContainer;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallBack = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String filename = UUID.randomUUID().toString()+"jpg";
            //保存文件到磁盘
            FileOutputStream os = null;
            boolean success = true;
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e) {

                Log.e(TAG,"Error writing to file"+filename,e);
                success = false;
            }finally {
                    try {
                        if (os!=null)
                        os.close();
                    } catch (Exception e) {
                       Log.e(TAG,"Error closing file"+filename,e);
                        success = false;
                    }
                Log.i(TAG,"JPEG saved at"+filename);
            }

            if (success){
                if (success) {//判断照片的保存状态
                    Intent i = new Intent();
                    i.putExtra(EXTRA_PHOTO_FILENAME,filename);
                    getActivity().setResult(Activity.RESULT_OK,i);//设置结果码为RESULT_OK
                }else {
                    getActivity().setResult(Activity.RESULT_CANCELED);//设置结果码为RESULT_CANCELED
                }
            }
            getActivity().finish();
        }

    };


    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera,container,false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                if (mCamera!=null){
                    mCamera.takePicture(mShutterCallback,null,mJpegCallBack);
                }
            }
        });
        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {//实现surface与客户端mCamer实例相关联
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);//此方法用于连接Camer和Surface
                    }
                }catch (IOException exception){
                    Log.e(TAG,"Error setting up preview displaying",exception);

                }
            }

            @Override
            //用于控制surface客户端的显示区域的大小
            public void surfaceChanged(SurfaceHolder holder, int format, int w , int h ) {
                if (mCamera == null)
                    return;
                Camera.Parameters parameters = mCamera.getParameters();//获取支持相机预览尺寸列表
                Size s =getBestSupportedSize(parameters.getSupportedPreviewSizes(), w , h );
                parameters.setPreviewSize(s.width,s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(),w,h);//设置图片的尺寸
                parameters.setPictureSize(s.width,s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                }
                catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
            @Override
            //通知Surface的客户端停止使用Surface
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera!=null){
                    mCamera.stopPreview();//断开连接
                }

            }
        });

        return v;
    }
    @TargetApi(9)
    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            mCamera =Camera.open(0);//打开后置摄像头
        }else {
            mCamera =Camera.open();//打开前置摄像头
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mCamera!=null){
            mCamera.release();//释放资源
            mCamera=null;
        }
    }
    @SuppressWarnings("deprecation")//解除代码警告
    //找出具有最大数目的像素的尺寸
    private Size getBestSupportedSize(List<Size> sizes ,int width,int height){
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width*bestSize.height;
        for (Size s :sizes){
            int area = s.width*s.height;
            if (area>largestArea){
                bestSize= s;
                largestArea = area;
            }
        }
        return bestSize;
    }
}
