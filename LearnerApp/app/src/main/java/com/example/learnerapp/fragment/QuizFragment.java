package com.example.learnerapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.learnerapp.R;
import com.example.learnerapp.adapter.QuizPagerAdapter;
import com.example.learnerapp.model.QuizQuestion;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private TextView taskTitle, taskDescription, generatedByAiText, nextQuestionLabel;
    private ProgressBar loadingSpinner;
    private ViewPager2 viewPager;
    private View nextQuestionCard;
    private Button nextQuestionBtn, submitQuizBtn;

    private final ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private QuizPagerAdapter pagerAdapter;

    public QuizFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        generatedByAiText = view.findViewById(R.id.generatedByAiText);
        taskTitle = view.findViewById(R.id.taskTitle);
        taskDescription = view.findViewById(R.id.taskDescription);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        viewPager = view.findViewById(R.id.viewPager);
        nextQuestionBtn = view.findViewById(R.id.nextQuestionBtn);
        nextQuestionLabel = view.findViewById(R.id.nextQuestionLabel);
        submitQuizBtn = view.findViewById(R.id.submitQuizBtn);
        nextQuestionCard = view.findViewById(R.id.nextQuestionCard);

        nextQuestionCard.setVisibility(View.GONE);
        submitQuizBtn.setVisibility(View.GONE);

        // Get task title and description from bundle args
        if (getArguments() != null) {
            taskTitle.setText(getArguments().getString("task_title", "Default Title"));
            taskDescription.setText(getArguments().getString("task_description", "Default Description"));
        }

        fetchQuizFromBackend();

        nextQuestionBtn.setOnClickListener(v -> goToNextQuestion());
        submitQuizBtn.setOnClickListener(v -> submitQuiz());
    }

    private void fetchQuizFromBackend() {
        loadingSpinner.setVisibility(View.VISIBLE);

        String url = "http://10.0.2.2:5002/getQuiz?topic=Movies";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    loadingSpinner.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                    nextQuestionCard.setVisibility(View.VISIBLE);
                    nextQuestionBtn.setEnabled(false);
                    nextQuestionBtn.setAlpha(0.7f);

                    try {
                        JSONArray quizArray = response.getJSONArray("quiz");
                        for (int i = 0; i < quizArray.length(); i++) {
                            JSONObject quizQuestion = quizArray.getJSONObject(i);
                            String question = quizQuestion.getString("question");
                            JSONArray optionsArray = quizQuestion.getJSONArray("options");
                            String correctAnswer = quizQuestion.getString("correct_answer");

                            List<String> options = new ArrayList<>();
                            for (int j = 0; j < optionsArray.length(); j++) {
                                options.add(optionsArray.getString(j));
                            }

                            quizQuestions.add(new QuizQuestion(question, options, correctAnswer));
                        }

                        pagerAdapter = new QuizPagerAdapter(requireActivity(), quizQuestions,
                                (index, answer) -> {
                                    QuizQuestion q = quizQuestions.get(index);
                                    q.userSelectedAnswer = answer;
                                    if (index == currentQuestionIndex) {
                                        setNextButtonEnabled(true);
                                    }
                                }
                        );
                        viewPager.setAdapter(pagerAdapter);
                        viewPager.setUserInputEnabled(false);

                        updateNextQuestionLabel();
                    } catch (Exception e) {
                        Log.e("QuizFragment", "JSON parse error: " + e.getMessage());
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, 1.0f));
        Volley.newRequestQueue(requireContext()).add(jsonRequest);
    }

    private void goToNextQuestion() {
        if (currentQuestionIndex < quizQuestions.size() - 1) {
            currentQuestionIndex++;
            viewPager.setCurrentItem(currentQuestionIndex, true);
            setNextButtonEnabled(false);
            updateNextQuestionLabel();
        }

        if (currentQuestionIndex == quizQuestions.size() - 1) {
            nextQuestionCard.setVisibility(View.GONE);
            submitQuizBtn.setVisibility(View.VISIBLE);
        }
    }

    private void updateNextQuestionLabel() {
        if (currentQuestionIndex + 1 < quizQuestions.size()) {
            nextQuestionLabel.setText((currentQuestionIndex + 2) + ". Question");
        }
    }

    public void setNextButtonEnabled(boolean enabled) {
        nextQuestionBtn.setEnabled(enabled);
        nextQuestionBtn.setAlpha(enabled ? 1.0f : 0.7f);
    }

    public void onAnswerSubmitted(String selectedAnswer) {
        QuizQuestion current = quizQuestions.get(currentQuestionIndex);
        current.userSelectedAnswer = selectedAnswer;
        if (selectedAnswer.equals(current.correctAnswer)) {
            score++;
        }
    }

    private void submitQuiz() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("quizQuestions", quizQuestions);
        bundle.putInt("score", score);
        bundle.putInt("totalQuestions", quizQuestions.size());

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_quizFragment_to_resultFragment, bundle);
    }
}