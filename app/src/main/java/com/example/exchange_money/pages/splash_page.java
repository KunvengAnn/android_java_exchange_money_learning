package com.example.exchange_money.pages;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exchange_money.R;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.exchange_money.MainActivity;

public class splash_page extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_page);

        ImageView imageView = findViewById(R.id.splash_image);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        imageView.startAnimation(zoomIn);

        // Redirect to MainActivity after the animation ends
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash_page.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000); // Adjust the duration if needed
    }
}

