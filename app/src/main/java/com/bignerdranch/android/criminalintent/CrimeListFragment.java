package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ListFragment;

import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class CrimeListFragment extends ListFragment {

   // private static final String TAG = "CrimeListFragement";

    private ArrayList<Crime> mCrimes;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;


    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    //用于刷新列表
    public void updateUI(){

        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//通知FragmentManager,其管理的fragment应接受onCreatOptionMenu
        // 方法的调用指令
        getActivity().setTitle(R.string.crime_title);//显示在标题栏上的文字
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);

        setRetainInstance(true);
        mSubtitleVisible = false;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);//获取具体的列表项内容
//        Log.d(TAG, c.getTitle() + " was clicked");
        //Intent i = new Intent(getActivity(),CrimePagerActivity.class);//第一个参数用于
        //获取托管CrimeListFragment的Activity也就是CrimeListActivity。 最终实现跳转到CrimePageActivity
       // i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());//通过intent来存储数据内容
       // startActivityForResult(i,0);
        mCallbacks.onCrimeSelected(c);//调用接口方法

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();//刷新列表
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();//与onResum功能相同，刷新列表
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if (mSubtitleVisible){//根据变量mSubtitleVisible的状态设置子标题
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }
        ListView listView = (ListView) v.findViewById(android.R.id.list);//获取listview
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.HONEYCOMB){//针对低版本的Android系统，采用浮动上下文菜单
            registerForContextMenu(listView);//直接登记ListView视图，然后会自动登记各个列表项视图
        }else {//高版本的android系统采取上下文操作栏
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);//设置为多模式

            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {//实例化上下文
                // 菜单，并显示在上下文操作栏上的任务完成的地方
                    MenuInflater inflater = mode.getMenuInflater();//从操作模式中获取
                    inflater.inflate(R.menu.crime_list_item_context,menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {//当前上下文
                    //需要刷新显示新数据时调用
                    return false;
                }

                @Override
                //当用户选中某个菜单项时调用
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_item_delete_crime:
                            CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
                            CrimeLab crimeLab = CrimeLab.get(getActivity());
                            for (int i = adapter.getCount()-1;i>=0;i--){
                                if(getListView().isItemChecked(i)){//如果列表中某一项被选中
                                    crimeLab.deleteCrime(adapter.getItem(i));
                                }
                            }
                            mode.finish();//销毁操作模式
                            adapter.notifyDataSetChanged();//刷新
                            return true;
                            default:
                                return false;
                    }
                }
                @Override
                //用户退出上下文操作模式或者所选菜单已经被响应，调用此方法
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {//回调方法
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);//实例化定义的菜单文件
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible&&showSubtitle !=null){//查看子标题的状态
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @Override
    //首先获取与CrimeListActivity关联的MenuInflater然后调用inflate方法传入菜单资源ID和ContextMenu实例
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //获取要删除的crime对象的信息
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
        Crime crime = adapter.getItem(position);//获取要删除的crime数据

        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();//刷新
                return  true;
        }
        return  super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
//                Intent i = new Intent(getActivity(),CrimePagerActivity.class);
//                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
//                startActivityForResult(i,0);
                ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();//只要增加一项纪录就会重新加载Crime列表
                mCallbacks.onCrimeSelected(crime);
                return  true;
            case R.id.menu_item_show_subtitle:
                if (getActivity().getActionBar().getSubtitle()==null){
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    mSubtitleVisible = true;//根据菜单项的选择设置变量值
                    item.setTitle(R.string.hide_subtitle);

                }
                else {
                    getActivity().getActionBar().setSubtitle(null);
                    mSubtitleVisible = false;//根据菜单项的选择设置变量值
                    item.setTitle(R.string.show_subtitle);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }


    private class CrimeAdapter extends ArrayAdapter<Crime> {
        public CrimeAdapter(ArrayList<Crime> crimes) {//构造函数
           super(getActivity(),0,crimes);//第一个参数是Context对象
            //由于不打算使用预定义的布局文件，所以传入0作为布局ID参数；第三个参数是数据集
        }

        //重新定于新的布局视图对象，用于显示列表项的标题，日期，状态等信息
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_crime, null);//在adapter的构造方法中
                        // 定义其他的布局视图对象
            }

            // configure the view for this Crime
            Crime c = getItem(position);//找到对应的列表项

            TextView titleTextView =      //获取显示标题
                    (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());

            TextView dateTextView =      //获取显示日期
                    (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());

            CheckBox solvedCheckBox =     //获取显示解决状态
                    (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;   //返回新的布局视图对象
        }
    }
    }


