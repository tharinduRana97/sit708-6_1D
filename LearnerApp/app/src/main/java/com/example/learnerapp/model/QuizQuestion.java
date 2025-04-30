package com.example.learnerapp.model;

import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {
    public String question;
    public List<String> options;
    public String correctAnswer;
    public String userSelectedAnswer;

    // Constructor used when loading quiz
    public QuizQuestion(String question, List<String> options, String correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public QuizQuestion(String question, List<String> options, String correctAnswer, String userSelectedAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.userSelectedAnswer = userSelectedAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getUserSelectedAnswer() {
        return userSelectedAnswer;
    }

    public void setUserSelectedAnswer(String userSelectedAnswer) {
        this.userSelectedAnswer = userSelectedAnswer;
    }
}
