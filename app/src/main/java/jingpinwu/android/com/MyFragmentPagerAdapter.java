package jingpinwu.android.com;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter
{

    List<Fragment> fragmentList;
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list)
    {
        super(fm);
        this.fragmentList = list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        //注释掉这一行是取消pager的自动销毁
        //super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }
}
