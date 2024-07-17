package com.example.bpmonitorapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SecondActivity extends AppCompatActivity {

    private TextView instructionText;
    private TextView warningText;
    private TextView bpLevelText;
    private TextView countdownText;
    private ImageView bpIcon;
    private Handler handler;
    private Runnable runnable;
    private int countdownSeconds = 20; // Set to 20 seconds
    private boolean isCountingDown = false;
    private Animation fadeInTop;
    private Animation fadeIn;
    private Animation fadeInIcon;
    private Animation heartbeatAnimation;
    private Animation findInAnimation; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        instructionText = findViewById(R.id.instructionText);
        warningText = findViewById(R.id.warningText);
        bpLevelText = findViewById(R.id.bpLevelText);
        countdownText = findViewById(R.id.countdownText);
        bpIcon = findViewById(R.id.ic_bp);
        handler = new Handler();

        // Hide the BP icon initially
        bpIcon.setVisibility(View.GONE);

        // Load animations
        fadeInTop = AnimationUtils.loadAnimation(this, R.anim.fade_in_top);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInIcon = AnimationUtils.loadAnimation(this, R.anim.fade_in_icon);
        heartbeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heartbeat);
        findInAnimation = AnimationUtils.loadAnimation(this, R.anim.find_in_animation); // Load the find_in animation

        // Initialize FingerprintManager
        ImageView fingerprintIcon = findViewById(R.id.fingerprintIcon);
        fingerprintIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isCountingDown) {
                            if (hasFingerprintPermission()) {
                                startCountdown();
                                fingerprintIcon.startAnimation(heartbeatAnimation);
                            } else {
                                // Request permission
                                ActivityCompat.requestPermissions(SecondActivity.this,
                                        new String[]{Manifest.permission.USE_FINGERPRINT}, 100);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        stopCountdown();
                        fingerprintIcon.clearAnimation();
                        return true;
                }
                return false;
            }
        });

        Button instructionsButton = findViewById(R.id.instructionsButton);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructions();
                instructionsButton.startAnimation(findInAnimation); // Start find_in animation on click
            }
        });

        // Start animations for text and icons
        startInitialAnimations();
    }

    private void startInitialAnimations() {
        instructionText.startAnimation(fadeInTop);
        warningText.startAnimation(fadeIn);
        countdownText.startAnimation(fadeIn); // Ensure countdown text animates as well
        bpIcon.startAnimation(fadeInIcon); // Animate BP icon if visible initially
        ImageView fingerprintIcon = findViewById(R.id.fingerprintIcon);
        fingerprintIcon.startAnimation(fadeInIcon);
    }

    private boolean hasFingerprintPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCountdown() {
        if (!isCountingDown) {
            isCountingDown = true;
            instructionText.setText("Measuring BP level...");
            countdownText.setVisibility(View.VISIBLE); // Show countdown text
            handler.postDelayed(updateCountdown, 1000);
        }
    }

    private void stopCountdown() {
        isCountingDown = false;
        handler.removeCallbacks(updateCountdown);
        instructionText.setText("Please touch and hold the fingerprint icon");
        countdownText.setText("");
        countdownText.setVisibility(View.GONE); // Hide countdown text
        bpLevelText.setText(""); // Clear BP level text
        bpIcon.setVisibility(View.GONE); // Hide BP icon
        countdownSeconds = 20; // Reset countdown seconds
    }

    private Runnable updateCountdown = new Runnable() {
        @Override
        public void run() {
            if (countdownSeconds > 0) {
                countdownText.setText("Time left " + countdownSeconds + "s");
                countdownSeconds--;
                handler.postDelayed(this, 1000);
            } else {
                measureBP();
            }
        }
    };

    private void measureBP() {
        instructionText.setText("Measuring BP...");
        warningText.setText("");
        bpLevelText.setText("");

        runnable = new Runnable() {
            @Override
            public void run() {
                int systolic = 120 + (int) (Math.random() * 30);
                int diastolic = 80 + (int) (Math.random() * 20);
                bpLevelText.setText("BP Level " + systolic + "/" + diastolic + " mmHg");
                bpIcon.setVisibility(View.VISIBLE); // Show BP icon
                countdownText.setVisibility(View.GONE); // Hide countdown text
                instructionText.setText("Please touch and hold the fingerprint icon");
            }
        };

        handler.postDelayed(runnable, 500); // Simulate measuring time (1 second)
    }

    public void showInstructions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.instructions_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button okButton = dialogView.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

        // Optionally apply animations to dialog elements
        TextView instructionTitle = dialogView.findViewById(R.id.instructionTitle);
        TextView instructionContent = dialogView.findViewById(R.id.instructionContent);
        instructionTitle.startAnimation(fadeInTop);
        instructionContent.startAnimation(fadeIn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}
