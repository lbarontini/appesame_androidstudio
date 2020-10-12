package com.example.appesame.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appesame.AddFileDialog;
import com.example.appesame.R;
import com.example.appesame.dbutilities.ExamViewModel;
import com.example.appesame.entities.EntityRecording;
import com.example.appesame.uiutilities.AdapterRecordings;

import java.util.List;

public class FragmentRecordings extends Fragment implements AddFileDialog.OnInputSelected {

    private static final String AUDIOS = "audio/*";
    private ExamViewModel examViewModel;
    private String examname;

    private RecyclerView recyclerViewRecordings;
    private AdapterRecordings adapterRecordings;
    private ImageButton addbtn;
    private ImageView imageView;
    private TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        examname = getArguments().getString("exam_name") + "";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recordings,container,false);

        imageView = view.findViewById(R.id.empty_recycler_image_r);
        textView = view.findViewById(R.id.textView_mem_r);

        recyclerViewRecordings = view.findViewById(R.id.recicler_view_recordings);
        recyclerViewRecordings.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapterRecordings = new AdapterRecordings(this.getContext());
        recyclerViewRecordings.setAdapter(adapterRecordings);

        addbtn = view.findViewById(R.id.add_button_r);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddFileDialog addFileDialog = AddFileDialog.newInstance(AUDIOS);
                addFileDialog.setTargetFragment(FragmentRecordings.this, 1);
                assert getFragmentManager() != null;
                addFileDialog.show(getFragmentManager(), "add_dialog");
            }
        });

        examViewModel = new ViewModelProvider(getActivity()).get(ExamViewModel.class);
        examViewModel.getRecordings(examname)
                .observe(getViewLifecycleOwner(), new Observer<List<EntityRecording>>() {
                    @Override
                    public void onChanged(@Nullable final List<EntityRecording> entityRecordingListList) {
                        if (entityRecordingListList != null) {
                            adapterRecordings.setDataList(entityRecordingListList);
                            if (adapterRecordings.getItemCount() == 0) {
                                imageView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.INVISIBLE);
                            }
                            else {
                                imageView.setVisibility(View.INVISIBLE);
                                textView.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            Log.v("observer Recordings", "null");
                        }
                    }
                });

        adapterRecordings.setOnItemClickListener(new AdapterRecordings.OnItemClickListener() {
            @Override
            public void OnCheckClick(int position) {
                if (adapterRecordings.get(position).isMemorized()) {
                    examViewModel.updateRecording(adapterRecordings.get(position).getExamName() + "",
                            adapterRecordings.get(position).getUri() + "",
                            adapterRecordings.get(position).getTitle() + "",
                            false);
                }
                else{
                    examViewModel.updateRecording(adapterRecordings.get(position).getExamName() + "",
                            adapterRecordings.get(position).getUri() + "",
                            adapterRecordings.get(position).getTitle() + "",
                            true);
                }
            }

            @Override
            public void OnDeleteClick(final int position) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.dialog_cancel_title);
                alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        examViewModel.deleteRecording(examname, adapterRecordings.get(position).getTitle()+"");
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
                if(fileOpener(adapterRecordings.get(position).getUri(),adapterRecordings.get(position).getType()))
                {}
                else{
                    Toast.makeText(getContext(), R.string.missing_file, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void sendInput(String filename, Uri fileuri) {
        examViewModel.insertRecording(new EntityRecording(examname,AUDIOS,fileuri+"",filename));
    }

    Boolean fileOpener(String uri, String type){
        try {
            Intent openfile = new Intent(Intent.ACTION_VIEW);
            openfile.setDataAndType(Uri.parse(uri), type);
            openfile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            Intent intent1 = Intent.createChooser(openfile, "Open With");
            startActivity(intent1);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
