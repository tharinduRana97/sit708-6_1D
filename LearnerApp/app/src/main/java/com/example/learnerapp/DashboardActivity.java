package com.example.learnerapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.learnerapp.model.User;
import com.example.learnerapp.utils.SessionManager;

public class DashboardActivity extends BaseActivity {
    private SessionManager sessionManager;
    private TextView welcomeText, taskNotification, taskTitle, taskDescription;
    private CardView taskCard;
    private Button startTaskBtn;
    private RequestQueue queue;

    private ProgressBar loadingSpinner;
    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        welcomeText = findViewById(R.id.welcomeText);
        taskNotification = findViewById(R.id.taskNotification);
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        taskCard = findViewById(R.id.taskCard);
        startTaskBtn = findViewById(R.id.startTaskBtn);
        loadingSpinner = findViewById(R.id.loadingSpinner);

        queue = Volley.newRequestQueue(this);

        User user = sessionManager.getUser();
        welcomeText.setText("Hello,\n" + (user != null ? user.username:""));

        fetchTaskFromBackend();

        startTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("task_title", taskTitle.getText().toString());
            intent.putExtra("task_description", taskDescription.getText().toString());
            startActivity(intent);
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_dashboard;
    }

    @Override
    protected String getToolbarTitle() {
        return "Dashboard";
    }

    @Override
    protected boolean enableBackButton() {
        return false;
    }

    private void fetchTaskFromBackend() {
        loadingSpinner.setVisibility(View.VISIBLE);
        String url = "http://10.0.2.2:5002/getTask?topic=Movies"; // Topic can be dynamic later

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingSpinner.setVisibility(View.GONE);
                    try {
                        String title = response.getString("task_title");
                        String description = response.getString("task_description");

                        taskTitle.setText(title);
                        taskDescription.setText(description);
                        taskCard.setVisibility(View.VISIBLE); // Show the card now

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing task response: " + e.getMessage());
                        Toast.makeText(this, "Failed to load task!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingSpinner.setVisibility(View.GONE);
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    Toast.makeText(this, "Network error!", Toast.LENGTH_SHORT).show();
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonObjectRequest);
    }
}
