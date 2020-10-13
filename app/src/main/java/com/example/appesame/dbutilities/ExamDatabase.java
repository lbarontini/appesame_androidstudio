package com.example.appesame.dbutilities;

import android.content.Context;
import com.example.appesame.entities.EntityCmap;
import com.example.appesame.entities.EntityExam;
import com.example.appesame.entities.EntityExercise;
import com.example.appesame.entities.EntityFlashcard;
import com.example.appesame.entities.EntityRecording;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {EntityExam.class,
                        EntityFlashcard.class,
                        EntityRecording.class,
                        EntityCmap.class,
                        EntityExercise.class},
            version = 1,exportSchema = false)

public abstract class ExamDatabase extends RoomDatabase {


    public abstract ExamDao examDao();
    private static volatile ExamDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ExamDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ExamDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ExamDatabase.class, "exam_DB")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

