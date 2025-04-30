package com.example.learnerapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learnerapp.fragment.InterestsFragment;
import com.example.learnerapp.fragment.RegisterFragment;
import com.example.learnerapp.model.User;

public class RegisterActivity extends BaseActivity implements RegisterFragment.RegisterListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show register fragment 
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
        }
    }

    @Override
    public void onRegisterComplete(User user) {
        // Move to interest fragment with user data
        InterestsFragment interestsFragment = InterestsFragment.newInstance(user);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, interestsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_register;
    }

    @Override
    protected String getToolbarTitle() {
        return "Register";
    }

    @Override
    protected boolean enableBackButton() {
        return true;
    }
}
