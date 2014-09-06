package com.seclab.microblogshare.ui;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.seclab.microblogshare.bean.Account;
import com.seclab.microblogshare.util.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler.Callback;
import android.os.Bundle;
import android.os.Message;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.authorize.AuthorizeAdapter;
import cn.sharesdk.sina.weibo.SinaWeibo;

public class AuthorizeActivity extends Activity implements
		PlatformActionListener, Callback {
	private Platform weibo = null;
	public DBManager mDBManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);
		mDBManager = new DBManager(getApplicationContext());
		weibo = ShareSDK.getPlatform(AuthorizeActivity.this, SinaWeibo.NAME);
		weibo.setPlatformActionListener(this);
		weibo.authorize();
	}

	public void onDestroy() {
		super.onDestroy();
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
		String userInfo = weibo.getDb().exportData();
		try {
			JSONObject userInfoObject = new JSONObject(userInfo);
			// String iconString = userInfoObject.getString("icon");
			String name = userInfoObject.getString("nickname");
			String id = userInfoObject.getString("weibo");
			String url = userInfoObject.getString("icon");
			String plf = "sina";
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
