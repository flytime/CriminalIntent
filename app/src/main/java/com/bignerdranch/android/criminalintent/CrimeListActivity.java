package com.bignerdranch.android.criminalintent;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Luwei on 2015/10/11.
 */
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks{


    @Override
    protected Fragment createFragment() {//实现父类的抽象方法，返回CrimeListFragment实例
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return  R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {

        if (findViewById(R.id.detailFragmentContainer)==null){
            Intent i = new Intent(this,CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID,crime.getId());
            startActivityForResult(i,0);
        }else {
            FragmentManager fm = getSupportFragmentManager();
             FragmentTransaction ft = fm.beginTransaction();

            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            if (oldDetail!=null){
                ft.remove(oldDetail);
            }
            ft.add(R.id.detailFragmentContainer,newDetail);
            ft.commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {//实现列表刷新
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment ListFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragmentContainer);
        ListFragment.updateUI();
    }
}
