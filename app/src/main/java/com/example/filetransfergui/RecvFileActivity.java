package com.example.filetransfergui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

public class RecvFileActivity extends AppCompatActivity {

    private TextView recvFileView;
    private TextView recvPercentView;
    private TextView recvFileNameView;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv_file);

        recvFileView = findViewById(R.id.recvFileView);
        recvPercentView = findViewById(R.id.recvPercentView);
        recvFileNameView = findViewById(R.id.recvFileNameView);

        Intent intent = getIntent();
        String ipAddress = intent.getStringExtra("ip");
        int port = intent.getIntExtra("port", 333);

        Thread recvThread = new Thread(() -> {
            try {
                Socket socket = new Socket(ipAddress, port);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                String downloadPath = CommonUtility.getDownloadPath((StorageManager) getSystemService(STORAGE_SERVICE));

                int numFiles = input.readByte();
                if (numFiles > 1) {
                    runOnUiThread(() -> recvFileView.setText("Receiving Files"));
                }

                for (int i = 0; i < numFiles; i++) {
                    String metadata = input.readUTF();
                    String[] metas = StringUtils.splitByWholeSeparatorPreserveAllTokens(metadata, "\\_()_/");
                    long fileSize = Long.parseLong(metas[1]);

                    FileUtility.setCurrentFileName(metas[0]);
                    File testFile = new File(downloadPath + FileUtility.getCurrentFileName());
                    while (testFile.exists()) {
                        FileUtility.renameFile();
                        testFile = new File(downloadPath + FileUtility.getCurrentFileName());
                    }

                    FileOutputStream output = new FileOutputStream(downloadPath + FileUtility.getCurrentFileName());
                    int fileNumber = i + 1;
                    runOnUiThread(() -> {
                        if (numFiles > 1) {
                            recvFileView.setText("Receiving File " + fileNumber + "/" + numFiles);
                        }
                        recvFileNameView.setText(FileUtility.getCurrentFileName() + FileUtility.formatFileSize((float) fileSize));
                    });

                    long sizeRemaining = fileSize;
                    int bytesRead;
                    byte[] buffer = new byte[socket.getReceiveBufferSize()];
                    while (sizeRemaining > 0 && (bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, sizeRemaining))) != -1) {
                        output.write(buffer, 0, bytesRead);
                        sizeRemaining -= bytesRead;

                        double temp = (fileSize-sizeRemaining)/(double) fileSize * 100;
                        runOnUiThread(() -> recvPercentView.setText(String.format(Locale.US,"%." + 1 + "f", temp) + "% Complete"));
                    }

                    output.close();
                }

                socket.close();
            } catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Failed to Connect", Toast.LENGTH_SHORT).show());
            } catch (SocketException socketException) {
                socketException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Connection Error", Toast.LENGTH_SHORT).show());
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Error Creating File", Toast.LENGTH_SHORT).show());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Error Receiving/Writing Data", Toast.LENGTH_SHORT).show());
            }

            SystemClock.sleep(3000);
            CommonUtility.returnHomeFromMainThread(this);
        });
        recvThread.start();
    }

    @Override
    public void onBackPressed() {
        //disable back button when receiving file
    }
}