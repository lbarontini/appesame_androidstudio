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

    //private LiveData<List<EntityExam>> listExam;
    //private LiveData<List<EntityFlashcard>> listFlashcard;
    private LiveData<List<EntityRecording>> listRecording;
    private LiveData<List<EntityCmap>> listCmap;
    private LiveData<List<EntityExercise>> listExercise;

    ExamRepository(Application application) {
        ExamDatabase db = ExamDatabase.getDatabase(application);
        examDao = db.examDao();
    }

    //Get
    LiveData<List<EntityExam>> getExams() {
        //listExam = examDao.getExams();
        return examDao.getExams();
    }
    LiveData<List<EntityFlashcard>> getFlashcards(String examName) {
        return examDao.getFlashcards(examName);
    }
    LiveData<List<EntityRecording>> getRecordings(String examName) {
        listRecording = examDao.getRecordings(examName);
        return listRecording;
    }
    LiveData<List<EntityCmap>> getCmaps(String examName) {
        listCmap = examDao.getCmaps(examName);
        return listCmap;
    }
    LiveData<List<EntityExercise>> getExercise(String examName) {
        listExercise = examDao.getExercises(examName);
        return listExercise;
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
