  
package com.seclab.microblogshare.bean;   

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
  


public class QSource extends JSONObject implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String source_nick ="";
	private String source_text = "";// 微博文字信息，包含URL、昵称、是否认证的信息
	private String source_image = "";//小图
	private String source_medium_image = "";//大图
	

	private QSource (){}
	
	public String getSource_nick() {
		return source_nick;
	}

	public void setSource_nick(String source_nick) {
		this.source_nick = source_nick;
	}

	public String getSource_text() {
		return source_text;
	}

	public void setSource_text(String source_text) {
		this.source_text = source_text;
	}

	public String getSource_image() {
		return source_image;
	}

	public void setSource_image(String source_image) {
		this.source_image = source_image;
	}
	public String getSource_medium_image() {
		return source_medium_image;
	}

	public void setSource_medium_image(String source_medium_image) {
		this.source_medium_image = source_medium_image;
	}

	public static QSource getQSource(JSONObject json){
		QSource mQsource = new QSource();
		if (!json.isNull("source")) {
			JSONObject source;
			try {
				source = json.getJSONObject("source");
				if (!source.isNull("image")) {
					JSONArray images = source.getJSONArray("image");
					if (images != null && images.length() > 0) {
						mQsource.setSource_image(images.optString(0) + "/120");
						mQsource.setSource_medium_image(images.optString(0) + "/460");
						//Log.v(TAG, "source_image_url:  " + source_image);
					}
				}
				if (!source.isNull("nick")) {
					mQsource.setSource_nick(source.getString("nick"));
					//Log.v(TAG, "source_nick:  " + source_nick);

					if (!source.isNull("origtext")) {
						mQsource.setSource_text(mQsource.getSource_nick() + ":  " + source.getString("origtext"));
						//Log.v(TAG, "source_origtext:  " + source_text);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			//Log.v(TAG, "sourcel:  " + source);

		return mQsource;
		}
		
		else return null;
		
	}
}
  