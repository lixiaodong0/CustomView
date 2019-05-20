package com.lixd.costom.view.recyclerview.decoration;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lixd.costom.view.R;
import com.lixd.costom.view.recyclerview.decoration.base.BaseDecorationActivity;
import com.lixd.costom.view.recyclerview.decoration.logistics.LogisticsDecorationActivity;

public class ItemDecorationDemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_decoration_demo);
    }

    public void onBaseDecorationActivity(View v) {
        startActivity(new Intent(this, BaseDecorationActivity.class));
    }

    public void onLogisticsDecorationActivity(View v) {
        startActivity(new Intent(this, LogisticsDecorationActivity.class));
    }
}
