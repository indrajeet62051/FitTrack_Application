package com.example.gymesticapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingActivity extends AppCompatActivity {

    private TextView tvResetPassword, tvDeleteAccount, tvAboutUs;
    private FirebaseAuth auth;

    ImageView back_profile;

    FirebaseAuth authProfile;
    StorageReference storageReference;
    FirebaseUser firebaseUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tvResetPassword = findViewById(R.id.txtChangePass);
        tvDeleteAccount = findViewById(R.id.txtDelete);
        tvAboutUs = findViewById(R.id.txtAboutUs);
        back_profile=findViewById(R.id.btn_back_profile);

        back_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        tvDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });
        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SettingActivity.this, AboutusActivity.class);
                startActivity(i);
            }
        });

    }
    private void deleteAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_account_dialog, null);
        builder.setView(dialogView);

        final TextInputEditText emailEditText = dialogView.findViewById(R.id.delemail);
        final TextInputEditText passwordEditText = dialogView.findViewById(R.id.delpassword);
        Button deleteButton = dialogView.findViewById(R.id.btn_delete);
        Button cancelButton=dialogView.findViewById(R.id.btn_cancel);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        deleteButton.setOnClickListener(v -> {

            //progress bar
            AlertDialog.Builder progressBuilder=new AlertDialog.Builder(SettingActivity.this);
            progressBuilder.setCancelable(false);
            progressBuilder.setView(R.layout.progress_layout);
            AlertDialog progressDialog= progressBuilder.create();
            if (progressDialog.getWindow()!=null){
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    if (!email.isEmpty() && !password.isEmpty()) {
                        reauthenticateAndDelete(email, password);
                        dialog.dismiss();
                        progressDialog.dismiss();// Optionally dismiss the dialog after deletion
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            },2000);

        });
    }
    private void reauthenticateAndDelete(final String email, final String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference folderRef = databaseReference.child("Users").child(userId);

        firebaseUser=mAuth.getCurrentUser();
        //get reference of storage
        storageReference= FirebaseStorage.getInstance().getReference("profile_images");
        Uri imageUri=firebaseUser.getPhotoUrl();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //delete user details from firebase realtime database
                        folderRef.removeValue();
                        //delete user image from firebase storage
                        deleteUserProfileImage(imageUri);
                        // Re-authentication successful, proceed to delete user
                        deleteUserAccount();

                    } else {
                        // Handle re-authentication failure
                        Toast.makeText(getApplicationContext(), "Re-authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Handle case where user is not authenticated
            Toast.makeText(getApplicationContext(), "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUserAccount() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null) {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // User account deleted successfully
                        Toast.makeText(getApplicationContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        // Optionally, finish the current activity
                        finish();
                    } else {
                        // Handle account deletion failure
                        Toast.makeText(getApplicationContext(), "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void deleteUserProfileImage(Uri imageUri) {

        FirebaseAuth authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser!=null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Get reference of Firebase Storage
            StorageReference fileReference=storage.getReference("profile_images").child(authProfile.getCurrentUser().getUid()+".jpg");

            //Delete the user profile
            fileReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.e("DeleteProfileImage","Profile image deleted successfully");
                    }else {
                        Log.e("DeleteProfileImageError","Failed to delete profile image:"+task.getException().getMessage());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("DeleteProfileImageError","Error deleting profile image: " + e.getMessage());
                }
            });
        }

    }

    private void checkPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.reset_password_dialog, null);
        EditText etEmail = dialogView.findViewById(R.id.edMail);
        EditText etOldPassword = dialogView.findViewById(R.id.edOldPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow()!=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();

        dialogView.findViewById(R.id.resetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                //progress bar
                AlertDialog.Builder progressBuilder=new AlertDialog.Builder(SettingActivity.this);
                progressBuilder.setCancelable(false);
                progressBuilder.setView(R.layout.progress_layout);
                AlertDialog progressDialog= progressBuilder.create();
                if (progressDialog.getWindow()!=null){
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                progressDialog.show();

                if (email.isEmpty() && oldPassword.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()){
                    Toast.makeText(SettingActivity.this, "Fill all details", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(SettingActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                    etEmail.setError("Valid email is required");
                    etEmail.requestFocus();
                    progressDialog.dismiss();
                    return;
                }
                if (oldPassword.isEmpty()){
                    etOldPassword.setError("Old Password Required");
                    progressDialog.dismiss();
                }
                if (newPassword.isEmpty()){
                    etNewPassword.setError("New password is empty");
                    progressDialog.dismiss();
                }

                if (newPassword.equals(confirmPassword)){
                    auth.signInWithEmailAndPassword(email,oldPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                auth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> passwordTask) {
                                        if (passwordTask.isSuccessful()){
                                            dialog.dismiss();
                                            progressDialog.dismiss();
                                            Toast.makeText(SettingActivity.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(SettingActivity.this, "Password reset failed", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(SettingActivity.this, "Old Password is incorrect", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }else {
                    Toast.makeText(SettingActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

        dialogView.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}