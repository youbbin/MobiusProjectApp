package com.example.mobiusprojectapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IrelandLedActivity extends Activity {
    ImageButton backButton, powerButtonRed, powerButtonBlue;
    Button setButtonRed, setButtonBlue;
    NumberPicker numberPickerRed, numberPickerBlue;
    Integer levelRed, levelBlue;
    Boolean powerRed = false;
    Boolean powerBlue = false;
    String packet=""; // packet : led종류(1: red, 2: blue, 3: fan)/제어종류(on/off: 1, 세기 조절: 2)/제어값(on: 1, off: 2, 세기: 1~10)
    HttpURLConnection connection_post;
    HttpURLConnection connection_get;
    HttpRequestGET_IrelandLed thread_get;
    String result = "";
    String[] parsedData;
    TextView textview_power_red, textview_power_blue, textview_level_red, textview_level_blue;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ireland_led);
        context=this;

        textview_power_red = (TextView) findViewById(R.id.textview_red_power);
        textview_power_blue = (TextView) findViewById(R.id.textview_blue_power);
        textview_level_red = (TextView) findViewById(R.id.textview_red_level);
        textview_level_blue = (TextView) findViewById(R.id.textview_blue_level);

        backButton = (ImageButton) findViewById(R.id.button_back_ireland_led); // 뒤로가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection_post.disconnect(); // Http 연결 해제
                connection_get.disconnect();
                finish(); // 액티비티 종료
            }
        });

        powerButtonRed = (ImageButton) findViewById(R.id.button_power_red);
        powerButtonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(powerRed){
                    powerRed = false;
                    packet="1/1/2";
                    Log.d("Log","Red LED 전원 버튼 누름(Off)");
                    Toast.makeText(context,"Red LED 전원을 껐습니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    powerRed = true;
                    packet="1/1/1";
                    Log.d("Log","Red LED 전원 버튼 누름(On)");
                    Toast.makeText(context,"Red LED 전원을 켰습니다.",Toast.LENGTH_SHORT).show();
                }

                Runnable requestHttpPOST = new HttpRequestPOST_IrelandLed(packet); // 전송
                new Thread(requestHttpPOST).start();
            }
        });

        powerButtonBlue = (ImageButton) findViewById(R.id.button_power_blue);
        powerButtonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(powerBlue){
                    powerBlue = false;
                    packet="2/1/2";
                    Log.d("Log","Blue LED 전원 버튼 누름(Off)");
                    Toast.makeText(context,"Blue LED 전원을 껐습니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    powerBlue = true;
                    packet="2/1/1";
                    Log.d("Log","Blue LED 전원 버튼 누름(On)");
                    Toast.makeText(context,"Blue LED 전원을 켰습니다.",Toast.LENGTH_SHORT).show();
                }

                Runnable requestHttpPOST = new HttpRequestPOST_IrelandLed(packet); // 전송
                new Thread(requestHttpPOST).start();
            }
        });


        numberPickerRed = (NumberPicker) findViewById(R.id.numberPicker_red_led);
        numberPickerRed.setMax(10);
        numberPickerRed.setMin(1);
        numberPickerRed.setValue(0);

        numberPickerBlue = (NumberPicker) findViewById(R.id.numberPicker_blue_led);
        numberPickerBlue.setMax(10);
        numberPickerBlue.setMin(1);
        numberPickerBlue.setValue(0);

        setButtonRed = (Button) findViewById(R.id.button_set_red);
        setButtonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelRed = numberPickerRed.getValue();
                textview_level_red.setText((levelRed/10)+"/10");
                packet="1/2/"+levelRed;
                Runnable requestHttpPOST = new HttpRequestPOST_IrelandLed(packet); // 전송
                new Thread(requestHttpPOST).start();
            }
        });

        setButtonBlue = (Button) findViewById(R.id.button_set_blue);
        setButtonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                levelBlue = numberPickerBlue.getValue();
                textview_level_blue.setText((levelBlue/10)+"/10");
                packet="2/2/"+levelBlue;
                Runnable requestHttpPOST = new HttpRequestPOST_IrelandLed(packet); // 전송
                new Thread(requestHttpPOST).start();
            }
        });



        thread_get = new HttpRequestGET_IrelandLed();
        thread_get.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        thread_get.setRunningState(false);
    }


    public class HttpRequestPOST_IrelandLed implements Runnable{ // HTTP POST 요청 스레드
        String packetString;
        public HttpRequestPOST_IrelandLed(String packetString){
            this.packetString=packetString;
        }
        public void run(){
            StringBuffer sb=new StringBuffer();
            try {
                URL url=new URL("http://210.102.142.15:7579/Mobius/raspberry/cnt-control");
                connection_post = (HttpURLConnection) url.openConnection();
                connection_post.setRequestMethod("POST");
                connection_post.setDoOutput(true);
                connection_post.setDoInput(true);
                connection_post.setUseCaches(false);
                connection_post.setRequestProperty("Accept","application/xml");
                connection_post.setRequestProperty("X-M2M-RI","123sdfgd45");
                connection_post.setRequestProperty("X-M2M-Origin","S");
                connection_post.setRequestProperty("Content-Type","application/vnd.onem2m-res+xml; ty=4");

                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<m2m:cin\n    " + "xmlns:m2m=\"http://www.onem2m.org/xml/protocols\" \n    " +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n    " +
                        "<con>"+packetString+ "</con>\n</m2m:cin>");
                OutputStream os=connection_post.getOutputStream();
                os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                BufferedReader br=new BufferedReader(new InputStreamReader(connection_post.getInputStream()));
                String line;
                String page="";
                while((line = br.readLine())!=null){
                    page += line;
                }
                Log.d("Log","Code >> "+connection_post.getResponseCode());
                Log.d("Log",page);


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("Log","ddddddddddddddddddd");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Log","eeeeeeeeeeeeeeeeee");
            }
        }

    }

    class HttpRequestGET_IrelandLed extends Thread{
        private boolean isRunning = true;

        @Override
        public void run() {
            while(isRunning){
                try {
                    URL url = new URL("http://210.102.142.15:7579/Mobius/raspberry/cnt-ledfan/latest");
                    connection_get = (HttpURLConnection) url.openConnection();
                    connection_get.setRequestMethod("GET"); //전송방식
                    connection_get.setDoOutput(false);       //데이터를 쓸 지 설정
                    connection_get.setDoInput(true);        //데이터를 읽어올지 설정
                    connection_get.setRequestProperty("Accept","application/json");
                    connection_get.setRequestProperty("X-M2M-RI","12345");
                    connection_get.setRequestProperty("X-M2M-Origin","S20170717074825768bp2l");
                    Log.d("log",">>>>>>>> GET 요청");
                    InputStream is = connection_get.getInputStream();
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
                            textview_level_red.setText((Integer.parseInt(parsedData[0])/10)+"/10");
                            textview_level_blue.setText((Integer.parseInt(parsedData[1])/10)+"/10");
                            if(parsedData[3].equals("1")){
                                textview_power_red.setText("ON");
                                powerRed = true;
                            }else{
                                textview_power_red.setText("OFF");
                                powerRed = false;
                            }
                            if(parsedData[4].equals("1")){
                                textview_power_blue.setText("ON");
                                powerBlue = true;
                            }else{
                                textview_power_blue.setText("OFF");
                                powerBlue = false;
                            }

                        }
                    });

                    Thread.sleep(1000);
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
