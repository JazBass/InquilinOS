package com.mycityhome.InquilinOs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Event> mData;
    private LayoutInflater inflater;
    private Context context;
    final onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClick(Event item);
    }

    public ListAdapter(List<Event> itemList, Context context, onItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView color, name, estadio;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            estadio = itemView.findViewById(R.id.estadio);
            color = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image);
        }

        void bindData(final Event item) {

        }
    }
}
