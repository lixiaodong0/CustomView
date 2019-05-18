package com.lixd.costom.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lixd.costom.view.flowlayout.CustomFlowLayoutActivity;
import com.lixd.costom.view.linearlayout.CustomLinearLayoutActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onCustomLinearLayout(View v) {
        startActivity(new Intent(this, CustomLinearLayoutActivity.class));
    }

    public void onCustomFlowLayout(View v) {
        startActivity(new Intent(this, CustomFlowLayoutActivity.class));
    }
}
