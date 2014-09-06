package com.seclab.microblogshare.adapter;

import java.util.Date;
import java.util.List;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.seclab.microblogshare.R;
import com.seclab.microblogshare.bean.Status;
import com.seclab.microblogshare.bean.User;
import com.seclab.microblogshare.util.TimeUtil;
import com.seclab.microblogshare.util.UIHelper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendTimeLineAdapter extends BaseAdapter{

	private final String TAG = "FriendTimeLineAdapter";
	private Context context;
	private LayoutInflater mInflater;
	public List<Status> mData;
	ViewHolder holder;
	//利用第三方插件来加载图片
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = null;
	static class ViewHolder {
		ImageView avatar;
		TextView name;
		TextView text;
		ImageView image;
		TextView source;
		ImageView verified;
		TextView statuses_count;
		TextView followers_count;
		TextView created_at;
		TextView rt_text;
		ImageView rt_image;
		LinearLayout rt_ll;
	}
	
	
	
	public FriendTimeLineAdapter(Context context,List<Status> mData) {
		super();
		this.context = context;
		this.mData = mData;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this.context));
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		return this.mData != null ? this.mData.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return this.mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Status mStatus = mData.get(position);
		User user = mStatus.getUser();
		Status retweetedStatus = null;
		if (mStatus.getRetweetedStatus() != null) {
			retweetedStatus = mStatus.getRetweetedStatus();
		}
		String avatar_url = user.getProfileImageUrl();
		String name = user.getName();
		String text = mStatus.getText();
		String image_url = mStatus.getThumbnailPic();
		String source = mStatus.getSource().getName();
		Boolean verified = user.isVerified();
		int statuses_count = mStatus.getCommentsCount();
		int followers_count = mStatus.getRepostsCount();
		String created_at = TimeUtil.converTime(new Date(mStatus.getCreatedAt()).getTime() / 1000);

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.weibo_listview_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.item_screen_name);
			holder.avatar = (ImageView) convertView.findViewById(R.id.item_profile_image);holder.text = (TextView) convertView.findViewById(R.id.item_text);
			holder.image = (ImageView) convertView.findViewById(R.id.item_microBlogImage);
			holder.source = (TextView) convertView.findViewById(R.id.item_from);
			holder.statuses_count = (TextView) convertView.findViewById(R.id.item_tweet_statuses_count);
			holder.followers_count = (TextView) convertView.findViewById(R.id.item_tweet_followers_count);
			holder.created_at = (TextView) convertView.findViewById(R.id.item_created_at);
			holder.verified = (ImageView) convertView.findViewById(R.id.item_vipImage);
			holder.rt_text = (TextView) convertView.findViewById(R.id.item_retweeted_status_text);
			holder.rt_image = (ImageView) convertView.findViewById(R.id.item_retweeted_status_microBlogImage);
			holder.rt_ll = (LinearLayout) convertView.findViewById(R.id.item_retweeted_status_ll);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(name);
		holder.text.setText(text);
		// image = mStatus.getThumbnailPic();
		if (!image_url.equals("")) {
			imageLoader.displayImage(image_url, holder.image, options);
			holder.image.setOnClickListener(imageClickListener);
			holder.image.setTag(R.string.first_tag, image_url);
			holder.image.setTag(R.string.second_tag, mStatus.getOriginalPic());
			holder.image.setVisibility(ImageView.VISIBLE);
		} else {
			holder.image.setVisibility(View.GONE);
		}
		holder.source.setText(source);
		if (!avatar_url.equals("")) imageLoader.displayImage(avatar_url, holder.avatar, options);
		holder.statuses_count.setText(statuses_count + "");
		holder.followers_count.setText(followers_count + "");
		holder.created_at.setVisibility(View.VISIBLE);
		if (!created_at.equals("") && created_at != null) holder.created_at.setText(created_at);
		else holder.created_at.setVisibility(View.INVISIBLE);
		if (verified) holder.verified.setVisibility(View.VISIBLE);
		else holder.verified.setVisibility(View.INVISIBLE);

		// ------------------------转发微博----------------------------------
		String rt_text_value = "";
		if (retweetedStatus != null) {
			rt_text_value = retweetedStatus.getText();
			Log.v(TAG, "rt_text_value:  " + rt_text_value);
		} else {
			// do nothing
		}

		if (!rt_text_value.equals("")) {
			holder.rt_text.setVisibility(View.VISIBLE);
			holder.rt_text.setText(rt_text_value);
		} else {
			holder.rt_text.setVisibility(View.GONE);
		}
		String rt_Image_url = "";
		if (retweetedStatus != null) {

			rt_Image_url = retweetedStatus.getThumbnailPic();
			Log.v(TAG, "rt_Image_url:  "+ rt_Image_url);
		} else {
			// do nothing
		}
		if (!rt_Image_url.equals("")) {
			holder.rt_image.setOnClickListener(imageClickListener);
			holder.rt_image.setTag(R.string.first_tag, rt_Image_url);
			holder.rt_image.setTag(R.string.second_tag, retweetedStatus.getOriginalPic());
			holder.rt_image.setVisibility(View.VISIBLE);
			imageLoader.displayImage(rt_Image_url, holder.rt_image, options);
		} else {
			holder.rt_image.setVisibility(View.GONE);
		}
		if (rt_text_value.equals("") && rt_Image_url.equals(""))
			holder.rt_ll.setVisibility(View.GONE);
		else
			holder.rt_ll.setVisibility(View.VISIBLE);

		return convertView;
	}

	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Status mStatus = (Status)v.getTag();
			//UIHelper.showUserCenter(v.getContext(), mStatus.getAuthorId(), mStatus.getAuthor());
		}
	};
	
	
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	private View.OnClickListener imageClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			UIHelper.showImageDialog(v.getContext(), (String)v.getTag(R.string.first_tag), (String)v.getTag(R.string.second_tag));
		}
	};
	
}
