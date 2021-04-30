package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appesame.entities.StudiedExam;
import com.example.appesame.uiutilities.AdapterExams;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
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
                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    StudiedExam newExam = dc.getDocument().toObject(StudiedExam.class);
                                    int i = adapterExams.getDataList().indexOf(newExam);
                                    switch (dc.getType()) {
                                        case ADDED:
                                            if (i == -1) {
                                                adapterExams.getDataList().add(newExam);
                                                adapterExams.notifyItemInserted(adapterExams.getItemCount() - 1);
                                            } else
                                                adapterExams.notifyDataSetChanged();
                                            break;
                                        case MODIFIED:
                                            adapterExams.getDataList().set(i, newExam);
                                            adapterExams.notifyItemChanged(i);
                                            break;
                                        case REMOVED:
                                            adapterExams.getDataList().remove(newExam);
                                            adapterExams.notifyItemRemoved(i);
                                            break;
                                    }
                                    if (adapterExams.getItemCount() == 0) {
                                        imageView.setVisibility(View.VISIBLE);
                                        recyclerViewExams.setVisibility(View.INVISIBLE);
                                    } else {
                                        imageView.setVisibility(View.INVISIBLE);
                                        recyclerViewExams.setVisibility(View.VISIBLE);
                                    }
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
        topAppBar = findViewById(R.id.topAppBar);
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
        findViewById(R.id.add_button_exam)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            AddExamDialog examDialog = new AddExamDialog();
                            examDialog.show(getSupportFragmentManager(), "exam_dialog");
                        }else {
                            alertLogin();
                        }
                    }
                });


        final Drawable deleteIcon = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.deletebin);
        final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorPrimarylight,null));

        //handling recyclerview swipes
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 40; //so background is behind the rounded corners of itemView
                int backgroundHeightOffset = 30;
                int backgroundRightOffset = 30;

                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop()+ backgroundHeightOffset,
                            itemView.getRight()+backgroundRightOffset,
                            itemView.getBottom()- backgroundHeightOffset);
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                deleteIcon.draw(c);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (isOnline(getApplication())) {
                    final String examId= adapterExams.get(viewHolder.getLayoutPosition()).getExamId();
                    final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    AlertDialog.Builder alert = new MaterialAlertDialogBuilder(ExamChooserActivity.this);
                    alert.setTitle(R.string.dialog_cancel_title);
                    alert.setMessage(R.string.dialog_cancel_message);
                    alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Log.w("deleting",examId);
                            DocumentReference exam =db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examId);
                            deleteFirestoreCollection(exam.collection("Flashcards"));
                            deleteFirestoreCollection(exam.collection("Recordings"));
                            deleteFirestoreCollection(exam.collection("Cmaps"));
                            deleteFirestoreCollection(exam.collection("Exercises"));

                            exam.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.w(" firestore exam delete ", examId);
                                }
                            });

                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examId+"/Flashcards"));
                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examId+"/Recordings"));
                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examId+"/Cmaps"));
                            deleteStorageBuket(storageRef.child(user.getUid() +"/"+examId+"/Exercises"));
                        }
                    });
                    alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }else {
                    AlertDialog.Builder alert = new MaterialAlertDialogBuilder(ExamChooserActivity.this);
                    alert.setTitle(R.string.connection_title)
                            .setMessage(R.string.connection_message)
                            .show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewExams);

        //handling recyclerview cliks
        adapterExams.setOnItemClickListener(new AdapterExams.OnItemClickListener() {
            @Override
            //handling ExamSelect click
            public void OnExamSelected(int position) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intentExamName = new Intent(ExamChooserActivity.this, MainActivity.class);
                    intentExamName.putExtra("exam_name", adapterExams.get(position).getExamName());
                    intentExamName.putExtra("exam_id", adapterExams.get(position).getExamId());
                    intentExamName.putExtra("exam_date",adapterExams.get(position).getDate().toDate().getTime());
                    intentExamName.putExtra("exam_cfu", adapterExams.get(position).getCfu());
                    startActivity(intentExamName);
                }
            }

            @Override
            public void OnDateClick(int position) {
                final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if (user==null)
                    alertLogin();
                else {
                    final String examId = adapterExams.get(position).getExamId();
                    final Dialog calendarDialog = new Dialog(ExamChooserActivity.this);
                    final Calendar calendar= Calendar.getInstance();
                    calendarDialog.setContentView(R.layout.dialog_exam_date);
                    CalendarView calendarView =  calendarDialog.findViewById(R.id.calendarView);
                    calendarView.setMinDate(calendar.getTimeInMillis());
                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DATE, dayOfMonth);
                            Timestamp timestamp = new Timestamp(calendar.getTime());

                            DocumentReference exam =db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examId);
                            exam.update("date",timestamp);
                            calendarDialog.dismiss();
                        }
                    });
                    Button buttonCancel= calendarDialog.findViewById(R.id.date_cancel);
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            calendarDialog.dismiss();
                        }
                    });
                    calendarDialog.show();
                }
            }

            @Override
            public void OnCfuClick(int position) {
                final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if (user==null)
                    alertLogin();
                else {
                    final String examId = adapterExams.get(position).getExamId();
                    final Dialog cfuDialog = new Dialog(ExamChooserActivity.this);
                    cfuDialog.setContentView(R.layout.dialog_exam_cfu);
                    final NumberPicker numberPicker = cfuDialog.findViewById(R.id.cfu_numpic);
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(25);
                    numberPicker.setValue(adapterExams.get(position).getCfu());

                    Button okButton = cfuDialog.findViewById(R.id.name_ok);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference exam =db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examId);
                            exam.update("cfu",numberPicker.getValue());
                            cfuDialog.dismiss();
                        }
                    });
                    Button cancelButton = cfuDialog.findViewById(R.id.name_cancel);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cfuDialog.dismiss();
                        }
                    });
                    cfuDialog.show();
                }
            }

            @Override
            public void OnNameClick(int position) {
                final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if (user==null)
                    alertLogin();
                else {
                    final String examID = adapterExams.get(position).getExamId();
                    final String examName = adapterExams.get(position).getExamName();
                    final Dialog nameDialog = new Dialog(ExamChooserActivity.this);
                    nameDialog.setContentView(R.layout.dialog_name_update);
                    final EditText editText =  nameDialog.findViewById(R.id.dialog_name_editText);
                    editText.setText(examName);
                    final TextInputLayout textInputLayout =  nameDialog.findViewById(R.id.dialog_name_input_layout);
                    Button okButton = nameDialog.findViewById(R.id.name_ok);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String examNameNew = editText.getText()+"".toUpperCase();
                            if (examNameNew.trim().equals("")) {
                                textInputLayout.setError(getResources().getString(R.string.empty_name_field));
                                textInputLayout.requestFocus();
                            }else if(examNameNew.length()>15) {
                                textInputLayout.setError(getResources().getString(R.string.overflow_name_field));
                                textInputLayout.requestFocus();
                            }else if (IsSameName(examNameNew)){
                                textInputLayout.setError(getString(R.string.used_name));
                                textInputLayout.requestFocus();
                            } else{
                                final CollectionReference exams = db.collection("Users").document(user.getUid())
                                        .collection("Exams");
                                exams.document(examID).
                                        update("examName", examNameNew);
                                nameDialog.dismiss();
                            }
                        }
                    });
                    Button cancelButton = nameDialog.findViewById(R.id.name_cancel);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nameDialog.dismiss();
                        }
                    });
                    nameDialog.show();
                }
            }
        });
    }

    private boolean IsSameName(String examNameNew) {
        for (int j=0; j < adapterExams.getItemCount(); j++){
            if (adapterExams.get(j).getExamName().equals(examNameNew))
                return true;
        }
        return false;
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
        AlertDialog.Builder alert = new MaterialAlertDialogBuilder(ExamChooserActivity.this);
        alert.setTitle(R.string.login_title);
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

