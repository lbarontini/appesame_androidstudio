package com.example.appesame.entities;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudiedExam {
    @PropertyName("examName")
    public String  examName;
    @PropertyName("examId")
    public String examId;
    @PropertyName("date")
    public Timestamp date;
    @PropertyName("cfu")
    public int cfu;

    public StudiedExam(){}

    public StudiedExam(String examId, String examname, Timestamp date, int cfu){
        this.examId= examId;
        this.examName=examname;
        this.date=date;
        this.cfu=cfu;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getExamName() {
        return examName;
    }

    public int getCfu() {
        return this.cfu;
    }

    public String getExamId() {
        return examId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof StudiedExam) {
            if (((StudiedExam) obj).examId.equals(this.examId))
                return true;
        }
        return false;
    }
}
