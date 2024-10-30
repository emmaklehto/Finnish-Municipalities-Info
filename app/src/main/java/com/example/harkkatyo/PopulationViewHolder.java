package com.example.harkkatyo;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class PopulationViewHolder extends RecyclerView.ViewHolder {
    TextView txtPopulationData, txtYearData;

    public PopulationViewHolder(@NonNull View itemView) {
        super(itemView);
        txtPopulationData = itemView.findViewById(R.id.txtPopulationData);
        txtYearData = itemView.findViewById(R.id.txtYearData);
    }
}
