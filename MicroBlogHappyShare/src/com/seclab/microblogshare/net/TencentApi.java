package com.seclab.microblogshare.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seclab.microblogshare.bean.QStatus;


public class TencentApi {
	
	   public ArrayList<QStatus> getQStatusList(String jsonStrData) {
           String jsonData = jsonStrData;
           ArrayList<QStatus> statusList = new ArrayList<QStatus>();
           JSONObject obj = null;
           try {
                   obj = new JSONObject(jsonData);
           } catch (JSONException e2) {

                   e2.printStackTrace();
           }

           JSONObject dataObj = null;
           try {
                   if (!obj.isNull("data"))
                           dataObj = obj.getJSONObject("data");
                   else
                           return null;
           } catch (JSONException e1) {

                   e1.printStackTrace();
                   return null;
           }
           JSONArray data = null;
           try {
                   data = dataObj.getJSONArray("info");
           } catch (JSONException e) {

                   e.printStackTrace();
                   return null;
           }
           if (data != null && data.length() > 0) {
                   
                   int lenth = data.length();
                   for (int i = 0; i < lenth; i++) {
                           try {
                                   JSONObject json = data.optJSONObject(i);
                                   QStatus status = QStatus.getQStatus(json);
                                   statusList.add(status);
                           } catch (JSONException e) {
                                   // TODO Auto-generated catch block
                                   e.printStackTrace();
                           }
                   }
           }
           return statusList;
   }
	
}
