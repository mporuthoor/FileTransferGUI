package com.project.filetransfergui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this::onClick);

        Button recvButton = findViewById(R.id.recvButton);
        recvButton.setOnClickListener(this::onClick);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onBackPressed() {
        //disable back button on home activity
    }

    @SuppressLint("NonConstantResourceId")
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                Intent sendIntent = new Intent(getApplicationContext(), SelectFileActivity.class);
                startActivity(sendIntent);
                break;
            case R.id.recvButton:
                Intent recvIntent = new Intent(getApplicationContext(), EnterIPActivity.class);
                startActivity(recvIntent);
                break;
        }
    }
}