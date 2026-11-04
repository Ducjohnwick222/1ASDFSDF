package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {

    private TextView tvIcon, tvName, tvAmount, tvCategory, tvDate, tvNote;
    private DatabaseHelper db;
    private int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        findViewById(R.id.btnHeaderBack).setOnClickListener(v -> finish());

        db = new DatabaseHelper(this);
        expenseId = getIntent().getIntExtra(MainActivity.EXTRA_ID, -1);

        tvIcon = findViewById(R.id.tvIcon);
        tvName = findViewById(R.id.tvName);
        tvAmount = findViewById(R.id.tvAmount);
        tvCategory = findViewById(R.id.tvCategory);
        tvDate = findViewById(R.id.tvDate);
        tvNote = findViewById(R.id.tvNote);

        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditExpenseActivity.class);
            i.putExtra(MainActivity.EXTRA_ID, expenseId);
            startActivity(i);
        });
        ((Button) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Expense e = db.getExpenseById(expenseId);
        if (e == null) { finish(); return; } // đã bị xóa
        tvName.setText(e.getName());
        tvAmount.setText(Utils.formatMoney(e.getAmount()));
        tvCategory.setText(e.getCategory());
        tvIcon.setText(Utils.iconFor(e.getCategory()));
        tvIcon.setBackground(Utils.circleBg(e.getCategory()));
        tvDate.setText(Utils.toDisplayDate(e.getDate()));
        String note = e.getNote();
        tvNote.setText(note == null || note.isEmpty() ? "(Không có ghi chú)" : note);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa khoản chi?")
                .setMessage("Bạn có chắc chắn muốn xóa khoản chi \"" + tvName.getText() + "\" không?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (d, w) -> {
                    db.deleteExpense(expenseId);
                    finish();
                })
                .show();
    }

}
