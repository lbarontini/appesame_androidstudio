package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appesame.dialogs.AddItemDialog;
import com.example.appesame.fragments.FragmentCmaps;
import com.example.appesame.fragments.FragmentExercises;
import com.example.appesame.fragments.FragmentFlashcards;
import com.example.appesame.fragments.FragmentRecordings;
import com.example.appesame.dialogs.TakePicDialog;
import com.example.appesame.dialogs.TakeRecDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemChooserActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private FragmentFlashcards fragmentFlashcards = new FragmentFlashcards();
    private FragmentRecordings fragmentRecordings = new FragmentRecordings();
    private FragmentCmaps fragmentCmaps = new FragmentCmaps();
    private FragmentExercises fragmentExercise = new FragmentExercises();
    private String examName="", examId;
    private Date examDate;
    private int examCfu;
    private  String fileType = "application/pdf";
    private ImageButton takeRec,takePic;

    private Fragment selectedFragment = null;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
            setProPic(menu.getItem(0));
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_chooser);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            examName = extras.getString("exam_name");
            examId = extras.getString("exam_id");
            examCfu = extras.getInt("exam_cfu");
            examDate = new Date(extras.getLong("exam_date"));
            Bundle bundle = new Bundle();
            bundle.putString("exam_name", examName);
            bundle.putString("exam_id", examId);
            fragmentFlashcards.setArguments(bundle);
            fragmentRecordings.setArguments(bundle);
            fragmentCmaps.setArguments(bundle);
            fragmentExercise.setArguments(bundle);
            selectedFragment= fragmentFlashcards;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentFlashcards).commit();
        }

        takeRec = findViewById(R.id.record_button_m);
        takePic = findViewById(R.id.picture_button_m);

        MaterialToolbar topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.action_login) {
                    Intent intentLogin = new Intent(ItemChooserActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    return true;
                }
                return false;
            }
        });
        getSupportActionBar().setTitle(examName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MaterialTextView examDetailsTv = (MaterialTextView)findViewById(R.id.exam_detail_tv);
        DateFormat format = new SimpleDateFormat("dd/MMM/yy", Locale.ITALY);
        examDetailsTv.setText("Date: "+format.format(examDate)+"\nCfu: "+examCfu);

        //handling the navigation between fragments
        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.botmenu_recordings:
                        selectedFragment = fragmentRecordings;
                        takeRec.setVisibility(View.VISIBLE);
                        takePic.setVisibility(View.GONE);
                        fileType= "audio/*";
                        break;
                    case R.id.botmenu_cmaps:
                        selectedFragment = fragmentCmaps;
                        takeRec.setVisibility(View.GONE);
                        takePic.setVisibility(View.VISIBLE);
                        fileType= "image/*";
                        break;
                    case R.id.botmenu_exercise:
                        selectedFragment = fragmentExercise;
                        takeRec.setVisibility(View.GONE);
                        takePic.setVisibility(View.GONE);
                        fileType= "application/pdf";
                        break;
                    default:
                        selectedFragment = fragmentFlashcards;
                        takeRec.setVisibility(View.GONE);
                        takePic.setVisibility(View.GONE);
                        fileType= "application/pdf";
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });

        //handling add button click
        ImageButton addbtn = findViewById(R.id.add_button_m);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(getApplicationContext())) {
                    AddItemDialog addItemDialog = AddItemDialog.newInstance(fileType);
                    addItemDialog.setTargetFragment(selectedFragment, 1);
                    addItemDialog.show(getSupportFragmentManager(), "add_dialog");
                }else{
                    AlertDialog.Builder alert = new MaterialAlertDialogBuilder(ItemChooserActivity.this);
                    alert.setTitle(R.string.connection_title)
                            .setMessage(R.string.connection_message)
                            .show();
                }
            }
        });


        takeRec.setOnClickListener(v -> {
            if (isOnline(getApplicationContext())) {
                TakeRecDialog takeRecDialog = TakeRecDialog.newInstance(examId);
                takeRecDialog.setTargetFragment(selectedFragment, 1);
                takeRecDialog.show(getSupportFragmentManager(), "take_rec");
            }else{
                AlertDialog.Builder alert = new MaterialAlertDialogBuilder(ItemChooserActivity.this);
                alert.setTitle(R.string.connection_title)
                        .setMessage(R.string.connection_message)
                        .show();
            }
        });

        takePic.setOnClickListener(v -> {
            if (isOnline(getApplicationContext())) {
                TakePicDialog takePicDialog = TakePicDialog.newInstance(examId);
                takePicDialog.setTargetFragment(selectedFragment, 1);
                takePicDialog.show(getSupportFragmentManager(), "take_pic");
            }else{
                AlertDialog.Builder alert = new MaterialAlertDialogBuilder(ItemChooserActivity.this);
                alert.setTitle(R.string.connection_title)
                        .setMessage(R.string.connection_message)
                        .show();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            if (FirebaseAuth.getInstance().getCurrentUser()==null){
                this.finish();
            }
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void setProPic(final MenuItem item) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .circleCrop()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            item.setIcon(new BitmapDrawable(getApplicationContext().getResources(), resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }else{
            item.setIcon(R.drawable.ic_account_circle_light);
        }
    }
}

