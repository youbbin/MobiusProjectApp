package com.example.mobiusprojectapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LedLightActivity extends Activity {

    ImageButton back_button;
    private SeekBar seekbar_2700, seekbar_4000, seekbar_5000, seekbar_6500;
    private SeekBar[] seekbars;
    private TextView[] textNum;
    private Button button_set, button_on, button_off;
    String packet="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_light);

        back_button = (ImageButton) findViewById(R.id.button_back_2); // 뒤로가기 버튼
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 액티비티 종료
            }
        });

        seekbar_2700 = (SeekBar) findViewById(R.id.seekbar_2700);
        seekbar_4000 = (SeekBar) findViewById(R.id.seekbar_4000);
        seekbar_5000 = (SeekBar) findViewById(R.id.seekbar_5000);
        seekbar_6500 = (SeekBar) findViewById(R.id.seekbar_6500);
        seekbars = new SeekBar[4];
        seekbars[0] = seekbar_2700;
        seekbars[1] = seekbar_4000;
        seekbars[2] = seekbar_5000;
        seekbars[3] = seekbar_6500;
        textNum = new TextView[4];
        textNum[0] = (TextView) findViewById(R.id.textview_num1);
        textNum[1] = (TextView) findViewById(R.id.textview_num2);
        textNum[2] = (TextView) findViewById(R.id.textview_num3);
        textNum[3] = (TextView) findViewById(R.id.textview_num4);
        button_on = (Button) findViewById(R.id.button_on);
        button_off = (Button) findViewById(R.id.button_off);
        button_set = (Button) findViewById(R.id.button_set);

        // 시크바 이벤트
        for (int i = 0; i < seekbars.length; i++) {
            int index = i;
            seekbars[index].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textNum[index].setText(String.format("%03d", progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        /* 조명 점등 시 4개 채널 합 255 넘으면 안됨! */

        // On 버튼 이벤트
        button_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packet="0211FF3C3C3C3C03"; // 각 채널 세기 60으로 켜짐
                for(int i=0;i<seekbars.length;i++){
                    seekbars[i].setProgress(255);
                }
                Log.d("Packet",packet);
                Runnable requestHttpPOST = new HttpRequestPOST(packet);
                new Thread(requestHttpPOST).start();
            }
        });

        // Off 버튼 이벤트
        button_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packet="0211FF0000000003"; // 조명 끄기
                for(int i=0;i<seekbars.length;i++){
                    seekbars[i].setProgress(0);
                }
                Log.d("Packet",packet);
                Runnable requestHttpPOST = new HttpRequestPOST(packet);
                new Thread(requestHttpPOST).start();
            }
        });

        // Set 버튼 이벤트
        button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                packet = "0211FF";
                int sumPower = 0;
                for(int i=0;i<textNum.length;i++){
                    sumPower += Integer.parseInt((String)textNum[i].getText()); // 4채널 합 계산
                }
                if(sumPower > 255){ // 4채널 합이 255을 넘을 경우
                    Toast.makeText(LedLightActivity.this, "4채널 합이 255을 초과합니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    for(int i=0;i<textNum.length;i++){
                        String power = (String)textNum[i].getText();
                        String hex = String.format("%02x",Integer.parseInt(power));
                        packet += hex;
                    }
                    packet += "03";
                    Log.d("Packet",packet);
                    Runnable requestHttpPOST = new HttpRequestPOST(packet);
                    new Thread(requestHttpPOST).start();
                }
            }
        });
    }

    public class HttpRequestPOST implements Runnable{ // HTTP POST 요청 스레드
        String packetString;
        public HttpRequestPOST(String packetString){
            this.packetString=packetString;
        }
        public void run(){
            StringBuffer sb=new StringBuffer();
            try {
                URL url=new URL("http://210.102.142.15:7579/Mobius/raspberry/cnt-led");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Accept","application/xml");
                connection.setRequestProperty("X-M2M-RI","123sdfgd45");
                connection.setRequestProperty("X-M2M-Origin","S");
                connection.setRequestProperty("Content-Type","application/vnd.onem2m-res+xml; ty=4");

                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<m2m:cin\n    " + "xmlns:m2m=\"http://www.onem2m.org/xml/protocols\" \n    " +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n    " +
                        "<con>"+packetString+ "</con>\n</m2m:cin>");
                OutputStream os=connection.getOutputStream();
                os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();

                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                String page="";
                while((line = br.readLine())!=null){
                    page += line;
                }
                Log.d("Log","Code >> "+connection.getResponseCode());
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
}
