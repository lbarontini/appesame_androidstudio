package com.example.appesame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
            examName = extras.getString("exam_name");
            getSupportActionBar().setTitle(examName);

            Bundle bundle = new Bundle();
            bundle.putString("exam_name", examName);
            fragmentFlashcards.setArguments(bundle);
            fragmentRecordings.setArguments(bundle);
            fragmentCmaps.setArguments(bundle);
            fragmentExercise.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentFlashcards).commit();
        }

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

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(data != null){
                examName= data.getStringExtra("exam_name");
        }
    }*/
}
