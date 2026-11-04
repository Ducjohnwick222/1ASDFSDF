package com.example.expensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "expense_manager.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "amount INTEGER NOT NULL, " +
                "category TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "note TEXT)");

        db.execSQL("INSERT INTO users (username, password) VALUES ('admin', '123456')");
        db.execSQL("INSERT INTO expenses (name, amount, category, date) VALUES ('Ăn sáng', 30000, 'Ăn uống', '2026-10-13')");
        db.execSQL("INSERT INTO expenses (name, amount, category, date) VALUES ('Đổ xăng', 50000, 'Đi lại', '2026-10-12')");
        db.execSQL("INSERT INTO expenses (name, amount, category, date) VALUES ('Mua sách', 120000, 'Học tập', '2026-10-11')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // ---------- Đăng nhập ----------
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM users WHERE username = ? AND password = ?",
                new String[]{username, password});
        boolean ok = c.getCount() > 0;
        c.close();
        return ok;
    }

    // ---------- CRUD ----------
    public long insertExpense(Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", e.getName());
        v.put("amount", e.getAmount());
        v.put("category", e.getCategory());
        v.put("date", e.getDate());
        v.put("note", e.getNote());
        // INSERT INTO expenses (name, amount, category, date, note) VALUES (?, ?, ?, ?, ?)
        return db.insert("expenses", null, v);
    }

    public int updateExpense(Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", e.getName());
        v.put("amount", e.getAmount());
        v.put("category", e.getCategory());
        v.put("date", e.getDate());
        v.put("note", e.getNote());
        // UPDATE expenses SET name=?, amount=?, category=?, date=?, note=? WHERE id = ?
        return db.update("expenses", v, "id = ?", new String[]{String.valueOf(e.getId())});
    }

    public int deleteExpense(int id) {
        SQLiteDatabase db = getWritableDatabase();
        // DELETE FROM expenses WHERE id = ?
        return db.delete("expenses", "id = ?", new String[]{String.valueOf(id)});
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, name, amount, category, date, note FROM expenses WHERE id = ?",
                new String[]{String.valueOf(id)});
        Expense e = null;
        if (c.moveToFirst()) e = fromCursor(c);
        c.close();
        return e;
    }

    // ---------- Tìm kiếm / lọc ----------
    private boolean hasCategory(String category) {
        return category != null && !category.isEmpty() && !category.equals(Utils.ALL_CATEGORIES);
    }

    private String buildWhere(String keyword, String category, List<String> args) {
        StringBuilder where = new StringBuilder(" WHERE name LIKE ?");
        args.add("%" + (keyword == null ? "" : keyword.trim()) + "%");
        if (hasCategory(category)) {
            where.append(" AND category = ?");
            args.add(category);
        }
        return where.toString();
    }

    public List<Expense> getExpenses(String keyword, String category) {
        List<String> args = new ArrayList<>();
        String sql = "SELECT id, name, amount, category, date, note FROM expenses"
                + buildWhere(keyword, category, args)
                + " ORDER BY date DESC, id DESC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, args.toArray(new String[0]));
        List<Expense> list = new ArrayList<>();
        while (c.moveToNext()) list.add(fromCursor(c));
        c.close();
        return list;
    }

    public long getTotal(String keyword, String category) {
        List<String> args = new ArrayList<>();
        String sql = "SELECT SUM(amount) FROM expenses" + buildWhere(keyword, category, args);
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, args.toArray(new String[0]));
        long total = 0;
        if (c.moveToFirst()) total = c.getLong(0); // SUM = NULL -> 0
        c.close();
        return total;
    }

    // ---------- Thống kê ----------
    public Map<String, Long> getTotalByCategory() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (String cat : Utils.CATEGORIES) map.put(cat, 0L); // đủ 5 danh mục
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT category, SUM(amount) FROM expenses GROUP BY category", null);
        while (c.moveToNext()) map.put(c.getString(0), c.getLong(1));
        c.close();
        return map;
    }

    private Expense fromCursor(Cursor c) {
        return new Expense(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getLong(c.getColumnIndexOrThrow("amount")),
                c.getString(c.getColumnIndexOrThrow("category")),
                c.getString(c.getColumnIndexOrThrow("date")),
                c.getString(c.getColumnIndexOrThrow("note")));
    }
}
