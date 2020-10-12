package com.example.appesame.uiutilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appesame.R;
import com.example.appesame.entities.EntityFlashcard;

import java.util.List;

import static androidx.core.content.res.ResourcesCompat.getColor;


public class AdapterFlashcards extends RecyclerView.Adapter<AdapterFlashcards.CViewHolder> {

    private List<EntityFlashcard> dataList;
    private OnItemClickListener mlistener;
    private LayoutInflater layoutInflater;

    public AdapterFlashcards(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_flashcard,parent,false);
        return new CViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, int position) {
        holder.textView.setText(dataList.get(position).getTitle());
        holder.checkBox.setChecked(dataList.get(position).isMemorized());
    }

    @Override
    public int getItemCount() {
        if (dataList!=null)
        return dataList.size();
        else return 0;
    }

    public void setDataList(List<EntityFlashcard> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public EntityFlashcard get(int position) {
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

    static class CViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        CheckBox checkBox;
        ImageButton delBtn;
        ConstraintLayout RowLayout;

        public CViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_f);
            checkBox = itemView.findViewById(R.id.checkBox_f);
            delBtn = itemView.findViewById(R.id.delete_btn_f);
            RowLayout = itemView.findViewById(R.id.flashcard_row_layout);

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
