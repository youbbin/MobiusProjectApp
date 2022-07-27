package com.example.mobiusprojectapp;

import org.json.JSONException;
import org.json.JSONObject;

public class DataParsing {
    public String[] getParsedData(Integer len, String result){
        String[] data = new String[len];
        try {
            JSONObject jsonObject=new JSONObject(result);
            JSONObject m2mCin=(JSONObject) jsonObject.get("m2m:cin");
            String con = m2mCin.getString("con");
            String[] split_comma=con.split(","); // ,를 기준으로 문자열 잘라서 배열에 저장
            for(int i=0;i<split_comma.length;i++){
                String[] split_slash=split_comma[i].split("/");
                data[i]=split_slash[1];
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
