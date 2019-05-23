package com.lixd.costom.view.recyclerview.decoration.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lixd.costom.view.R;
import com.lixd.costom.view.recyclerview.adapter.TestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 介绍基础装饰的用法
 */
public class BaseDecorationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_decoration);

        RecyclerView rv = findViewById(R.id.rv_demo);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("第" + (i + 1) + "条数据");
        }
        TestAdapter testAdapter = new TestAdapter(this, list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(testAdapter);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new CircleItemDecoration());
    }
}
