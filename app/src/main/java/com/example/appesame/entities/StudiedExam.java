package com.example.appesame.entities;

import java.util.ArrayList;
import java.util.List;

public class StudiedExam {
    private String  examName;

    public StudiedExam(){}
    public StudiedExam(String examname){
        this.examName=examname;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }
}
