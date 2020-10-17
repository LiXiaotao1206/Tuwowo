package jingpinwu.android.com;

/**
 * Created by lixiaotao on 2018/7/29.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtlis {
    /**
     *get请求封装
     */
    public static void getRequest(String url, Map<String,String> params, String encode, OnResponseListner listner) {
        StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        if (params!=null && !params.isEmpty()){
            for (Map.Entry<String,String> entry:params.entrySet()) {    //增强for遍历循环添加拼接请求内容
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            if (listner!=null) {
                try {
                    URL path = new URL(sb.toString());
                    if (path!=null) {
                        HttpURLConnection con = (HttpURLConnection) path.openConnection();
                        con.setRequestMethod("GET");    //设置请求方式
                        con.setConnectTimeout(3000);    //链接超时3秒
                        con.setDoOutput(true);
                        con.setDoInput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(sb.toString().getBytes(encode));
                        os.close();
                        if (con.getResponseCode() == 200) {    //应答码200表示请求成功
                            onSucessResopond(encode, listner, con);
                        }
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    onError(listner, error);
                }
            }
        }
    }

    /**
     * POST请求
     */
    public static void postRequest(String url,Map<String,String> params,String encode,OnResponseListner listner){
        StringBuffer sb = new StringBuffer();
        if (params!=null && !params.isEmpty()){
            for (Map.Entry<String,String> entry: params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        if (listner!=null) {
            try {
                URL path = new URL(url);
                if (path!=null){
                    HttpURLConnection con = (HttpURLConnection) path.openConnection();
                    con.setRequestMethod("POST");   //设置请求方法POST
                    con.setConnectTimeout(3000);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    byte[] bytes = sb.toString().getBytes();
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.close();
                    if (con.getResponseCode()==200){
                        onSucessResopond(encode, listner,  con);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(listner, e);
            }
        }
    }

    private static void onError(OnResponseListner listner,Exception onError) {
        listner.onError(onError.toString());
    }

    private static void onSucessResopond(String encode, OnResponseListner listner, HttpURLConnection con) throws IOException
    {
        InputStream inputStream = con.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//创建内存输出流
        int len = 0;
        byte[] bytes = new byte[1024];
        if (inputStream != null) {
            while ((len = inputStream.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            String str = new String(baos.toByteArray(), encode);
            listner.onSucess(str);
        }
    }

    public interface OnResponseListner {
        void onSucess(String response);
        void onError(String error);
    }

}