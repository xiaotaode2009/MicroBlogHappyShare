package com.seclab.microblogshare.ui;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.seclab.microblogshare.bean.Account;
import com.seclab.microblogshare.util.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

public class QAuthorizeActivity extends Activity  implements PlatformActionListener,Callback{

	private Platform tecentWeibo = null;
	public DBManager mDBManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);
		mDBManager = new DBManager(getApplicationContext());
		tecentWeibo = ShareSDK.getPlatform(QAuthorizeActivity.this,TencentWeibo.NAME );
		tecentWeibo.setPlatformActionListener(this);
		tecentWeibo.authorize();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		String userInfo = tecentWeibo.getDb().exportData();
		Log.v("xiaotao", userInfo);
		try {
			JSONObject userInfoObject = new JSONObject(userInfo);
			// String iconString = userInfoObject.getString("icon");
			String name = userInfoObject.getString("nickname");
			String id = userInfoObject.getString("weibo");
			String url = userInfoObject.getString("icon");
			String plf = "tencent";
			String token = userInfoObject.getString("token");
			String expires_in = userInfoObject.getString("expiresIn");
			Account account = new Account(name, id, url, plf, token, expires_in);
			mDBManager.add(account);
			Intent intent = new Intent();
			intent.setClass(this, MainActivityActivity.class);
			startActivity(intent);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

}
