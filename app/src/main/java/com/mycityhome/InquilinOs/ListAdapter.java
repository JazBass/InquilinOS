package com.mycityhome.InquilinOs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>  {
    private List<Event> mData;
    private final LayoutInflater inflater;
    final ListAdapter.OnItemClickListener listener;
    final ListAdapter.OnItemClickListener listener2;

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public ListAdapter(List<Event> itemList, Context context, ListAdapter.OnItemClickListener listener,
                       ListAdapter.OnItemClickListener listener2) {
        this.inflater = LayoutInflater.from(context);
        this.mData = itemList;
        this.listener = listener;
        this.listener2 = listener2;
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

    /*
    public void setItems(List<Event> items) {
        mData = items;
    }
     */

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dataStart, dataEnd, name, place;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            place = itemView.findViewById(R.id.place);
            dataStart = itemView.findViewById(R.id.dataStart);
            dataEnd = itemView.findViewById(R.id.dataEnd);
        }

        void bindData(final Event event) {
            name.setText(event.getName());
            dataStart.setText(String.format("Del: %s", event.getStDate()));
            dataEnd.setText(String.format("Al: %s", event.getEndDate()));
            place.setText(event.getStreetAddress());
            itemView.findViewById(R.id.btnSeeMap).setOnClickListener( v -> listener.onItemClick(event));
            itemView.findViewById(R.id.btnWebSite).setOnClickListener(v -> listener2.onItemClick(event));
        }
    }
}
