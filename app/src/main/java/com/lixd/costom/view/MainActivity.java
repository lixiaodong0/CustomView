package com.lixd.costom.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lixd.costom.view.comment.input.OnInputFinishListener;
import com.lixd.costom.view.comment.input.WechatInputLayout;
import com.lixd.costom.view.flowlayout.CustomFlowLayoutActivity;
import com.lixd.costom.view.linearlayout.CustomLinearLayoutActivity;
import com.lixd.costom.view.recyclerview.RecyclerViewDemoActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WechatInputLayout wechatInputLayout = findViewById(R.id.WechatInputLayout);
        wechatInputLayout.setOnInputFinishListener(new OnInputFinishListener() {

            @Override
            public void onAudioFinish(File recordAudioFile, long recordDuration) {
                Toast.makeText(MainActivity.this, "音频地址:" + recordAudioFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAudioError(String errMsg) {
                Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextFinish(String text) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onCustomLinearLayout(View v) {
        startActivity(new Intent(this, CustomLinearLayoutActivity.class));
    }

    public void onCustomFlowLayout(View v) {
        startActivity(new Intent(this, CustomFlowLayoutActivity.class));
    }

    public void onRecyclerView(View v) {
        startActivity(new Intent(this, RecyclerViewDemoActivity.class));
    }
}
