package com.example.appesame;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;

public class AddFileDialog extends DialogFragment {

    private Button actionSearch, actionOk, actionCancel;
    private TextView fileuriTV;
    private EditText filenameET;

    private Intent searchfile;
    private Uri fileUri;
    String argValue;

    public OnInputSelected onInputSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        argValue= getArguments().getString("ArgKey");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.dialog_add,container, false);

        fileuriTV=view.findViewById(R.id.dialog_uri_tv);
        filenameET = view.findViewById(R.id.dialog_title_editText);

        actionSearch=view.findViewById(R.id.dialog_search_btn);
        actionOk=view.findViewById(R.id.dialog_ok_btn);
        actionCancel=view.findViewById(R.id.dialog_cancel_btn);

        actionSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchfile= new Intent(Intent.ACTION_OPEN_DOCUMENT);
                searchfile.setType(argValue);
                searchfile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                searchfile.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(searchfile,21);
            }
        });

        actionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filenameET.getText()+"" == "") {
                    Toast.makeText(getContext(), R.string.empty_name_field, Toast.LENGTH_SHORT).show();
                }else {
                    onInputSelected.sendInput(filenameET.getText() + "", fileUri);
                    getDialog().dismiss();
                }
            }
        });

        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 21 && resultCode == RESULT_OK) {
            fileUri = data.getData();
            fileuriTV.setText(fileUri.getPath()+"");
            ContentResolver contentResolver = getActivity().getContentResolver();
            contentResolver.takePersistableUriPermission(fileUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Cursor returnCursor = contentResolver.query(fileUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            filenameET.setText(returnCursor.getString(nameIndex));
        }
    }

    public interface OnInputSelected {
        void sendInput(String filename,Uri fileuri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputSelected = (OnInputSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e("on attach","exception"+e.getMessage());
        }
    }

    public static AddFileDialog newInstance(String Arg) {
        AddFileDialog addFileDialog =new AddFileDialog();
        Bundle args = new Bundle();
        args.putString("ArgKey", Arg);
        addFileDialog.setArguments(args);
        return addFileDialog;
    }
}
