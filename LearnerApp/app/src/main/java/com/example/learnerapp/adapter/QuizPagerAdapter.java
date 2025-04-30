package com.example.learnerapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.learnerapp.fragment.QuestionFragment;
import com.example.learnerapp.model.QuizQuestion;

import java.util.List;

public class QuizPagerAdapter extends FragmentStateAdapter {
    private final List<QuizQuestion> questions;
    private final QuestionFragment.OnAnswerSelectedListener listener;

    public QuizPagerAdapter(@NonNull FragmentActivity fa, List<QuizQuestion> questions,
                            QuestionFragment.OnAnswerSelectedListener listener) {
        super(fa);
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        QuizQuestion q = questions.get(position);
        return QuestionFragment.newInstance(position, q.question, q.options, listener);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
