package com.seclab.microblogshare.ui;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;

import com.seclab.microblogshare.R;
import com.seclab.microblogshare.adapter.AccountAdapter;
import com.seclab.microblogshare.bean.Account;
import com.seclab.microblogshare.util.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivityActivity extends Activity {

	public static final int QUICKACTION_SINA = 0;
	public static final int QUICKACTION_TENCENT = 1;
	public static final int QUICKACTION_WEIXIN = 2;
	public DBManager mDBManager;
	private ArrayList<Account> listData = null;
	private ListView lv;
	private AccountAdapter adapter = null;
	private TextView title = null;
	private ProgressBar refreshBtn = null;
	private ImageView tweet = null;
	private View mAddBtn = null;
	private View mDelBtn = null;
	private View mExitBtn = null;
	private QuickActionWidget mGrid;// 快捷栏控件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window win = getWindow();
		win.requestFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.main_activity);
		initQuickActionGrid();
		title = (TextView) findViewById(R.id.weibo_headbar_title);
		refreshBtn = (ProgressBar) findViewById(R.id.weibo_headbar_refreshBtn);
		tweet = (ImageView) findViewById(R.id.weibo_headbar_tweet);
		title.setText("账号列表");
		title.setTextSize(25);
		refreshBtn.setVisibility(View.INVISIBLE);
		tweet.setVisibility(View.INVISIBLE);
		lv = (ListView) findViewById(R.id.account_lv);
		mAddBtn = findViewById(R.id.account_add);
		mDelBtn = findViewById(R.id.account_delete);
		mExitBtn = findViewById(R.id.account_exit);
		mAddBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mGrid.show(v);

			}
		});
       mDBManager = new DBManager(getApplicationContext());
		listData = (ArrayList<Account>) mDBManager.getAccounts();
		adapter = new AccountAdapter(this, listData);
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		//System.out.println(listData);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		listData =  (ArrayList<Account>) mDBManager.getAccounts();
		adapter.mData = listData;
		 adapter.notifyDataSetChanged();
	}




	/**
	 * 初始化快捷栏
	 */
	private void initQuickActionGrid() {
		mGrid = new QuickActionGrid(this);
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.sina3,
				R.string.sina));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.tencent2,
				R.string.tencent));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.weixin2,
				R.string.weixin));
		mGrid.setOnQuickActionClickListener(mActionListener);
	}

	/**
	 * 快捷栏item点击事件
	 */
	private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			Intent intent = null;
			switch (position) {
			case QUICKACTION_SINA:
				intent = new Intent(MainActivityActivity.this,
						AuthorizeActivity.class);
				startActivity(intent);
				break;
			case QUICKACTION_TENCENT:
				intent = new Intent(MainActivityActivity.this,
						QAuthorizeActivity.class);
				startActivity(intent);
				break;
			case QUICKACTION_WEIXIN:
				intent = new Intent(MainActivityActivity.this,
						WAuthorizeActivity.class);
				startActivity(intent);
				break;
			}
		}
	};

}
