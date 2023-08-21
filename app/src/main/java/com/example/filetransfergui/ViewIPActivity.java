package com.example.filetransfergui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ViewIPActivity extends AppCompatActivity {

    @Override
    @SuppressLint("SetTextI18n")
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ip);

        TextView ipAddressView = findViewById(R.id.ipAddressView);
        TextView portNumberView = findViewById(R.id.portNumberView);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ipAddressView.setText("Local IP: " + Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()));

        int port = new Random().nextInt(10) + 15120;
        portNumberView.setText("Port: " + port);

        Thread socketThread = new Thread(() -> {
            try {

                ServerSocket serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();

                CommonUtility.setSocket(socket);
                startSendFileActivityFromMainThread();
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Connection Failed", Toast.LENGTH_SHORT).show());
            }
        });
        socketThread.start();
    }

    private void startSendFileActivityFromMainThread() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Intent intent = new Intent (getApplicationContext(), SendFileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        //disable back button while waiting for connection
    }
}