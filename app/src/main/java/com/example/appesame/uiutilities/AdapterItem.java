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

import java.util.List;


public class AdapterItem extends RecyclerView.Adapter<AdapterItem.CViewHolder> {


    private List<StudiedItem> dataList;
    private OnItemClickListener mlistener;

    public AdapterItem(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        return new CViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
        holder.textView.setText(dataList.get(position).getItemName());
        holder.checkBox.setChecked(dataList.get(position).isMemorized());
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

    public StudiedItem get(int position) {
        return dataList.get(position);
    }

    public interface OnItemClickListener {
        void OnCheckClick(int position);
        void OnDeleteClick(int position);
        void OnRowClick(int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener){
        this.mlistener=listener;
    }

    public static class CViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        CheckBox checkBox;
        ImageButton delBtn;
        ConstraintLayout RowLayout;

        public CViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            checkBox = itemView.findViewById(R.id.checkBox);
            delBtn = itemView.findViewById(R.id.delete_btn);
            RowLayout = itemView.findViewById(R.id.row_layout);

            RowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnRowClick(position);
                        }
                    }
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
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

            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnDeleteClick(position);
                        }
                    }
                }
            });
        }

    }
}
