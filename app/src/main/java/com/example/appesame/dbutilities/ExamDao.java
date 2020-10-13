package com.example.appesame.dbutilities;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.appesame.entities.EntityCmap;
import com.example.appesame.entities.EntityExam;
import com.example.appesame.entities.EntityExercise;
import com.example.appesame.entities.EntityFlashcard;
import com.example.appesame.entities.EntityRecording;

import java.util.List;

@androidx.room.Dao
public interface ExamDao {

    //exam
    @Query("SELECT * from exams_table")
    LiveData<List<EntityExam>> getExams();

    @Insert(entity = EntityExam.class, onConflict = OnConflictStrategy.IGNORE)
    void insertExam(EntityExam entityExam);

    @Query("DELETE FROM exams_table WHERE exams_table.name like :examName")
    void deleteExam(String examName);

    //flashcards
    @Query("SELECT * from flashcard_table WHERE flashcard_table.exam_name like :examName")
    LiveData<List<EntityFlashcard>> getFlashcards(String examName);

    @Insert(entity = EntityFlashcard.class, onConflict = OnConflictStrategy.IGNORE)
    void insertFlashcard(EntityFlashcard entityFlashcard);

    @Query("DELETE FROM flashcard_table WHERE flashcard_table.exam_name like :examName AND flashcard_table.title like :title")
    void deleteFlashcard(String examName, String title);

    @Query("UPDATE flashcard_table SET title = :setTitle, isMemorized = :setMemorized WHERE exam_name like :examName AND uri like :uri")
    void updateFlashcard(String examName, String uri, String setTitle, boolean setMemorized);

    //recording
    @Query("SELECT * from recording_table WHERE recording_table.exam_name like :examName")
    LiveData<List<EntityRecording>> getRecordings(String examName);

    @Insert(entity = EntityRecording.class, onConflict = OnConflictStrategy.IGNORE)
    void insertRecording(EntityRecording entityRecording);

    @Query("DELETE FROM recording_table WHERE recording_table.exam_name like :examName AND recording_table.title like :title")
    void deleteRecording(String examName,String title);

    @Query("UPDATE recording_table SET title = :setTitle, isMemorized = :setMemorized WHERE exam_name like :examName AND uri like :uri")
    void updateRecording(String examName, String uri, String setTitle, boolean setMemorized);

    //cmaps
    @Query("SELECT * from cmap_table WHERE cmap_table.exam_name like :examName")
    LiveData<List<EntityCmap>> getCmaps(String examName);

    @Insert(entity = EntityCmap.class, onConflict = OnConflictStrategy.IGNORE)
    void insertCmap(EntityCmap entityCmap);

    @Query("DELETE FROM cmap_table WHERE cmap_table.exam_name like :examName AND cmap_table.title like :title")
    void deleteCmap(String examName,String title);

    @Query("UPDATE cmap_table SET title = :setTitle, isMemorized = :setMemorized WHERE exam_name like :examName AND uri like :uri")
    void updateCmap(String examName, String uri, String setTitle, boolean setMemorized);


    //exercises
    @Query("SELECT * from exercise_table WHERE exercise_table.exam_name like :examName")
    LiveData<List<EntityExercise>> getExercises(String examName);

    @Insert(entity = EntityExercise.class, onConflict = OnConflictStrategy.IGNORE)
    void insertExercise(EntityExercise entityExercise);

    @Query("DELETE FROM exercise_table WHERE exercise_table.exam_name like :examName AND exercise_table.title like :title")
    void deleteExercise(String examName,String title);

    @Query("UPDATE exercise_table SET title = :setTitle, isMemorized = :setMemorized WHERE exam_name like :examName AND uri like :uri")
    void updateExercise(String examName, String uri, String setTitle, boolean setMemorized);
}
