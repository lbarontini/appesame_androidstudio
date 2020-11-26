package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appesame.entities.StudiedExam;
import com.example.appesame.uiutilities.AdapterExams;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
// first activity on startup
public class ExamChooserActivity extends AppCompatActivity {

    private RecyclerView recyclerViewExams;
    private AdapterExams adapterExams;
    private ImageView imageView;
    MaterialToolbar topAppBar;

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        MenuItem item = menu.getItem(0);
        setProPic(menu.getItem(0));
        return true;
    }

    private boolean flag=false;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            MenuItem item = topAppBar.getMenu().getItem(0);
            setProPic(item);
            if (user != null) {
                // User is signed in
                flag=false;
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
                                if (adapterExams.getItemCount() == 0) {
                                    imageView.setVisibility(View.VISIBLE);
                                    recyclerViewExams.setVisibility(View.INVISIBLE);
                                } else {
                                    imageView.setVisibility(View.INVISIBLE);
                                    recyclerViewExams.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            } else if (!flag) {
                flag = true;
                alertLogin();
            }else {
                imageView.setVisibility(View.VISIBLE);
                recyclerViewExams.setVisibility(View.INVISIBLE);
            }
        }
    }

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
        topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.action_login) {
                    LoginDialog loginDialog = new LoginDialog();
                    loginDialog.show(getSupportFragmentManager(), "login_dialog");
                    return true;
                }
                return false;
            }
        });

        //handling add button click
        ImageButton addbtn = findViewById(R.id.add_button_exam);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ExamChooserActivity.this);
                    final EditText edittext = new EditText(ExamChooserActivity.this);
                    alert.setTitle(R.string.dialog_exam_title);
                    alert.setView(edittext);
                    alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String examName = edittext.getText() + "";
                            examName = examName.toUpperCase();
                            if (examName.equals("")) {
                                Toast.makeText(getApplicationContext(), R.string.empty_name_field, Toast.LENGTH_SHORT).show();
                            } else {
                                // Add a new exam
                                db.collection("Users").document(user.getUid())
                                        .collection("Exams").document(examName)
                                        .set(new StudiedExam(examName), SetOptions.merge());
                            }
                        }
                    });
                    alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }else{
                    alertLogin();
                }
            }
        });

        adapterExams.setOnItemClickListener(new AdapterExams.OnItemClickListener() {

            //handling delete button click
            @Override
            public void OnDeleteClick(int position) {
                if (isOnline(getApplication())) {
                    final String examName= adapterExams.get(position).getExamName();
                    final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    AlertDialog.Builder alert = new AlertDialog.Builder(ExamChooserActivity.this);
                    alert.setTitle(R.string.dialog_cancel_title);
                    alert.setMessage(R.string.dialog_cancel_message);
                    alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.w("deleting",examName);
                            DocumentReference exam =db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examName);
                            deleteFirestoreCollection(exam.collection("Flashcards"));
                            deleteFirestoreCollection(exam.collection("Recordings"));
                            deleteFirestoreCollection(exam.collection("Cmaps"));
                            deleteFirestoreCollection(exam.collection("Exercises"));

                            exam.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.w(" firestore exam delete ", examName);
                                }
                            });

                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examName+"/Flashcards"));
                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examName+"/Recordings"));
                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examName+"/Cmaps"));
                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examName+"/Exercises"));
                        }
                    });
                    alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "you need to be online",Toast.LENGTH_SHORT).show();
                }
            }
            //handling row click
            @Override
            public void OnRowClick(int position) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intentExamName = new Intent(ExamChooserActivity.this, MainActivity.class);
                    intentExamName.putExtra("exam_name", adapterExams.get(position).getExamName());
                    startActivity(intentExamName);
                }
            }
        });
    }

    private void setProPic(final MenuItem item) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .circleCrop()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            item.setIcon(new BitmapDrawable(getApplicationContext().getResources(), resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }else{
            item.setIcon(R.drawable.ic_account_circle_light);
        }
    }
    private void alertLogin() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ExamChooserActivity.this);
        alert.setTitle("Login needed");
        alert.setMessage(R.string.alert_login);
        alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.show(getSupportFragmentManager(), "login_dialog");
            }
        });
        alert.show();
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    void deleteFirestoreCollection(CollectionReference collection) {
        collection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.w("firestore delete: ",document.getId()+"");
                                document.getReference().delete();
                            }
                        } else {
                            Log.d( "deleting exception: ", task.getException()+"");
                        }
                    }
                });
    }

    void deleteStorageBuket(StorageReference storageRef){
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    // All the items under listRef.
                    Log.w("storage delete: ",item.getName());
                    item.delete();
                }
            }
        });
    }
}

