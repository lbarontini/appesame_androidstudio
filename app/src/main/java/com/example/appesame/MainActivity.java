package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appesame.fragments.FragmentCmaps;
import com.example.appesame.fragments.FragmentExercises;
import com.example.appesame.fragments.FragmentFlashcards;
import com.example.appesame.fragments.FragmentRecordings;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//todo change the layouts for app crash
public class MainActivity extends AppCompatActivity {

    private static String APP_PDF = "application/pdf";


    private BottomNavigationView navigation;
    private FragmentFlashcards fragmentFlashcards = new FragmentFlashcards();
    private FragmentRecordings fragmentRecordings = new FragmentRecordings();
    private FragmentCmaps fragmentCmaps = new FragmentCmaps();
    private FragmentExercises fragmentExercise = new FragmentExercises();
    private String examName="";

    Fragment selectedFragment = null;

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(user.getPhotoUrl())
                    .centerCrop()
                    .circleCrop()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            menu.getItem(0).setIcon(new BitmapDrawable(getApplicationContext().getResources(), resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            examName = extras.getString("exam_name").toUpperCase();
            Bundle bundle = new Bundle();
            bundle.putString("exam_name", examName);
            fragmentFlashcards.setArguments(bundle);
            fragmentRecordings.setArguments(bundle);
            fragmentCmaps.setArguments(bundle);
            fragmentExercise.setArguments(bundle);
            selectedFragment= fragmentFlashcards;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentFlashcards).commit();
        }

        //handling the navigation between fragments
        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.botmenu_recordings:
                        selectedFragment = fragmentRecordings;
                        break;
                    case R.id.botmenu_cmaps:
                        selectedFragment = fragmentCmaps;
                        break;
                    case R.id.botmenu_exercise:
                        selectedFragment = fragmentExercise;
                        break;
                    default:
                        selectedFragment = fragmentFlashcards;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });

        //top app bar show and title set
//        setSupportActionBar((MaterialToolbar) findViewById(R.id.topAppBar));
//        getSupportActionBar().setTitle(examName);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MaterialToolbar topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.action_login) {
//                    Intent intent= new Intent(MainActivity.this, LoginDialog.class);
//                    startActivity(intent);
                    LoginDialog loginDialog = new LoginDialog();
                    //loginDialog.setcony(getc, 1);
                    //if (getFragmentManager() != null) throw new AssertionError();
                    loginDialog.show(getSupportFragmentManager(), "login_dialog");
                    return true;
                }
                return false;
            }
        });
        getSupportActionBar().setTitle(examName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //handling add button click
        ImageButton addbtn = findViewById(R.id.add_button_m);
        addbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline(getApplicationContext())) {
                        AddFileDialog addFileDialog = AddFileDialog.newInstance(APP_PDF);
                        addFileDialog.setTargetFragment(selectedFragment, 1);
                        if (getFragmentManager() != null) throw new AssertionError();
                        addFileDialog.show(getSupportFragmentManager(), "add_dialog");
                    }else{
                        Toast.makeText(getApplicationContext(), "you need to be online",Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}

