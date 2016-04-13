package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Luwei on 2015/10/9.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static final String FILENAME ="crimes.json";

    private ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerilalizer;//创建实例

    private static CrimeLab sCrimeLab;//单例设计模式，
    // 在该类中创建本类对象，私有化是为了保证对象的可控

    private Context mAppContext;

    private CrimeLab(Context appContext) {//私有化构造函数
        mAppContext = appContext;
        mSerilalizer = new CriminalIntentJSONSerializer(mAppContext,FILENAME);//在类的构造
        // 方法中创建实例
//        mCrimes = new ArrayList<Crime>();
//        for (int i = 0;i<100;i++){
//            Crime c = new Crime();//创建实例
//            c.setTitle("Crime #" + i);
//            c.setSolved(i % 2 == 0);
//            mCrimes.add(c);
        try {
            mCrimes = mSerilalizer.loadCrimes();//尝试加载数据
        } catch (IOException e) {
           mCrimes = new ArrayList<Crime>();//加载失败就创建一个空数组列表
            Log.e(TAG,"Error loading crimes:"+e);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public static CrimeLab get(Context c){//静态工厂方法，懒汉式，
    // 对外提供方法，让其他程序可以获取本类对象，可以实现静态调用
        if (sCrimeLab ==null){
            sCrimeLab = new CrimeLab(c.getApplicationContext());//确保单例总会有Context可用
        }
        return  sCrimeLab;
    }
    public Crime getCrime(UUID id) {
        for (Crime c : mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }
    public ArrayList<Crime> getCrimes(){
        return  mCrimes;
    }

    public  void addCrime (Crime c){
        mCrimes.add(c);
    }

    public void deleteCrime(Crime c){
        mCrimes.remove(c);
    }

    public boolean saveCrimes(){
        try {
            mSerilalizer.saveCrime(mCrimes);
            Log.d(TAG,"crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG,"Error saving crimes :"+e);
            return false;
        }
    }
}
