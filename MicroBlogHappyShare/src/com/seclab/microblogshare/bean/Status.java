package com.seclab.microblogshare.bean;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;





import com.seclab.microblogshare.net.WeiboException;

import cn.sharesdk.dropbox.e;


public class Status implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private final static String TAG = "Status";

	private  User user = null;                            //作者信息
	private  String createdAt="";                         //status创建时间
	private  String id="";                                //status id
	private  String mid;                                  //微博MID
	private  long idstr;                                  //保留字段，请勿使用                     
	private  String text="";                              //微博内容
	private  Source source;                               //微博来源
	private  boolean favorited;                           //是否已收藏
	private  boolean truncated;
	private  long inReplyToStatusId;                      //回复ID
	private  long inReplyToUserId;                        //回复人ID
	private  String inReplyToScreenName="";               //回复人昵称
	private  String thumbnailPic="";                      //微博内容中的图片的缩略地址
	private  String bmiddlePic="";                        //中型图片
	private  String originalPic="";                       //原始图片
	private  Status retweetedStatus = null;               //转发的博文，内容为status，如果不是转发，则没有此字段
	private  String geo;                                  //地理信息，保存经纬度，没有时不返回此字段
	private  double latitude = -1;                        //纬度
	private  double longitude = -1;                       //经度
	private  int repostsCount= 0;                         //转发数
	private  int commentsCount= 0;                        //评论数
	private  String annotations;                          //元数据，没有时不返回此字段
	private  int mlevel= 0;
	private  String visible;
	
	private Status()  {
	
	}

	public static Status getStatus(JSONObject json) throws WeiboException {
		Status mStatus = new Status();
		try {
			if(!json.isNull("created_at")) mStatus.createdAt = json.getString("created_at");
			if(!json.isNull("id")) mStatus.id = json.getString("id");
			if(!json.isNull("mid")) mStatus.mid = json.getString("mid");
			if(!json.isNull("idstr")) mStatus.idstr = json.getLong("idstr");
			if(!json.isNull("text")) mStatus.text = json.getString("text");
			if (json.getString("source").length() != 0) mStatus.source = new Source(json.getString("source"));
			// inReplyToStatusId = getLong("in_reply_to_status_id", json);
			// inReplyToUserId = getLong("in_reply_to_user_id", json);
			// inReplyToScreenName=json.getString("in_reply_toS_screenName");
			// favorited = getBoolean("favorited", json);
			// truncated = getBoolean("truncated", json);
			if(!json.isNull("thumbnail_pic")) mStatus.thumbnailPic = json.getString("thumbnail_pic");
			if(!json.isNull("bmiddle_pic")) mStatus.bmiddlePic = json.getString("bmiddle_pic");
			if(!json.isNull("original_pic")) mStatus.originalPic = json.getString("original_pic");
			if(!json.isNull("reposts_count")) mStatus.repostsCount = json.getInt("reposts_count");
			if(!json.isNull("comments_count")) mStatus.commentsCount = json.getInt("comments_count");
			//annotations = json.getString("annotations");
			if (!json.isNull("user")) mStatus.user = new User(json.getJSONObject("user"));
			if (!json.isNull("retweeted_status")) mStatus.retweetedStatus = getStatus(json.getJSONObject("retweeted_status"));
			if(!json.isNull("mlevel")) mStatus.mlevel = json.getInt("mlevel");
			//if(!json.isNull("visible")) visible= new Visible(json.getJSONObject("visible"));
//			Log.v(TAG, "createdAt:   " + createdAt);
//			Log.v(TAG, "id:   " + id);
		} catch (JSONException je) {
//			throw new WeiboException(je.getMessage() + ":" + json.toString(), je);
			je.printStackTrace();
		}
		return mStatus;
	}

	public static Status getStatus(HashMap<String, Object> hashMap) throws WeiboException {
		Status mStatus = new Status();
		//if(!json.isNull("created_at")) mStatus.createdAt = json.getString("created_at");
		if(!hashMap.containsKey("created_at")) mStatus.createdAt = (String) hashMap.get("created_at");
		if(!hashMap.containsKey("id")) mStatus.id = (String) hashMap.get("id");
		if(!hashMap.containsKey("mid")) mStatus.mid = (String) hashMap.get("mid");
		if(!hashMap.containsKey("idstr")) mStatus.idstr =  (Long) hashMap.get("idstr");
		if(!hashMap.containsKey("text")) mStatus.mid = (String) hashMap.get("text");
		if(!hashMap.containsKey("source")) mStatus.mid = (String) hashMap.get("source");
		
		
//			if(!json.isNull("id")) mStatus.id = json.getString("id");
//			if(!json.isNull("mid")) mStatus.mid = json.getString("mid");
//			if(!json.isNull("idstr")) mStatus.idstr = json.getLong("idstr");
//			if(!json.isNull("text")) mStatus.text = json.getString("text");
//			if (json.getString("source").length() != 0) mStatus.source = new Source(json.getString("source"));
		// inReplyToStatusId = getLong("in_reply_to_status_id", json);
		// inReplyToUserId = getLong("in_reply_to_user_id", json);
		// inReplyToScreenName=json.getString("in_reply_toS_screenName");
		// favorited = getBoolean("favorited", json);
		// truncated = getBoolean("truncated", json);
		if(!hashMap.containsKey("thumbnail_pic")) mStatus.thumbnailPic = (String) hashMap.get("thumbnail_pic");
		if(!hashMap.containsKey("bmiddle_pic")) mStatus.bmiddlePic = (String) hashMap.get("bmiddle_pic");
		if(!hashMap.containsKey("original_pic")) mStatus.originalPic = (String) hashMap.get("original_pic");
		if(!hashMap.containsKey("reposts_count")) mStatus.repostsCount = Integer.parseInt((String) hashMap.get("reposts_count"));
		if(!hashMap.containsKey("comments_count")) mStatus.commentsCount = Integer.parseInt((String) hashMap.get("comments_count"));;
		return mStatus;
	}
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public long getIdstr() {
		return idstr;
	}

	public void setIdstr(long idstr) {
		this.idstr = idstr;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(long inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public String getThumbnailPic() {
		return thumbnailPic;
	}

	public void setThumbnailPic(String thumbnailPic) {
		this.thumbnailPic = thumbnailPic;
	}

	public String getBmiddlePic() {
		return bmiddlePic;
	}

	public void setBmiddlePic(String bmiddlePic) {
		this.bmiddlePic = bmiddlePic;
	}

	public String getOriginalPic() {
		return originalPic;
	}

	public void setOriginalPic(String originalPic) {
		this.originalPic = originalPic;
	}

	public Status getRetweetedStatus() {
		return retweetedStatus;
	}

	public void setRetweetedStatus(Status retweetedStatus) {
		this.retweetedStatus = retweetedStatus;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getRepostsCount() {
		return repostsCount;
	}

	public void setRepostsCount(int repostsCount) {
		this.repostsCount = repostsCount;
	}

	public int getCommentsCount() {

		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public String getAnnotations() {
		return annotations;
	}

	public void setAnnotations(String annotations) {
		this.annotations = annotations;
	}

	public int getMlevel() {
		return mlevel;
	}

	public void setMlevel(int mlevel) {
		this.mlevel = mlevel;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}
	
	@Override
	public String toString(){
		if (user!=null) return "id:"+id+", text:"+text+", source:"+source+", createdAt:"+createdAt+", UserName:"+user.getName();
		else
			return "id:"+id+", text:"+text+", source:"+source+", createdAt:"+createdAt;
		
	}
}
