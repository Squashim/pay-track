package com.example.paytrack;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;
    private TextView loginError;
    private TextView passError;


    private void togglePasswordVisibility() {
        if (loginPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
            loginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // Reveal password
        } else {
            loginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Hide password

        }

        // Move the cursor to the end of the text for a better user experience
        loginPassword.setSelection(loginPassword.getText().length());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupRedirectText = findViewById(R.id.signup_redirect);
        ImageButton passwordVisibilityButton = findViewById(R.id.passwordVisibilityButton);
        loginError = findViewById(R.id.login_error);
        passError = findViewById(R.id.pass_error);

//        Hiding login error text on user input
        loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0){
                    loginError.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        Hiding password error text on user input
        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.length() != 0){
                    passError.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        loginButton.setOnClickListener(view -> {
            String email = loginEmail.getText().toString();
            String pass = loginPassword.getText().toString();
            boolean isValid = false;

            if (email.isEmpty()) {
                loginError.setText(R.string.empty_email_err);
                if (pass.isEmpty()) {
                    passError.setText(R.string.empty_pass_err);
                } else if (pass.length() < 8) {
                    passError.setText(R.string.short_pass_err);
                }
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginError.setText(R.string.invalid_email_err);
                if (pass.isEmpty()) {
                    passError.setText(R.string.empty_pass_err);
                } else if (pass.length() < 8) {
                    passError.setText(R.string.short_pass_err);
                }
            } else if (pass.isEmpty()) {
                passError.setText(R.string.empty_pass_err);
            } else if (pass.length() < 8) {
                passError.setText(R.string.short_pass_err);
            } else {
                isValid = true;
            }

            if(isValid){
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(authResult -> {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }).addOnFailureListener(authResult -> Toast.makeText(LoginActivity.this, "Authentication failure!", Toast.LENGTH_SHORT).show());
            }

        });
        passwordVisibilityButton.setOnClickListener(view -> {
            Drawable.ConstantState currentDrawable = passwordVisibilityButton.getDrawable().getConstantState();
            @SuppressLint("UseCompatLoadingForDrawables") Drawable.ConstantState closedEyeDrawable = getDrawable(R.drawable.baseline_closedeye_24).getConstantState();
            togglePasswordVisibility();
            if(currentDrawable!=closedEyeDrawable){
                passwordVisibilityButton.setImageResource(R.drawable.baseline_closedeye_24);
            }else {
                passwordVisibilityButton.setImageResource(R.drawable.baseline_openeye_24);
            }
        });

        signupRedirectText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }
}
