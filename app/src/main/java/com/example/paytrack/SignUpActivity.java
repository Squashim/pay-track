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
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail;
    private EditText signupPassword;
    private EditText signupUsername;
    private EditText signupConfirmPassword;

    private TextView signupUsernameErr;
    private TextView  signupEmailErr;
    private TextView signupPasswordErr;
    private TextView signupConfirmPasswordErr;

    private void togglePasswordVisibility() {
        if (signupPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
            signupPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // Reveal password
            signupConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            signupPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); // Hide password
            signupConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        // Move the cursor to the end of the text for a better user experience
        signupPassword.setSelection(signupPassword.getText().length());
        signupConfirmPassword.setSelection(signupConfirmPassword.getText().length());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupUsername = findViewById(R.id.signup_username);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById(R.id.signup_confirm_password);

         signupUsernameErr = findViewById(R.id.signup_user_error);
         signupEmailErr = findViewById(R.id.signup_email_error);
         signupPasswordErr = findViewById(R.id.signup_pass_error);
         signupConfirmPasswordErr = findViewById(R.id.signup_confirm_pass_error);

        Button signupButton = findViewById(R.id.signup_button);
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);
        ImageButton passwordVisibilityButton = findViewById(R.id.passwordVisibilityButton);

        signupPassword.setTransformationMethod(new PasswordTransformationMethod());
        signupConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());

        signupButton.setOnClickListener(view -> login());

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

        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));

        //        Hiding errors text on user input
        signupUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0){
                    signupUsernameErr.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signupEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0){
                    signupEmailErr.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signupPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0){
                    signupPasswordErr.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signupConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0){
                    signupConfirmPasswordErr.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void login() {
        String username = signupUsername.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        String confirm_password = signupConfirmPassword.getText().toString().trim();
        boolean isValid = true;

        if (username.isEmpty()) {
            signupUsernameErr.setText(R.string.empty_username_err);
            isValid = false;
        }
        if (email.isEmpty()) {
            signupEmailErr.setText(R.string.empty_email_err);
            isValid = false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signupEmailErr.setText(R.string.invalid_email_err);
            isValid = false;
        }
        if (password.isEmpty()) {
            signupPasswordErr.setText(R.string.empty_pass_err);
            isValid = false;
        }
        if(password.length() < 8){
            signupPasswordErr.setText(R.string.short_pass_err);
            isValid = false;
        }
        if (confirm_password.isEmpty()) {
            signupConfirmPasswordErr.setText(R.string.must_be_filled);
            isValid = false;
        }
        if (!password.equals(confirm_password)) {
            signupConfirmPasswordErr.setText(R.string.pass_match_err);
            isValid = false;
        }

        if (isValid) {
            registerUser(username, email, password);
        }
    }

    private void registerUser(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task-> {
            if(task.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();
                if(user != null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                       if(task1.isSuccessful()){
                           saveUserToFirestore(user.getUid(), username, email);

                           Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                           finish();
                       }else {
                           Toast.makeText(SignUpActivity.this, "Failed to update display name", Toast.LENGTH_SHORT).show();
                       }
                    });
                }
            }else {
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    // User with the same email already exists
                    Toast.makeText(SignUpActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "SignUp Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserToFirestore(String userId, String username, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);

        userRef.set(user)
                .addOnSuccessListener(aVoid -> Log.d("SignUpActivity", "User document created in Firestore"))
                .addOnFailureListener(e -> Log.e("SignUpActivity", "Error creating user document in Firestore: " + e.getMessage()));
    }

}