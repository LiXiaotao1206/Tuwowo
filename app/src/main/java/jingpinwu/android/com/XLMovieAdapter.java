package jingpinwu.android.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class XLMovieAdapter extends ArrayAdapter
{
    private final int resourceId;

    public XLMovieAdapter(Context context, int textViewResourceId, List<XLMovie> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        XLMovie xlMovie = (XLMovie) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        MyImageView XLImage = (MyImageView) view.findViewById(R.id.xlimg);//获取该布局内的图片视图
        TextView XLName = (TextView) view.findViewById(R.id.xlname);//获取该布局内的文本视图
        TextView XLActor = (TextView) view.findViewById(R.id.xlactor);//获取该布局内的文本视图
        TextView XLTime = (TextView) view.findViewById(R.id.xltime);//获取该布局内的文本视图
        TextView XLBeizhu = (TextView) view.findViewById(R.id.xlbeizhu);//获取该布局内的文本视图TextView XLName = (TextView) view.findViewById(R.id.betatitle);//获取该布局内的文本视图
        TextView XLType = (TextView) view.findViewById(R.id.xltype);//获取该布局内的文本视图
        XLImage.setImageURL(xlMovie.getImgUrl());//为图片视图设置图片资源
        XLName.setText(xlMovie.getName());//为文本视图设置文本内容
        XLActor.setText(xlMovie.getActor());//为文本视图设置文本内容
        XLTime.setText(xlMovie.getTime());//为文本视图设置文本内容
        XLType.setText(xlMovie.getType());//为文本视图设置文本内容
        XLBeizhu.setText(xlMovie.getBeizhu());//为文本视图设置文本内容

        return view;
    }
}
