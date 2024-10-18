package com.example.gymesticapp.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gymesticapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Random;

public class PlanFragment extends Fragment {

    private TextView navMembershipName, selectedTime, title, icardname,ogp;
    private Button btnGold, btnSilver, btnPlatinum;
    private CardView planLayout;
    private RadioGroup radioGroupBatchTime;
    private AlertDialog.Builder dialogBuilder;
    private View dialogView;
    private Button btnBuy;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private CardView cardplatinum, cardgold, cardsilver;
    private ProgressBar progressBar;
    ShapeableImageView icardimage;

    private ImageView qrCodeImageView;

    public PlanFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plan, container, false);
        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        // Initialize views
        navMembershipName = view.findViewById(R.id.navMembership);
        selectedTime = view.findViewById(R.id.selectedTime);
        btnGold = view.findViewById(R.id.btnGold);
        btnSilver = view.findViewById(R.id.btnSilver);
        btnPlatinum = view.findViewById(R.id.btnPlatinum);
        planLayout = view.findViewById(R.id.plan);
        cardplatinum = view.findViewById(R.id.cardplatinum);
        cardgold = view.findViewById(R.id.cardGold);
        cardsilver = view.findViewById(R.id.cardSilver);
        progressBar = view.findViewById(R.id.progressBar);

        icardimage = view.findViewById(R.id.icardimage);//user profile
        icardname = view.findViewById(R.id.iCardName);//username

        qrCodeImageView = view.findViewById(R.id.qrCodeImageView); // ImageView for QR Code
        ogp=view.findViewById(R.id.Ogp);

        // Initially hide the plan details and scan status
        planLayout.setVisibility(View.GONE);

        String uid = firebaseAuth.getCurrentUser().getUid();
        loadMembershipData();

        btnGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMembership("Gold");
            }
        });
        btnSilver.setOnClickListener(v -> selectMembership("Silver"));
        btnPlatinum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMembership("Platinum");
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        cardgold.setVisibility(View.GONE);
        cardsilver.setVisibility(View.GONE);
        cardplatinum.setVisibility(View.GONE);

        return view;
    }
    private void loadMembershipData() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded() && getActivity() != null) {
                    if (snapshot.exists()) {

                        String username = snapshot.child("fullName").getValue(String.class);
                        icardname.setText(username);

                        //set user profile image
                        FirebaseAuth authProfile=FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser=authProfile.getCurrentUser();
                        Uri uri=firebaseUser.getPhotoUrl();
                        //image set
                        Glide.with(PlanFragment.this).load(uri).placeholder(R.drawable.userprofilepic).into(icardimage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Fetch membership data from Firebase
        databaseReference.child("Users").child(userId).child("membership").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String membershipType = snapshot.child("plan").getValue(String.class);
                    String batchTime = snapshot.child("batchTime").getValue(String.class);
                    String uniqueMembershipID = snapshot.child("uniqueMembershipID").getValue(String.class);

                    if (membershipType != null && batchTime != null) {
                        navMembershipName.setText(membershipType + " Member");
                        selectedTime.setText(batchTime);

                        if (uniqueMembershipID != null) {
                            Bitmap qrCodeBitmap = generateQRCode(uniqueMembershipID);
                            qrCodeImageView.setImageBitmap(qrCodeBitmap);
                        }

                        planLayout.setVisibility(View.VISIBLE);
                        cardplatinum.setVisibility(View.GONE);
                        cardgold.setVisibility(View.GONE);
                        cardsilver.setVisibility(View.GONE);
                        ogp.setVisibility(View.GONE);


                    } else {
                        cardplatinum.setVisibility(View.VISIBLE);
                        cardgold.setVisibility(View.VISIBLE);
                        cardsilver.setVisibility(View.VISIBLE);
                        ogp.setVisibility(View.VISIBLE);
                    }
                } else {
                    cardplatinum.setVisibility(View.VISIBLE);
                    cardgold.setVisibility(View.VISIBLE);
                    cardsilver.setVisibility(View.VISIBLE);
                    ogp.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void selectMembership(String membershipType) {
        navMembershipName.setText(membershipType + " Member");
        showBatchTimeDialog(membershipType);
    }

    private void showBatchTimeDialog(String membershipType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        dialogView = inflater.inflate(R.layout.membershipdialogue, null);

        radioGroupBatchTime = dialogView.findViewById(R.id.radioGroupBatchTime);
        btnBuy = dialogView.findViewById(R.id.btnBuy);
        title = dialogView.findViewById(R.id.dialogTitle);
        title.setText(membershipType);

        dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(dialogView);

        AlertDialog dialog = dialogBuilder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedBatchId = radioGroupBatchTime.getCheckedRadioButtonId();
                if (selectedBatchId == -1) {
                    Toast.makeText(getContext(), "Please select a batch time", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedBatchButton = dialogView.findViewById(selectedBatchId);
                    String selectedBatchTime = selectedBatchButton.getText().toString();
                    navMembershipName.setText(membershipType + " Member");
                    selectedTime.setText(selectedBatchTime);
                    saveMembershipToFirebase(membershipType, selectedBatchTime);
                    Toast.makeText(requireActivity(), "Membership successfully purchased.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    planLayout.setVisibility(View.VISIBLE);
                    cardplatinum.setVisibility(View.GONE);
                    cardgold.setVisibility(View.GONE);
                    cardsilver.setVisibility(View.GONE);
                    ogp.setVisibility(View.GONE);

                }
            }
        });
    }

    private void saveMembershipToFirebase(String membershipType, String batchTime) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String uniqueMembershipID = "MEM" + new Random().nextInt(1000) + "-" + userId.substring(0, 5);

        databaseReference.child("Users").child(userId).child("membership")
                .child("plan").setValue(membershipType);

        databaseReference.child("Users").child(userId).child("membership")
                .child("batchTime").setValue(batchTime);

        databaseReference.child("Users").child(userId).child("membership")
                .child("uniqueMembershipID").setValue(uniqueMembershipID);

        Bitmap qrCodeBitmap = generateQRCode(uniqueMembershipID);
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
    }
    //QR CODE GENERATED
    private Bitmap generateQRCode(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}