package com.seclab.microblogshare.ui;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;

import com.seclab.microblogshare.R;
import com.seclab.microblogshare.adapter.FriendTimeLineAdapter;
import com.seclab.microblogshare.adapter.QListViewAdapter;
import com.seclab.microblogshare.bean.QStatus;
import com.seclab.microblogshare.bean.Status;
import com.seclab.microblogshare.net.SinaApi;
import com.seclab.microblogshare.net.TencentApi;
import com.seclab.microblogshare.net.WeiboException;
import com.seclab.microblogshare.util.HashMapToJSONObject;
import com.seclab.microblogshare.util.UIHelper;
import com.seclab.microblogshare.widget.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TimeLineActivity extends Activity {
	// 公用数据
	private int pageIndex = 1;
	private int pageSize = 20;
	private int pageSum = 20;
	public static final String TAG = "TimeLineActivity";
	private static final short ACTION_SINAWEIBO = 5;
	private static final int SHARE_SDK_ACTION = 65535;
	public final static short LISTVIEW_ACTION_REFRESH = 0x02;
	public final static short LISTVIEW_ACTION_SCROLL = 0x03;

	// 平台选择
	private boolean isSina = false;
	private boolean isTencent = false;

	// ----------头部工具栏-----------------------//
	public ImageView tweet = null;
	public ProgressBar mHeadProgress = null;
	public TextView title = null;

	// -------中部ListView组件和适配器------------//
	public PullToRefreshListView pullToRefreshListView;
	public View listView_footer;
	public TextView listView_foot_more;
	public ProgressBar listView_foot_progress;
	public static String jsonData;

	// ----------底部导航栏------------------------//
	public View friendTimeLine;
	public View userTimeLine;
	public View userNews;
	public View userInfo;
	public View more;
	public int mCurFooterTab = 0;

	// 新浪微博参数
	public Handler sinaListViewHandler;
	private List<Status> mStatusList = null;
	private FriendTimeLineAdapter sinaFriendTimeLineAdapter = null;
	
	//腾讯微博参数
	public Handler tencentListViewHandler;
	private List<QStatus> qStatusList = null;
	private QListViewAdapter tencentFriendTimeLineAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		isSina = intent.getBooleanExtra("isSina", false);
		isTencent = intent.getBooleanExtra("isTencent", false);
		setContentView(R.layout.friendtimeline);
		ShareSDK.initSDK(this);
		initHeader();
		initCenter();
		initFooter();
		if (isSina) {
			sinaListViewHandler = getSianListViewHandler();
			requestSinaFriendTimeLine(20, 1, ACTION_SINAWEIBO);
			initSinaData();
		}
		
		
		if(isTencent){
			tencentListViewHandler = getTencentListViewHandler();
			requestTencentFriendTimeLine("0", "0", ACTION_SINAWEIBO);
			inTencentData();		
		}
		
	}

	private void initHeader() {
		// -------头部工具栏----------------------------------
		tweet = (ImageView) findViewById(R.id.weibo_headbar_tweet);
		tweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(TAG, "ImageView onClick");
				OnekeyShare oks = new OnekeyShare();
				oks.setNotification(R.drawable.ic_launcher,
						"ShareSDK onekeyshare sample");
				oks.setAddress("12345678901");
				oks.setTitle("ShareSDK title");
				oks.setTitleUrl("http://sharesdk.cn");
				oks.setText("ShareSDK text");
				// oks.setImagePath(MainActivity.TEST_IMAGE);
				// oks.setImageUrl("http://img.appgo.cn/imgs/sharesdk/content/2013/07/25/1374723172663.jpg");
				oks.setUrl("http://sharesdk.cn");
				oks.setComment("renren weibo comment");
				oks.setSite("qzone share website");
				oks.setSiteUrl("http://sharesdk.cn");
				oks.setLatitude(23.122619f);
				oks.setLongitude(113.372338f);
				oks.setSilent(false);
				oks.show(TimeLineActivity.this);
			}
		});
		title = (TextView) findViewById(R.id.weibo_headbar_title);
		mHeadProgress = (ProgressBar) findViewById(R.id.weibo_headbar_refreshBtn);
	}

	private void initCenter() {
		// --------------中部ListView和适配器---------------------
		listView_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		listView_foot_more = (TextView) listView_footer
				.findViewById(R.id.listview_foot_more);
		listView_foot_progress = (ProgressBar) listView_footer
				.findViewById(R.id.listview_foot_progress);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
		pullToRefreshListView.addFooterView(listView_footer);// 添加底部视图
		pullToRefreshListView.addFooterView(listView_footer); // 必须在setAdapter前
	}

	private void initFooter() {
		// -----------------底部导航栏---------------------------
		friendTimeLine = findViewById(R.id.weibo_menu_friendTimeLine);
		friendTimeLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFooterTab(0);
				// if(isSina)loadSinaLvData(mCurFooterTab,"1", listViewHandler,
				// UIHelper.LISTVIEW_ACTION_REFRESH);
				// if(isTencent)loadTencentLvData(mCurFooterTab, 0, "0",
				// mQlistViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		userTimeLine = findViewById(R.id.weibo_menu_userTimeLine);
		userTimeLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFooterTab(1);
				// if(isSina)loadSinaLvData(mCurFooterTab,"1", listViewHandler,
				// UIHelper.LISTVIEW_ACTION_REFRESH);
				// if(isTencent)loadTencentLvData(mCurFooterTab, 0, "0",
				// mQlistViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		userNews = findViewById(R.id.weibo_menu_userNews);
		userNews.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Log.v(TAG, "userNews");
				setSelectedFooterTab(2);
				//
				// if(isSina)loadSinaLvData(mCurFooterTab,"1", listViewHandler,
				// UIHelper.LISTVIEW_ACTION_REFRESH);
				// if(isTencent)loadTencentLvData(mCurFooterTab, 0, "0",
				// mQlistViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		userInfo = findViewById(R.id.weibo_menu_myInfo);
		userInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectedFooterTab(3);
				//
				// Log.v(TAG, "userInfo");
				// Intent i = new Intent();
				// i.putExtra("isSina", isSina);
				// i.putExtra("isTencent", isTencent);
				// i.putExtra("currentTag", 3);
				// i.setClass(getApplicationContext(), UserInfo.class);
				// startActivity(i);
				// finish();
			}
		});
		more = findViewById(R.id.weibo_menu_more);
		more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Intent i = new Intent();
				// i.putExtra("isSina", isSina);
				// i.putExtra("isTencent", isTencent);
				// i.putExtra("currentTag", 4);
				// i.setClass(getApplicationContext(), More.class);
				// startActivity(i);
				// finish();
			}
		});
	}

	protected void setSelectedFooterTab(int i) {
		mCurFooterTab = i;
		friendTimeLine.setBackgroundResource(0);
		userTimeLine.setBackgroundResource(0);
		userNews.setBackgroundResource(0);
		userInfo.setBackgroundResource(0);
		more.setBackgroundResource(0);
		if (i == 0) {
			friendTimeLine.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
			title.setText("微博主页");
			if (isSina) {
				requestSinaFriendTimeLine(20, 1, ACTION_SINAWEIBO);
			}
			
			else if (isTencent) {
				requestTencentFriendTimeLine("0", "0", ACTION_SINAWEIBO);
			}
			pageSum = 20;
		}
		if (i == 1) {
			userTimeLine
					.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
			title.setText("我的微博");
			if (isSina) {
				requestSinaUserTimeLine(20, 1, ACTION_SINAWEIBO);
			}
			else if (isTencent) {
				Log.e(TAG, "requestTencentUserTimeLine");
				requestTencentUserTimeLine("0", "0", ACTION_SINAWEIBO);
			}
			pageSum = 20;
		}
		if (i == 2) {
			userNews.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
			title.setText("微博动态");
			if(isSina) { }
		}
		if (i == 3){
		Log.e(TAG, "CURRENTTAG==3");
		userInfo.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
		Intent intent = new Intent();
		intent.putExtra("isSina", isSina);
		intent.putExtra("isTencent", isTencent);
		intent.setClass(this, UserInfoActivity.class);
		startActivity(intent);
		}
			
		if (i == 4)
			more.setBackgroundResource(R.drawable.weibo_menu_cp_bg_selected);
	}

	//调用SinaApi读取微博FriendTimeLine
	private void requestSinaFriendTimeLine(int count, int page,short customerAction) {
		Platform weibo = ShareSDK.getPlatform(this, "SinaWeibo");
		weibo.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = arg1;
				msg.obj = arg2;
				sinaListViewHandler.sendMessage(msg);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		String url = "https://api.weibo.com/2/statuses/friends_timeline.json";
		String method = "GET";
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("since_id", 0);
		values.put("max_id", 0);
		values.put("count", count);
		values.put("page", page);
		weibo.customerProtocol(url, method, customerAction, values, null);
	}
	
	
	//调用Tencent读取腾讯微博的FriendTimeLine
	//String pageTime, String pageSize
	private void requestTencentFriendTimeLine(String pageflag, String pagetime,short customerAction) {
		Platform weibo = ShareSDK.getPlatform(this, TencentWeibo.NAME);
		weibo.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = arg1;
				msg.obj = arg2;
				tencentListViewHandler.sendMessage(msg);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		String url = "http://open.t.qq.com/api/statuses/home_timeline";
		String method = "GET";
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("format", "json");
		values.put("pageflag", pageflag);
		values.put("pagetime", pagetime);
		values.put("reqnum", "20");
		weibo.customerProtocol(url, method, customerAction, values, null);
	}

	
	//调用SinaApi读取微博UserTimeLine
	private void requestSinaUserTimeLine(int count, int page, short customerAction) {
		Platform weibo = ShareSDK.getPlatform(this, "SinaWeibo");
		weibo.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = arg1;
				msg.obj = arg2;
				sinaListViewHandler.sendMessage(msg);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		String url = "https://api.weibo.com/2/statuses/user_timeline.json";
		String method = "GET";
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("since_id", 0);
		values.put("max_id", 0);
		values.put("count", count);
		values.put("page", page);
		weibo.customerProtocol(url, method, customerAction, values, null);
	}
	
	
	//调用腾讯API读取腾讯微博UserTimeLine
	
	private void requestTencentUserTimeLine(String pageflag, String pagetime,short customerAction) {
		Log.e(TAG, "requestTencentUserTimeLine");
		Platform weibo = ShareSDK.getPlatform(this, TencentWeibo.NAME);
		weibo.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = arg1;
				msg.obj = arg2;
				Log.e(TAG, "requestTencentUserTimeLine"+arg2.toString());
				tencentListViewHandler.sendMessage(msg);
				
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		String url = "http://open.t.qq.com/api/statuses/broadcast_timeline";
		String method = "GET";
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("format", "json");
		values.put("pageflag", pageflag);
		values.put("pagetime", pagetime);
		values.put("reqnum", "20");
		weibo.customerProtocol(url, method, customerAction, values, null);
		
	}
	

	
	//新浪微博Handler
	@SuppressLint({ "HandlerLeak", "NewApi" })
	private Handler getSianListViewHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if ((msg.arg1 & SHARE_SDK_ACTION) == ACTION_SINAWEIBO ) {
//						JSONObject jsonObject = (JSONObject) JSONObject.wrap(msg.obj);

						JSONObject jsonObject = new JSONObject();
						try {
							HashMapToJSONObject.HashMapWrapToJSONObject(jsonObject, (HashMap<String, Object>)msg.obj);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String str = jsonObject.toString();
						SinaApi sinaApi = new SinaApi();
						try {
							mStatusList = sinaApi.getStatusList(str);
						} catch (WeiboException e) {
							e.printStackTrace();
						}
						sinaFriendTimeLineAdapter = new FriendTimeLineAdapter(
								TimeLineActivity.this, mStatusList);
						pullToRefreshListView.setAdapter(sinaFriendTimeLineAdapter);
					}
					else if ((msg.arg1 & SHARE_SDK_ACTION) == LISTVIEW_ACTION_SCROLL) {
						List<Status> tmpStatusList = null;
						SinaApi sinaApi = new SinaApi();
						JSONObject jsonObject = (JSONObject) JSONObject.wrap(msg.obj);
						String str = jsonObject.toString();
						try {
							tmpStatusList = sinaApi.getStatusList(str);
						} catch (WeiboException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.e(TAG,"tmpStatusList长度为" + tmpStatusList.size());
						if (mStatusList.size() > 0) {
							Log.e(TAG,"mStatusList.size()" + mStatusList.size());
							for (Status status : tmpStatusList) {
								boolean b = false;
								for (Status status2 : mStatusList) {
									if (status.getId().equals(status2.getId())) {
										b = true;
										break;
									}
								}
								if (!b) {
									pageSum++;
									Log.e(TAG, "添加一个statues");
									mStatusList.add(status);
								}

							}

						}
						Log.e(TAG,
								"onScrollStateChanged方法执行到达底部notifyDataSetChanged执行"
										+ "mStatusList size"
										+ mStatusList.size());
						sinaFriendTimeLineAdapter.mData = mStatusList;
						sinaFriendTimeLineAdapter.notifyDataSetChanged();
						// sinaFriendTimeLineAdapter = new
						// FriendTimeLineAdapter(FriendTimeLineActivitySecond.this,mStatusList);
						// pullToRefreshListView.setAdapter(sinaFriendTimeLineAdapter);

					}
					// 到达顶部刷新
					else if ((msg.arg1 & SHARE_SDK_ACTION) == LISTVIEW_ACTION_REFRESH) {
						List<Status> tmpStatusList = null;
						JSONObject jsonObject = (JSONObject) JSONObject.wrap(msg.obj);
						String str = jsonObject.toString();
						SinaApi sinaApi = new SinaApi();
						try {
							tmpStatusList = sinaApi.getStatusList(str);
						} catch (WeiboException e) {
							e.printStackTrace();
						}
						int length = tmpStatusList.size() - 1;
						for (int i = length; i >= 0; i--) {
							Status tmp = tmpStatusList.get(i);
							boolean flag = false;
							for (Status status2 : mStatusList) {
								if (tmp.getId().equals(status2.getId())) {
									flag = true;
									break;
								}
							}
							if (!flag) {
								pageSum++;
								mStatusList.add(0, tmp);
								Log.e(TAG, "增加数据");
							} else
								Log.e(TAG, "没有增加数据");
						}

						sinaFriendTimeLineAdapter.mData = tmpStatusList;
						sinaFriendTimeLineAdapter.notifyDataSetChanged();
						pullToRefreshListView.onRefreshComplete();
					}

					break;

				default:
					break;
				}

			}

		};

	}
	
	
	//腾讯微博Handler
	
	@SuppressLint({ "HandlerLeak", "NewApi" })
	private Handler getTencentListViewHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					if ((msg.arg1 & SHARE_SDK_ACTION) == ACTION_SINAWEIBO ) {
						Log.e(TAG, "msg.arg1 & SHARE_SDK_ACTION) == ACTION_SINAWEIBO");
						new JSONObject();
						JSONObject jsonObject = (JSONObject) JSONObject.wrap(msg.obj);
						String str = jsonObject.toString();
						TencentApi tencentApi = new TencentApi();
						try {
							Log.e(TAG, str);
							qStatusList = tencentApi.getQStatusList(str);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// pageSum = 20;
						
						tencentFriendTimeLineAdapter =  new QListViewAdapter(TimeLineActivity.this, qStatusList);
						pullToRefreshListView.setAdapter(tencentFriendTimeLineAdapter);	
						
					}

					else if ((msg.arg1 & SHARE_SDK_ACTION) == LISTVIEW_ACTION_SCROLL) {
						Log.e(TAG, "onScrollStateChanged方法执行到达底部Handler执行"+msg.obj.toString());
						List<QStatus> tmpStatusList = null;
						new JSONObject();
						JSONObject jsonObject = (JSONObject) JSONObject.wrap(msg.obj);
						String str = jsonObject.toString();
						TencentApi tencentApi = new TencentApi();
						try {
							tmpStatusList =  tencentApi.getQStatusList(str);
							Log.e(TAG,"tmpStatusList长度为" + tmpStatusList.size());
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (qStatusList.size() > 0) {
							Log.e(TAG,"qStatusList.size()" + qStatusList.size());
							for (QStatus status : tmpStatusList) {
								boolean b = false;
								for (QStatus status2 : qStatusList) {
									if (status.getId().equals(status2.getId())) {
										b = true;
										break;
									}
								}
								if (!b) {
									pageSum++;
									Log.e(TAG, "添加一个statues");
									qStatusList.add(status);
								}

							}

						}
						tencentFriendTimeLineAdapter.list = qStatusList;
						tencentFriendTimeLineAdapter.notifyDataSetChanged();
						// sinaFriendTimeLineAdapter = new
						// FriendTimeLineAdapter(FriendTimeLineActivitySecond.this,mStatusList);
						// pullToRefreshListView.setAdapter(sinaFriendTimeLineAdapter);

					}
					// 到达顶部刷新
					else if ((msg.arg1 & SHARE_SDK_ACTION) == LISTVIEW_ACTION_REFRESH) {
						List<Status> tmpStatusList = null;
						new JSONObject();
						JSONObject jsonObject = (JSONObject) JSONObject.wrap(msg.obj);
						String str = jsonObject.toString();
						SinaApi sinaApi = new SinaApi();
						try {
							tmpStatusList = sinaApi.getStatusList(str);
						} catch (WeiboException e) {
							e.printStackTrace();
						}
						int length = tmpStatusList.size() - 1;
						for (int i = length; i >= 0; i--) {
							Status tmp = tmpStatusList.get(i);
							boolean flag = false;
							for (Status status2 : mStatusList) {
								if (tmp.getId().equals(status2.getId())) {
									flag = true;
									break;
								}
							}
							if (!flag) {
								pageSum++;
								mStatusList.add(0, tmp);
								Log.e(TAG, "增加数据");
							} else
								Log.e(TAG, "没有增加数据");
						}

						tencentFriendTimeLineAdapter.list = qStatusList;
						tencentFriendTimeLineAdapter.notifyDataSetChanged();
						pullToRefreshListView.onRefreshComplete();
					}

					break;

				default:
					break;
				}

			}

		};

	}
	
	
	
	
	
	//初始化新浪微博数据

	private void initSinaData() {

		pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						Log.e(TAG, "onScrollStateChanged方法执行");
						pullToRefreshListView.onScrollStateChanged(view,
								scrollState);
						if (mStatusList.isEmpty())
							return;
						boolean scrollEnd = false;
						try {
							if (view.getPositionForView(listView_footer) == (view
									.getLastVisiblePosition() - 1))
								scrollEnd = true;
							Log.e(TAG,
									"view.getPositionForView(listView_footer):"
											+ view.getPositionForView(listView_footer)
											+ "  "
											+ "view.getLastVisiblePosition()"
											+ view.getLastVisiblePosition());
						} catch (Exception e) {
							scrollEnd = false;
						}
						if (scrollEnd)// &&
										// lvDataState==UIHelper.LISTVIEW_DATA_MORE
						{
							Log.e(TAG, "onScrollStateChanged方法执行到达底部"
									+ "pageSize:" + pageSize + "pageIndex:"
									+ pageSum / pageSize);
							pullToRefreshListView
									.setTag(UIHelper.LISTVIEW_DATA_LOADING);
							listView_foot_more.setText(R.string.loading);
							listView_foot_progress.setVisibility(View.VISIBLE);
							pageIndex = (pageSum / pageSize) + 1;
							if(mCurFooterTab == 0){requestSinaFriendTimeLine(pageSize, pageIndex,LISTVIEW_ACTION_SCROLL);}
							else if (mCurFooterTab ==1) {requestSinaUserTimeLine(pageSize, pageIndex,LISTVIEW_ACTION_SCROLL);}
							
						}

					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						Log.e(TAG, "onScroll方法执行");
						pullToRefreshListView.onScroll(view, firstVisibleItem,
								visibleItemCount, totalItemCount);

					}
				});

		pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						Log.e(TAG, "onRefreshonRefresh方法执行");
						if(mCurFooterTab == 0){	requestSinaFriendTimeLine(20, 1,LISTVIEW_ACTION_REFRESH);}
						else if (mCurFooterTab == 1) {requestSinaUserTimeLine(20, 1,LISTVIEW_ACTION_REFRESH);}
					

					}
				});

	}
	
	
	
	
	private void inTencentData() {

		pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						Log.e(TAG, "onScrollStateChanged方法执行");
						pullToRefreshListView.onScrollStateChanged(view,
								scrollState);
						if (qStatusList.isEmpty())
							return;
						boolean scrollEnd = false;
						try {
							if (view.getPositionForView(listView_footer) == (view
									.getLastVisiblePosition() - 1))
								scrollEnd = true;
							Log.e(TAG,
									"view.getPositionForView(listView_footer):"
											+ view.getPositionForView(listView_footer)
											+ "  "
											+ "view.getLastVisiblePosition()"
											+ view.getLastVisiblePosition());
						} catch (Exception e) {
							scrollEnd = false;
						}
						if (scrollEnd)// &&
										// lvDataState==UIHelper.LISTVIEW_DATA_MORE
						{
							Log.e(TAG, "onScrollStateChanged方法执行到达底部"
									+ "pageSize:" + pageSize + "pageIndex:"
									+ pageSum / pageSize);
							pullToRefreshListView
									.setTag(UIHelper.LISTVIEW_DATA_LOADING);
							listView_foot_more.setText(R.string.loading);
							listView_foot_progress.setVisibility(View.VISIBLE);
							Log.e(TAG, "qStatusList size()"+qStatusList.size()+"qStatusList.get(qStatusList.size()-1).getCreated_at()"+qStatusList.get(qStatusList.size()-1).getPageTime());
							if(mCurFooterTab == 0){requestTencentFriendTimeLine("1",qStatusList.get(qStatusList.size()-1).getPageTime() ,LISTVIEW_ACTION_SCROLL);}
							else if (mCurFooterTab ==1) {requestTencentUserTimeLine("1",qStatusList.get(qStatusList.size()-1).getPageTime() ,LISTVIEW_ACTION_SCROLL);}
							
						}

					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						Log.e(TAG, "onScroll方法执行");
						pullToRefreshListView.onScroll(view, firstVisibleItem,
								visibleItemCount, totalItemCount);

					}
				});

		pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						Log.e(TAG, "onRefreshonRefresh方法执行");
						if(mCurFooterTab == 0){	requestTencentFriendTimeLine("0", "0",LISTVIEW_ACTION_REFRESH);}
						else if (mCurFooterTab == 1) {requestTencentUserTimeLine("0", "0",LISTVIEW_ACTION_REFRESH);}					
					}
				});

	}
	
	


}
