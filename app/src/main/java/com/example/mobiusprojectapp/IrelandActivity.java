package com.example.mobiusprojectapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IrelandActivity extends Activity {
        ImageButton back_button, led_button, fan_button;
        TextView textview_time, textview_temp, textview_humi, textview_co2, textview_illu, textview_gas;
        HttpURLConnection connection;
        HttpRequestGET_ireland thread;
        String result = "";
        String[] parsedData;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_ireland);
                back_button = (ImageButton) findViewById(R.id.button_back_5); // 뒤로가기 버튼
                back_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                connection.disconnect(); // Http 연결 해제
                                finish(); // 액티비티 종료
                        }
                });

                textview_time = (TextView) findViewById(R.id.textview_time);
                textview_temp = (TextView) findViewById(R.id.textview_temp);
                textview_humi = (TextView) findViewById(R.id.textview_humi);
                textview_co2 = (TextView) findViewById(R.id.textview_co2);
                textview_illu = (TextView) findViewById(R.id.textview_illu);
                textview_gas = (TextView) findViewById(R.id.textview_gas);

                led_button = (ImageButton) findViewById(R.id.button_ireland_led);
                led_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent intent=new Intent(getApplicationContext(),IrelandLedActivity.class);
                                startActivity(intent);
                                thread.setRunningState(false);
                                connection.disconnect(); // Http 연결 해제
                                finish(); // 액티비티 종료
                        }
                });

                fan_button = (ImageButton) findViewById(R.id.button_ireland_fan);
                fan_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent intent=new Intent(getApplicationContext(),IrelandFanActivity.class);
                                startActivity(intent);
                                thread.setRunningState(false);
                                connection.disconnect(); // Http 연결 해제
                                finish(); // 액티비티 종료
                        }
                });

                thread = new HttpRequestGET_ireland();
                thread.start();
        }



        @Override
        protected void onDestroy() {
                super.onDestroy();
                thread.setRunningState(false);
        }

        // Http GET 요청해서 데이터 받아오기
        class HttpRequestGET_ireland extends Thread{
                private boolean isRunning = true;

                @Override
                public void run() {
                        while(isRunning){
                                try {
                                        URL url = new URL("http://210.102.142.15:7579/Mobius/raspberry/cnt-sensor/latest");
                                        connection = (HttpURLConnection) url.openConnection();
                                        connection.setRequestMethod("GET"); //전송방식
                                        connection.setDoOutput(false);       //데이터를 쓸 지 설정
                                        connection.setDoInput(true);        //데이터를 읽어올지 설정
                                        connection.setRequestProperty("Accept","application/json");
                                        connection.setRequestProperty("X-M2M-RI","12345");
                                        connection.setRequestProperty("X-M2M-Origin","S20170717074825768bp2l");
                                        Log.d("log",">>>>>>>> GET 요청");
                                        InputStream is = connection.getInputStream();
                                        StringBuffer sb = new StringBuffer();
                                        BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                                        String inputLine;
                                        while((inputLine = br.readLine())!=null){
                                                sb.append(inputLine);
                                        }
                                        result = sb.toString();
                                        br.close();
                                        Log.d("Log",">>>>>>>> GET 완료 : "+result);

                                        DataParsing dataParsing = new DataParsing();
                                        parsedData = dataParsing.getParsedData(6,result);

                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                        textview_time.setText(parsedData[0]);
                                                        textview_temp.setText(parsedData[1]+" ℃");
                                                        textview_humi.setText(parsedData[2]+" %");
                                                        textview_co2.setText(parsedData[3]+" ppm");
                                                        textview_illu.setText(parsedData[4]+" lx");
                                                        textview_gas.setText(parsedData[5]);
                                                }
                                        });

                                        Thread.sleep(2000);
                                } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                        Log.d("Log",">>>>>>>> "+e.getMessage());
                                        break;
                                } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.d("Log",">>>>>>>> "+e.getMessage());
                                        break;
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                }
                        }
                }

                public void setRunningState(boolean state){
                        isRunning = state;
                }
        }
}
