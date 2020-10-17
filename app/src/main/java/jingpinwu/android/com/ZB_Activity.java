package jingpinwu.android.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixiaotao on 2018/4/10.
 */

public class ZB_Activity extends AppCompatActivity
{
    ImageView imgys;
    ImageView imgws;
    ImageView imggat;
    ImageView imgse;
    ImageView imgty;
    ImageView imgdf;
    ImageView imgmore;
    Spinner spinner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_zhibo);
        new AlertDialog.Builder(this).setTitle("提示").setMessage("点击上方直播源可直接进行切换哦！").setPositiveButton("好", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        }).create();
        spinner = (Spinner) super.findViewById(R.id.xlSpinner);
        String[] strings=new String[]{"兔喔喔TV"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ZB_Activity.this,R.layout.spinnertext,strings);
        spinner.setAdapter(adapter);
        imgys = (ImageView)findViewById(R.id.zbimgys);
        imgws = (ImageView)findViewById(R.id.zbimgws);
        imgdf = (ImageView)findViewById(R.id.zbimgdf);
        imgse = (ImageView)findViewById(R.id.zbimgse);
        imgty = (ImageView)findViewById(R.id.zbimgty);
        imggat = (ImageView)findViewById(R.id.zbimggat);
        imgmore = (ImageView)findViewById(R.id.zbimgmore);
        imgws.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ZB_Activity.this, ZBcontent_Activity.class);
                intent1.putExtra("ZHIBO", 1);
                startActivity(intent1);
            }
        });
        imgys.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ZB_Activity.this, ZBcontent_Activity.class);
                intent1.putExtra("ZHIBO", 2);
                startActivity(intent1);
            }
        });
        imgty.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ZB_Activity.this, ZBcontent_Activity.class);
                intent1.putExtra("ZHIBO", 3);
                startActivity(intent1);
            }
        });
        imgdf.setOnClickListener(new View.OnClickListener()
        {
        @Override
        public void onClick(View v)
        {
            Intent intent1 = new Intent(ZB_Activity.this,ZBcontent_Activity.class);
            intent1.putExtra("ZHIBO",4);
            startActivity(intent1);
        }
        });
        imggat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ZB_Activity.this, ZBcontent_Activity.class);
                intent1.putExtra("ZHIBO", 5);
                startActivity(intent1);
            }
        });
        imgse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent1 = new Intent(ZB_Activity.this,ZBcontent_Activity.class);
                intent1.putExtra("ZHIBO",6);
                startActivity(intent1);
            }
        });
        imgmore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(ZB_Activity.this,"更多直播内容，等待更新！",Toast.LENGTH_LONG).show();
            }
        });
    }
}
