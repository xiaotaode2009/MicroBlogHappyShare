package com.seclab.microblogshare.ui;

import java.util.HashMap;
import org.json.JSONObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.seclab.microblogshare.R;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfoActivity extends Activity {
	public static final String TAG = "UserInfoActivity";

	// ----------头部工具栏-----------------------
	private ImageView tweet = null;
	private TextView title = null;

	// ----------底部导航栏------------------------
	private View friendTimeLine;
	private View userTimeLine;
	private View userNews;
	private View userInfo;
	private View more;
	private FooterClickListener listener;
	private ImageView userHead = null;
	private TextView userName = null;
	private TextView genderOfUser = null;
	private TextView locationOfUser = null;
	private TextView descriptionOfUser = null;

	private Button mblogNumBtn = null;
	private Button fansNumBtn = null;
	private Button guanzhuNumBtn = null;

	// -----------------Sina---------------
	private String jsonData;
	private JSONObject jsonObj = null;
	// ---------------Tencent------------------
	private String jsonQData;
	private JSONObject jsonQObj = null;
	private JSONObject mInfo = null;

	private String name = null;
	private String headerImageUrl = null;
	private String gender = null;
	private int sex = 0;// sex : 用户性别，1-男，2-女，0-未填写
	private String location = null;
	private String description = null;
	// followers_count:
	private String followers_count = null;
	// statuses_count:
	private String statuses_count = null;
	// friends_count:
	private String friends_count = null;
	// ------------标识符-------------------//
	private Boolean sina = false;
	private Boolean tencent = false;
	
	private Handler userInfoHandler = null;
	
	
	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		Intent intent = getIntent();
		sina = intent.getBooleanExtra("isSina", false);
		tencent = intent.getBooleanExtra("isTencent", false);
		initComponents();
		initFooter();
		userInfoHandler = getUseInfoHandler();
		ShareSDK.initSDK(this);
		if(sina){
			Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
			weibo.setPlatformActionListener(new PlatformActionListener() {
				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
	
				}

				@Override
				public void onComplete(Platform arg0, int arg1,HashMap<String, Object> arg2) {

					Log.e(TAG, arg2.toString());
					Message msg = new Message();
					msg.what = 1;
					msg.obj = arg2;
					userInfoHandler.sendMessage(msg);
					
					
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {

				}
			});
			weibo.showUser(null);
		}
		
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(20))
		.build();

	}

	private void initComponents() {
		tweet = (ImageView) findViewById(R.id.weibo_headbar_tweet);
		tweet.setVisibility(View.INVISIBLE);
		title = (TextView) findViewById(R.id.weibo_headbar_title);
		title.setText("个人资料");
		userHead = (ImageView) findViewById(R.id.userHead);
		userName = (TextView) findViewById(R.id.userName);
		genderOfUser = (TextView) findViewById(R.id.sexOfUser);
		locationOfUser = (TextView) findViewById(R.id.locationOfUser);
		descriptionOfUser = (TextView) findViewById(R.id.introductionTV);
		mblogNumBtn = (Button) findViewById(R.id.mblogNumBtn);
		mblogNumBtn.setOnClickListener(mListener);
		fansNumBtn = (Button) findViewById(R.id.fansNumBtn);
		fansNumBtn.setOnClickListener(mListener);
		guanzhuNumBtn = (Button) findViewById(R.id.guanzhuNumBtn);
		guanzhuNumBtn.setOnClickListener(mListener);

	}

	private void initFooter(){
		// -----------------底部导航栏---------------------------
		listener = new FooterClickListener();
		friendTimeLine = findViewById(R.id.weibo_menu_friendTimeLine);
		userTimeLine = findViewById(R.id.weibo_menu_userTimeLine);
		userNews = findViewById(R.id.weibo_menu_userNews);
		userInfo = findViewById(R.id.weibo_menu_myInfo);
		more = findViewById(R.id.weibo_menu_more);
		friendTimeLine.setId(0);
		userTimeLine.setId(1);
		userNews.setId(2);
		userInfo.setId(3);
		more.setId(4);
		friendTimeLine.setOnClickListener(listener);
		userTimeLine.setOnClickListener(listener);
		userNews.setOnClickListener(listener);
		userInfo.setOnClickListener(listener);
		more.setOnClickListener(listener);
	}
	
	
	
	private class FooterClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			i.putExtra("isSina", sina);
			i.putExtra("isTencent",tencent);
			i.putExtra("currentTag", v.getId());
			i.setClass(getApplicationContext(), TimeLineActivity.class);
			startActivity(i);
			finish();
		}
	}
	
	private OnClickListener mListener = new OnClickListener() {
		Intent i = new Intent();
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.mblogNumBtn:
				
				i.putExtra("isSina", sina);
				i.putExtra("isTencent", tencent);
				i.putExtra("currentTag", 1);
				i.setClass(UserInfoActivity.this, TimeLineActivity.class);
				UserInfoActivity.this.startActivity(i);
				break;
				
			case R.id.fansNumBtn:
				
//				i.setClass(UserInfoActivity.this, FansListActivity.class);
//				UserInfo.this.startActivity(i);
//				break;
				
			case R.id.guanzhuNumBtn:
//				i.setClass(UserInfo.this, IdolListActivity.class);
//				UserInfo.this.startActivity(i);
				break;
			}
			//finish();
		}
	};
	
	@SuppressLint("NewApi")
	private Handler getUseInfoHandler(){
		return new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if (sina) {
					JSONObject jsonObject = (JSONObject) JSONObject.wrap(msg.obj);
					bindData(jsonObject);
					
				}
				
				
			}
			
			
		};
	}
	private void bindData(JSONObject jsonObj) {
		try {
			name = jsonObj.getString("name");
			location = jsonObj.getString("location");
			gender = jsonObj.getString("gender");
			headerImageUrl = jsonObj.getString("profile_image_url");
			description = jsonObj.getString("description");
			statuses_count = jsonObj.getString("statuses_count");
			followers_count = jsonObj.getString("followers_count");
			friends_count = jsonObj.getString("friends_count");

		} catch (Exception e) {
			Log.v(TAG, "getDataFromJSON:exception");
		}
		userName.setText(name);
		userName.setTextColor(Color.BLACK);
		userName.setTextSize(20);
		locationOfUser.setText(location);
		locationOfUser.setTextColor(Color.BLACK);
		Log.v(TAG, description);
		descriptionOfUser.setText(description);

		if (gender.equals("m")) genderOfUser.setText("男");
		else if (gender.equals("f")) genderOfUser.setText("女");
		else genderOfUser.setText("未设置"); 
		genderOfUser.setTextColor(Color.BLACK);
		
		
		//setViewImage(userHead, headerImageUrl);
		imageLoader.displayImage(headerImageUrl, userHead);

		String statuses_count_temp = statuses_count + "</font><br><font size='10px' color='#A7A7A7'>微博";
		Spanned localSpanned1 = Html.fromHtml(statuses_count_temp);
		mblogNumBtn.setText(localSpanned1);
		String followers_count_temp = followers_count + "</font><br><font size='10px' color='#A7A7A7'>粉丝";
		Spanned localSpanned2 = Html.fromHtml(followers_count_temp);
		fansNumBtn.setText(localSpanned2);
		String friends_count_temp = friends_count + "</font><br><font size='10px' color='#A7A7A7'>关注";
		Spanned localSpanned3 = Html.fromHtml(friends_count_temp);
		guanzhuNumBtn.setText(localSpanned3);
	}

}
