package com.example.appesame;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.appesame.entities.StudiedExam;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.N)
public class AddExamDialog extends DialogFragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;

    private Date date;
    private int cfu=0;
    private String examName="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.dialog_exam,container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        user = FirebaseAuth.getInstance().getCurrentUser();

        //handling date
        final Calendar calendar = Calendar.getInstance(Locale.ITALY);
        final MaterialButton buttondate =  view.findViewById(R.id.date_button);
        buttondate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog calendarDialog = new Dialog(getContext());
                calendarDialog.setTitle("please choose a date");
                calendarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                calendarDialog.setContentView(R.layout.dialog_exam_date);
                CalendarView calendarView =  calendarDialog.findViewById(R.id.calendarView);
                calendarView.setMinDate(calendar.getTimeInMillis());
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DATE, dayOfMonth);
                        date = calendar.getTime();
                        DateFormat format = new SimpleDateFormat("dd/MM/yy");
                        buttondate. setText(format.format(date));
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
        });


        //handling cfu
        NumberPicker cfuNumPic = view.findViewById(R.id.cfu_numpic);
        cfuNumPic.setMinValue(0);
        cfuNumPic.setMaxValue(25);
        cfuNumPic.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                AddExamDialog.this.cfu=newVal;
            }
        });

        //handling ok click
        final TextInputLayout textInputLayout = view.findViewById(R.id.dialog_trec_input_layout);
        view.findViewById(R.id.dialog_ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 CollectionReference examsCollection=db.collection("Users").document(user.getUid())
                        .collection("Exams");
                final EditText nameEditText = view.findViewById(R.id.dialog_trec_editText);
                examName = nameEditText.getText()+"";
                examName = examName.toUpperCase();
                if (user ==null){
                    Toast.makeText(getContext(), R.string.alert_login, Toast.LENGTH_SHORT).show();
                } else if (examName.trim().equals("")) {
                    textInputLayout.setError(getResources().getString(R.string.empty_name_field));
                    textInputLayout.requestFocus();
                }else if(examName.length()>10) {
                    textInputLayout.setError(getResources().getString(R.string.overflow_name_field));
                    textInputLayout.requestFocus();
                } else if (date==null) {
                    Toast.makeText(getContext(), R.string.please_select_a_date, Toast.LENGTH_SHORT).show();
                }else if (IsSameName(examName)){
                    textInputLayout.setError(getResources().getString(R.string.used_name));
                    textInputLayout.requestFocus();
                }
                else{
                    Timestamp ts= new Timestamp(date);
                    String uniqueID = UUID.randomUUID().toString();
                    examsCollection.document(uniqueID)
                            .set(new StudiedExam(uniqueID,examName,ts,AddExamDialog.this.cfu),SetOptions.merge());
                    dismiss();
                }
            }
        });
        //handling cancel click
        view.findViewById(R.id.dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    boolean IsSameName( String examNewName)
    {
        final ArrayList<StudiedExam> list = new ArrayList<StudiedExam>();
        db.collection("Users").document(user.getUid())
                .collection("Exams")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.toObject(StudiedExam.class));
                            }
                        }
                    }
                });
        for (StudiedExam exam :list) {
            if (exam.examName.equals(examNewName))
                return true;
        }
        return false;
    }
}
