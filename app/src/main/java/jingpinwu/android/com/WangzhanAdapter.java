package jingpinwu.android.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jingpinwu.android.com.R;
import jingpinwu.android.com.Wangzhan;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class WangzhanAdapter extends ArrayAdapter
{
    private final int resourceId;

    public WangzhanAdapter(Context context, int textViewResourceId, List<Wangzhan> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Wangzhan fruit = (Wangzhan) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        ImageView fruitImage = (ImageView) view.findViewById(R.id.settings_img);//获取该布局内的图片视图
        TextView fruitName = (TextView) view.findViewById(R.id.settings_title);//获取该布局内的文本视图
        fruitImage.setImageResource(fruit.getImageId());//为图片视图设置图片资源
        fruitName.setText(fruit.getName());//为文本视图设置文本内容
        return view;
    }
}
