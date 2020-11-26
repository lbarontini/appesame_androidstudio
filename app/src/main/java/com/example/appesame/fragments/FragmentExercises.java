package com.example.appesame.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appesame.AddFileDialog;
import com.example.appesame.BuildConfig;
import com.example.appesame.R;
import com.example.appesame.entities.StudiedItem;
import com.example.appesame.uiutilities.AdapterItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentExercises extends Fragment implements AddFileDialog.OnInputSelected{

    private static String FILE_TYPE = "application/pdf";
    private static String STORAGE_FOLDER = "Exercises";

    private String examname;

    private RecyclerView recyclerView;
    private AdapterItem adapterFlashcard;
    private ImageView imageView;

    private FirebaseUser user;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        examname = getArguments().getString("exam_name");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_items, container, false);

        imageView = view.findViewById(R.id.empty_recycler_image);

        recyclerView = view.findViewById(R.id.recicler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapterFlashcard = new AdapterItem(this.getContext());
        recyclerView.setAdapter(adapterFlashcard);

        final File storagePath = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), examname+"/"+STORAGE_FOLDER);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            db.collection("Users").document(user.getUid())
                    .collection("Exams").document(examname)
                    .collection(STORAGE_FOLDER)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(this.getClass().toString(), "Listen failed.", e);
                                return;
                            }
                            List<StudiedItem> itemList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                itemList.add(doc.toObject(StudiedItem.class));
                            }
                            adapterFlashcard.setDataList(itemList);
                            if (adapterFlashcard.getItemCount() == 0) {
                                imageView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.INVISIBLE);
                            } else {
                                imageView.setVisibility(View.INVISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            // No user is signed in show alert
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.dialog_cancel_title);
            alert.setMessage("google login needed");
            alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
        }

        //handling recyclerview click
        adapterFlashcard.setOnItemClickListener(new AdapterItem.OnItemClickListener() {
            //handling checkbox click
            @Override
            public void OnCheckClick(int position) {
                DocumentReference ItemToUpdate = db.collection("Users").document(user.getUid())
                        .collection("Exams").document(examname)
                        .collection(STORAGE_FOLDER)
                        .document(adapterFlashcard.get(position).getItemName());
                if (adapterFlashcard.get(position).isMemorized()) {
                    ItemToUpdate.update("memorized", false);
                }else{
                    ItemToUpdate.update("memorized", true);
                }
            }
            //handling delete button click
            @Override
            public void OnDeleteClick(final int position) {
                if (isOnline(getContext())) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(R.string.dialog_cancel_title);
                    alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final File myFile = new File(storagePath, adapterFlashcard.get(position).getItemName());
                            if (myFile.exists()) {
                                myFile.delete();
                            }
                            storageRef.child(user.getUid() +"/"+examname+"/"+STORAGE_FOLDER+"/" + adapterFlashcard.get(position).getItemName())
                                    .delete();
                            db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examname)
                                    .collection(STORAGE_FOLDER)
                                    .document(adapterFlashcard.get(position).getItemName())
                                    .delete();
                        }
                    });
                    alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }else {
                    Toast.makeText(getContext(), "you need to be online", Toast.LENGTH_SHORT).show();
                }
            }
            //handling recyclerview row click
            @Override
            public void OnRowClick(int position) {
                // Create directory if not exists
                if (!storagePath.exists()) {
                    storagePath.mkdirs();
                }
                final File myFile = new File(storagePath, adapterFlashcard.get(position).getItemName());
                if (myFile.exists()) {
                    fileOpener(myFile);
                }else if(isOnline(getContext())){
                    final StorageReference FileRef = storageRef.child(user.getUid()+"/"+examname+"/"+STORAGE_FOLDER+"/" + adapterFlashcard.get(position).getItemName());
                    FileRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            fileOpener(myFile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getContext(), "Faliure loading file", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else
                {
                    Toast.makeText(getContext(), "you need to be online",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    //override method of file dialog for adding data to the correct table
    @Override
    public void sendInput(final String filename, Uri fileuri) {
        if (user != null&&isOnline(getContext())) {
            // Register observers to listen for when the download is done or if it fails
            storageRef.child(user.getUid()+"/"+examname+"/"+STORAGE_FOLDER+"/"+filename)
                    .putFile(fileuri)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getContext(), "faliure storage", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //if the download succeed register observer for database update
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "success storage", Toast.LENGTH_SHORT).show();
                            db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examname)
                                    .collection(STORAGE_FOLDER).document(filename)
                                    .set(new StudiedItem(filename), SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "success firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "faliure firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        } else {
            Toast.makeText(getContext(), "you need to be online and signed in",Toast.LENGTH_SHORT).show();
        }
    }

    //launching intent for file opening
    private void fileOpener(File file){
        Uri uri = FileProvider.getUriForFile(getContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        Intent openfile = new Intent(Intent.ACTION_VIEW);
        openfile.setDataAndType(uri, FILE_TYPE);
        openfile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent1 = Intent.createChooser(openfile, "Open With");
        startActivity(intent1);
    }

    //checking if there is connection
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
