package com.example.appesame.uiutilities;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.Calendar;
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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterExams extends RecyclerView.Adapter<AdapterExams.ExamViewHolder> {

    private List<StudiedExam> studiedExamList;
    private AdapterExams.OnItemClickListener mlistener;
    private LayoutInflater layoutInflater;
    Context context;

    public AdapterExams (Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        studiedExamList= new ArrayList<>();
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.exam_row_layout,parent,false);
        return new ExamViewHolder(view, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        StudiedExam exam = studiedExamList.get(position);
        holder.examNameTV.setText(exam.getExamName());
        holder.cfuButton.setText(exam.getCfu() + "");

        DateFormat df = new SimpleDateFormat("dd/MMM/yy", Locale.ITALY);
        String fdate = df.format(exam.getDate().toDate());
        holder.examDateTV.setText(fdate);

        DateTime examDT = new DateTime(exam.getDate().toDate());
        Days result = Days.daysBetween(DateTime.now(), examDT);
        if (result.getDays() >= 0 && result.getDays() <= 2) {
            holder.examDateTV.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else if (result.getDays() >= 15) {
            holder.examDateTV.setTextColor(context.getResources().getColor(R.color.colorTextPrimary));
        } else if (result.getDays() > 2) {
            holder.examDateTV.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else if (examDT.isBeforeNow()) {
            holder.examDateTV.setTextColor(context.getResources().getColor(R.color.colorTextSecondary));
        }
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
    public List<StudiedExam> getDataList() {
        return studiedExamList;
    }

    public StudiedExam get(int position) {
        return studiedExamList.get(position);
    }

    public interface OnItemClickListener {
        void OnExamSelected(int position);
        void OnDateClick(int position);
        void OnCfuClick(int position);
        void OnNameClick(int position);

    }
    public void setOnItemClickListener (OnItemClickListener listener){
        this.mlistener=listener;
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        TextView examNameTV, examDateTV;
        Button cfuButton;
        ImageButton selectButton;

        ExamViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            card = itemView.findViewById(R.id.exam_row_layout);

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


            cfuButton = itemView.findViewById(R.id.exam_cfu_button);
            cfuButton.setOnClickListener(new View.OnClickListener() {
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

            selectButton =itemView.findViewById(R.id.select_arrow);
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.OnExamSelected(position);
                        }
                    }
                }
            });
        }
    }

}
