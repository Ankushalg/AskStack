package com.allstudio.askstack;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String SEARCH_Q = "searchQuery";
    private String searchQuery;
    private boolean isBackPressed = false;
    private EditText e1;
    private Button b1,b2;
    private SharedMemory shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shared = new SharedMemory(this);
        findViewIds();
        setUpListeners();
        Intent i = getIntent();
        if(i.hasExtra(SEARCH_Q)){
            searchQuery = i.getStringExtra(SEARCH_Q);
            e1.setText(searchQuery);
        }
        resetDefaults();
        checkPermissions();
    }

    private void resetDefaults() {
        shared.setNotTagged(null);
        shared.setTagged(null);
        shared.setSearchFilter(null);
    }

    private final int MY_PERMISSIONS_REQUEST = 101;
    private ArrayList<String> permissions = new ArrayList<>();

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.INTERNET)) {
            pInternet = false;
            showMyDialog(MainActivity.this, "Internet Permission Required", "Ask Stack can't work without Internet. So, Please Give the Internet Permission to use this app.", 0);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.ACCESS_NETWORK_STATE)) {
            pAccessNetworkState = false;
            showMyDialog(MainActivity.this, "Network Permission Required", "Ask Stack can't work without Network Access. So, Please Give the Network Permission to use this app.", 1);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            pReadStorage = false;
            showMyDialog(MainActivity.this, "Storage Permission Required", "Ask Stack can't work without Storage Access. So, Please Give the Storage Permission to use this app.", 2);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            pWriteStorage = false;
            showMyDialog(MainActivity.this, "Storage Permission Required", "Ask Stack can't work without Storage Access. So, Please Give the Storage Permission to use this app.", 3);
        }
        takePermissions();
    }

    private boolean pInternet = true,
            pAccessNetworkState = true,
            pWriteStorage = true,
            pReadStorage = true,
            isPermissionTaking = false;

    private void showMyDialog(Context ctx, String title, String message, final int permissionId){
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(permissionId == 0){
                            pInternet = true;
                        } else if (permissionId == 1){
                            pAccessNetworkState = true;
                        } else if (permissionId == 2){
                            pReadStorage = true;
                        } else if (permissionId == 3){
                            pWriteStorage = true;
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void takePermissions(){
        if (pInternet && pAccessNetworkState && pWriteStorage && pReadStorage){
            isPermissionTaking = false;
            if(permissions.size() > 0){
                String[] mStringArray = new String[permissions.size()];
                mStringArray = permissions.toArray(mStringArray);

                ActivityCompat.requestPermissions(MainActivity.this,
                        mStringArray,
                        MY_PERMISSIONS_REQUEST);
            }
        } else {
                new CountDownTimer(5000, 5000){
                    @Override
                    public void onTick(long millisUntilFinished) { }
                    @Override
                    public void onFinish() {
                        isPermissionTaking = true;
                        takePermissions();
                    }
                }.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        // permission was not granted, Do the Closing
                        ts("Some Permissions Not Granted. App is Closing");
                        finish();
                    }
                }
                // permission was granted, yay! Do the work
                ts("Permissions Granted.");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!isBackPressed){
            isBackPressed = true;
            ts("Press Back Button one more time to exit...");
            new CountDownTimer(2000, 2000){
                @Override
                public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() { isBackPressed = false;}
            }.start();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    private void setUpListeners() {
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionTaking){
                    ts("Please Give the required permissions to app. If Permissions not visible than kindly close from recent the app and start it again..");
                } else {
                    searchQuery = e1.getText().toString().trim();
                    if(searchQuery.length() > 0){
                        Intent i = new  Intent(MainActivity.this, SearchResultActivity.class);
                        i.putExtra(SEARCH_Q,searchQuery);
                        startActivity(i);
                        finish();
                    } else {
                        ts("Oops! Search box is empty...\nPlease write something related to your search in it.");
                    }
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ts("Welcome! If you have any Questions, Email me at ankushalg@gmail.com");
//                String email = "ankushalg@gmail.com";
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("*/*");
//                intent.putExtra(Intent.EXTRA_EMAIL, email);
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
            }
        });
    }

    private void findViewIds() {
        b1 = findViewById(R.id.m_b1);
        b2 = findViewById(R.id.m_b2);
        e1 = findViewById(R.id.m_e1);
        String credits = "Devloped By: Ankush Kumar";
        b2.setText(credits);
    }

    private void ts(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}