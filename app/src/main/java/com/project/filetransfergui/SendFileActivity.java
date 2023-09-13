package com.project.filetransfergui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;

public class SendFileActivity extends AppCompatActivity {

    private TextView sendPercentView;
    private TextView sendFileView;
    private TextView sendFileNameView;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);

        sendFileView = findViewById(R.id.sendFileView);
        sendPercentView = findViewById(R.id.sendPercentView);
        sendFileNameView = findViewById(R.id.sendFileNameView);

        int numFiles = FileUtility.getUris().size();
        if (numFiles > 1) {
            sendFileView.setText("Sending Files");
        }

        Thread sendThread = new Thread(() -> {
            try {
                Socket socket = CommonUtility.getSocket();
                DataOutputStream output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                output.writeByte(numFiles);
                output.flush();

                for (int i = 0; i < numFiles; i++) {
                    ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(FileUtility.getUris().get(i), "r");
                    long fileSize = descriptor.getStatSize();
                    FileUtility.setCurrentFileNameFromUri(FileUtility.getUris().get(i), getContentResolver());

                    output.writeUTF(FileUtility.getCurrentFileName() + "\\_()_/" + fileSize);
                    output.flush();

                    FileInputStream input = new FileInputStream(descriptor.getFileDescriptor());
                    int fileNumber = i + 1;
                    runOnUiThread(() -> {
                        if (numFiles > 1) {
                            sendFileView.setText("Sending File " + fileNumber + "/" + numFiles);
                        }
                        sendFileNameView.setText(FileUtility.getCurrentFileName() + FileUtility.formatFileSize((float) fileSize));
                    });

                    long sizeRemaining = fileSize;
                    int bytesRead;
                    byte[] buffer = new byte[socket.getSendBufferSize()];
                    while (sizeRemaining > 0 && (bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, sizeRemaining))) != -1) {
                        output.write(buffer, 0, bytesRead);
                        sizeRemaining -= bytesRead;

                        double temp = (fileSize - sizeRemaining) / (double) fileSize * 100;
                        runOnUiThread(() -> sendPercentView.setText(String.format(Locale.US, "%." + 1 + "f", temp) + "% Complete"));
                    }

                    output.flush();
                    input.close();
                }

                socket.close();
            } catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to Connect", Toast.LENGTH_SHORT).show());
            } catch (SocketException socketException) {
                socketException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show());
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error Creating File", Toast.LENGTH_SHORT).show());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error Receiving/Writing Data", Toast.LENGTH_SHORT).show());
            }

            SystemClock.sleep(3000);
            CommonUtility.returnHomeFromMainThread(this);
        });
        sendThread.start();
    }

    @Override
    public void onBackPressed() {
        //disable back button while sending file
    }
}