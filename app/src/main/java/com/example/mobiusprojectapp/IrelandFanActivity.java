package com.example.mobiusprojectapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.travijuu.numberpicker.library.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class IrelandFanActivity extends Activity {

    ImageButton backButton, powerButton, menuButton;
    Button setButton;
    NumberPicker numberPicker;
    Integer level;
    Boolean power = false;
    String packet="";
    HttpURLConnection connection_post;
    HttpURLConnection connection_get;
    HttpRequestGET_IrelandFan thread_get;
    String result = "";
    String[] parsedData;
    TextView textViewPower, textViewLevel;
    Context context;
    int startHour = 0, startMinute = 0, finishHour = 0, finishMinute = 0;
    View view;
    String startTime="", finishTime="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ireland_fan);
        context = this;

        textViewLevel = (TextView) findViewById(R.id.textview_fan_level);
        textViewPower = (TextView) findViewById(R.id.textview_fan_power);

        backButton = (ImageButton) findViewById(R.id.button_back_ireland_fan); // 뒤로가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection_post.disconnect(); // Http 연결 해제
                connection_get.disconnect();
                finish(); // 액티비티 종료
            }
        });

        powerButton = (ImageButton) findViewById(R.id.button_power_fan);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(power){
                    power = false;
                    packet="3/1/2";
                    Log.d("Log","FAN 전원 버튼 누름(Off)");
                    Toast.makeText(context,"FAN 전원을 껐습니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    power = true;
                    packet="3/1/1";
                    Log.d("Log","FAN 전원 버튼 누름(On)");
                    Toast.makeText(context,"FAN 전원을 켰습니다.",Toast.LENGTH_SHORT).show();
                }

                Runnable requestHttpPOST = new HttpRequestPOST_IrelandFan(packet); // 전송
                new Thread(requestHttpPOST).start();
            }
        });

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker_fan);
        numberPicker.setMax(10);
        numberPicker.setMin(1);
        numberPicker.setValue(0);

        setButton = (Button) findViewById(R.id.button_set_fan);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level = numberPicker.getValue();
                textViewLevel.setText((level/10)+"/10");
                packet="3/2/"+level;
                Runnable requestHttpPOST = new HttpRequestPOST_IrelandFan(packet); // 전송
                new Thread(requestHttpPOST).start();
            }
        });



        menuButton = (ImageButton) findViewById(R.id.button_menu_ireland_fan);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForContextMenu(menuButton);
                openContextMenu(menuButton);
                unregisterForContextMenu(menuButton);
            }
        });

        view=(View) View.inflate(context, R.layout.dialog, null);


        thread_get = new HttpRequestGET_IrelandFan();
        thread_get.start();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ireland_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_set_on_off_time:
                showDialog(); // 시간 설정 대화상자 시작
                return true;
        }
        return super.onContextItemSelected(item);

    }


    public void showDialog(){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context); // 대화상자 빌더
        dialogBuilder.setTitle("예약 설정"); // 타이틀 설정
        view=(View) View.inflate(context, R.layout.dialog, null);
        dialogBuilder.setView(view);
        dialogBuilder.setPositiveButton("확인", // 확인 버튼
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("log","시작 시간 설정 >>> "+startHour+":"+startMinute);
                        Log.d("log","종료 시간 설정 >>> "+finishHour+":"+finishMinute);
                        packet="3/3/"+startHour+":"+startMinute+","+finishHour+":"+finishMinute;
                        Runnable requestHttpPOST = new HttpRequestPOST_IrelandFan(packet); // 전송
                        new Thread(requestHttpPOST).start();
                        Toast.makeText(context,"예약 설정을 완료했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
        dialogBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        TextView textViewStartTime = view.findViewById(R.id.textview_start_time);
        TextView textViewFinishTime = view.findViewById(R.id.textview_finish_time);

        textViewStartTime.setText(startTime);
        textViewFinishTime.setText(finishTime);

        String[] start_split = startTime.split(":");
        String [] finish_split = finishTime.split(":");

        startHour = Integer.parseInt(start_split[0]);
        startMinute = Integer.parseInt(start_split[1]);
        finishHour = Integer.parseInt(finish_split[0]);
        finishMinute = Integer.parseInt(finish_split[1]);

        View.OnClickListener clickListener = new View.OnClickListener(){
            public void onClick(View view){
                TimePickerDialog startTimePickerDialog = new TimePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        startHour = hour;
                        startMinute = minute;
                        textViewStartTime.setText(String.format("%02d",startHour)+":"+String.format("%02d",startMinute));
                    }
                },startHour,startMinute,false);
                startTimePickerDialog.setMessage("시작 시간");
                startTimePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                startTimePickerDialog.show();
            }
        };
        Button buttonStartTime = view.findViewById(R.id.button_start_time);
        buttonStartTime.setOnClickListener(clickListener);

        Button buttonFinishTime = view.findViewById(R.id.button_finish_time);
        buttonFinishTime.setOnClickListener(clickListener);

        textViewStartTime.setOnClickListener(clickListener);
        textViewFinishTime.setOnClickListener(clickListener);
    }

    protected void onDestroy() {
        super.onDestroy();
        thread_get.setRunningState(false);
    }

    public class HttpRequestPOST_IrelandFan implements Runnable{ // HTTP POST 요청 스레드
        String packetString;
        public HttpRequestPOST_IrelandFan(String packetString){
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

    class HttpRequestGET_IrelandFan extends Thread{
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
                    parsedData = dataParsing.getParsedData(12,result);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewLevel.setText((Integer.parseInt(parsedData[2])/10)+"/10");
                            if(parsedData[5].equals("1")){
                                textViewPower.setText("ON");
                                power = true;
                            }else{
                                textViewPower.setText("OFF");
                                power = false;
                            }

                            //testTime="test";
                            //textViewStartTime.setText("test");
                            startTime=parsedData[10];
                            finishTime=parsedData[11];
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
