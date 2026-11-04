package com.example.expensemanager;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utils {
    public static final String[] CATEGORIES = {"Ăn uống", "Đi lại", "Học tập", "Giải trí", "Khác"};
    public static final String ALL_CATEGORIES = "Tất cả danh mục";

    private static final String[] ICONS = {"🍜", "🚌", "📚", "🎮", "🧾"};
    private static final int[] COLORS = {
            Color.parseColor("#F59E0B"), Color.parseColor("#3B82F6"), Color.parseColor("#8B5CF6"),
            Color.parseColor("#EC4899"), Color.parseColor("#64748B")};

    private static int indexOf(String category) {
        for (int i = 0; i < CATEGORIES.length; i++) if (CATEGORIES[i].equals(category)) return i;
        return CATEGORIES.length - 1;
    }

    public static String iconFor(String category) { return ICONS[indexOf(category)]; }
    public static int colorFor(String category) { return COLORS[indexOf(category)]; }

    private static int soft(int color) {
        return Color.argb(36, Color.red(color), Color.green(color), Color.blue(color));
    }

    /** Hình tròn màu nhạt theo danh mục (nền cho icon). */
    public static GradientDrawable circleBg(String category) {
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.OVAL);
        g.setColor(soft(colorFor(category)));
        return g;
    }

    /** Nhãn bo tròn màu nhạt theo danh mục. */
    public static GradientDrawable chipBg(String category, float density) {
        GradientDrawable g = new GradientDrawable();
        g.setCornerRadius(10 * density);
        g.setColor(soft(colorFor(category)));
        return g;
    }

    /** 120000 -> "120.000 đ" */
    public static String formatMoney(long amount) {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.US);
        s.setGroupingSeparator('.');
        return new DecimalFormat("#,###", s).format(amount) + " đ";
    }

    /** "2026-10-13" -> "13/10/2026" */
    public static String toDisplayDate(String sqlDate) {
        if (sqlDate == null || sqlDate.length() != 10) return sqlDate;
        return sqlDate.substring(8, 10) + "/" + sqlDate.substring(5, 7) + "/" + sqlDate.substring(0, 4);
    }

    /** yyyy-MM-dd (month: 0-11 như DatePicker) */
    public static String toSqlDate(int year, int month, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
    }
}
