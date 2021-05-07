package com.example.appesame.dialogs;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.fragment.app.DialogFragment;

import com.example.appesame.R;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;
//dialog for adding files
public class AddItemDialog extends DialogFragment {

    private Button actionSearch, actionOk, actionCancel;
    private TextView fileuriTV;
    private EditText filenameET;
    private TextInputLayout textInputLayout;

    private Intent searchfile;
    private Uri fileUri;
    private String argValue;

    private OnInputSelected onInputSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            argValue = getArguments().getString("ArgKey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.dialog_file,container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        fileuriTV=view.findViewById(R.id.dialog_uri_tv);
        filenameET = view.findViewById(R.id.dialog_trec_editText);
        textInputLayout = view.findViewById(R.id.dialog_trec_input_layout);

        actionSearch=view.findViewById(R.id.dialog_search_btn);
        actionOk=view.findViewById(R.id.dialog_ok_btn);
        actionCancel=view.findViewById(R.id.dialog_cancel_btn);

        //handling search file click
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

        //handling ok click
        actionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String itemNameNew = filenameET.getText()+"".trim();
                if (itemNameNew.equals("")) {
                    textInputLayout.setError(getResources().getString(R.string.empty_name_field));
                    textInputLayout.requestFocus();
                }else if (itemNameNew.length()>=20){
                    textInputLayout.setError(getString(R.string.overflow_name_field));
                    textInputLayout.requestFocus();
                }else if (fileuriTV.getText()+""== "") {
                    Toast.makeText(getContext(), R.string.empty_file_field, Toast.LENGTH_SHORT).show();
                }else{
                    OnInputSelected.nameState result = onInputSelected.sendInput(filenameET.getText() + "", fileUri);
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

        //handling cancel click
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
            contentResolver.takePersistableUriPermission(fileUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Cursor returnCursor = contentResolver.query(fileUri,
                    null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            if (filenameET.getText()+"".trim() == "")
            filenameET.setText(returnCursor.getString(nameIndex));
        }
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

    public static AddItemDialog newInstance(String Arg) {
        AddItemDialog addItemDialog =new AddItemDialog();
        Bundle args = new Bundle();
        args.putString("ArgKey", Arg);
        addItemDialog.setArguments(args);
        return addItemDialog;
    }

    //interface to override for getting the uri and the file name
    public interface OnInputSelected {
        enum nameState {OK, USED, H_ERROR};
        nameState sendInput(String filename,Uri fileuri);
    }
}
