package com.example.harkkatyo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchListAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(String search);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {

        mListener = listener;
    }

    public SearchListAdapter(Context context) {

        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        String search = SearchHistory.getInstance().getSearchHistoryList().get(position);
        holder.searchName.setText(search);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(search);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return SearchHistory.getInstance().getSearchHistoryList().size();
    }
}
