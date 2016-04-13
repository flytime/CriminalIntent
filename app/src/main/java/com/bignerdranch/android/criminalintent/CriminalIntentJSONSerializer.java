package com.bignerdranch.android.criminalintent;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Luwei on 2015/10/23.
 */
public class CriminalIntentJSONSerializer {

    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context c,String f) {
        mContext = c;
        mFilename = f;
    }

    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine())!=null){
                jsonString.append(line);//�������
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i= 0;i<array.length();i++){
                crimes.add(new Crime(array.getJSONObject(i)));
            }

        } catch (FileNotFoundException e) {

        }finally {
            if (reader!=null)
                reader.close();
        }
        return crimes;
    }

    public void saveCrime(ArrayList<Crime> crimes) throws JSONException, IOException {
        //����JSON����
        JSONArray arry = new JSONArray();
        for (Crime c : crimes){
            arry.put(c.toJSON());//��Crime����ת��ΪJSON��ʽ��
            Writer writer = null;
            try {
                OutputStream out = mContext.openFileOutput(mFilename,Context.MODE_PRIVATE);//���ļ�
                // ��д������
                writer = new OutputStreamWriter(out);//ʵ���ַ����ֽڵ�ת��
                writer.write(arry.toString());//����д�����������е�����д���ļ���
            } finally {
                if (writer!=null)
                writer.close();
            }
        }
    }

}
