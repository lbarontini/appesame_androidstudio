package com.example.appesame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appesame.dbutilities.ExamViewModel;
import com.example.appesame.entities.EntityExam;
import com.example.appesame.uiutilities.AdapterExams;

import java.util.List;

public class ExamChooserActivity extends AppCompatActivity {

    private RecyclerView recyclerViewExams;
    private AdapterExams adapterExams;
    private ImageButton addbtn;
    private ImageView imageView;

    private ExamViewModel examViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_chooser);

        imageView = findViewById(R.id.empty_recycler_image);
        recyclerViewExams = findViewById(R.id.recicler_view_exams);
        recyclerViewExams.setLayoutManager(new LinearLayoutManager(this));
        adapterExams = new AdapterExams(this);
        recyclerViewExams.setAdapter(adapterExams);

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
                        if (examName.equals("")) {
                            Toast.makeText(getApplicationContext(), R.string.empty_name_field, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            examViewModel.insertExam(new EntityExam(edittext.getText() + ""));
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
            @Override
            public void OnDeleteClick(final int position) {

                AlertDialog.Builder alert = new AlertDialog.Builder(ExamChooserActivity.this);
                alert.setTitle(R.string.dialog_cancel_title);
                alert.setMessage(R.string.dialog_cancel_message);
                alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String examnametemp=adapterExams.get(position).getName()+"";
                        Log.v("exam_delete",examnametemp);
                        examViewModel.deleteExam(adapterExams.get(position).getName()+"");
                    }
                });

                alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }

            @Override
            public void OnRowClick(int position) {
                Intent intentExamName = new Intent(ExamChooserActivity.this, MainActivity.class);
                intentExamName.putExtra("exam_name", adapterExams.get(position).getName());
                startActivity(intentExamName);
            }
        });

        examViewModel = new ViewModelProvider(this).get(ExamViewModel.class);
        examViewModel.getExams().observe(this, new Observer<List<EntityExam>>() {
            @Override
            public void onChanged(@Nullable final List<EntityExam> entityExamList) {
                if (entityExamList != null) {
                        adapterExams.setDataList(entityExamList);
                        if (adapterExams.getItemCount()==0){
                            imageView.setVisibility(View.VISIBLE);
                        }
                        else {
                            imageView.setVisibility(View.INVISIBLE);
                        }
                }
                else
                    Log.v("observer Exams", "null");
            }
        });
    }
}
