package com.example.mobiusprojectapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageButton rgb_sensor_button, led_light_button, camera_button, temp_humi_button, ireland_button;
    TextView rgb_sensor_textview, led_light_textview, camera_textview, temp_humi_textview, ireland_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // rgb sensor 버튼, 텍스트뷰
        rgb_sensor_button=(ImageButton) findViewById(R.id.button_rgb_sensor);
        rgb_sensor_textview=(TextView) findViewById(R.id.textview_rgb_sensor);

        // rgb sensor 버튼이나 텍스트뷰 클릭 시 RgbSensorActivity 액티비티 실행
        rgb_sensor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RgbSensorActivity.class);
                startActivity(intent);
            }
        });

        rgb_sensor_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RgbSensorActivity.class);
                startActivity(intent);
            }
        });

        // led light 버튼, 텍스트뷰
        led_light_button=(ImageButton) findViewById(R.id.button_led_light);
        led_light_textview=(TextView) findViewById(R.id.textview_led_light);

        // led light 버튼이나 텍스트뷰 클릭 시 LedLightActivity 액티비티 실행
        led_light_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LedLightActivity.class);
                startActivity(intent);
            }
        });

        led_light_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LedLightActivity.class);
                startActivity(intent);
            }
        });

        camera_button=(ImageButton) findViewById(R.id.button_camera);
        camera_textview=(TextView) findViewById(R.id.textview_camera);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
                startActivity(intent);
            }
        });

        camera_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),CameraActivity.class);
                startActivity(intent);
            }
        });

        temp_humi_button =(ImageButton) findViewById(R.id.button_temp_humi);
        temp_humi_textview = (TextView) findViewById(R.id.textview_temp_humi);

        temp_humi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),DHTSensorActivity.class);
                startActivity(intent);
            }
        });
        temp_humi_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),DHTSensorActivity.class);
                startActivity(intent);
            }
        });

        ireland_button = (ImageButton) findViewById(R.id.button_ireland);
        ireland_textview = (TextView) findViewById(R.id.textview_ireland);
        ireland_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),IrelandActivity.class);
                startActivity(intent);
            }
        });

        ireland_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),IrelandActivity.class);
                startActivity(intent);
            }
        });


    }
}