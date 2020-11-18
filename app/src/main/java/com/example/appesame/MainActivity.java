package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.appesame.fragments.FragmentCmaps;
import com.example.appesame.fragments.FragmentExercises;
import com.example.appesame.fragments.FragmentFlashcards;
import com.example.appesame.fragments.FragmentRecordings;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//todo change the layouts for app crash
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private FragmentFlashcards fragmentFlashcards = new FragmentFlashcards();
    private FragmentRecordings fragmentRecordings = new FragmentRecordings();
    private FragmentCmaps fragmentCmaps = new FragmentCmaps();
    private FragmentExercises fragmentExercise = new FragmentExercises();
    private String examName="";

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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentFlashcards).commit();
        }
        //top app bar show and title set
        //MaterialToolbar topAppBar = (MaterialToolbar) findViewById(R.id.topAppBar);
        setSupportActionBar((MaterialToolbar) findViewById(R.id.topAppBar));
        getSupportActionBar().setTitle(examName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //handling the navigation between fragments
        navigation = findViewById(R.id.bottom_navigation);
        BottomNavigationView.OnNavigationItemSelectedListener menuNavigator = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
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
        };
        navigation.setOnNavigationItemSelectedListener(menuNavigator);
    }
}
