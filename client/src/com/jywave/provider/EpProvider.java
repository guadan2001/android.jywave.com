package com.jywave.provider;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.jywave.AppMain;
import com.jywave.net.ApiRequest;
import com.jywave.net.ApiResponse;
import com.jywave.sql.DatabaseHelper;
import com.jywave.vo.Ep;

public class EpProvider {

	public static final String TAG = "EpProvider";
	public AppMain app = AppMain.getInstance();
	private Context context;

	// Database
	public DatabaseHelper database;

	public EpProvider(Context context) {
		database = new DatabaseHelper(context);
		this.context = context;
	}

	public Ep getEp(int id) {
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM eps WHERE id=?",
				new String[] { String.valueOf(id) });

		if (c.moveToFirst()) {
			Ep ep = new Ep();

			ep.id = c.getInt(c.getColumnIndex("id"));
			ep.title = c.getString(c.getColumnIndex("title"));
			ep.category = c.getString(c.getColumnIndex("category"));
			ep.sn = c.getInt(c.getColumnIndex("sn"));
			ep.duration = c.getInt(c.getColumnIndex("duration"));
			ep.description = c.getString(c.getColumnIndex("description"));
			ep.url = c.getString(c.getColumnIndex("url"));
			ep.coverUrl = c.getString(c.getColumnIndex("coverUrl"));
			ep.coverThumbnailUrl = c.getString(c
					.getColumnIndex("coverThumbnailUrl"));
			ep.star = c.getInt(c.getColumnIndex("star"));
			ep.rating = c.getInt(c.getColumnIndex("rating"));
			ep.publishDate = c.getLong(c.getColumnIndex("publishDate"));
			
			if (c.getInt(c.getColumnIndex("isNew")) == 1) {
				ep.isNew = true;
			} else {
				ep.isNew = false;
			}

			ep.checkStatus();

			db.close();
			c.close();

			return ep;
		} else {
			db.close();
			c.close();

			return null;
		}
	}

	public ArrayList<Ep> getEps(int start, int count) {
		ArrayList<Ep> eps = new ArrayList<Ep>();

		SQLiteDatabase db = database.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM eps ORDER BY id DESC LIMIT ?, ?",
				new String[] { String.valueOf(start), String.valueOf(count) });

		while (c.moveToNext()) {
			Ep ep = new Ep();
			ep.id = c.getInt(c.getColumnIndex("id"));
			ep.title = c.getString(c.getColumnIndex("title"));
			ep.duration = c.getInt(c.getColumnIndex("duration"));
			ep.description = c.getString(c.getColumnIndex("description"));
			ep.category = c.getString(c.getColumnIndex("category"));
			ep.sn = c.getInt(c.getColumnIndex("sn"));
			ep.url = c.getString(c.getColumnIndex("url"));
			ep.coverUrl = c.getString(c.getColumnIndex("coverUrl"));
			ep.coverThumbnailUrl = c.getString(c
					.getColumnIndex("coverThumbnailUrl"));
			ep.star = c.getInt(c.getColumnIndex("star"));
			ep.publishDate = c.getLong(c.getColumnIndex("publishDate"));
			if (c.getInt(c.getColumnIndex("isNew")) == 1) {
				ep.isNew = true;
			} else {
				ep.isNew = false;
			}

			ep.checkStatus();

			eps.add(ep);
		}

		db.close();
		c.close();

		if (eps.size() > 0) {
			return eps;
		} else {
			eps = null;
			return null;
		}
	}

	public static Ep fromJSON(String strJSON) {
		Ep ep = new Ep();
		JSONObject json;
		try {
			json = new JSONObject(strJSON);
			ep.id = Integer.parseInt(json.getString("id"));
			ep.title = json.getString("title");
			ep.duration = Integer.parseInt(json.getString("duration"));
			ep.description = json.getString("description");
			ep.url = json.getString("url");
			ep.coverUrl = json.getString("coverUrl");
			ep.coverThumbnailUrl = json.getString("coverThumbnailUrl");
			ep.star = Integer.parseInt(json.getString("star"));
			ep.publishDate = Long.parseLong(json.getString("publishDate"));

		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}

		return ep;
	}

	public void save(Ep ep) {
		SQLiteDatabase db = database.getWritableDatabase();

		Cursor c = db.rawQuery("SELECT id FROM eps WHERE id=?",
				new String[] { String.valueOf(ep.id) });

		try {
			if (c.moveToFirst()) {
				ContentValues cv = new ContentValues();
				cv.put("title", ep.title);
				cv.put("category", ep.category);
				cv.put("sn", ep.sn);
				cv.put("description", ep.description);
				cv.put("duration", ep.duration);
				cv.put("publishDate", ep.publishDate);
				cv.put("star", ep.star);
				cv.put("rating", ep.rating);
				cv.put("url", ep.url);
				cv.put("coverUrl", ep.coverUrl);
				cv.put("coverThumbnailUrl", ep.coverThumbnailUrl);
				if(ep.isNew)
				{
					cv.put("isNew", 1);
				}
				else
				{
					cv.put("isNew", 0);
				}
				
				
				db.update("eps", cv, "id=?", new String[]{String.valueOf(ep.id)});
			} else {
				ContentValues cv = new ContentValues();
				cv.put("id", ep.id);
				cv.put("title", ep.title);
				cv.put("category", ep.category);
				cv.put("sn", ep.sn);
				cv.put("description", ep.description);
				cv.put("duration", ep.duration);
				cv.put("publishDate", ep.publishDate);
				cv.put("star", ep.star);
				cv.put("rating", ep.rating);
				cv.put("url", ep.url);
				cv.put("coverUrl", ep.coverUrl);
				cv.put("coverThumbnailUrl", ep.coverThumbnailUrl);
				cv.put("isNew", ep.isNew);

				db.insert("eps", null, cv);
				db.execSQL(
						"INSERT INTO eps(id, title, duration, description, url, coverUrl, coverThumbnailUrl, star, publishDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
						new String[] { String.valueOf(ep.id), ep.title,
								String.valueOf(ep.duration), ep.description,
								ep.url, ep.coverUrl, ep.coverThumbnailUrl,
								String.valueOf(ep.star),
								String.valueOf(ep.publishDate) });
			}

			db.close();
			c.close();

		} catch (SQLException e) {
			db.close();
			c.close();
			Log.e(TAG, e.toString());
		}
	}

	public void save(JSONObject ep) {

		String epId = "";

		try {
			epId = ep.getString("id");
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT id FROM eps WHERE id=?",
				new String[] { epId });

		try {

			if (c.moveToFirst()) {
				
				ContentValues cv = new ContentValues();
				cv.put("title", ep.getString("title"));
				cv.put("category", ep.getString("category"));
				cv.put("sn", ep.getInt("sn"));
				cv.put("description", ep.getString("description"));
				cv.put("duration", ep.getInt("duration"));
				cv.put("publishDate", ep.getLong("publishDate"));
				cv.put("star", ep.getInt("star"));
				cv.put("url", ep.getString("url"));
				cv.put("coverUrl", ep.getString("coverUrl"));
				cv.put("coverThumbnailUrl", ep.getString("coverThumbnailUrl"));
				
				db.update("eps", cv, "id=?", new String[]{ep.getString("id")});
				
			} else {
				ContentValues cv = new ContentValues();
				cv.put("id", ep.getInt("id"));
				cv.put("title", ep.getString("title"));
				cv.put("category", ep.getString("category"));
				cv.put("sn", ep.getInt("sn"));
				cv.put("description", ep.getString("description"));
				cv.put("duration", ep.getInt("duration"));
				cv.put("publishDate", ep.getLong("publishDate"));
				cv.put("star", ep.getInt("star"));
				cv.put("rating", 0);
				cv.put("url", ep.getString("url"));
				cv.put("coverUrl", ep.getString("coverUrl"));
				cv.put("coverThumbnailUrl", ep.getString("coverThumbnailUrl"));
				cv.put("isNew", 1);

				db.insert("eps", null, cv);
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

	public void sync() {
		//TODO: Delete "clear()" here.
		//clear();
		try {
			ApiRequest request = new ApiRequest();
			ApiResponse response = request.get(AppMain.apiLocation
					+ "getEpTimestamps");

			if (response.httpCode == 200) {
				
				Log.d(TAG, String.valueOf(response.json.toString()));

				JSONArray list = response.json.getJSONArray("epTimestamps");

				SQLiteDatabase db = database.getWritableDatabase();
				Cursor c = db.rawQuery(
						"SELECT id, publishDate FROM eps ORDER BY id",
						new String[] {});

				if (c.getCount() > 0) {
					ArrayList<String> epIdToUpdate = new ArrayList<String>();

					c.moveToFirst();
					for (int i = 0; i < list.length(); i++) {
						JSONObject epJson = list.getJSONObject(i);

						if (c.getString(c.getColumnIndex("id")).equals(epJson.getString("id"))) {
							if (!c.getString(c.getColumnIndex("publishDate")).equals(epJson.getString("publishDate"))) {

								epIdToUpdate.add(c.getString(c
										.getColumnIndex("id")));
							}
							c.moveToNext();
						}
					}
					
					Log.d(TAG, "epIdToUpdate: " + String.valueOf(epIdToUpdate.size()));
					
					if(epIdToUpdate.size() > 0)
					{
						String requestBody = "{\"ids\":[" + epIdToUpdate.get(0);
						for(int i=1; i<epIdToUpdate.size();i++)
						{
							requestBody = requestBody + ", " + epIdToUpdate.get(i);
						}
						requestBody = requestBody + "]}";
						
						Log.d(TAG, "requestBody: " + requestBody);
						
						response = request.post(AppMain.apiLocation + "getEpsByIds", new JSONObject(requestBody));
						
						Log.d(TAG, "getEpsByIds: " + String.valueOf(response.httpCode) + ", " + response.json.toString());
						
						if (response.httpCode == 200) {
							list = response.json.getJSONArray("eps");

							for (int i = 0; i < list.length(); i++) {
								save(list.getJSONObject(i));
							}
						} else {
							Toast.makeText(this.context, "数据同步失败，请检查网络连接",
									Toast.LENGTH_LONG).show();
							Log.e(TAG,
									"Eps Sync Error, http code:"
											+ String.valueOf(response.httpCode));
						}
						
					}

				} else {
					JSONObject json = list.getJSONObject(list.length() - 1);

					String idMax = json.getString("id");
					response = request.get(AppMain.apiLocation
							+ "getEpsByIdRange/1/" + idMax);

					if (response.httpCode == 200) {
						Log.d(TAG, "getEpsByIdRange: " + response.json.toString());
						list = response.json.getJSONArray("eps");

						for (int i = 0; i < list.length(); i++) {
							save(list.getJSONObject(i));
						}
					} else {
						Toast.makeText(this.context, "数据同步失败，请检查网络连接",
								Toast.LENGTH_LONG).show();
						Log.e(TAG,
								"Eps Sync Error, http code:"
										+ String.valueOf(response.httpCode));
					}
				}

				db.close();
				c.close();
			} else {
				Toast.makeText(this.context, "数据同步失败，请检查网络连接",
						Toast.LENGTH_LONG).show();
				Log.e(TAG,
						"Eps Sync Error, http code:"
								+ String.valueOf(response.httpCode));

			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	private void clear() {
		SQLiteDatabase db = database.getWritableDatabase();
		try {
			db.execSQL("DELETE FROM eps");
		} catch (SQLException e) {
			db.close();
			Log.e(TAG, e.toString());
		}
	}
	
	public int getEpCount()
	{
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT id FROM eps", new String[]{});
		
		int result = c.getCount();
		
		db.close();
		c.close();
		
		return result;
	}
}
