package com.example.bpmonitorapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button getStartedButton;
    private ImageView[] socialIcons;
    private ImageView localImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        welcomeText = findViewById(R.id.welcome_text);
        getStartedButton = findViewById(R.id.getstart);
        localImage = findViewById(R.id.imageView2);
        LinearLayout layout = findViewById(R.id.layoutLinear);

        // Load animations
        Animation fadeInTop = AnimationUtils.loadAnimation(this, R.anim.fade_in_top);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeInIcon = AnimationUtils.loadAnimation(this, R.anim.fade_in_icon);
        Animation backgroundAnimation = AnimationUtils.loadAnimation(this, R.anim.background_animation);

        // Start background animation
        layout.startAnimation(backgroundAnimation);

        // Start the welcome text animation
        welcomeText.startAnimation(fadeInTop);

        // Show local image with animation
        localImage.setVisibility(View.VISIBLE);
        localImage.startAnimation(fadeIn);

        // Delay the button animation to start after welcome text
        welcomeText.postDelayed(() -> {
            getStartedButton.startAnimation(fadeIn);
        }, 600); // Match this with fade_in_top duration

        // Initialize and animate social media icons
        socialIcons = new ImageView[]{
                findViewById(R.id.ic_instagram),
                findViewById(R.id.ic_linkedin),
                findViewById(R.id.ic_facebook)
        };

        for (int i = 0; i < socialIcons.length; i++) {
            final int index = i;
            socialIcons[index].setVisibility(View.INVISIBLE); // Initially hide
            socialIcons[index].postDelayed(() -> {
                socialIcons[index].setVisibility(View.VISIBLE);
                socialIcons[index].startAnimation(fadeInIcon);
            }, 800 + (index * 200)); // Staggered animation
        }

        // Set click listeners for social media icons
        socialIcons[0].setOnClickListener(v -> openLink(getString(R.string.instagram_link)));
        socialIcons[1].setOnClickListener(v -> openLink(getString(R.string.linkedin_link)));
        socialIcons[2].setOnClickListener(v -> openLink(getString(R.string.facebook_link)));

        // Set click listener for the button
        getStartedButton.setOnClickListener(this::startMainActivity);
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void startMainActivity(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
        finish(); // Remove MainActivity from the back stack
    }
}
