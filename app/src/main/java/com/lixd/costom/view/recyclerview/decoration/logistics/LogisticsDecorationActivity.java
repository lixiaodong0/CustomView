package com.lixd.costom.view.recyclerview.decoration.logistics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lixd.costom.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 物流效果装饰
 */
public class LogisticsDecorationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_decoration);

        RecyclerView rvDemo = findViewById(R.id.rv_demo);
        LogisticsAdapter logisticsAdapter = new LogisticsAdapter(getData(), this);
        rvDemo.addItemDecoration(new LogisticsItemDecoration(this));
        rvDemo.setLayoutManager(new LinearLayoutManager(this));
        rvDemo.setAdapter(logisticsAdapter);

    }

    private List<LogisticsBean> getData() {
        List<LogisticsBean> data = new ArrayList<>();
        data.add(new LogisticsBean("05-11", "13:35", "已签收", "包裹已签收", new LogisticsBean.Node(R.mipmap.ic_launcher_round, true,true,true)));
        data.add(new LogisticsBean("05-11", "10:24", "派送中", "派送中.....", new LogisticsBean.Node(R.mipmap.ic_launcher_round, false)));
        data.add(new LogisticsBean("05-11", "10:24", "运输中", "运输中.....", new LogisticsBean.Node(R.mipmap.logistics_2, false)));
        data.add(new LogisticsBean("05-11", "03:05", "", "运输中.....", new LogisticsBean.Node(0, false)));
        data.add(new LogisticsBean("05-11", "03:04", "", "运输中.....", new LogisticsBean.Node(0, false)));
        data.add(new LogisticsBean("05-11", "03:03", "", "运输中.....", new LogisticsBean.Node(0, false)));
        data.add(new LogisticsBean("05-11", "03:02", "", "运输中.....", new LogisticsBean.Node(0, false)));
        data.add(new LogisticsBean("05-11", "13:35", "已揽件", "已揽件......", new LogisticsBean.Node(R.mipmap.logistics_1, false)));
        data.add(new LogisticsBean("05-11", "13:35", "已发货", "已发货......", new LogisticsBean.Node(R.mipmap.ic_launcher_round, false)));
        data.add(new LogisticsBean("05-11", "13:35", "已出库", "已出库......", new LogisticsBean.Node(R.mipmap.ic_launcher_round, false)));
        data.add(new LogisticsBean("05-11", "13:35", "仓库处理中", "仓库处理中.....", new LogisticsBean.Node(R.mipmap.logistics_3, false)));
        data.add(new LogisticsBean("05-11", "13:35", "", "打印已完成....", new LogisticsBean.Node(0, false)));
        data.add(new LogisticsBean("05-11", "13:35", "仓库已接单", "仓库已接单....", new LogisticsBean.Node(R.mipmap.ic_launcher_round, false, false,false)));
        return data;
    }
}
