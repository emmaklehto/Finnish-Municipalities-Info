package com.example.harkkatyo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PopulationAdapter extends RecyclerView.Adapter<PopulationViewHolder> {
    private Context context;
    private ArrayList<MunicipalityData> populationData;

    public PopulationAdapter(Context context) {
        this.context = context;
        this.populationData = new ArrayList<>();
    }

    public void setPopulationData(ArrayList<MunicipalityData> populationData) {
        this.populationData = populationData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopulationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.population_view, parent, false);
        return new PopulationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopulationViewHolder holder, int position) {
        MunicipalityData data = populationData.get(position);
        holder.txtYearData.setText("Vuosi: " + data.getYear());
        holder.txtPopulationData.setText("Väkiluku: " + data.getPopulation());
    }

    @Override
    public int getItemCount() {
        return populationData.size();
    }
}
