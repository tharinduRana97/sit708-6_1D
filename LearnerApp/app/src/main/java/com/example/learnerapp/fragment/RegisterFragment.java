package com.example.learnerapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.learnerapp.R;
import com.example.learnerapp.database.AppDatabase;
import com.example.learnerapp.model.User;

public class RegisterFragment extends Fragment {

    private EditText username, email, confirmEmail, password, confirmPassword, phoneNumber;
    private Button createAccountBtn;
    private AppDatabase db;
    private RegisterListener listener;

    public interface RegisterListener {
        void onRegisterComplete(User user);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RegisterListener) {
            listener = (RegisterListener) context;
        } else {
            throw new RuntimeException("Hosting activity must implement RegisterListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = AppDatabase.getInstance(requireContext());

        username = view.findViewById(R.id.usernameRegisterInput);
        email = view.findViewById(R.id.emailInput);
        confirmEmail = view.findViewById(R.id.confirmEmailInput);
        password = view.findViewById(R.id.passwordRegisterInput);
        confirmPassword = view.findViewById(R.id.confirmPasswordRegisterInput);
        phoneNumber = view.findViewById(R.id.phoneInput);
        createAccountBtn = view.findViewById(R.id.createAccountBtn);

        createAccountBtn.setOnClickListener(v -> {
            String usernameInput = username.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String confirmEmailInput = confirmEmail.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            String confirmPasswordInput = confirmPassword.getText().toString().trim();
            String phoneNumberInput = phoneNumber.getText().toString().trim();

            if (usernameInput.isEmpty() || emailInput.isEmpty() || confirmEmailInput.isEmpty()
                    || passwordInput.isEmpty() || confirmPasswordInput.isEmpty() || phoneNumberInput.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!emailInput.equals(confirmEmailInput)) {
                Toast.makeText(getContext(), "Emails do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordInput.equals(confirmPasswordInput)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPasswordStrong(passwordInput)) {
                Toast.makeText(getContext(), "Password must be at least 8 characters, contain 1 digit and 1 uppercase letter", Toast.LENGTH_LONG).show();
                return;
            }

            User user = new User(usernameInput, passwordInput, emailInput, phoneNumberInput);

            new Thread(() -> {
                User existingUser = db.userDao().findUserByUsernameOrEmail(user.getUsername(), user.getEmail());

                requireActivity().runOnUiThread(() -> {
                    if (existingUser != null) {
                        Toast.makeText(getContext(), "Username or email already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        listener.onRegisterComplete(user);
                    }
                });
            }).start();
        });
    }

    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;
        boolean hasDigit = false, hasUppercase = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isUpperCase(c)) hasUppercase = true;
        }
        return hasDigit && hasUppercase;
    }
}
