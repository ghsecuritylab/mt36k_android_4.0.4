package com.mediatek.tvcm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mediatek.common.capture.MtkCaptureCapability;
import com.mediatek.common.capture.MtkCaptureInfo;
import com.mediatek.common.capture.MtkCaptureLogo;
import com.mediatek.common.capture.MtkCaptureLogoSaveInfo;
import com.mediatek.common.capture.NotSupportException;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.ConfigService;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.util.Log;
import android.view.WindowManager;

/**
 * TVStorage is a Class provided to App to storage data in both share
 * preferences and sqlite database.
 */
public class TVStorage extends TVComponent {
	private static final String DATABASE_NAME = "tvcm.db";
	private static final int DATABASE_VERSION = 1;

	private static final String SCHEDULE_EVENT_TABLE = "schedule_event_table";

	// the following column name are common for both table schedule_event_table
	// and tv_channel_table
	private static final String _ID = "_id";
	protected static final String CHANNEL_ID = "channel_id";

	protected static final String START_TIME = "start_time";
	protected static final String OFFSET_TIME = "offset_time";
	protected static final String NOTIFY_TIME = "notify_time";

	private static final String TV_CHANNEL_TABLE = "tv_channel_table";
	protected static final String CHANNEL_MASK = "mask";
	protected static final String CHANNEL_NUM = "number";
	protected static final String CHANNEL_USERDATA = "userdata";
	protected static final String CHANNEL_AUDIO_LANGUAGE = "audio_language";

	private SQLiteDatabase db = null;
	private DatabaseHelper dbHelper = null;

	// TODO: Currently we need make this dummy.
	// static boolean dummyMode = true;
	private static TVStorage inst = null;
	private SharedPreferences table = null;
	Setting setting;

	TVStorage(Context context) {
		super(context);
		table = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
	}

	static synchronized TVStorage getInstance(Context context) {
		if (inst == null) {
			inst = new TVStorage(context);
			inst.init();
		}
		return inst;
	}

	void init() {
		update();
		dbHelper = new DatabaseHelper(context);
		// db = dbHelper.getWritableDatabase();
		new Thread(new Runnable() {

			public void run() {
				// this method may take a long time to return, so you should not
				// call it from the application main thread
				db = dbHelper.getWritableDatabase();
			}
		}).start();
	}

	/**
	 * @return the instance of share preferences.
	 */
	public SharedPreferences getSharedPreferences() {
		return table;
	}

	private Editor editor = null;

	/**
	 * @return the editor of share preference.
	 */
	public Editor getEditor() {
		if (editor == null) {
			editor = table.edit();
		}
		return editor;
	}

	public void set(String k, String v) {
		getEditor().putString(k, v);
		getEditor().commit();
		getContent().flushMedia();
	}

	/**
	 * @return the value of Key k
	 * @param k
	 *            the key to identify value
	 */
	public String get(String k) {
		return table.getString(k, null);
	}

	/**
	 * @return the value of key k
	 * @param k
	 *            the key to identify value
	 * @param defVal
	 *            if the Key k do not exit in share preference, return the
	 *            default value.
	 */
	public String get(String k, String defVal) {
		// String v = table.get(k);
		return table.getString(k, defVal);
	}

	String fileName = "tvcm_storage_table";

	public void clean() {
		getEditor().clear();
		getEditor().commit();
		getContent().flushMedia();
	}

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SCHEDULE_EVENT_TABLE + " (" + _ID
					+ " INTEGER PRIMARY KEY," + CHANNEL_ID + " INTEGER,"
					+ START_TIME + " INTEGER," + OFFSET_TIME + " INTEGER,"
					+ NOTIFY_TIME + " INTEGER" + ");");
			db.execSQL("CREATE TABLE " + TV_CHANNEL_TABLE + " (" + _ID
					+ " INTEGER PRIMARY KEY," + CHANNEL_ID
					+ " INTEGER NOT NULL UNIQUE," + CHANNEL_NUM + " INTEGER,"
					+ CHANNEL_MASK + " INTEGER," + CHANNEL_USERDATA
					+ " INTEGER," + CHANNEL_AUDIO_LANGUAGE + " TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE_EVENT_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + TV_CHANNEL_TABLE);
			onCreate(db);
		}
	}

	public void insertTVEventRecord(int ch, long start, long offset) {

		ContentValues val = new ContentValues();
		val.put(CHANNEL_ID, ch);
		val.put(START_TIME, start);
		val.put(OFFSET_TIME, offset);
		val.put(NOTIFY_TIME, start + offset);
		if (db != null) {
			db.insert(SCHEDULE_EVENT_TABLE, null, val);
			getContent().flushMedia();
		}
	}

	protected void deleteTimeupTVEventRecords(long curTime) {
		if (db != null) {
			db.delete(SCHEDULE_EVENT_TABLE, NOTIFY_TIME + "<" + "?",
					new String[] { Long.toString(curTime) });
			getContent().flushMedia();
		}
	}

	protected void deleteTVEventRecord(int ch, long start) {
		String where = new StringBuffer(CHANNEL_ID).append("=").append(ch)
				.append(" AND (").append(START_TIME).append("=").append(start)
				.append(")").toString();
		TVComponent.TvLog(where);
		if (db != null) {
			db.delete(SCHEDULE_EVENT_TABLE, where, null);
			getContent().flushMedia();
		}
	}

	protected void deleteAllTVEventRecords() {
		if (db != null) {
			db.delete(SCHEDULE_EVENT_TABLE, null, null);
			getContent().flushMedia();
		}
	}

	/**
	 * In order to avoid Warning:Finalizing a Cursor that has not been
	 * deactivated or closed, you should close cursor object when you don't need
	 * it any more.
	 */
	protected Cursor queryTVEventRecords() {
		if (db != null) {
			Cursor cursor = db.query(SCHEDULE_EVENT_TABLE, new String[] {
					CHANNEL_ID, START_TIME, OFFSET_TIME, NOTIFY_TIME }, null,
					null, null, null, null);
			return cursor;
		}
		return null;
	}

	protected Integer[] queryDistinctTVChannelsID() {
		List<Integer> ids = new ArrayList<Integer>();
		if (db != null) {
			Cursor cursor = db.query(true, SCHEDULE_EVENT_TABLE,
					new String[] { CHANNEL_ID }, null, null, null, null, null,
					null);
			try {
				cursor.moveToFirst();
				int index = cursor.getColumnIndex(CHANNEL_ID);
				for (int i = 0; i < cursor.getCount(); i++) {
					ids.add(cursor.getInt(index));
					cursor.moveToNext();
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		if (ids.size() == 0) {
			return null;
		} else {
			return ids.toArray(new Integer[0]);
		}
	}

	// svlRecID is unique in table
	protected Cursor queryTVChannelRecords(int svlRecID) {
		if (db != null) {
			Cursor cursor = db.query(TV_CHANNEL_TABLE, new String[] {},
					CHANNEL_ID + "=?", new String[] { Integer
							.toString(svlRecID) }, null, null, null);
			return cursor;
		}
		return null;
	}

	protected void updateTVChannelRecords(int svlRecID, ContentValues values) {

		if (db != null) {
			db.update(TV_CHANNEL_TABLE, values, CHANNEL_ID + "=?",
					new String[] { Integer.toString(svlRecID) });
			getContent().flushMedia();
		}
	}

	protected void insertTVChannelRecord(ContentValues values) {
		if (db != null) {
			db.insert(TV_CHANNEL_TABLE, null, values);
			getContent().flushMedia();
		}
	}

	protected void deleteAllTVChannelRecords() {
		if (db != null) {
			db.delete(TV_CHANNEL_TABLE, null, null);
			getContent().flushMedia();
		}
	}

	/**
	 * Print data in database, only for debug.
	 */
	public void printTVEventRecords() {
		Cursor cursor = queryTVEventRecords();
		if (cursor == null)
			return;
		try {
			cursor.moveToFirst();
			int chIndex = cursor.getColumnIndex(CHANNEL_ID);
			int startIndex = cursor.getColumnIndex(START_TIME);
			int offsetIndex = cursor.getColumnIndex(OFFSET_TIME);
			int notifyIndex = cursor.getColumnIndex(NOTIFY_TIME);

			for (int i = 0; i < cursor.getCount(); i++) {
				TVComponent.TvLog("\n Channel ID:" + cursor.getInt(chIndex)
						+ "  START TIME:" + cursor.getLong(startIndex)
						+ "  OFFSET TIME:" + cursor.getLong(offsetIndex)
						+ "  NOTIFY TIME:" + cursor.getLong(notifyIndex));
				cursor.moveToNext();
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * @deprecated
	 * @author mtk40063
	 * 
	 */
	public static class Setting implements Serializable, Cloneable {
		final int magic = 0xabcdef1;
		int version = 0;
		boolean currentIsStart = true;
		short startChNum = 0;
		short inputSrc = 0;
		int usrData[] = new int[8];

		Hashtable<String, String> usrFields = new Hashtable<String, String>();

		Setting() {
			version = 2;
			currentIsStart = true;
			startChNum = 0;
			inputSrc = 0;
			for (int i = 0; i < usrData.length; i++) {
				usrData[i] = 0;
			}
		}

		/**
		 * @deprecated
		 * 
		 */
		public synchronized boolean isCurrentAsStartChannel() {
			return currentIsStart;
		}

		public synchronized short getStartChannelNumber() {
			return startChNum;
		}

		/*
		 * 
		 */
		synchronized void setStartChannel(short num) {
			startChNum = num;
		}

		/**
		 * @deprecated
		 * @param num
		 */
		synchronized void forceSetStartChannelNumber(short num) {
			currentIsStart = false;
			startChNum = num;
		}

		/**
		 * @deprecated
		 * 
		 */
		synchronized void unsetStartChannelNumber() {
			currentIsStart = true;
		}

		/**
		 * @deprecated
		 * @param num
		 */
		synchronized void setStartChannelAsCurrent(short num) {
			if (currentIsStart) {
				startChNum = num;
			}
		}

		public synchronized void setInputSrc(short idx) {
			inputSrc = idx;
		}

		public synchronized short getInputSrc() {
			return inputSrc;
		}

		public synchronized int getUsrData(int idx) {
			return usrData[idx];
		}

		public synchronized void setUsrData(int idx, int val) {
			usrData[idx] = val;
		}

		synchronized void writeSetting(DataOutputStream out) throws IOException {
			out.writeInt(magic);
			out.writeInt(version);
			out.writeBoolean(currentIsStart);
			out.writeShort(startChNum);
			out.writeShort(inputSrc);
			for (int i = 0; i < usrData.length; i++) {
				out.writeInt(usrData[i]);
			}
		}

		synchronized void readSetting(DataInputStream in) throws IOException {
			int m = in.readInt();
			if (m == magic) {
				version = in.readInt();
				currentIsStart = in.readBoolean();
				startChNum = in.readShort();
				inputSrc = in.readShort();
				for (int i = 0; i < usrData.length; i++) {
					usrData[i] = in.readInt();
				}
			}
		}
	}

	void updateSetting() {

	}

	/**
	 * @deprecated
	 */
	public void flushSetting() {
	}

	void update() {
	}

	/**
	 * @deprecated
	 */
	public void flush() {
	}

	/**
	 * @deprecated
	 */
	public Setting getSetting() {
		return null;
	}

	/**
	 * Should be called when leave App to close database.
	 */
	public void onStop() {
		closeStorageDB();
	}

	private void closeStorageDB() {
		if (db != null) {
			db.close();
			db = null;
		}
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		closeStorageDB();
		super.finalize();
	}

	public static final int capture_save_index_0 = 0;
	public static final int capture_save_index_1 = 1;
	public static final int capture_save_index_none = 0xff;
	public static final int capture_save_index_default = 0x100;

	private static final String TAG = "TVStorage";

	private byte[] ps_path = null;

	public interface LogoCaptureListener {
		public static final int CAP_COMPLETE = 0;
		public static final int CAP_FAIL = 1;
		public static final int CAP_CANCLE = 2;

		public void onEvent(int event);

	}

	public static int CAP_LOGO_TV = MtkCaptureLogo.CAPTURE_SRC_TYPE_TV_VIDEO;
	public static int CAP_LOGO_MM_VIDEO = MtkCaptureLogo.CAPTURE_SRC_TYPE_MM_VIDEO;
	public static int CAP_LOGO_MM_IMAGE = MtkCaptureLogo.CAPTURE_SRC_TYPE_MM_IMAGE_ANDROID;
	public static int CAP_LOGO_MAX = CAP_LOGO_MM_IMAGE + 1;

	MtkCaptureLogo[] captureLogos = new MtkCaptureLogo[CAP_LOGO_MAX];

	/**
	 * 
	 * @param source
	 *            Source : CAP_LOGO_TV, CAP_LOGO_MM or CAP_LOGO_MM_IMAGE
	 * @param rect
	 *            Can be null, for full screen
	 * @param logoId
	 *            Id
	 * @param listener
	 *            Can be null.
	 */

	private void captureCmLogo(int source, Rect rect, final int logoId,
			final LogoCaptureListener listener, byte videoPath, int skBitmap,
			int bufferWidth, int bufferHeight, int bufferPitch, int colorMode) {
		if (source < 0 || source >= captureLogos.length) {
			throw new IllegalArgumentException();
		}
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int moveable_width = wm.getDefaultDisplay().getWidth();
		int moveable_height = wm.getDefaultDisplay().getHeight();
		if (rect == null) {
			rect = new Rect(0, 0, moveable_width, moveable_height);
		}
		MtkCaptureInfo mtkCaptureInfo = new MtkCaptureInfo(
				MtkCaptureLogo.CAP_FMT_TYPE_MPEG, 80, 262144,
				MtkCaptureLogo.CAP_OUT_RES_TYPE_HD, rect, moveable_width,
				moveable_height, videoPath, skBitmap, bufferWidth,
				bufferHeight, bufferPitch, colorMode);

		if (captureLogos[source] == null) {
			captureLogos[source] = new MtkCaptureLogo(source);
		}
		final MtkCaptureLogo logo = captureLogos[source];

		logo.setOnEventListener(new MtkCaptureLogo.OnEventListener() {
			public boolean onEvent(MtkCaptureLogo cap, final int event) {
				getHandler().post(new Runnable() {
					public void run() {
						if (listener != null) {

							if (event == MtkCaptureLogo.CAP_EVENT_TYPE_SAVE_DONE) {
								listener
										.onEvent(TVStorage.LogoCaptureListener.CAP_COMPLETE);
							} else if (event == MtkCaptureLogo.CAP_EVENT_TYPE_CAP_ERR
									|| event == MtkCaptureLogo.CAP_EVENT_TYPE_OPEN_ERROR
									|| event == MtkCaptureLogo.CAP_EVENT_TYPE_SAVE_ERROR) {
								listener
										.onEvent(TVStorage.LogoCaptureListener.CAP_FAIL);
							} else if (event == MtkCaptureLogo.CAP_EVENT_TYPE_CAP_DONE) {
								MtkCaptureLogoSaveInfo mtkCaptureSaveinfo = new MtkCaptureLogoSaveInfo(
										MtkCaptureLogo.CAP_DEVICE_TYPE_INTERNAL,
										logoId, ps_path);
								logo.SaveAsBootLogo(mtkCaptureSaveinfo);

							}
						}
					}
				});
				return false;
			}
		});
		try{
			logo.Capture(mtkCaptureInfo);
			} catch(NotSupportException e){
				e.printStackTrace();
				}
	}

	/**
	 * 
	 * @param source
	 * @param rect
	 * @param logoId
	 * @param listener
	 */
	public void captureLogo(int source, Rect rect, final int logoId,
			final LogoCaptureListener listener) {

		captureCmLogo(source, rect, logoId, listener, (byte) 0, 0, 0, 0, 0, 0);
	}

	/**
	 * for picture.
	 * 
	 * @param source
	 * @param rect
	 * @param logoId
	 * @param listener
	 * @param skBitmap
	 * @param bufferWidth
	 * @param bufferHeight
	 * @param bufferPitch
	 * @param colorMode
	 */
	public void captureLogo(int source, Rect rect, final int logoId,
			final LogoCaptureListener listener, int skBitmap, int bufferWidth,
			int bufferHeight, int bufferPitch, int colorMode) {

		captureCmLogo(source, rect, logoId, listener, (byte) 0, skBitmap,
				bufferWidth, bufferHeight, bufferPitch, colorMode);
	}

	public void cancleCaptureLogo(int source) {
		if (source < 0 || source >= captureLogos.length) {
			throw new IllegalArgumentException();
		}
		if (captureLogos[source] != null) {
			captureLogos[source].Stop();

		}
	}

	/**
	 * release resouce when finished
	 * 
	 * @param source
	 */
	public void finishCaptureLogo(int source) {
		if (source < 0 || source >= captureLogos.length) {
			throw new IllegalArgumentException();
		}
		if (captureLogos[source] != null) {
		}
	}

	public void setBootLogo(int source, int id) {
		if (source < 0 || source >= captureLogos.length) {
			throw new IllegalArgumentException();
		}
		if (captureLogos[source] == null) {
			captureLogos[source] = new MtkCaptureLogo(source);
		}
		MtkCaptureLogo logo = captureLogos[source];
		MtkCaptureCapability capCapability = logo.QueryCapability();
		if (id == capture_save_index_default) {
			if (capCapability.b_default_exist) {
				logo.SelectAsBootLogo(MtkCaptureLogo.CAP_DEVICE_TYPE_DEFAULT,
						id);
			} else {
				logo.SelectAsBootLogo(MtkCaptureLogo.CAP_DEVICE_TYPE_INTERNAL,
						capture_save_index_none);
			}
		} else {
			// fix the problem capture logo not correct when clear eeprom
			logo.SelectAsBootLogo(MtkCaptureLogo.CAP_DEVICE_TYPE_INTERNAL, id);
		}

	}
	/*Capture TV Screen and save to "path"*/
	public void captureTVPic(int source, Rect rect, final int logoId,
			final LogoCaptureListener listener, String path)
	{
		captureCmTVPic(source, rect, logoId, listener, (byte) 0, 0, 0, 0, 0, 0, path);
	}
	
	private void captureCmTVPic(int source, Rect rect, final int logoId,
			final LogoCaptureListener listener, byte videoPath, int skBitmap, int bufferWidth,
			int bufferHeight, int bufferPitch, int colorMode, final String path) {
		
		
		if(source != TVStorage.CAP_LOGO_TV) throw new IllegalArgumentException();
		
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int moveable_width = wm.getDefaultDisplay().getWidth();
		int moveable_height = wm.getDefaultDisplay().getHeight();
		if (rect == null) {
			rect = new Rect(0, 0, moveable_width, moveable_height);
		}
		MtkCaptureInfo mtkCaptureInfo = new MtkCaptureInfo(
				MtkCaptureLogo.CAP_FMT_TYPE_JPEG, 80, 262144,
				MtkCaptureLogo.CAP_OUT_RES_TYPE_HD, rect, moveable_width,
				moveable_height, videoPath, skBitmap, bufferWidth, bufferHeight, bufferPitch,
				colorMode);

		if (captureLogos[source] == null) {
			captureLogos[source] = new MtkCaptureLogo(source);
		}
		final MtkCaptureLogo logo = captureLogos[source];

		logo.setOnEventListener(new MtkCaptureLogo.OnEventListener() {
			public boolean onEvent(MtkCaptureLogo cap, final int event) {
				getHandler().post(new Runnable() {
					public void run() {
						if (listener != null) {

							if (event == MtkCaptureLogo.CAP_EVENT_TYPE_SAVE_DONE) {
								listener
										.onEvent(TVStorage.LogoCaptureListener.CAP_COMPLETE);
							} else if (event == MtkCaptureLogo.CAP_EVENT_TYPE_CAP_ERR
									|| event == MtkCaptureLogo.CAP_EVENT_TYPE_OPEN_ERROR
									|| event == MtkCaptureLogo.CAP_EVENT_TYPE_SAVE_ERROR) {
								listener
										.onEvent(TVStorage.LogoCaptureListener.CAP_FAIL);
							} else if (event == MtkCaptureLogo.CAP_EVENT_TYPE_CAP_DONE) {

								logo.Save(path);
								

							}
						}
					}
				});
				return false;
			}
		});
	
				try{
			logo.Capture(mtkCaptureInfo);
			} catch(NotSupportException e){
				e.printStackTrace();
				}
		
	}

	/**
	 * is capture logo can do or not.
	 * @return boolean
	 */
	public boolean isCaptureLogo() {
		BroadcastService brdSrv = (BroadcastService) getTVMngr().getService(
				BroadcastService.BrdcstServiceName);
		if (brdSrv != null) {
			try {
				return brdSrv.isCaptureLogo();
			} catch (TVMException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
