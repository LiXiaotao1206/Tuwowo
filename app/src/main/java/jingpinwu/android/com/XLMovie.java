package jingpinwu.android.com;

/**
 * Created by 李晓涛 on 2018/2/16.
 */

public class XLMovie
{

    private String name;
    private String type;
    private String time;
    private String actor;
    private String beizhu;
    private String imgurl;

    public XLMovie(String name,String actor,String type, String time,String beizhu, String imgurl)
    {
        this.name = name;
        this.type = type;
        this.time = time;
        this.actor = actor;
        this.beizhu = beizhu;
        this.imgurl = imgurl;
    }

    public String getName() {
        return name;
    }
    public String getActor() {
        return actor;
    }
    public String getBeizhu() {
        return beizhu;
    }
    public String getType() {
    return type;
}
    public String getTime() {
        return time;
    }
    public String getImgUrl() {
        return imgurl;
    }
}
