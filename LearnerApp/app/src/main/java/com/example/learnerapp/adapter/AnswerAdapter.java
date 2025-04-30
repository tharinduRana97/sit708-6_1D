package com.example.learnerapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.learnerapp.R;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private final List<String> questions;
    private final List<String> answers;

    public AnswerAdapter(List<String> questions, List<String> answers) {
        this.questions = questions;
        this.answers = answers;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer_card, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        holder.questionText.setText((position + 1) + ". " + questions.get(position));
        holder.answerText.setText(answers.get(position));
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, answerText;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            answerText = itemView.findViewById(R.id.answerText);
        }
    }
}
