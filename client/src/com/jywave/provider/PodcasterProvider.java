package com.jywave.provider;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.jywave.AppMain;
import com.jywave.net.ApiRequest;
import com.jywave.net.ApiResponse;
import com.jywave.sql.DatabaseHelper;
import com.jywave.util.CommonUtil;
import com.jywave.vo.Podcaster;

public class PodcasterProvider {
	public static final String TAG = "PodcasterProvider";
	public AppMain app = AppMain.getInstance();
	private Context context;

	public static enum resultCode {
		NETWORK_ERROR, DONE, FAILED
	};

	// Database
	public DatabaseHelper database;

	public PodcasterProvider(Context context) {
		database = new DatabaseHelper(context);
		this.context = context;
	}

	public void save(JSONObject podcaster) {
		String id = "";

		try {
			id = podcaster.getString("id");
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT id FROM podcasters WHERE id=?",
				new String[] { id });

		try {

			if (c.moveToFirst()) {

				ContentValues cv = new ContentValues();
				cv.put("name", podcaster.getString("name"));
				cv.put("description", podcaster.getString("description"));
				cv.put("heart", podcaster.getInt("heart"));
				cv.put("avatarUrl", podcaster.getString("avatarUrl"));
				cv.put("updateTime", podcaster.getLong("updateTime"));

				db.update("podcasters", cv, "id=?",
						new String[] { podcaster.getString("id") });

			} else {
				ContentValues cv = new ContentValues();
				cv.put("id", podcaster.getInt("id"));
				cv.put("name", podcaster.getString("name"));
				cv.put("description", podcaster.getString("description"));
				cv.put("heart", podcaster.getInt("heart"));
				cv.put("avatarUrl", podcaster.getString("avatarUrl"));
				cv.put("updateTime", podcaster.getLong("updateTime"));
				cv.put("iLikeIt", 0);

				db.insert("podcasters", null, cv);
			}
			db.close();
			c.close();
		} catch (SQLException e) {
			db.close();
			c.close();
			Log.e(TAG, e.toString());
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	private void deleteFromDB(int id) {
		SQLiteDatabase db = database.getWritableDatabase();
		try {
			db.delete("podcasters", "id=?", new String[] { String.valueOf(id) });
			db.close();
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			db.close();
		}
	}

	public resultCode sync() {

		Log.d(TAG, "syncing");
		if (CommonUtil.checkNetState(context)) {
			try {
				ApiRequest request = new ApiRequest();
				ApiResponse response = request.get(AppMain.apiLocation
						+ "getPodcasterTimestamps");

				if (response.httpCode == 200) {

					Log.d(TAG, String.valueOf(response.json.toString()));

					JSONArray list = response.json
							.getJSONArray("podcasterTimestamps");

					SQLiteDatabase db = database.getWritableDatabase();
					Cursor c = db
							.rawQuery(
									"SELECT id, updateTime FROM podcasters ORDER BY id",
									new String[] {});

					if (c.getCount() > 0) {
						ArrayList<String> idToUpdate = new ArrayList<String>();
						
						int lengthJson = list.length();
						int lengthDb = c.getCount();
						int i = 0, j = 0;
						
						for(i = 0; i < lengthJson; i++)
						{
							if(j >= lengthDb)
							{
								break;
							}
							else
							{
								c.moveToPosition(j);
							}
							JSONObject epJson = list.getJSONObject(i);
							int idDB = c.getInt(c.getColumnIndex("id"));
							int idJSON = epJson.getInt("id");
							
							if(j < lengthDb)
							{
								if (idDB == idJSON) {
									if (!c.getString(c.getColumnIndex("updateTime")).equals(epJson.getString("updateTime"))) {

										idToUpdate.add(String.valueOf(idJSON));
									}
									j++;
								}
								else if(idDB < idJSON)
								{
									deleteFromDB(idDB);
									idToUpdate.add(String.valueOf(idJSON));
									j++;
								}
								else
								{
									idToUpdate.add(String.valueOf(idJSON));
								}
							}
						}
						
						while(j < lengthDb)
						{
							c.moveToPosition(j);
							int id = c.getInt(c.getColumnIndex("id"));
							deleteFromDB(id);
							j++;
							
						}

						while(i < lengthJson)
						{
							String id = list.getJSONObject(i).getString("id");
							idToUpdate.add(id);
							i++;
						}

						db.close();
						c.close();

						Log.d(TAG,
								"podcasterIdToUpdate: "
										+ String.valueOf(idToUpdate.size()));

						if (idToUpdate.size() > 0) {
							String requestBody = "{\"ids\":["
									+ idToUpdate.get(0);
							for (i = 1; i < idToUpdate.size(); i++) {
								requestBody = requestBody + ", "
										+ idToUpdate.get(i);
							}
							requestBody = requestBody + "]}";

							Log.d(TAG, "requestBody: " + requestBody);

							response = request.post(AppMain.apiLocation
									+ "getPodcastersByIds", new JSONObject(
									requestBody));

							Log.d(TAG,
									"getPodcastersByIds: "
											+ String.valueOf(response.httpCode)
											+ ", " + response.json.toString());

							if (response.httpCode == 200) {
								list = response.json.getJSONArray("podcasters");

								for (i = 0; i < list.length(); i++) {
									save(list.getJSONObject(i));
								}

								return resultCode.DONE;
							} else {
								Log.e(TAG,
										"Service error, Method name: PodcasterProvider.sync(), http code:"
												+ String.valueOf(response.httpCode));
								return resultCode.FAILED;
							}

						} else {
							return resultCode.DONE;
						}
					} else {

						db.close();
						c.close();

						response = request.get(AppMain.apiLocation
								+ "getAllPodcasters");

						if (response.httpCode == 200) {
							list = response.json.getJSONArray("podcasters");

							Log.d(TAG, response.json.toString());

							for (int i = 0; i < list.length(); i++) {
								save(list.getJSONObject(i));
							}

							return resultCode.DONE;
						} else {
							Log.e(TAG,
									"Service error, Method name: PodcasterProvider.sync(), http code:"
											+ String.valueOf(response.httpCode));
							return resultCode.FAILED;
						}
					}

				} else {
					Log.e(TAG,
							"Service error, Method name: PodcasterProvider.sync(), http code:"
									+ String.valueOf(response.httpCode));
					return resultCode.FAILED;

				}
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
				return resultCode.FAILED;
			}
		} else {
			return resultCode.NETWORK_ERROR;
		}
	}

	public ArrayList<Podcaster> getAllPodcasters() {
		ArrayList<Podcaster> podcasters = new ArrayList<Podcaster>();

		SQLiteDatabase db = database.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM podcasters ORDER BY id",
				new String[] {});

		while (c.moveToNext()) {
			Podcaster podcaster = new Podcaster();
			podcaster.id = c.getInt(c.getColumnIndex("id"));
			podcaster.name = c.getString(c.getColumnIndex("name"));
			podcaster.description = c
					.getString(c.getColumnIndex("description"));
			podcaster.heart = c.getInt(c.getColumnIndex("heart"));
			podcaster.avatarUrl = c.getString(c.getColumnIndex("avatarUrl"));
			podcaster.updateTime = c.getLong(c.getColumnIndex("updateTime"));

			if (c.getInt(c.getColumnIndex("iLikeIt")) == 1) {
				podcaster.iLikeIt = true;
			} else {
				podcaster.iLikeIt = false;
			}

			podcasters.add(podcaster);
		}

		db.close();
		c.close();

		if (podcasters.size() > 0) {
			return podcasters;
		} else {
			podcasters = null;
			return null;
		}
	}

	public resultCode iLikeIt(int id) {

		SQLiteDatabase db = database.getWritableDatabase();

		if (CommonUtil.checkNetState(context)) {
			try {

				ApiRequest request = new ApiRequest();
				ApiResponse response = request.post(AppMain.apiLocation
						+ "iLikePodcaster/" + String.valueOf(id),
						new JSONObject());

				Log.d(TAG, response.json.toString());

				if (response.httpCode == 200) {

					int podcasterId = response.json.getJSONObject(
							"iLikePodcaster").getInt("id");
					int heart = response.json.getJSONObject("iLikePodcaster")
							.getInt("heart");

					ContentValues cv = new ContentValues();
					cv.put("iLikeIt", 1);
					cv.put("heart", heart);

					db.update("podcasters", cv, "id=?",
							new String[] { String.valueOf(podcasterId) });
					db.close();

					// int index = app.podcastersList.getIndexById(podcasterId);
					// app.podcastersList.get(index).heart = heart;

					return resultCode.DONE;

				} else {
					Log.e(TAG,
							"Service error, Method name: iLikePodcaster, http code:"
									+ String.valueOf(response.httpCode));
					return resultCode.FAILED;

				}

			} catch (Exception e) {
				db.close();
				Log.e(TAG, e.toString());
				return resultCode.FAILED;
			}
		} else {
			return resultCode.NETWORK_ERROR;
		}
	}

	public resultCode iDontLikeIt(int id) {

		SQLiteDatabase db = database.getWritableDatabase();

		if (CommonUtil.checkNetState(context)) {
			try {

				ApiRequest request = new ApiRequest();
				ApiResponse response = request.post(AppMain.apiLocation
						+ "iDontLikePodcaster/" + String.valueOf(id),
						new JSONObject());

				if (response.httpCode == 200) {

					int podcasterId = response.json.getJSONObject(
							"iDontLikePodcaster").getInt("id");
					int heart = response.json.getJSONObject(
							"iDontLikePodcaster").getInt("heart");

					ContentValues cv = new ContentValues();
					cv.put("iLikeIt", 0);
					cv.put("heart", heart);

					db.update("podcasters", cv, "id=?",
							new String[] { String.valueOf(podcasterId) });
					db.close();

					// int index = app.podcastersList.getIndexById(podcasterId);
					// app.podcastersList.get(index).heart = heart;

					return resultCode.DONE;

				} else {
					Log.e(TAG,
							"Service error, Method name: iDontLikePodcaster, http code:"
									+ String.valueOf(response.httpCode));
					return resultCode.FAILED;

				}

			} catch (Exception e) {
				db.close();
				Log.e(TAG, e.toString());
				return resultCode.FAILED;
			}
		} else {
			return resultCode.NETWORK_ERROR;
		}
	}

}
