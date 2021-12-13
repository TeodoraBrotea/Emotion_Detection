package com.example.emotiondetection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class intro_page extends AppCompatActivity{

  // private NavController navController;
    private Button btnDetectFaces, btnAppInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);

        btnDetectFaces = (Button) findViewById(R.id.btnDetectFaces);
        btnDetectFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetection();
            }
        });

        btnAppInfo = (Button) findViewById(R.id.btnAppInfo);
        btnAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfo();
            }
        });
    }

    private void openInfo() {
        Intent intent= new Intent(this, InfoPage.class);
        startActivity(intent);
    }

    private void openDetection() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}