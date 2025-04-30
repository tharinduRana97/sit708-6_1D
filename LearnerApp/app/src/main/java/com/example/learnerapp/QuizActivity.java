package com.example.learnerapp;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class QuizActivity extends BaseActivity {

    private Bundle quizBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String taskTitle = getIntent().getStringExtra("task_title");
        String taskDescription = getIntent().getStringExtra("task_description");

        quizBundle = new Bundle();
        quizBundle.putString("task_title", taskTitle);
        quizBundle.putString("task_description", taskDescription);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState == null && quizBundle != null) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.quizFragment, quizBundle);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_quiz;
    }

    @Override
    protected String getToolbarTitle() {
        return "Quiz";
    }

    @Override
    protected boolean enableBackButton() {
        return true;
    }
}
