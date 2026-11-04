package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "expense_id";

    private EditText edtSearch;
    private Spinner spCategory;
    private TextView tvTotal, tvEmpty;
    private RecyclerView rv;
    private ExpenseAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);

        edtSearch = findViewById(R.id.edtSearch);
        spCategory = findViewById(R.id.spCategory);
        tvTotal = findViewById(R.id.tvTotal);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv = findViewById(R.id.rvExpenses);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnStats = findViewById(R.id.btnStats);

        String[] filters = new String[Utils.CATEGORIES.length + 1];
        filters[0] = Utils.ALL_CATEGORIES;
        System.arraycopy(Utils.CATEGORIES, 0, filters, 1, Utils.CATEGORIES.length);
        spCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filters));

        adapter = new ExpenseAdapter(e -> {
            Intent i = new Intent(this, ExpenseDetailActivity.class);
            i.putExtra(EXTRA_ID, e.getId());
            startActivity(i);
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { loadData(); }
            @Override public void afterTextChanged(Editable s) { }
        });
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { loadData(); }
            @Override public void onNothingSelected(AdapterView<?> p) { }
        });

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddEditExpenseActivity.class)));
        btnStats.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String keyword = edtSearch.getText().toString();
        String category = (String) spCategory.getSelectedItem();
        List<Expense> list = db.getExpenses(keyword, category);
        long total = db.getTotal(keyword, category);

        adapter.setItems(list);
        tvTotal.setText(Utils.formatMoney(total));
        ((TextView) findViewById(R.id.tvTotalLabel))
                .setText("Tổng chi (theo bộ lọc)  ·  " + list.size() + " khoản");
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
