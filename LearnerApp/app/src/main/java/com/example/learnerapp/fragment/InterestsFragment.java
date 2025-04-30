package com.example.learnerapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnerapp.LoginActivity;
import com.example.learnerapp.R;
import com.example.learnerapp.adapter.InterestAdapter;
import com.example.learnerapp.database.AppDatabase;
import com.example.learnerapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class InterestsFragment extends Fragment {
    private static final String ARG_USER = "user_data";
    private static final int MAX_SELECTION = 10;
    private AppDatabase db;
    private RecyclerView interestsRecycler;
    private Button nextButton;
    private InterestAdapter adapter;
    private User user;

    private final String[] interests = {
            "Algorithms", "Data Science", "Web Design", "Software Test",
            "Cybersecurity", "Android Apps", "Cloud Systems", "Game Design",
            "DevOps Tools", "SQL Databases", "AI Ethics", "Blockchain",
            "Embedded Sys", "Networking", "UI Design", "System Design"
    };

    public static InterestsFragment newInstance(User user) {
        InterestsFragment fragment = new InterestsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interests, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USER);
        }

        db = AppDatabase.getInstance(requireContext());

        interestsRecycler = view.findViewById(R.id.interestsRecycler);
        nextButton = view.findViewById(R.id.nextButton);

        adapter = new InterestAdapter(requireContext(), List.of(interests), MAX_SELECTION);
        interestsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        interestsRecycler.setAdapter(adapter);

        nextButton.setOnClickListener(v -> {
            user.setInterests(new ArrayList<>(adapter.getSelectedInterests()));
            db.userDao().insert(user);
            Toast.makeText(getContext(), "Account created!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}
