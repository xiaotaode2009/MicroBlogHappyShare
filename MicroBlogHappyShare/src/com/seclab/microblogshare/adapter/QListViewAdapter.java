package com.seclab.microblogshare.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.seclab.microblogshare.R;
import com.seclab.microblogshare.bean.QSource;
import com.seclab.microblogshare.bean.QStatus;
import com.seclab.microblogshare.util.UIHelper;

/**
 * 腾讯微博列表适配器
 * @author Logan <a href="https://github.com/Logan676/JustSharePro"/>
 *   
 * @version 1.0 
 *  
 */
public class QListViewAdapter extends BaseAdapter {
	//private final String TAG = "QListViewAdapter";
	//private Context context;
	private LayoutInflater mInflater;
	public List<QStatus> list;
	private ViewHolder holder;

	// Universal Image Loader for Android 第三方框架组件
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public QListViewAdapter(Context context, List<QStatus> list) {
		super();
		//this.context = context;	
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.icon)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
	}

	@Override
	public int getCount() {
		
		return this.list != null ? this.list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		QStatus mQstatus = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.weibo_listview_item, null);
			holder.nick = (TextView) convertView.findViewById(R.id.item_screen_name);
			holder.head = (ImageView) convertView.findViewById(R.id.item_profile_image);

			holder.origText = (TextView) convertView.findViewById(R.id.item_text);
			holder.image = (ImageView) convertView.findViewById(R.id.item_microBlogImage);
			holder.from = (TextView) convertView.findViewById(R.id.item_from);

			holder.mcount = (TextView) convertView.findViewById(R.id.item_tweet_statuses_count);
			holder.count = (TextView) convertView.findViewById(R.id.item_tweet_followers_count);
			holder.timeStamp = (TextView) convertView.findViewById(R.id.item_created_at);
			holder.isVip = (ImageView) convertView.findViewById(R.id.item_vipImage);

			// ---------------转发微博---------------------
			holder.source_text = (TextView) convertView.findViewById(R.id.item_retweeted_status_text);
			holder.source_image = (ImageView) convertView.findViewById(R.id.item_retweeted_status_microBlogImage);
			holder.source_ll = convertView.findViewById(R.id.item_retweeted_status_ll);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nick.setText(mQstatus.getNick());
		holder.origText.setText(mQstatus.getOrigText());
		String image_url = mQstatus.getImage();
		//Log.v(TAG, image_URL);
		if (!image_url.equals("")) {
			holder.image.setVisibility(View.VISIBLE);
			imageLoader.displayImage(mQstatus.getImage(), holder.image, options);
			holder.image.setTag(R.string.first_tag, image_url);
			holder.image.setTag(R.string.second_tag, mQstatus.getMediun_image());
			holder.image.setOnClickListener(imageClickListener);
		} else {
			holder.image.setVisibility(View.GONE);
		}
		holder.from.setText(mQstatus.getFrom());

		if (!mQstatus.getHead().equals("") && mQstatus.getHead() != null) {
			imageLoader.displayImage(mQstatus.getHead(), holder.head, options);
		}

		holder.mcount.setText(mQstatus.getMcount());
		holder.count.setText(mQstatus.getCount());
		holder.timeStamp.setVisibility(View.VISIBLE);
		if (!mQstatus.getCreated_at().equals(""))
			holder.timeStamp.setText(mQstatus.getCreated_at());
		else
			holder.timeStamp.setVisibility(View.INVISIBLE);
		if (mQstatus.getIsVip() == 1)
			holder.isVip.setVisibility(View.VISIBLE);
		else
			holder.isVip.setVisibility(View.INVISIBLE);

		// ------------------------转发微博----------------------------------
		QSource mQSource = mQstatus.getSource();
		if(mQSource == null) mQstatus.setIsVisible(false);
		else{
			String rt_image_url =mQSource.getSource_image();
			if (!rt_image_url.equals("")) {
				holder.source_image.setVisibility(View.VISIBLE);
				imageLoader.displayImage(mQSource.getSource_image(), holder.source_image, options);
				holder.source_image.setTag(R.string.first_tag, rt_image_url);
				holder.source_image.setTag(R.string.second_tag, mQSource.getSource_medium_image());
				holder.source_image.setOnClickListener(imageClickListener);
			} else
				holder.source_image.setVisibility(View.GONE);
			if (!mQSource.getSource_text().equals("")) {
				holder.source_text.setVisibility(View.VISIBLE);
				holder.source_text.setText(mQSource.getSource_text());
			} else
				holder.source_text.setVisibility(View.GONE);
		}
		if (!mQstatus.getIsVisible()) holder.source_ll.setVisibility(View.GONE);
		else
			holder.source_ll.setVisibility(View.VISIBLE);
		

		return convertView;
	}


	static class ViewHolder {
		ImageView head;			// 头像
		TextView nick;			// 用户名
		TextView origText;		// 微博文本
		ImageView image;		// 微博图片
		TextView from;			// 平台来源
		ImageView isVip;
		TextView mcount;
		TextView count;
		TextView timeStamp;
		// --------转发微博-----------------
		TextView source_text;	// 转发微博文字
		ImageView source_image;	// 转发微博图片
		View source_ll;			// 转发微博父容器
	}
	
	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			QStatus mStatus = (QStatus)v.getTag();
			//UIHelper.showUserCenter(v.getContext(), mStatus.getAuthorId(), mStatus.getAuthor());
		}
	};
	
	private View.OnClickListener imageClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			UIHelper.showImageDialog(v.getContext(), (String)v.getTag(R.string.first_tag), (String)v.getTag(R.string.second_tag));
		}
	};
}
