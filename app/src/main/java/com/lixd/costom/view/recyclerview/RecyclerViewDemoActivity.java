package com.lixd.costom.view.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lixd.costom.view.R;
import com.lixd.costom.view.recyclerview.decoration.ItemDecorationDemoActivity;

public class RecyclerViewDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_demo);
    }

    public void onItemDecorationActivity(View v) {
        startActivity(new Intent(this, ItemDecorationDemoActivity.class));
    }
}
