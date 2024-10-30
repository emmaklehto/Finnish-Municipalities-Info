package com.example.harkkatyo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchViewHolder extends RecyclerView.ViewHolder {
    TextView searchName;
    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);
        searchName = itemView.findViewById(R.id.txtSearchName);
    }
}
