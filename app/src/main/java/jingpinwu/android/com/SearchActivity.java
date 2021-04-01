package jingpinwu.android.com;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by 李晓涛 on 2018/2/18.
 */

public class SearchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_search);

        Button button_search = (Button)findViewById(R.id.button_search);
        final EditText editText_search = (EditText)findViewById(R.id.edit_videosearch);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_search.getText().toString().length()<1)
                {
                    Toast.makeText(SearchActivity.this,"请输入视频搜索关键字",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Toast.makeText(SearchActivity.this,"1"+editText_search.getText().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SearchActivity.this, JiexiActivity.class);
                    intent.putExtra("SEARCH", "https://www.baidu.com/s?wd=" + editText_search.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}
