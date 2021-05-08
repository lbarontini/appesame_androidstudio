package com.example.appesame.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appesame.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class TakePicDialog extends DialogFragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private AddItemDialog.OnInputSelected onInputSelected;
    private EditText filenameET;
    private TextInputLayout textInputLayout;
    private MaterialButton saveButton = null;
    private String examId;

    private File photoFile = null;
    Uri photoURI;

    private ImageView picView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            examId = getArguments().getString("examId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dispatchTakePictureIntent();
        View view = inflater.inflate(R.layout.dialog_take_pic, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        picView = view.findViewById(R.id.dialog_tpic_imageview);
        textInputLayout =view.findViewById(R.id.dialog_tpic_text_layout);
        filenameET = view.findViewById(R.id.dialog_tpic_editText);

        saveButton= view.findViewById(R.id.save);
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
                }else if (!photoFile.exists()) {
                    Toast.makeText(getContext(), R.string.no_picture_found, Toast.LENGTH_SHORT).show();
                }else{
                    AddItemDialog.OnInputSelected.nameState result = onInputSelected.sendInput(filenameET.getText() + "", photoURI);
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.appesame.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Glide.with(getContext())
//                    .asBitmap()
//                    .load(photoFile.getAbsolutePath())
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            picView.setImageBitmap(resource);
//                        }
//                    });
            //todo test if works
            Glide.with(getContext())
                    .load(photoFile.getAbsolutePath())
                    .override(640,640)
                    .fitCenter()
                    .into(picView);
        }else if (resultCode == RESULT_CANCELED){
            getDialog().dismiss();
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

    public static TakePicDialog newInstance(String examId) {
        TakePicDialog fragment = new TakePicDialog();
        Bundle args = new Bundle();
        args.putString("examId", examId);
        fragment.setArguments(args);
        return fragment;
    }
}