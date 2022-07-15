package com.mycityhome.InquilinOs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Event> mData;
    private final LayoutInflater inflater;
    final onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClick(Event item);
    }

    public ListAdapter(List<Event> itemList, Context context, onItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.mData = itemList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.layout_event, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Event> items) {
        mData = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dataStart, dataEnd, name, place;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            place = itemView.findViewById(R.id.place);
            dataStart = itemView.findViewById(R.id.dataStart);
            dataEnd = itemView.findViewById(R.id.dataEnd);
        }

        void bindData(final Event item) {
            name.setText(item.getName());
            dataStart.setText(String.format("Del: %s", item.getStDate()));
            dataEnd.setText(String.format("Al: %s", item.getEndDate()));
            place.setText(item.getStreetAddress());
        }
    }
}
