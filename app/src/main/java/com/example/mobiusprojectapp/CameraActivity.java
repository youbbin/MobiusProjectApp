package com.example.mobiusprojectapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class CameraActivity extends Activity {
    WebView webView;
    ImageButton back_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        back_button = (ImageButton) findViewById(R.id.button_back_3); // 뒤로가기 버튼
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 액티비티 종료
            }
        });

        webView=(WebView) findViewById(R.id.webView_camera);
        webView.loadUrl("http:192.168.100.201:3000/");
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
    }
}
