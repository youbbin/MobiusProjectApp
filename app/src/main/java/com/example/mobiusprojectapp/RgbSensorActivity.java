package com.example.mobiusprojectapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.spec.PSSParameterSpec;


public class RgbSensorActivity extends Activity {

    ImageButton back_button;
    TextView textview_r, textview_g, textview_b, textview_cct, textview_lux;
    String result = "";
    String[] parsedData;
    HttpURLConnection connection;
    HttpRequestGET thread;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_sensor);
        back_button = (ImageButton) findViewById(R.id.button_back_1); // 뒤로가기 버튼
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection.disconnect(); // Http 연결 해제
                finish(); // 액티비티 종료
            }
        });
        textview_r = (TextView) findViewById(R.id.textview_r);
        textview_g = (TextView) findViewById(R.id.textview_g);
        textview_b = (TextView) findViewById(R.id.textview_b);
        textview_cct = (TextView) findViewById(R.id.textview_cct);
        textview_lux = (TextView) findViewById(R.id.textview_lux);

        thread=new HttpRequestGET();
        thread.start();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.setRunningState(false);
    }
    // Http GET 요청해서 데이터 받아오기
    class HttpRequestGET extends Thread{
        private boolean isRunning = true;

        @Override
        public void run() {
            while(isRunning){
                try {
                    URL url = new URL("http://210.102.142.15:7579/Mobius/raspberry/cnt-rgb/latest");
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

                    DataParsing dataParsing=new DataParsing();
                    parsedData = dataParsing.getParsedData(6,result);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textview_r.setText("R :   "+parsedData[0]);
                            textview_g.setText("G :   "+parsedData[1]);
                            textview_b.setText("B :   "+parsedData[2]);
                            textview_cct.setText("CCT :   "+parsedData[4]);
                            textview_lux.setText("LUX :   "+parsedData[5]);

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
