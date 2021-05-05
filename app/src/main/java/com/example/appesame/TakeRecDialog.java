package com.example.appesame;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;

public class TakeRecDialog extends DialogFragment {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private File record;
    private AddItemDialog.OnInputSelected onInputSelected;
    private EditText filenameET;
    private TextInputLayout textInputLayout;

    private AppCompatImageButton recordButton = null;
    private MediaRecorder recorder = null;

    private MaterialButton saveButton = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private String examId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        examId = getArguments().getString("exam_id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.dialog_take_rec,container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        record = new File(getActivity().getExternalCacheDir(),"audiorecordtemp.mp3");
        if (record.exists())
            record.delete();

        filenameET= view.findViewById(R.id.dialog_trec_editText);
        textInputLayout = view.findViewById(R.id.dialog_trec_input_layout);
        recordButton= view.findViewById(R.id.rec);
        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    recordButton.setBackgroundResource(R.drawable.button_roundshape_accent);
                } else {
                    recordButton.setBackgroundResource(R.color.transparent);
                }
                mStartRecording = !mStartRecording;
            }
        });
        saveButton =view.findViewById(R.id.save);
        //handling ok click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String itemNameNew = filenameET.getText()+"".trim();
                if (itemNameNew.equals("")) {
                    textInputLayout.setError(getResources().getString(R.string.empty_name_field));
                    textInputLayout.requestFocus();
                }else if (itemNameNew.length()>=20){
                    textInputLayout.setError(getString(R.string.overflow_name_field));
                    textInputLayout.requestFocus();
                }else if (!record.exists()) {
                    Toast.makeText(getContext(), R.string.no_recording_found, Toast.LENGTH_SHORT).show();
                }else{
                    stopRecording();
                    AddItemDialog.OnInputSelected.nameState result = onInputSelected.sendInput(filenameET.getText() + "", Uri.fromFile(record));
                    switch (result) {
                        case OK:
                            getDialog().dismiss();
                            break;
                        case H_ERROR:
                            getDialog().dismiss();
                            break;
                        case USED: {
                            textInputLayout.setError(getString(R.string.used_name));;
                            textInputLayout.requestFocus();
                        }
                        break;
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
            getDialog().dismiss();
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setOutputFile(record);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }

        recorder.start();
    }

    private void stopRecording() {
        if (recorder!=null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputSelected = (AddItemDialog.OnInputSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e("on attach","exception"+e.getMessage());
        }
    }
}
