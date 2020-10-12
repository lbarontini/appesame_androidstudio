package com.example.appesame.dbutilities;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.appesame.entities.EntityCmap;
import com.example.appesame.entities.EntityExam;
import com.example.appesame.entities.EntityExercise;
import com.example.appesame.entities.EntityFlashcard;
import com.example.appesame.entities.EntityRecording;

import java.util.List;

public class ExamViewModel extends AndroidViewModel {
    private ExamRepository examRepository;

    private LiveData<List<EntityExam>> examslist;
    private LiveData<List<EntityFlashcard>> flashcardslist;
    private LiveData<List<EntityRecording>> recordingslist;
    private LiveData<List<EntityCmap>> cmapslist;
    private LiveData<List<EntityExercise>> exerciseslist;

    public ExamViewModel (Application application) {
        super(application);
        examRepository = new ExamRepository(application);
    }

    //get
    public LiveData<List<EntityExam>> getExams() {
        examslist = examRepository.getExams();
        return examslist;
    }
    public LiveData<List<EntityFlashcard>> getFlashcards(String examName) {
        flashcardslist= examRepository.getFlashcards(examName);
        return flashcardslist;
    }
    public LiveData<List<EntityRecording>> getRecordings(String examName) {
        recordingslist= examRepository.getRecordings(examName);
        return recordingslist;
    }
    public LiveData<List<EntityCmap>> getCmaps(String examName) {
        cmapslist= examRepository.getCmaps(examName);
        return cmapslist;
    }
    public LiveData<List<EntityExercise>> getExercises(String examName) {
        exerciseslist= examRepository.getExercise(examName);
        return exerciseslist;
    }

    //insert
    public void insertExam(EntityExam entityExam) {examRepository.insertExam(entityExam);}
    public void insertFlashcard(EntityFlashcard entityFlashcard) {examRepository.insertFlashcard(entityFlashcard);}
    public void insertRecording(EntityRecording entityRecording) {examRepository.insertRecording(entityRecording);}
    public void insertCmap(EntityCmap entityCmap) {examRepository.insertCmap(entityCmap);}
    public void insertExercise(EntityExercise entityExercise) {examRepository.insertExercise(entityExercise);}

    //delete
    public void deleteExam(String examName){examRepository.deleteExam(examName);}
    public void deleteFlashcard(String examName,String title){examRepository.deleteFlashcard(examName,title);}
    public void deleteRecording(String examName,String title){examRepository.deleteRecording(examName,title);}
    public void deleteCmap(String examName,String title){examRepository.deleteCmap(examName,title);}
    public void deleteExercise(String examName,String title){examRepository.deleteExercise(examName,title);}

    //update
    public void updateFlashcard(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        examRepository.updateFlashcard(examName,uri,setTitle,setMemorized);
    }
    public void updateCmap(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        examRepository.updateCmap(examName,uri,setTitle,setMemorized);
    }
    public void updateRecording(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        examRepository.updateRecording(examName,uri,setTitle,setMemorized);
    }
    public void updateExercise(final String examName, final String uri, final String setTitle, final boolean setMemorized){
        examRepository.updateExercise(examName,uri,setTitle,setMemorized);
    }

}
