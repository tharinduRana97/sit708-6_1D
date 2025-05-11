package com.example.learnerapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnerapp.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends Fragment {
    private static final String ARG_INDEX = "index";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_OPTIONS = "options";

    private TextView questionText;
    private RadioGroup optionsGroup;
    private int questionIndex;
    private OnAnswerSelectedListener listener;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int index, String answer);
    }

    public static QuestionFragment newInstance(int index, String question, List<String> options, OnAnswerSelectedListener listener) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.listener = listener;

        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_QUESTION, question);
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(options));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        questionText = view.findViewById(R.id.questionText);
        optionsGroup = view.findViewById(R.id.optionsGroup);

        if (getArguments() != null) {
            questionIndex = getArguments().getInt(ARG_INDEX);
            questionText.setText(getArguments().getString(ARG_QUESTION));
            List<String> options = getArguments().getStringArrayList(ARG_OPTIONS);

            optionsGroup.removeAllViews();

            for (String option : options) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(option);
                radioButton.setTextColor(getResources().getColor(android.R.color.white));
                optionsGroup.addView(radioButton);
            }

            // Animate
            questionText.setAlpha(0f);
            questionText.animate().alpha(1f).setDuration(500).start();
            for (int i = 0; i < optionsGroup.getChildCount(); i++) {
                View child = optionsGroup.getChildAt(i);
                child.setAlpha(0f);
                child.animate()
                        .alpha(1f)
                        .setStartDelay(100 * i)
                        .setDuration(300)
                        .start();
            }
        }

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = group.findViewById(checkedId);
            if (selected != null && listener != null) {
                listener.onAnswerSelected(questionIndex, selected.getText().toString());
            }
        });

        return view;
    }
}
