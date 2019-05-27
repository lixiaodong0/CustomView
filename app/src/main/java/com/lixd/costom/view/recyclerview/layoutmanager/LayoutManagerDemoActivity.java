package com.lixd.costom.view.recyclerview.layoutmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.lixd.costom.view.R;
import com.lixd.costom.view.recyclerview.adapter.TestAdapter;

import java.util.ArrayList;
import java.util.List;

public class LayoutManagerDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_manager_demo);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            list.add("第" + (i + 1) + "数据");
        }
        RecyclerView rcDemo = findViewById(R.id.rv_demo);
        rcDemo.setLayoutManager(new CustomLayoutManagerRecyclered());
        //        rcDemo.setLayoutManager(new LinearLayoutManager(this));
        rcDemo.setAdapter(new TestAdapter(this, list));
    }
}
