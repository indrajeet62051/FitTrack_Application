package com.example.gymesticapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UploadProfilePicActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ImageView addimg,back;
    ImageView userProfilePic;
    Button btnUploadImage;
    FirebaseAuth authProfile;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Uri imageUri;
    private static final int PIC_IMAGE_REQUEST=1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        progressBar=findViewById(R.id.progreessBarProfile);
        addimg=findViewById(R.id.addImage);
        userProfilePic=findViewById(R.id.userProfilePic);
        btnUploadImage=findViewById(R.id.btnUploadImg);
        back=findViewById(R.id.backProfile);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(UploadProfilePicActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("profile_images");
        imageUri=firebaseUser.getPhotoUrl();
        Glide.with(this).load(imageUri).placeholder(R.drawable.userprofilepic).into(userProfilePic);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageUri=data.getData();
        userProfilePic.setImageURI(imageUri);
    }
    private void uploadImage(){

        //progress dialog
        AlertDialog.Builder progressBuilder=new AlertDialog.Builder(UploadProfilePicActivity.this);
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
                if (imageUri!=null){
                    //save the img
                    StorageReference fileReference=storageReference.child(authProfile.getCurrentUser().getUid()+ ".jpg");

                    //upload profile image
                    fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri=uri;
                                    String imageUrl=uri.toString();
                                    firebaseUser=authProfile.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(downloadUri).build();
                                    firebaseUser.updateProfile(profileUpdates);
                                    updateProfileImageInDatabase(imageUrl);
                                    Toast.makeText(UploadProfilePicActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });

                            finish();
                            progressDialog.dismiss();
                        }
                    });
                }
                progressDialog.dismiss();
            }
        },8000);

    }

    private void updateProfileImageInDatabase(String imageUrl){
        DatabaseReference databaseRef= FirebaseDatabase.getInstance().getReference("Users").child(authProfile.getCurrentUser().getUid());
        HashMap<String,Object> updates=new HashMap<>();
        updates.put("profileImageUrl",imageUrl);

        databaseRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(UploadProfilePicActivity.this, "Profile Uploaded ", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(UploadProfilePicActivity.this, "Failed to save profile image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}