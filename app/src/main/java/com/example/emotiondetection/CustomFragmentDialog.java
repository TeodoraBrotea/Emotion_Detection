package com.example.emotiondetection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomFragmentDialog extends DialogFragment {
    TextView textView;
    String emotion;
    Boolean mask;
    Uri url;
    public CustomFragmentDialog(String emotion, Boolean mask, Uri url){
        this.emotion = emotion;
        this.mask = mask;
        this.url = url;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog, null);
        textView = (TextView) view.findViewById(R.id.dynamicText);
        textView.setText("Emotion: " + this.emotion);

        textView = (TextView) view.findViewById(R.id.textView7);
        textView.setText("Wear mask: " + this.mask);

        Button cancel = view.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
            }
        });

        Button listen = view.findViewById(R.id.btn_oky);
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
                startActivity(browserIntent);
            }
        });

        return view;
    }
}
