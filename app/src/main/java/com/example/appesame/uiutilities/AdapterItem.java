package com.example.appesame.uiutilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appesame.R;
import com.example.appesame.entities.StudiedItem;

import java.util.ArrayList;
import java.util.List;


public class AdapterItem extends RecyclerView.Adapter<AdapterItem.CViewHolder> {


    private List<StudiedItem> dataList;
    private OnItemClickListener mlistener;
    public ImageButton selectItem;

    public AdapterItem(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        dataList = new ArrayList<StudiedItem>();
    }

    @NonNull
    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_layout,parent,false);
        return new CViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
        holder.textView.setText(dataList.get(position).getItemName());
        holder.check.setChecked(dataList.get(position).isMemorized());
        if (dataList.get(position).isPlaying){
            holder.selectItem.setImageResource(R.drawable.ic_baseline_stop_24);
        }else{
            holder.selectItem.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
        }
        if (holder.check.isChecked()){
            holder.chekedText.setVisibility(View.VISIBLE);
        }else {
            holder.chekedText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (dataList!=null)
        return dataList.size();
        else return 0;
    }

    public void setDataList(List<StudiedItem> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<StudiedItem> getDataList() {
        return this.dataList;
    }

    public StudiedItem get(int position) {
        return dataList.get(position);
    }

    public interface OnItemClickListener {
        void OnCheckClick(int position);
        void OnSelectClick(int position);
        void OnNameClick(int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener){
        this.mlistener=listener;
    }

    public static class CViewHolder extends RecyclerView.ViewHolder{

        TextView textView, chekedText;
        CheckBox check;
        public ImageButton selectItem;


        public CViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            check = itemView.findViewById(R.id.check);
            chekedText = itemView.findViewById(R.id.checktext);
            selectItem = itemView.findViewById(R.id.select_arrow);

            selectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnSelectClick(position);
                        }
                    }
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnNameClick(position);
                        }
                    }
                }
            });

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnCheckClick(position);
                        }
                    }
                }
            });
        }

    }
}
