package com.seclab.microblogshare.net;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import com.seclab.microblogshare.bean.Status;

public class SinaApi {
	private String mdata = null;
	public SinaApi(){};

	@SuppressWarnings("static-access")
	public  List<Status> getStatusList(String object)throws WeiboException {
//		String jsonData = jsonStrData;
		List<Status> statusList = new ArrayList<Status>();
		JSONObject jsonStatus = null;
		try {
			jsonStatus = new JSONObject(object);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		JSONArray jArrayStatus = null;
//		if (!jsonStatus.isNull("statuses")) {
			if (!jsonStatus.isNull("statuses")) {
			try {
				jArrayStatus = jsonStatus.getJSONArray("statuses");
				if (!jsonStatus.isNull("reposts")) {
					jArrayStatus = jsonStatus.getJSONArray("reposts");
				}
				int size = jArrayStatus.length();
				statusList = new ArrayList<Status>(size);
				for (int i = 0; i < size; i++) {
					statusList.add(Status.getStatus(jArrayStatus.getJSONObject(i)));
				}
			} catch (JSONException e) {

				e.printStackTrace();
			} catch (WeiboException e) {

				e.printStackTrace();
			}
		}
		return statusList;
	}

}
