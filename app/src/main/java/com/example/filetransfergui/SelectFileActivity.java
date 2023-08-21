package com.example.filetransfergui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectFileActivity extends AppCompatActivity {

    private TextView selectedFileView;

    private List<Uri> uris;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        selectedFileView = findViewById(R.id.selectedFileView);
        Button pickFilesButton = findViewById(R.id.selectFilesButton);
        Button sendFileButton = findViewById(R.id.sendFileButton);

        pickFilesButton.setOnClickListener(this::onClick);
        sendFileButton.setOnClickListener(this::onClick);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            uris = new ArrayList<>();
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                uris.add(data.getClipData().getItemAt(i).getUri());
                            }

                            if (uris.size() > 1) {
                                selectedFileView.setText("Multiple Files Selected");
                            } else {
                                selectedFileView.setText(FileUtility.getFileNameFromUri(uris.get(0), getContentResolver()));
                            }
                        } else {
                            Uri uri = data.getData();

                            uris = Collections.singletonList(uri);
                            selectedFileView.setText(FileUtility.getFileNameFromUri(uri, getContentResolver()));
                        }
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectFilesButton:
                Intent selectFilesIntent = new Intent(Intent.ACTION_GET_CONTENT);
                selectFilesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                selectFilesIntent.setType("*/*");
                activityResultLauncher.launch(selectFilesIntent);
                break;
            case R.id.sendFileButton:
                if (uris != null) {
                    Intent sendIntent = new Intent(getApplicationContext(), ViewIPActivity.class);
                    FileUtility.setUris(uris);
                    startActivity(sendIntent);
                }
                break;
        }
    }
}