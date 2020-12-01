package com.example.appesame.uiutilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appesame.R;
import com.example.appesame.entities.StudiedExam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdapterExams extends RecyclerView.Adapter<AdapterExams.ExamViewHolder> {

    private List<StudiedExam> studiedExamList;
    private AdapterExams.OnItemClickListener mlistener;
    private LayoutInflater layoutInflater;

    public AdapterExams (Context context) { layoutInflater = LayoutInflater.from(context);}

    static class ExamViewHolder extends RecyclerView.ViewHolder {

        TextView examNameTV, examDateTV;
        Button CfuButton;
        CardView RowLayout;

        ExamViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            examNameTV = itemView.findViewById(R.id.exam_name_tv);
            examNameTV.setOnClickListener(new View.OnClickListener() {
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


            CfuButton= itemView.findViewById(R.id.exam_cfu_button);
            CfuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnCfuClick(position);
                        }
                    }
                }
            });

            examDateTV = itemView.findViewById(R.id.exam_date_tv);
            examDateTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnDateClick(position);
                        }
                    }
                }
            });

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
        }
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.exam_row_layout,parent,false);
        return new ExamViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        holder.examNameTV.setText(studiedExamList.get(position).getExamName());

        DateFormat df = new SimpleDateFormat("dd/MMM/yy", Locale.ITALY);
        String fdate = df.format(studiedExamList.get(position).getDate().toDate());
        holder.examDateTV.setText(fdate);

        String cfuString =studiedExamList.get(position).getCfu()+"";
        holder.CfuButton.setText(cfuString);

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
        void OnRowClick(int position);
        void OnDateClick(int position);
        void OnCfuClick(int position);
        void OnNameClick(int position);

    }
    public void setOnItemClickListener (OnItemClickListener listener){
        this.mlistener=listener;
    }

}
