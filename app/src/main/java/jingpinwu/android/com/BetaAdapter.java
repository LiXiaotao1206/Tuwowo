package jingpinwu.android.com;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class BetaAdapter extends ArrayAdapter
{
    private final int resourceId;

    public BetaAdapter(Context context, int textViewResourceId, List<Beta> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Beta beta = (Beta) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        MyImageView BetaImage = (MyImageView) view.findViewById(R.id.betaimg);//获取该布局内的图片视图
        TextView BetaTitle = (TextView) view.findViewById(R.id.betatitle);//获取该布局内的文本视图
        TextView BetaText = (TextView) view.findViewById(R.id.betatext);//获取该布局内的文本视图
        if(beta.getImgUrl()!=null && beta.getImgUrl().equals("video"))
        {
            BetaImage.setMyImageBitmap(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.videobitmap));
        }
        else if(beta.getImgUrl()!=null && beta.getImgUrl().equals("book"))
        {
            BetaImage.setMyImageBitmap(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.bookbitmap));
        }
        else if(beta.getImgUrl()!=null && beta.getImgUrl().equals("live"))
        {
            BetaImage.setMyImageBitmap(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.livebitmap));
        }
        else
        {
            BetaImage.setImageURL(beta.getImgUrl());//为图片视图设置图片资源
        }
        BetaTitle.setText(beta.getTitle());//为文本视图设置文本内容
        BetaText.setText(beta.getText());//为文本视图设置文本内容
        return view;
    }
}
