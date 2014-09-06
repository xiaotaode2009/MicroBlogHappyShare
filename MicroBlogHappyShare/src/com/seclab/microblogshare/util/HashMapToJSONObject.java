package com.seclab.microblogshare.util;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.seclab.microblogshare.widget.NewDataToast;

import android.R.string;
import android.annotation.SuppressLint;
import android.text.StaticLayout;



@SuppressLint("NewApi")
public class HashMapToJSONObject {
	
	 public static final Object NULL = new Object() {
	        @Override public boolean equals(Object o) {
	            return o == this || o == null; // API specifies this broken equals implementation
	        }
	        @Override public String toString() {
	            return "null";
	        }
	    };

	
	
	public static void HashMapWrapToJSONObject(JSONObject jsonObject,Object obj) throws JSONException{
		
		    Map<?, ?> contentsTyped = (Map<?, ?>) obj;
	        for (Map.Entry<?, ?> entry : contentsTyped.entrySet()) {
	            String key = (String) entry.getKey();
	            if (key == null) {
	                throw new NullPointerException("key == null");
	            }
	      
	            else if(key == ""){return;}
	            else{
	            	jsonObject.put(key,wrapHashToJSON(contentsTyped.get(key)) );
	            	}
	            
	            System.out.println(jsonObject.length());
	        }
	}
	
	
	
	

	public static Object wrapHashToJSON(Object o){
		 if (o == null) {
	            return NULL;
	        }
	        if (o instanceof JSONArray || o instanceof JSONObject) {
	            return o;
	        }
	        if (o.equals(NULL)) {
	            return o;
	        }
	        try {
	            if (o instanceof Collection) {
//	               ?return new JSONArray((Collection) o);
	            	return wrpaArrayList(o);
	            } else if (o.getClass().isArray()) {
	            	return wrpaArrayList(o);
	            }
	            if (o instanceof Map) {
	                return new JSONObject((Map) o);
	            }
	            if (o instanceof Boolean ||
	                o instanceof Byte ||
	                o instanceof Character ||
	                o instanceof Double ||
	                o instanceof Float ||
	                o instanceof Integer ||
	                o instanceof Long ||
	                o instanceof Short ||
	                o instanceof String) {
	                return o;
	            }
	            if (o.getClass().getPackage().getName().startsWith("java.")) {
	                return o.toString();
	            }
	        } catch (Exception ignored) {
	        }
	        return null;
	    }


	   public static JSONArray wrpaArrayList(Object array) throws JSONException {
	        if (!array.getClass().isArray()) {
	            throw new JSONException("Not a primitive array: " + array.getClass());
	        }
	        final int length = Array.getLength(array);
	        
	        JSONArray jsonArray = new JSONArray();
	        for (int i = 0; i < length; ++i) {
	            Object object = Array.get(array, i);
	            jsonArray.put(wrapHashToJSON(object));
	        }
	        return jsonArray;
	    }
	
	

	
}
