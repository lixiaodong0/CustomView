package com.lixd.costom.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lixd.costom.view.flowlayout.CustomFlowLayoutActivity;
import com.lixd.costom.view.linearlayout.CustomLinearLayoutActivity;
import com.lixd.costom.view.recyclerview.RecyclerViewDemoActivity;

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

    public void onRecyclerView(View v) {
        startActivity(new Intent(this, RecyclerViewDemoActivity.class));
    }
}
