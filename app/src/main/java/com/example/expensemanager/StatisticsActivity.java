package com.example.expensemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        findViewById(R.id.btnHeaderBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper db = new DatabaseHelper(this);
        Map<String, Long> map = db.getTotalByCategory();

        long total = 0;
        for (long v : map.values()) total += v;
        ((TextView) findViewById(R.id.tvGrandTotal)).setText(Utils.formatMoney(total));

        LinearLayout container = findViewById(R.id.containerStats);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Map.Entry<String, Long> en : map.entrySet()) {
            View row = inflater.inflate(R.layout.item_stat, container, false);
            int percent = total == 0 ? 0 : (int) Math.round(en.getValue() * 100.0 / total);
            ((TextView) row.findViewById(R.id.tvCatName)).setText(en.getKey());
            ((TextView) row.findViewById(R.id.tvCatAmount)).setText(Utils.formatMoney(en.getValue()));
            ((TextView) row.findViewById(R.id.tvPercent)).setText(percent + "%");
            ProgressBar pb = row.findViewById(R.id.progress);
            pb.setProgress(percent);
            pb.setProgressTintList(android.content.res.ColorStateList.valueOf(Utils.colorFor(en.getKey())));
            TextView ic = row.findViewById(R.id.tvCatIcon);
            ic.setText(Utils.iconFor(en.getKey()));
            ic.setBackground(Utils.circleBg(en.getKey()));
            container.addView(row);
        }
    }

}
