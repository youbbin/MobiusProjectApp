package com.example.mobiusprojectapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.BaseKeyListener;
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
import java.net.ProtocolException;
import java.net.URL;

public class DHTSensorActivity extends Activity {

    ImageButton back_button;
    TextView textview_temp, textview_humi;
    String result = "";
    String[] parsedData;
    HttpURLConnection connection;
    HttpRequestGET thread;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_humi_sensor);
        back_button = (ImageButton) findViewById(R.id.button_back_4);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection.disconnect(); // Http 연결 해제
                finish();
            }
        });

        textview_temp = (TextView) findViewById(R.id.textview_temp);
        textview_humi = (TextView) findViewById(R.id.textview_humi);

        thread = new HttpRequestGET();
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.setRunningState(false);
    }

    class HttpRequestGET extends Thread{
        private boolean isRunning = true;

        public void run(){
            while(isRunning){
                try{
                    URL url=new URL("http://210.102.142.15:7579/Mobius/raspberry/cnt-dht/latest");
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
                    parsedData = dataParsing.getParsedData(2,result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textview_temp.setText("Temperature :   "+parsedData[0]+"°C");
                            textview_humi.setText("Humidity :   "+parsedData[1]+"%");
                        }
                    });
                    Thread.sleep(2000);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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
