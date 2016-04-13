package com.bignerdranch.android.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Luwei on 2015/9/26.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private Photo mPhoto;
    private  String mSuspect;

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_SOLVED ="solved";
    private static final String JSON_PHOTO ="photo";
    private static final String JSON_SUSPECT ="suspect";


    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }
    //添加一个接受JSONObject对象的构造方法
    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString((JSON_ID)));
        if (json.has(JSON_TITLE)){
            mTitle= json.getString(JSON_TITLE);
        }
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO)){
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        }
        if (json.has(JSON_SUSPECT)){
            mSuspect = json.getString(JSON_SUSPECT);
        }
    }

    public JSONObject toJSON() throws JSONException {//以JSON格式保存Crime数据
        JSONObject json = new JSONObject();
        json.put(JSON_ID,mId.toString());
        json.put(JSON_TITLE,mTitle);
        json.put(JSON_DATE,mDate.getTime());
        json.put(JSON_SOLVED,mSolved);
        if (mPhoto!=null){
            json.put(JSON_PHOTO,mPhoto.toJSON());
        }
        json.put(JSON_SUSPECT,mSuspect);

        return json;

    }

    public UUID getId(){
        return  mId;
    }
    public String getTitle(){
        return mTitle;
    }
    public void setTitle(String title){
        mTitle = title;
    }
    public boolean isSolved(){
        return mSolved;
    }
    public void setSolved(boolean solved){
        mSolved = solved;
    }
    public Date getDate(){
        return mDate;
    }
    public void setDate(Date date){
        mDate = date;
    }

    @Override
    public String toString() {
        return mTitle;
    }
    public Photo getPhoto(){
        return mPhoto;
    }
    public void setPhoto(Photo photo){
        mPhoto = photo;
    }
    public String getSuspect(){
        return mSuspect;
    }
    public void setSuspect(String suspect){
        mSuspect = suspect;
    }
}
