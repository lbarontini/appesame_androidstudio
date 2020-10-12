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
import com.example.appesame.entities.EntityCmap;
import com.example.appesame.entities.EntityFlashcard;
import com.example.appesame.uiutilities.AdapterCmaps;

import java.util.ArrayList;
import java.util.List;

public class FragmentCmaps extends Fragment implements AddFileDialog.OnInputSelected{

    static final String IMAGES= "image/*";
    private ExamViewModel examViewModel;
    private String examname;

    private RecyclerView recyclerViewCmaps;
    private AdapterCmaps adapterCmaps;
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
        final View view = inflater.inflate(R.layout.fragment_cmaps,container,false);

        imageView = view.findViewById(R.id.empty_recycler_image_c);
        textView = view.findViewById(R.id.textView_mem_c);

        recyclerViewCmaps = view.findViewById(R.id.recicler_view_cmaps);
        recyclerViewCmaps.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapterCmaps = new AdapterCmaps(this.getContext());
        recyclerViewCmaps.setAdapter(adapterCmaps);

        addbtn = view.findViewById(R.id.add_button_c);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddFileDialog addFileDialog = AddFileDialog.newInstance(IMAGES);
                addFileDialog.setTargetFragment(FragmentCmaps.this, 1);
                assert getFragmentManager() != null;
                addFileDialog.show(getFragmentManager(), "add_dialog");
            }
        });

        examViewModel = new ViewModelProvider(getActivity()).get(ExamViewModel.class);
        examViewModel.getCmaps(examname)
                .observe(getViewLifecycleOwner(), new Observer<List<EntityCmap>>() {
                    @Override
                    public void onChanged(@Nullable final List<EntityCmap> entityCmapList) {
                        if (entityCmapList != null) {
                            adapterCmaps.setDataList(entityCmapList);
                            if (adapterCmaps.getItemCount() == 0) {
                                imageView.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.INVISIBLE);
                            }
                            else {
                                imageView.setVisibility(View.INVISIBLE);
                                textView.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            Log.v("observer Cmaps", "null");
                        }
                    }
                });

        adapterCmaps.setOnItemClickListener(new AdapterCmaps.OnItemClickListener() {
        @Override
        public void OnCheckClick(int position) {
            if (adapterCmaps.get(position).isMemorized()) {
                examViewModel.updateCmap(adapterCmaps.get(position).getExamName() + "",
                        adapterCmaps.get(position).getUri() + "",
                        adapterCmaps.get(position).getTitle() + "",
                        false);
            } else {
                examViewModel.updateCmap(adapterCmaps.get(position).getExamName() + "",
                        adapterCmaps.get(position).getUri() + "",
                        adapterCmaps.get(position).getTitle() + "",
                        true);
            }
        }

        @Override
        public void OnDeleteClick(final int position) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.dialog_cancel_title);
            alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    examViewModel.deleteCmap(examname, adapterCmaps.get(position).getTitle()+"");
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
            if (fileOpener(adapterCmaps.get(position).getUri(), adapterCmaps.get(position).getType())) {
            } else {
                Toast.makeText(getContext(), R.string.missing_file, Toast.LENGTH_SHORT).show();
            }
        }
    });

        return view;
    }

    @Override
    public void sendInput(String filename, Uri fileuri) {
        examViewModel.insertCmap(new EntityCmap(examname,IMAGES,fileuri+"",filename));
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
