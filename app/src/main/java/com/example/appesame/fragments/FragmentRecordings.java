package com.example.appesame.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appesame.AddItemDialog;
import com.example.appesame.BuildConfig;
import com.example.appesame.R;
import com.example.appesame.entities.StudiedItem;
import com.example.appesame.uiutilities.AdapterItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FragmentRecordings extends Fragment implements AddItemDialog.OnInputSelected {

    private static String FILE_TYPE = "audio/*";
    private static String STORAGE_FOLDER = "Recordings";
    private static MediaPlayer mediaPlayer= null;

    private String examname, examId;

    private RecyclerView recyclerView;
    private AdapterItem adapterItem;
    private ContentLoadingProgressBar progressIndicator;
    private FirebaseUser user;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        examname = getArguments().getString("exam_name");
        examId = getArguments().getString("exam_id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_items, container, false);


        progressIndicator = view.findViewById(R.id.progressIndicator);
        recyclerView = view.findViewById(R.id.recicler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapterItem = new AdapterItem(this.getContext());
        recyclerView.setAdapter(adapterItem);

        final Drawable deleteIcon = ContextCompat.getDrawable(getContext(),
                R.drawable.deletebin);
        final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorPrimarylight,null));

        final File storagePath = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), examId+"/"+STORAGE_FOLDER);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UpdateUI(recyclerView, view);
        }

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 40; //so background is behind the rounded corners of itemView
                int backgroundHeightOffset = 22;
                int backgroundRightOffset = 20;

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
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (isOnline(getContext())) {
                    AlertDialog.Builder alert = new MaterialAlertDialogBuilder(getActivity());
                    alert.setTitle(R.string.dialog_cancel_title);
                    alert.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final File myFile = new File(storagePath, adapterItem.get(viewHolder.getAdapterPosition()).getItemId());
                            if (myFile.exists()) {
                                myFile.delete();
                            }
                            storageRef.child(user.getUid() +"/"+examId+"/"+STORAGE_FOLDER+"/" + adapterItem.get(viewHolder.getAdapterPosition()).getItemId())
                                    .delete();
                            db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examId)
                                    .collection(STORAGE_FOLDER)
                                    .document(adapterItem.get(viewHolder.getAdapterPosition()).getItemId())
                                    .delete();
                        }
                    });
                    alert.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            UpdateUI(recyclerView,view);
                        }
                    });
                    alert.show();
                }else {
                    AlertDialog.Builder alert = new MaterialAlertDialogBuilder(getContext());
                    alert.setTitle(R.string.connection_title)
                            .setMessage(R.string.connection_message)
                            .show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //handling recyclerview click
        adapterItem.setOnItemClickListener(new AdapterItem.OnItemClickListener() {
            //handling checkbox click
            @Override
            public void OnCheckClick(int position) {
                DocumentReference ItemToUpdate = db.collection("Users").document(user.getUid())
                        .collection("Exams").document(examId)
                        .collection(STORAGE_FOLDER)
                        .document(adapterItem.get(position).getItemId());
                if (adapterItem.get(position).isMemorized()) {
                    ItemToUpdate.update("memorized", false);
                }else{
                    ItemToUpdate.update("memorized", true);
                }
            }
            //handling recyclerview row click
            @Override
            public void OnSelectClick(int position) {
                // Create directory if not exists
                if (!storagePath.exists()) {
                    storagePath.mkdirs();
                }
                final File myFile = new File(storagePath, adapterItem.get(position).getItemId());
                if (myFile.exists()) {
                    startAudio(myFile,position);
                }else if(isOnline(getContext())){
                    final StorageReference FileRef = storageRef.child(user.getUid()+"/"+examId+"/"+STORAGE_FOLDER+"/" + adapterItem.get(position).getItemId());
                    FileRef.getFile(myFile)
                            .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                    progressIndicator.show();
                                    progressIndicator.setMax((int)snapshot.getTotalByteCount());
                                    progressIndicator.setProgress((int) snapshot.getBytesTransferred(),true);
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    progressIndicator.hide();
                                    startAudio(myFile,position);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressIndicator.hide();
                                    Toast.makeText(getContext(), "Faliure loading file", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    AlertDialog.Builder alert = new MaterialAlertDialogBuilder(getContext());
                    alert.setTitle(R.string.connection_title)
                            .setMessage(R.string.connection_message)
                            .show();
                }
            }

            @Override
            public void OnNameClick(int position) {
                final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                final String itemId = adapterItem.get(position).getItemId();
                final String itemName = adapterItem.get(position).getItemName();
                final Dialog nameDialog = new Dialog(getContext());
                nameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                nameDialog.setContentView(R.layout.dialog_name_update);
                final EditText editText =  nameDialog.findViewById(R.id.dialog_trec_editText);
                editText.setText(itemName);
                final TextInputLayout textInputLayout =  nameDialog.findViewById(R.id.dialog_trec_input_layout);
                Button okButton = nameDialog.findViewById(R.id.name_ok);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String itemNameNew = editText.getText()+"".toUpperCase();
                        if (itemNameNew.trim().equals("")) {
                            textInputLayout.setError(getResources().getString(R.string.empty_name_field));
                            textInputLayout.requestFocus();
                        }else if (IsSameName(itemNameNew)){
                            textInputLayout.setError(getString(R.string.used_name));
                            textInputLayout.requestFocus();
                        } else{
                            final CollectionReference items = db.collection("Users").document(user.getUid())
                                    .collection("Exams").document(examId).collection(STORAGE_FOLDER);
                            final DocumentReference itemDoc= items.document(itemId);
                            itemDoc.update("itemName", itemNameNew);
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
        });
        return view;
    }

    private void UpdateUI(final RecyclerView recyclerView, final View view) {
        final ImageView imageView = view.findViewById(R.id.empty_recycler_image);
        // User is signed in
        db.collection("Users").document(user.getUid())
                .collection("Exams").document(examId)
                .collection(STORAGE_FOLDER)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(this.getClass().toString(), "Listen failed.", e);
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            StudiedItem newItem = dc.getDocument().toObject(StudiedItem.class);

                            int i = adapterItem.getDataList().indexOf(newItem);
                            switch (dc.getType())
                            {
                                case ADDED:
                                    if (i == -1)
                                    {
                                        adapterItem.getDataList().add(newItem);
                                        adapterItem.notifyItemInserted(adapterItem.getItemCount() - 1);
                                    }
                                    else
                                        adapterItem.notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    adapterItem.getDataList().remove(newItem);
                                    adapterItem.notifyItemRemoved(i);
                                    break;
                                case MODIFIED:
                                    adapterItem.getDataList().set(i,newItem);
                                    adapterItem.notifyItemChanged(i);
                                    break;
                            }
                        }
                        if (adapterItem.getItemCount() == 0) {
                            imageView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        } else {
                            imageView.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    //override method of file dialog for adding data to the correct table
    @Override
    public nameState sendInput(final String filename, final Uri fileuri) {
        if (isOnline(getContext())) {
            if (user != null) {
                if (!IsSameName(filename)) {
                    // Register observers to listen for when the download is done or if it fails
                    final String uniqueID = UUID.randomUUID().toString();
                    storageRef.child(user.getUid() + "/" + examId + "/" + STORAGE_FOLDER + "/" + uniqueID)
                            .putFile(fileuri)
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    progressIndicator.show();
                                    progressIndicator.setMax((int) taskSnapshot.getTotalByteCount());
                                    progressIndicator.setProgress((int) taskSnapshot.getBytesTransferred(), true);
                                }
                            })
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
                                    db.collection("Users").document(user.getUid())
                                            .collection("Exams").document(examId)
                                            .collection(STORAGE_FOLDER).document(uniqueID)
                                            .set(new StudiedItem(uniqueID, filename), SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressIndicator.hide();
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
                    return nameState.OK;
                }else{
                    return nameState.USED;
                }
            }else{
                AlertDialog.Builder alert = new MaterialAlertDialogBuilder(this.getContext());
                alert.setTitle(R.string.login_title)
                        .setMessage(R.string.alert_login)
                        .show();
                return nameState.H_ERROR;
            }
        }else {
            AlertDialog.Builder alert = new MaterialAlertDialogBuilder(this.getContext());
            alert.setTitle(R.string.connection_title)
                    .setMessage(R.string.connection_message)
                    .show();
            return nameState.H_ERROR;
        }
    }

    //launching intent for file opening
    private void startAudio(File file, int adapterPosition){
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            if (adapterItem.get(adapterPosition).isPlaying) {
                adapterItem.get(adapterPosition).isPlaying=false;
                adapterItem.notifyItemChanged(adapterPosition);
                return;
            }
            for (StudiedItem item :adapterItem.getDataList()) {
                item.isPlaying=false;
            }
            adapterItem.notifyDataSetChanged();
        }
        adapterItem.get(adapterPosition).isPlaying=true;
        adapterItem.notifyItemChanged(adapterPosition);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("record Opener error", file.getAbsolutePath());
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                adapterItem.get(adapterPosition).isPlaying=false;
                adapterItem.notifyItemChanged(adapterPosition);
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    //checking if there is connection
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private boolean IsSameName(String itemNameNew) {
        for (int j=0; j < adapterItem.getItemCount(); j++){
            if (adapterItem.get(j).getItemName().equals(itemNameNew))
                return true;
        }
        return false;
    }
}
