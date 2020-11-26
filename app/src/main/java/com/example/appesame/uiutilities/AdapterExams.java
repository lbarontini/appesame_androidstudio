package com.example.appesame.uiutilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appesame.R;
import com.example.appesame.entities.StudiedExam;

import java.util.List;

public class AdapterExams extends RecyclerView.Adapter<AdapterExams.ExamViewHolder> {

    private List<StudiedExam> studiedExamList;
    private AdapterExams.OnItemClickListener mlistener;
    private LayoutInflater layoutInflater;

    public AdapterExams (Context context) { layoutInflater = LayoutInflater.from(context);}

    static class ExamViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageButton delBtn;
        ConstraintLayout RowLayout;

        ExamViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            textView= itemView.findViewById(R.id.exam_name_tv);
            delBtn= itemView.findViewById(R.id.exam_delete);
            RowLayout= itemView.findViewById(R.id.exam_row_layout);

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

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.rv_exam,parent,false);
        return new ExamViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        holder.textView.setText(studiedExamList.get(position).getExamName());
    }

    @Override
    public int getItemCount() {
        if (studiedExamList!=null)
            return studiedExamList.size();
        else return 0;

    }


    public void setDataList(List<StudiedExam> dataList) {
        this.studiedExamList = dataList;
        notifyDataSetChanged();
    }

    public StudiedExam get(int position) {
        return studiedExamList.get(position);
    }

    public interface OnItemClickListener {
        void OnDeleteClick(int position);
        void OnRowClick(int position);
    }
    public void setOnItemClickListener (OnItemClickListener listener){
        this.mlistener=listener;
    }
}
