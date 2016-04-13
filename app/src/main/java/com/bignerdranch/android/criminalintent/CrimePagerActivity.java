package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Luwei on 2015/10/17.
 */
//以代码的方式创建视图ViewPager，而不是通过XML文件创建，ViewPager是一个fragment容器
@SuppressWarnings("deprecation")
public class CrimePagerActivity extends FragmentActivity implements CrimeFragment.Callbacks {
    ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mViewPager = new ViewPager(this);//创建ViewPage实例
        mViewPager.setId(R.id.viewPager);//赋值资源ID给ViewPage
        setContentView(mViewPager);//设置ViewPage为Activity的内容视图

        final ArrayList<Crime> crimes = CrimeLab.get(this).getCrimes();//获取数据集

        FragmentManager fm = getSupportFragmentManager();//获取Activity的FragmengManager管理实例
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {//CrimePageActivity是跳转后的
        // 方法，执行跳转是会传入position参数的
            @Override
            public Fragment getItem(int position) {
                UUID crimeId = crimes.get(position).getId();
                return  CrimeFragment.newInstance(crimeId);//返回一个已配置的用于显示
                // 指定位置Crime信息的Fragment
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Crime crime = crimes.get(position);
                if (crime.getTitle()!=null){
                    setTitle(crime.getTitle());
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for (int i = 0; i< crimes.size();i++){//循环检查CrimeID，将当前显示的列表项设置为Crime在数组中索引位置
            if (crimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
