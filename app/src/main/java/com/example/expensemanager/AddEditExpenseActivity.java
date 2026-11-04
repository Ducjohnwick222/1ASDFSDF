package com.example.expensemanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEditExpenseActivity extends AppCompatActivity {

    private EditText edtName, edtAmount, edtNote;
    private Spinner spCategory;
    private DatePicker datePicker;
    private DatabaseHelper db;
    private int editId = -1; // -1: thêm mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        findViewById(R.id.btnHeaderBack).setOnClickListener(v -> finish());
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);

        db = new DatabaseHelper(this);
        edtName = findViewById(R.id.edtName);
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        spCategory = findViewById(R.id.spCategory);
        datePicker = findViewById(R.id.datePicker);
        Button btnSave = findViewById(R.id.btnSave);

        spCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Utils.CATEGORIES));
        datePicker.setMaxDate(System.currentTimeMillis()); // không chọn ngày tương lai

        editId = getIntent().getIntExtra(MainActivity.EXTRA_ID, -1);
        if (editId != -1) {
            tvHeaderTitle.setText("Sửa khoản chi");
            btnSave.setText("Cập nhật khoản chi");
            Expense e = db.getExpenseById(editId);
            if (e != null) fillForm(e);
        } else {
            tvHeaderTitle.setText("Thêm khoản chi");
            btnSave.setText("Lưu khoản chi");
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void fillForm(Expense e) {
        edtName.setText(e.getName());
        edtAmount.setText(String.valueOf(e.getAmount()));
        edtNote.setText(e.getNote());
        for (int i = 0; i < Utils.CATEGORIES.length; i++) {
            if (Utils.CATEGORIES[i].equals(e.getCategory())) { spCategory.setSelection(i); break; }
        }
        String[] p = e.getDate().split("-");
        datePicker.updateDate(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
    }

    private void save() {
        String name = edtName.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Tên khoản chi không được để trống");
            edtName.requestFocus();
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException ex) {
            edtAmount.setError("Vui lòng nhập số tiền hợp lệ");
            edtAmount.requestFocus();
            return;
        }
        if (amount <= 0) {
            edtAmount.setError("Số tiền phải lớn hơn 0");
            edtAmount.requestFocus();
            return;
        }

        Calendar chosen = Calendar.getInstance();
        chosen.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        if (chosen.after(today)) {
            Toast.makeText(this, "Không được chọn ngày sau ngày hiện tại", Toast.LENGTH_SHORT).show();
            return;
        }

        Expense e = new Expense();
        e.setName(name);
        e.setAmount(amount);
        e.setCategory((String) spCategory.getSelectedItem());
        e.setDate(Utils.toSqlDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
        e.setNote(edtNote.getText().toString().trim());

        if (editId == -1) {
            if (db.insertExpense(e) == -1) {
                Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Đã thêm khoản chi", Toast.LENGTH_SHORT).show();
        } else {
            e.setId(editId);
            db.updateExpense(e);
            Toast.makeText(this, "Đã cập nhật khoản chi", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}
