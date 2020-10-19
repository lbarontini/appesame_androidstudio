package com.example.appesame.dbutilities;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.appesame.entities.EntityCmap;
import com.example.appesame.entities.EntityExam;
import com.example.appesame.entities.EntityExercise;
import com.example.appesame.entities.EntityFlashcard;
import com.example.appesame.entities.EntityRecording;

import java.util.List;

public class ExamRepository {

    private ExamDao examDao;

    ExamRepository(Application application) {
        ExamDatabase db = ExamDatabase.getDatabase(application);
        examDao = db.examDao();
    }

    //Get
    LiveData<List<EntityExam>> getExams() {
        return examDao.getExams();
    }
    LiveData<List<EntityFlashcard>> getFlashcards(String examName) {
        return examDao.getFlashcards(examName);
    }
    LiveData<List<EntityRecording>> getRecordings(String examName) {
        return examDao.getRecordings(examName);
    }
    LiveData<List<EntityCmap>> getCmaps(String examName) {
        return examDao.getCmaps(examName);
    }
    LiveData<List<EntityExercise>> getExercise(String examName) {
        return examDao.getExercises(examName);
    }

    //Insert
    void insertExam(final EntityExam entityExam) {
        ExamDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {examDao.insertExam(entityExam);}});
    }
    void insertFlashcard (final EntityFlashcard entityFlashcard) {
        ExamDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {examDao.insertFlashcard(entityFlashcard);}});
    }
    void insertRecording (final EntityRecording entityRecording) {
        ExamDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {examDao.insertRecording(entityRecording);}});
    }
    void insertCmap (final EntityCmap entityCmap) {
        ExamDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {examDao.insertCmap(entityCmap);}});
    }
    void insertExercise (final EntityExercise entityExercise) {
        ExamDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {examDao.insertExercise(entityExercise);}});
    }


    //Delete
    void deleteExam(final String examName){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.deleteExam(examName);
            }
        });
    }
    void deleteFlashcard(final String examName, final String title){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.deleteFlashcard(examName,title);
            }
        });
    }
    void deleteRecording(final String examName, final String title){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.deleteRecording(examName,title);
            }
        });
    }
    void deleteCmap(final String examName, final String title){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.deleteCmap(examName,title);
            }
        });
    }
    void deleteExercise(final String examName, final String title){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.deleteExercise(examName,title);
            }
        });
    }

    //Update
    void updateFlashcard(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.updateFlashcard(examName,uri,setTitle,setMemorized);
            }
        });
    }
    void updateCmap(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.updateCmap(examName,uri,setTitle,setMemorized);
            }
        });
    }

    void updateRecording(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.updateRecording(examName,uri,setTitle,setMemorized);
            }
        });
    }
    void updateExercise(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        ExamDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run() {
                examDao.updateExercise(examName,uri,setTitle,setMemorized);
            }
        });
    }

}
