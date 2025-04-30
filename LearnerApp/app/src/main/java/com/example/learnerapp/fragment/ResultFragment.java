package com.example.learnerapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.learnerapp.DashboardActivity;
import com.example.learnerapp.R;
import com.example.learnerapp.adapter.AnswerAdapter;
import com.example.learnerapp.model.QuizQuestion;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ResultFragment extends Fragment {

    private List<QuizQuestion> quizQuestions;
    private ProgressBar loadingSpinner;
    private RecyclerView answersRecycler;
    private Button continueButton;
    private AnswerAdapter adapter;
    private final List<String> aiAnswers = new ArrayList<>();

    private static final String TAG = "ResultFragment";

    public ResultFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        answersRecycler = view.findViewById(R.id.answersRecycler);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        continueButton = view.findViewById(R.id.continueButton);

        answersRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (getArguments() != null) {
            quizQuestions = (ArrayList<QuizQuestion>) getArguments().getSerializable("quizQuestions");
        }

        fetchGeneratedAnswers();

        continueButton.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), DashboardActivity.class));
            requireActivity().finish();
        });
    }

    private void fetchGeneratedAnswers() {
        loadingSpinner.setVisibility(View.VISIBLE);

        JSONObject requestBody = new JSONObject();
        JSONArray questionsArray = new JSONArray();

        try {
            for (QuizQuestion q : quizQuestions) {
                JSONObject qObj = new JSONObject();
                qObj.put("question", q.question);
                qObj.put("user_answer", q.userSelectedAnswer);
                questionsArray.put(qObj);
            }
            requestBody.put("questions", questionsArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:5002/getAnswers";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    loadingSpinner.setVisibility(View.GONE);
                    try {
                        JSONArray answersArray = response.getJSONArray("answers");
                        List<String> questions = new ArrayList<>();

                        for (QuizQuestion q : quizQuestions) {
                            questions.add(q.question);
                        }

                        for (int i = 0; i < answersArray.length(); i++) {
                            aiAnswers.add(answersArray.getString(i));
                        }

                        adapter = new AnswerAdapter(questions, aiAnswers);
                        answersRecycler.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing answers: " + e.getMessage(), e);
                    }
                },
                error -> {
                    loadingSpinner.setVisibility(View.GONE);
                    Log.e(TAG, "Error fetching answers: " + error.getMessage(), error);
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }
}
