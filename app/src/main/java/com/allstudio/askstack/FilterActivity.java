package com.allstudio.askstack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class FilterActivity extends AppCompatActivity {
    private EditText e1,e2,e3,e4;
    private RadioButton r1, r2, r3, r4, r5;
    private Button save;
    private SharedMemory shared;
    private int cR = 1;
    private final String SEARCH_Q = "searchQuery";
    private String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        findViewIDs();
        setUpViews();
        setUpListeners();
        Intent i = getIntent();
        if(i.hasExtra(SEARCH_Q)){
            searchQuery = i.getStringExtra(SEARCH_Q);
        }
    }

    private void setUpListeners() {
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cR = 1;
                selectR(cR);
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cR = 2;
                selectR(cR);
            }
        });
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cR = 3;
                selectR(cR);
            }
        });
        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cR = 4;
                selectR(cR);
            }
        });
        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cR = 5;
                selectR(cR);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int len = Integer.parseInt(e4.getText().toString().trim());
                if(len > 0) {
                    if(e1.getText().toString().trim().length() > 0){
                        shared.setSearchFilter(e1.getText().toString().trim());
                    } else {
                        shared.setSearchFilter(null);
                    }
                    if(e2.getText().toString().trim().length() > 0){
                        shared.setTagged(e2.getText().toString().trim());
                    } else {
                        shared.setTagged(null);
                    }
                    if(e3.getText().toString().trim().length() > 0){
                        shared.setNotTagged(e3.getText().toString().trim());
                    } else {
                        shared.setNotTagged(null);
                    }
                    shared.setPageSize(len);
                    shared.setSearchingSite(cR);
                    Toast.makeText(FilterActivity.this, "Filter Added...", Toast.LENGTH_SHORT).show();
                    Intent i = new  Intent(FilterActivity.this, SearchResultActivity.class);
                    i.putExtra(SEARCH_Q,searchQuery);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(FilterActivity.this, "You have to choose page size between 1 and 99 only...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new  Intent(FilterActivity.this, SearchResultActivity.class);
        i.putExtra(SEARCH_Q,searchQuery);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

    private void setUpViews() {
        cR = shared.getSearchingSite();
        selectR(cR);
        e4.setText(String.valueOf(shared.getPageSize()));
        if(shared.getSearchFilter() != null){
            e1.setText(shared.getSearchFilter());
        }
        if(shared.getTagged() != null){
            e2.setText(shared.getTagged());
        }
        if(shared.getNotTagged() != null){
            e3.setText(shared.getNotTagged());
        }
    }

    private void findViewIDs() {
        shared = new SharedMemory(this);
        e1 = findViewById(R.id.af_e1);
        e2 = findViewById(R.id.af_e2);
        e3 = findViewById(R.id.af_e3);
        e4 = findViewById(R.id.af_e4);
        r1 = findViewById(R.id.af_r1);
        r2 = findViewById(R.id.af_r2);
        r3 = findViewById(R.id.af_r3);
        r4 = findViewById(R.id.af_r4);
        r5 = findViewById(R.id.af_r5);
        save = findViewById(R.id.af_save);
    }

    private void selectR(int r){
        switch (r) {
            case 1:
                r1.setChecked(true);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                break;
            case 2:
                r1.setChecked(false);
                r2.setChecked(true);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                break;
            case 3:
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(true);
                r4.setChecked(false);
                r5.setChecked(false);
                break;
            case 4:
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(true);
                r5.setChecked(false);
                break;
            case 5:
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(true);
        }
    }

}
