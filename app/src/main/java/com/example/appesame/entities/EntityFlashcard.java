package com.example.appesame.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;


@Entity(tableName = "flashcard_table",
        primaryKeys = {"exam_name","uri"},
        foreignKeys = @ForeignKey(entity = EntityExam.class,
            parentColumns = "name",
            childColumns = "exam_name",
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE))

public class EntityFlashcard {

    @NonNull
    @ColumnInfo(name = "exam_name")
    private String examName;

    @ColumnInfo(name = "type")
    private String type;

    @NonNull
    @ColumnInfo(name = "uri")
    private String uri;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "isMemorized")
    private boolean isMemorized;

    public EntityFlashcard(String examName,String type, String uri, String title) {
        this.examName= examName;
        this.uri = uri;
        this.title = title;
        this.isMemorized = false;
        this.type= type;
    }

    public String getTitle() {
        return title;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMemorized() {
        return isMemorized;
    }

    public void setMemorized(boolean memorized) {
        isMemorized = memorized;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

