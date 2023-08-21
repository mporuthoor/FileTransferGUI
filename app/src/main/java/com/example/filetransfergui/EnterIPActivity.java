package com.example.filetransfergui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.InetAddresses;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EnterIPActivity extends AppCompatActivity {

    private EditText ipAddressEditText;
    private EditText portNumberEditText;

    @Override
    @SuppressWarnings("deprecation")
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_ip);

        ipAddressEditText = findViewById(R.id.ipAddressEditText);
        portNumberEditText = findViewById(R.id.portNumberEditText);
        Button recvFileButton = findViewById(R.id.recvFileButton);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ipAddressEditText.setText(Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()));

        portNumberEditText.setText("15120");

        recvFileButton.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        if (view.getId() == R.id.recvFileButton) {
            String ipAddress = ipAddressEditText.getText().toString();
            int port = Integer.parseInt(portNumberEditText.getText().toString());

            if (!InetAddresses.isNumericAddress(ipAddress)) {
                Toast.makeText(getApplicationContext(), "IP Address is Invalid", Toast.LENGTH_SHORT).show();
                return;
            }
            if (port < 15120 || port > 15129) {
                Toast.makeText(getApplicationContext(), "Port Number is Invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getApplicationContext(), RecvFileActivity.class);
            intent.putExtra("ip", ipAddress);
            intent.putExtra("port", port);
            startActivity(intent);
        }
    }
}