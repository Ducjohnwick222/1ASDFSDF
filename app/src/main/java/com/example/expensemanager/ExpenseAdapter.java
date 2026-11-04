package com.example.expensemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.VH> {

    public interface OnItemClickListener { void onItemClick(Expense e); }

    private List<Expense> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public ExpenseAdapter(OnItemClickListener listener) { this.listener = listener; }

    public void setItems(List<Expense> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Expense e = items.get(position);
        h.tvName.setText(e.getName());
        h.tvAmount.setText(Utils.formatMoney(e.getAmount()));
        h.tvCategory.setText(e.getCategory());
        h.tvDate.setText(Utils.toDisplayDate(e.getDate()));
        h.tvIcon.setText(Utils.iconFor(e.getCategory()));
        h.tvIcon.setBackground(Utils.circleBg(e.getCategory()));
        float d = h.itemView.getResources().getDisplayMetrics().density;
        h.tvCategory.setBackground(Utils.chipBg(e.getCategory(), d));
        h.tvCategory.setTextColor(Utils.colorFor(e.getCategory()));
        h.itemView.setOnClickListener(v -> listener.onItemClick(e));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvAmount, tvCategory, tvDate;
        VH(View v) {
            super(v);
            tvIcon = v.findViewById(R.id.tvIcon);
            tvName = v.findViewById(R.id.tvName);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvCategory = v.findViewById(R.id.tvCategory);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }
}
