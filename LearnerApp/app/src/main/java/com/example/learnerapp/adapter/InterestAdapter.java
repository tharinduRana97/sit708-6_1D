package com.example.learnerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnerapp.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {

    private final List<String> interests;
    private final Set<String> selectedInterests = new HashSet<>();
    private final int maxSelection;
    private final Context context;

    public InterestAdapter(Context context, List<String> interests, int maxSelection) {
        this.context = context;
        this.interests = interests;
        this.maxSelection = maxSelection;
    }

    public Set<String> getSelectedInterests() {
        return selectedInterests;
    }

    @NonNull
    @Override
    public InterestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_interest_button, parent, false);
        return new InterestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestViewHolder holder, int position) {
        String interest = interests.get(position);
        holder.button.setText(interest);
        updateButtonBackground(holder, interest);

        holder.button.setOnClickListener(v -> {
            if (selectedInterests.contains(interest)) {
                selectedInterests.remove(interest);
            } else {
                if (selectedInterests.size() < maxSelection) {
                    selectedInterests.add(interest);
                }
            }
            notifyItemChanged(position); // refresh the specific item
        });
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    private void updateButtonBackground(InterestViewHolder holder, String interest) {
        if (selectedInterests.contains(interest)) {
            holder.button.setBackgroundResource(R.drawable.interest_button_background_selected);
            holder.button.setTextColor(context.getColor(android.R.color.black));
        } else {
            holder.button.setBackgroundResource(R.drawable.interest_button_background);
            holder.button.setTextColor(context.getColor(android.R.color.white));
        }
    }

    public static class InterestViewHolder extends RecyclerView.ViewHolder {
        public final TextView button;

        public InterestViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.interest_button);
        }
    }
}
