package com.example.learnerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private FrameLayout baseContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base); // fixed base layout

        toolbar = findViewById(R.id.toolbar);
        baseContainer = findViewById(R.id.base_container);

        LayoutInflater.from(this).inflate(getLayoutResourceId(), baseContainer, true);

        setupToolbar(getToolbarTitle(), enableBackButton());
    }

    protected void setupToolbar(String title, boolean showBackButton) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title);
            if (showBackButton) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }
    }

    /*** Child activities must override these three methods ***/
    @LayoutRes
    protected abstract int getLayoutResourceId();

    protected abstract String getToolbarTitle();

    protected abstract boolean enableBackButton();
}
