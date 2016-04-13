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
    private CriminalIntentJSONSerializer mSerilalizer;//����ʵ��

    private static CrimeLab sCrimeLab;//�������ģʽ��
    // �ڸ����д����������˽�л���Ϊ�˱�֤����Ŀɿ�

    private Context mAppContext;

    private CrimeLab(Context appContext) {//˽�л����캯��
        mAppContext = appContext;
        mSerilalizer = new CriminalIntentJSONSerializer(mAppContext,FILENAME);//����Ĺ���
        // �����д���ʵ��
//        mCrimes = new ArrayList<Crime>();
//        for (int i = 0;i<100;i++){
//            Crime c = new Crime();//����ʵ��
//            c.setTitle("Crime #" + i);
//            c.setSolved(i % 2 == 0);
//            mCrimes.add(c);
        try {
            mCrimes = mSerilalizer.loadCrimes();//���Լ�������
        } catch (IOException e) {
           mCrimes = new ArrayList<Crime>();//����ʧ�ܾʹ���һ���������б�
            Log.e(TAG,"Error loading crimes:"+e);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public static CrimeLab get(Context c){//��̬��������������ʽ��
    // �����ṩ������������������Ի�ȡ������󣬿���ʵ�־�̬����
        if (sCrimeLab ==null){
            sCrimeLab = new CrimeLab(c.getApplicationContext());//ȷ�������ܻ���Context����
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
