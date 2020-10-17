package jingpinwu.android.com;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class Beta
{

    private String title;
    private String text;
    private String imgurl;

    public Beta(String title, String text, String imgurl)
    {
        this.title = title;
        this.text = text;
        this.imgurl = imgurl;
    }

    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
    public String getImgUrl() {
        return imgurl;
    }
}
