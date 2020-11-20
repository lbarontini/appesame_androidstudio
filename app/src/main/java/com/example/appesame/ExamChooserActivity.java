package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appesame.dbutilities.ExamViewModel;
import com.example.appesame.entities.EntityExam;
import com.example.appesame.entities.StudiedExam;
import com.example.appesame.uiutilities.AdapterExams;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
// first activity on startup
public class ExamChooserActivity extends AppCompatActivity {

    private RecyclerView recyclerViewExams;
    private AdapterExams adapterExams;
    private ImageButton addbtn;
    private ImageView imageView;

    FirebaseUser user;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MaterialToolbar topAppBar;
    //private ExamViewModel examViewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_login:
//                Toast.makeText(this, "dfgjhgfj", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_chooser);

        imageView = findViewById(R.id.empty_recycler_image);
        recyclerViewExams = findViewById(R.id.recicler_view_exams);
        recyclerViewExams.setLayoutManager(new LinearLayoutManager(this));
        adapterExams = new AdapterExams(this);
        recyclerViewExams.setAdapter(adapterExams);

        //top app bar show and button click
        topAppBar= (MaterialToolbar)findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 if (item.getItemId()==R.id.action_login) {
                     Intent intent= new Intent(ExamChooserActivity.this, LoginActivity.class);
                     startActivity(intent);
                     return true;
                 }
                 return false;
             }
         });

        //handling add button click
        addbtn = findViewById(R.id.add_button_exam);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ExamChooserActivity.this);
                final EditText edittext = new EditText(ExamChooserActivity.this);
                alert.setTitle(R.string.dialog_exam_title);
                alert.setView(edittext);
                alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String examName = edittext.getText()+"";
                        examName= examName.toUpperCase();
                        if (examName.equals("")) {
                            Toast.makeText(getApplicationContext(), R.string.empty_name_field, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //examViewModel.insertExam(new EntityExam(edittext.getText() + ""));
                            // Add a new exam
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                // User is signed in
                            db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examName)
                                    .set(new StudiedExam(examName), SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "success ", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "insuccess", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            } else {
                                // No user is signed in
                            }
                        }
                    }
                });
                alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        adapterExams.setOnItemClickListener(new AdapterExams.OnItemClickListener() {
            //handling delete button click
            @Override
            public void OnDeleteClick(final int position) {

                AlertDialog.Builder alert = new AlertDialog.Builder(ExamChooserActivity.this);
                alert.setTitle(R.string.dialog_cancel_title);
                alert.setMessage(R.string.dialog_cancel_message);
                alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //String examnametemp=adapterExams.get(position).getName()+"";
                        String examnametemp=adapterExams.get(position).getExamName()+"";
                        Log.v("exam_delete", examnametemp);
                        //examViewModel.deleteExam(adapterExams.get(position).getName()+"");
                        if (user != null) {
                            db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examnametemp)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                        Log.d(getApplicationContext().toString(), "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(getApplicationContext().toString(), "Error deleting document", e);
                                }
                            });
                        }
                    }
                });

                alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
            //handling row click
            @Override
            public void OnRowClick(int position) {
                Intent intentExamName = new Intent(ExamChooserActivity.this, MainActivity.class);
                //intentExamName.putExtra("exam_name", adapterExams.get(position).getName());
                intentExamName.putExtra("exam_name", adapterExams.get(position).getExamName());
                startActivity(intentExamName);
            }
        });

//        examViewModel = new ViewModelProvider(this).get(ExamViewModel.class);
//        examViewModel.getExams().observe(this, new Observer<List<EntityExam>>() {
//            @Override
//            public void onChanged(@Nullable final List<EntityExam> entityExamList) {
//                if (entityExamList != null) {
//                        adapterExams.setDataList(entityExamList);
//                        if (adapterExams.getItemCount()==0){
//                            imageView.setVisibility(View.VISIBLE);
//                            recyclerViewExams.setVisibility(View.INVISIBLE);
//                        }
//                        else {
//                            imageView.setVisibility(View.INVISIBLE);
//                            recyclerViewExams.setVisibility(View.VISIBLE);
//                        }
//                }
//                else
//                    Log.v("observer Exams", "null");
//            }
//        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            db.collection("Users").document(user.getUid())
                    .collection("Exams")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(this.getClass().toString(), "Listen failed.", e);
                                return;
                            }
                            List<StudiedExam> examList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                examList.add(doc.toObject(StudiedExam.class));
                            }
                            adapterExams.setDataListS(examList);
                            if (adapterExams.getItemCount()==0){
                                imageView.setVisibility(View.VISIBLE);
                                recyclerViewExams.setVisibility(View.INVISIBLE);
                            }
                            else {
                                imageView.setVisibility(View.INVISIBLE);
                                recyclerViewExams.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            // No user is signed in show alert
            AlertDialog.Builder alert = new AlertDialog.Builder(ExamChooserActivity.this);
            alert.setTitle("Login needed");
            alert.setMessage("you need tobe logged in in order to use this app");
            alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }
}

