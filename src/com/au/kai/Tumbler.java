package com.au.kai;

import java.util.Date;
import java.util.HashMap;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

public class Tumbler extends ContentProvider {
	// private static String mainuri = August.dataprovider;
	private static final int DB_VERSION = 30;

	private static String TAG = "ok";

	private SQLiteDatabase mDb;
	private DatabaseHelper mDbHelper;

	private static String DATABASE_NAME = "sky.db";

	private static final int ALL_MESSAGES = 1;
	private static final int SPECIFIC_MESSAGE = 2;

	private static final int ALL_MESSAGESFILTER = 3;
	private static final int SPECIFIC_MESSAGEFILTER = 4;

	private static final int ALL_MESSAGESPERSPECTIVE = 5;
	private static final int SPECIFIC_MESSAGEPERSPECTIVE = 6;

	private static final int ALL_MESSAGESRETROSPECT = 7;
	private static final int SPECIFIC_MESSAGERETROSPECT = 8;

	private static final int ALL_MESSAGESXSAW = 9;
	private static final int SPECIFIC_MESSAGEXSAW = 10;

	private static final int SEARCH_MOMENT = 11;
	private static final int GET_MOMENT = 12;
	private static final int SEARCH_SUGGEST = 13;

	private static final int REFRESH_SHORTCUT = 14;

	private static final int ALL_HERE = 23;
	private static final int THIS_HERE = 24;

	private static final int ALL_CHAT = 25;
	private static final int THIS_CHAT = 26;


	// private static String cp = getContext().getString(R.string.cp);
	private static final UriMatcher URI_MATCHER;
	public static String AUTHORITY = "com.au.kai";
	// static String ta =
	// getContext().getResources().getString(R.string.mytablea);
	static {

		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		// URI_MATCHER.addURI("*", "*", ALL_MESSAGES);
		// URI_MATCHER.addURI("*", "*/#", SPECIFIC_MESSAGE);

		URI_MATCHER.addURI(AUTHORITY, "mytablea", SEARCH_MOMENT);
		URI_MATCHER.addURI(AUTHORITY, "mytablea" + "/#", SPECIFIC_MESSAGE);
		// to get suggestions...
		URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,
				SEARCH_SUGGEST);
		URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY
				+ "/*", SEARCH_SUGGEST);

		URI_MATCHER.addURI(AUTHORITY, "here", ALL_HERE);
		URI_MATCHER.addURI(AUTHORITY, "here" + "/#", THIS_HERE);

		URI_MATCHER.addURI(AUTHORITY, "chat", ALL_CHAT);
		URI_MATCHER.addURI(AUTHORITY, "chat" + "/#", THIS_CHAT);


		/*
		 * The following are unused in this implementation, but if we include
		 * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our
		 * suggestions table, we could expect to receive refresh queries when a
		 * shortcutted suggestion is displayed in Quick Search Box, in which
		 * case, the following Uris would be provided and we would return a
		 * cursor with a single item representing the refreshed suggestion data.
		 */
		URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT,
				REFRESH_SHORTCUT);
		URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT
				+ "/*", REFRESH_SHORTCUT);

		// URI_MATCHER.addURI(cp, "moment", ALL_MESSAGES);
		// URI_MATCHER.addURI(getContext().getString(R.string.cp), "moment/#",
		// SPECIFIC_MESSAGE);
		// URI_MATCHER.addURI(getContext().getString(R.string.cp),
		// "perspective", ALL_MESSAGESPERSPECTIVE);
		// URI_MATCHER.addURI(getContext().getString(R.string.cp),
		// "perspective/#", SPECIFIC_MESSAGEPERSPECTIVE);
		// URI_MATCHER.addURI(getContext().getString(R.string.cp), "retrospect",
		// ALL_MESSAGESPERSPECTIVE);
		// URI_MATCHER.addURI(getContext().getString(R.string.cp),
		// "retrospect/#", SPECIFIC_MESSAGEPERSPECTIVE);
		// URI_MATCHER.addURI(getContext().getString(R.string.cp), "filter",
		// ALL_MESSAGESFILTER);
		// URI_MATCHER.addURI(getContext().getString(R.string.cp), "filter/#",
		// SPECIFIC_MESSAGEFILTER);
		// "+getString(R.string.cp)+"
	}
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	boolean ts3 = false;

	// url text unique not null, urltext text, farkpicurl text, farkpictext
	// text, commenturl text, commenttext text, description text

	// Database creation/version management helper.
	// Create it statically because we don't need to have customized instances.
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static String TAG = "DataProviderDB";

		private Context ctx;

		// public DatabaseHelper(Context context, String name, CursorFactory
		// factory, int version) {
		// super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		// }
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
			ctx = context;
			// mLog = new Custom(mContext, TAG + " DatabaseHelper() 52");
			Log.i(TAG, "DatabaseHelper() 53");

		}

		SQLiteDatabase db = null;
		boolean ts3 = false;

		@Override
		public void onCreate(SQLiteDatabase db2) {
			db = db2;
			Log.i(TAG, "onCreate(SQL) DatabaseHelper");
			try {
				// SQL += mContext.getString(R.string.contactStore_columns);
				// USING fts3

				String tablea = ctx.getResources().getString(R.string.mytablea);

				{
					// USING fts3
					String SQL = "";

					// IF NOT EXISTS
					if (ts3) {
						SQL += "create VIRTUAL TABLE " + tablea
								+ " USING fts3 (";
						SQL += "rowid integer primary key";
						SQL += ", atitle text default '', acontent text default '', avec";
						SQL += ", apublished DATE, aurl text, aauthor text";
						SQL += ", lat, lon, adeep, aloclist, aheardlist text default '', animportlist text default '', areflist text default ''";
						SQL += ", mres, hres, pres, mycomment default '', tag INTEGER default 1, refresh integer default 0, refreshstart DATE, refresherror DATE, refreshpass DATE, refreshfail TEXT default '', refreshcookies TEXT default '', refreshjumpurl TEXT default '', refreshstatus TEXT default '', refreshnew INTEGER default 0, refreshready DATE, keyref INTEGER default 0";

						SQL += ", readdepth INTEGER default 0";
						SQL += ", updatedepth INTEGER default 0";
						SQL += ", created DATE";
						SQL += ", updated DATE";
						SQL += ", status INTEGER default 1"; // < 0
																// deleted(value
					} else {

						SQL += "create  TABLE " + tablea + " (";
						SQL += "rowid integer primary key autoincrement ";
						SQL += ", atitle text default '', acontent text default '', avec text";
						SQL += ", apublished DATE, aurl text, aauthor text";
						SQL += ", lat double, lon double, adeep int, aloclist text, aheardlist text default '', animportlist text default '', areflist text default ''";
						SQL += ", mres text, hres text, pres text, mycomment text default '', tag integer default 1, refresh integer default 0, refreshstart DATE, refresherror DATE, refreshpass DATE, refreshfail TEXT default '', refreshcookies TEXT default '', refreshjumpurl TEXT default '', refreshstatus TEXT default '', refreshnew INTEGER default 0, refreshready DATE, keyref INTEGER default 0";

						SQL += ", updatedepth INTEGER default 0";
						SQL += ", readdepth INTEGER default 0";
						SQL += ", created DATE";
						SQL += ", updated DATE";
						SQL += ", status INTEGER default 1"; // < 0
																// deleted(value

					}
					SQL += ");";

					db.execSQL(SQL);

					if (!ts3) {

						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_lat on " + tablea + "(lat);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_lon on " + tablea + "(lon);");

						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_keyref on " + tablea + "(keyref);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_atitle on " + tablea + "(atitle);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_aauthor on " + tablea + "(aauthor);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_acontent on " + tablea + "(acontent);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_aurl on " + tablea + "(aurl);");

						// db.execSQL("create index IF NOT EXISTS " + tablea
						// + "_apublished_atitle_aurl on " + tablea
						// + "(apublished,atitle,aurl);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_atitle_aurl on " + tablea
								+ "(atitle,aurl);");

						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_apublished on " + tablea + "(apublished);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_updated on " + tablea + "(updated);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_created on " + tablea + "(created);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_refresh on " + tablea + "(refresh);");
						db.execSQL("create index IF NOT EXISTS " + tablea
								+ "_status on " + tablea + "(status);");
					}
				}

				{
					String SQL = "";
					SQL += "create table " + tablea + "4 (";
					SQL += "_id integer primary key autoincrement ";
					// SQL += mContext.getString(R.string.contactStore_columns);
					SQL += ", url text";
					SQL += ", title text, published DATE, content text, author text";
					SQL += ", created DATE";
					SQL += ", updated DATE";
					SQL += ", status INTEGER default 1"; // < 0 deleted(value *
															// -1), 1
															// active(created),
															// ++ per update
					SQL += ");\n";
					// db.execSQL(SQL);
					// db.execSQL("create index moment4_published on moment4(published);");
				}

				{
					String SQL = "";
					SQL += "create table IF NOT EXISTS refplay (";
					SQL += "_id integer primary key autoincrement ";
					// SQL += ", url text, rurl text";
					// SQL +=
					// ", title text, summary text, published DATE, content text, author text";
					SQL += ", aspark int default 0";
					SQL += ", atref int default 0";
					SQL += ", attable text";
					SQL += ", atcolumn text";
					SQL += ", atwidth int default 0";
					SQL += ", atheight int default 0";
					SQL += ", atx int default 0";
					SQL += ", aty int default 0";
					SQL += ", created DATE";
					SQL += ", updated DATE";
					SQL += ", status INTEGER default 1"; // < 0 deleted(value *
															// -1),
															// 1
															// active(created),
															// ++
															// per update
					SQL += ");\n";
					db.execSQL(SQL);

					db.execSQL("create index IF NOT EXISTS refplay_aspark on refplay(aspark);");
					db.execSQL("create index IF NOT EXISTS refplay_atref on refplay(atref);");
					// db.execSQL("create index IF NOT EXISTS refplay_atrefaspark on refplay(atref,aspark);");
					// db.execSQL("create index IF NOT EXISTS moment_published_title_url on moment(published,title,url);");
					// db.execSQL("create index IF NOT EXISTS moment_published on moment(published);");
					db.execSQL("create index IF NOT EXISTS refplay_updated on refplay(updated);");
					db.execSQL("create index IF NOT EXISTS refplay_created on refplay(created);");
					db.execSQL("create index IF NOT EXISTS refplay_status on refplay(status);");
				}

				{
					String SQL = "";
					SQL += "create table IF NOT EXISTS contact (";
					SQL += "_id integer primary key autoincrement ";
					// SQL += ", url text, rurl text";
					// SQL +=
					// ", title text, summary text, published DATE, content text, author text";
					SQL += ", mms text";
					SQL += ", email text";
					SQL += ", display text";
					SQL += ", rotation int default 180";
					SQL += ", created DATE";
					SQL += ", updated DATE";
					SQL += ", status INTEGER default 1"; // < 0 deleted(value *
															// -1),
															// 1
															// active(created),
															// ++
															// per update
					SQL += ");\n";
					db.execSQL(SQL);

					db.execSQL("create index IF NOT EXISTS contact_mms on contact(mms);");
					db.execSQL("create index IF NOT EXISTS contact_email on contact(email);");
					db.execSQL("create index IF NOT EXISTS contact_display on contact(display);");
					// db.execSQL("create index IF NOT EXISTS moment_published_title_url on moment(published,title,url);");
					// db.execSQL("create index IF NOT EXISTS moment_published on moment(published);");
					db.execSQL("create index IF NOT EXISTS contact_updated on contact(updated);");
					db.execSQL("create index IF NOT EXISTS contact_created on contact(created);");
					db.execSQL("create index IF NOT EXISTS contact_status on contact(status);");
				}

				{
					String SQL = "";
					SQL += "create table IF NOT EXISTS perspective (";
					SQL += "_id integer primary key autoincrement ";
					SQL += ",title text, location text, detail text";
					SQL += ", cux float, cuy float, cuz float, aux float, auy float, auz float";
					SQL += ", updated date, created date, status integer default 1";
					SQL += ");\n";

					db.execSQL(SQL);
					db.execSQL("create index IF NOT EXISTS perspective_title on perspective(title)");
					db.execSQL("create index IF NOT EXISTS perspective_updated on perspective(updated);");
					db.execSQL("create index IF NOT EXISTS perspective_created on perspective(created);");
					db.execSQL("create index IF NOT EXISTS perspective_status on perspective(status);");
				}

				{
					String SQL = "";
					SQL += "create table IF NOT EXISTS checkin (";
					SQL += "_id integer primary key autoincrement ";
					// SQL += ", url text, rurl text";
					// SQL +=
					// ", title text, summary text, published DATE, content text, author text";
					// SQL += ", mms text";
					// SQL += ", email text";
					SQL += ", atitle text";
					SQL += ", anext DATE";
					SQL += ", atill DATE, atillset int default 0";
					SQL += ", rotation int default 180";
					SQL += ", created DATE";
					SQL += ", updated DATE";
					SQL += ", status INTEGER default 1"; // < 0 deleted(value *
															// -1),
															// 1
															// active(created),
															// ++
															// per update
					SQL += ");\n";
					db.execSQL(SQL);

					db.execSQL("create index IF NOT EXISTS checkin_atitle on checkin(atitle);");
					db.execSQL("create index IF NOT EXISTS checkin_anext on checkin(anext);");
					db.execSQL("create index IF NOT EXISTS checkin_atill on checkin(atill);");
					// db.execSQL("create index IF NOT EXISTS moment_published_title_url on moment(published,title,url);");
					// db.execSQL("create index IF NOT EXISTS moment_published on moment(published);");
					db.execSQL("create index IF NOT EXISTS checkin_updated on checkin(updated);");
					db.execSQL("create index IF NOT EXISTS checkin_created on checkin(created);");
					db.execSQL("create index IF NOT EXISTS checkin_status on checkin(status);");
				}



{
					String SQL = "";
					SQL += "create  TABLE chat (";
					SQL += "rowid integer primary key autoincrement ";
					SQL += ", who text default ''";
					SQL += ", said text default ''";
					SQL += ", cwhen INTEGER";
					SQL += ", reply text default ''";
					SQL += ", created DATE";
					SQL += ", updated DATE";
					SQL += ", status INTEGER default 1";
					SQL += ");";

					db.execSQL(SQL);
					db.execSQL("create index IF NOT EXISTS chat_rowid on chat(rowid);");
					db.execSQL("create index IF NOT EXISTS chat_said on chat(said);");
					db.execSQL("create index IF NOT EXISTS chat_who on chat(who);");
					db.execSQL("create index IF NOT EXISTS chat_cwhen on chat(cwhen);");
					db.execSQL("create index IF NOT EXISTS chat_updated on chat(updated);");
					db.execSQL("create index IF NOT EXISTS chat_created on chat(created);");
					db.execSQL("create index IF NOT EXISTS chat_status on chat(status);");
				}

				{
					String SQL = "";
					SQL += "create TABLE here (";
					SQL += "rowid integer primary key autoincrement ";
					SQL += ", ms INTEGER default 0";
					SQL += ", refreshjumpurl text default ''";
					SQL += ", created DATE";
					SQL += ", updated DATE";
					SQL += ", status INTEGER default 1";
					SQL += ");";

					db.execSQL(SQL);
					db.execSQL("create index IF NOT EXISTS here_updated on here(updated);");
					db.execSQL("create index IF NOT EXISTS here_created on here(created);");
					db.execSQL("create index IF NOT EXISTS here_status on here(status);");
				}




			} catch (SQLException e) {
				Log.e("ok", "major  ");
				e.printStackTrace();
			}

		}

		public String datetime() {
			String g = "";
			Date d = new Date();
			g = (d.getYear() + 1900) + "-" + ((d.getMonth() < 9) ? "0" : "")
					+ ((d.getMonth() + 1)) + "-"
					+ ((d.getDate() < 10) ? "0" : "") + d.getDate() + " "
					+ ((d.getHours() < 10) ? "0" : "") + d.getHours() + ":"
					+ ((d.getMinutes() < 10) ? "0" : "") + d.getMinutes()
					+ ":00";
			// Log.i(TAG,"generated date "+g);
			return g;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG, "onUpgrade(SQL) DatabaseHelper");
			db.execSQL("DROP TABLE IF EXISTS "
					+ ctx.getResources().getString(R.string.mytablea));
			db.execSQL("DROP TABLE IF EXISTS chat");
			db.execSQL("DROP TABLE IF EXISTS here");
			db.execSQL("DROP TABLE IF EXISTS prespective");

			onCreate(db);
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// mLog.w(TAG,"delete() uri("+uri+") lastsegment("+uri.getLastPathSegment()+")");
		int rowCount = mDb.delete(uri.getLastPathSegment(), selection,
				selectionArgs);

		// Notify any listeners and return the deleted row count.
		getContext().getContentResolver().notifyChange(uri, null);
		return rowCount;

	}

	@Override
	public String getType(Uri uri) {

		switch (URI_MATCHER.match(uri)) {
		case ALL_CHAT:
			return "vnd.android.cursor.dir/chat";
		case THIS_CHAT:
			return "vnd.android.cursor.item/chat";
		case ALL_HERE:
			return "vnd.android.cursor.dir/here";
		case THIS_HERE:
			return "vnd.android.cursor.item/here";

		case SEARCH_SUGGEST:
			return "vnd.android.cursor.dir/"
					+ ctx.getResources().getString(R.string.mytablea);
		case GET_MOMENT:
			return "vnd.android.cursor.item/"
					+ ctx.getResources().getString(R.string.mytablea);
		case SEARCH_MOMENT:
			return "vnd.android.cursor.dir/"
					+ ctx.getResources().getString(R.string.mytablea);
		case ALL_MESSAGES:
			// Log.w(TAG,"gettype() uri("+uri+") ALL MESSAGES");
			return "vnd.android.cursor.dir/"
					+ ctx.getResources().getString(R.string.mytablea); // List
																		// of
																		// items.
		case SPECIFIC_MESSAGE:
			// Log.w(TAG,"gettype() uri("+uri+") Specific Message");
			return "vnd.android.cursor.item/"
					+ ctx.getResources().getString(R.string.mytablea); // Specific
																		// item.
		case ALL_MESSAGESPERSPECTIVE:
			// Log.w(TAG,"gettype() uri("+uri+") ALL MESSAGES");
			return "vnd.android.cursor.dir/perspective"; // List of items.
		case SPECIFIC_MESSAGEPERSPECTIVE:
			// Log.w(TAG,"gettype() uri("+uri+") Specific Message");
			return "vnd.android.cursor.item/perspective"; // Specific item.
		case ALL_MESSAGESXSAW:
			// Log.w(TAG,"gettype() uri("+uri+") ALL MESSAGES");
			return "vnd.android.cursor.dir/xsaw"; // List of items.
		case SPECIFIC_MESSAGEXSAW:
			// Log.w(TAG,"gettype() uri("+uri+") Specific Message");
			return "vnd.android.cursor.item/xsaw"; // Specific item.
		case ALL_MESSAGESRETROSPECT:
			// Log.w(TAG,"gettype() uri("+uri+") ALL MESSAGES");
			return "vnd.android.cursor.dir/retrospect"; // List of items.
		case SPECIFIC_MESSAGERETROSPECT:
			// Log.w(TAG,"gettype() uri("+uri+") Specific Message");
			return "vnd.android.cursor.item/retrospect"; // Specific item.
		case ALL_MESSAGESFILTER:
			// Log.w(TAG,"gettype() uri("+uri+") ALL MESSAGES");
			return "vnd.android.cursor.dir/filter"; // List of items.
		case SPECIFIC_MESSAGEFILTER:
			// Log.w(TAG,"gettype() uri("+uri+") Specific Message");
			return "vnd.android.cursor.item/filter"; // Specific item.
		case -1:
			Log.e(TAG, "getType(-1) uri(" + uri + ")");
			return "vnd.android.cursor.item/"
					+ ctx.getResources().getString(R.string.mytablea);
		default:
			Log.e(TAG, "getType() uri(" + uri + ")");
			return "vnd.android.cursor.item/"
					+ ctx.getResources().getString(R.string.mytablea);
		}

	}

	public String datetime() {
		String g = "";
		Date d = new Date();

		g = (d.getYear() + 1900) + "-" + ((d.getMonth() < 9) ? "0" : "")
				+ ((d.getMonth() + 1)) + "-" + ((d.getDate() < 10) ? "0" : "")
				+ d.getDate() + " " + ((d.getHours() < 10) ? "0" : "")
				+ d.getHours() + ":" + ((d.getMinutes() < 10) ? "0" : "")
				+ d.getMinutes() + ":" + ((d.getSeconds() < 10) ? "0" : "")
				+ d.getSeconds();
		// Log.i(TAG,"generated date "+g);
		return g;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (mDb == null) {
			return null;
		}
		long rowId = -1;
		// Log.w(TAG,"insert() uri("+uri+") lastsegment("+uri.getLastPathSegment()+")");
		String tablename = uri.getLastPathSegment();
		values.put("created", datetime());
		values.put("updated", datetime());
		rowId = mDb.insertOrThrow(tablename, "rawcontent", values);
		Uri newUri = Uri.withAppendedPath(CONTENT_URI, tablename + "/" + rowId);
		// mLog.w(TAG,"insert()  newUri(" + newUri.toString() + ")");

		// Notify any listeners and return the URI of the new row.
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);

		/*
		 * / if( rowId > 100 ){ int del = (int) (rowId - 100);
		 * mDb.execSQL("update " + DATABASE_TABLE_NAME + " set "+ CONTENT
		 * +" = \"\" where _id < "+del+" "); } //
		 */

		return newUri;

	}

	Context ctx;

	@Override
	public boolean onCreate() {
		Log.i("ok", "onCreate(SQL)");
		// mLog = new Custom(this.getContext(), TAG + " onCreate() 130");
		ctx = getContext();
		mDbHelper = new DatabaseHelper(getContext());

		// final Context con = getContext();
		try {
			mDb = mDbHelper.getWritableDatabase();

			if (mDb != null) {
				Log.w(TAG, "db updates");
			}

			// mDb = mDbHelper.openDatabase(getContext(), DATABASE_NAME, null,
			// DB_VERSION);
			// mLogger.info("RssContentProvider.onCreate(): Opened a database");
		} catch (Exception ex) {
			Log.e(TAG, "Failed to connected to Database, exception");
			ex.printStackTrace();
			return false;
		}
		if (mDb == null) {
			Log.e(TAG, "Failed to connected to Database, mDb null");
			return false;
		} else {

			return true;
		}

	}

	// String ssh = "";

	private static HashMap<String, String> buildColumnMap() {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "atitle AS "
				+ SearchManager.SUGGEST_COLUMN_TEXT_1);
		map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, "apublished AS "
				+ SearchManager.SUGGEST_COLUMN_TEXT_2);

		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);

		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
		// "'com.docchomps.gosbit.GosBitActivity' AS "
				"'android.intent.action.VIEW' AS"
						+ SearchManager.SUGGEST_COLUMN_INTENT_ACTION);

		map.put(SearchManager.SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING,
				"'true' AS "
						+ SearchManager.SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING);
		map.put(SearchManager.SUGGEST_COLUMN_ICON_1, "mres AS "
				+ SearchManager.SUGGEST_COLUMN_ICON_1);

		map.put(SearchManager.SUGGEST_COLUMN_ICON_2, "pres AS "
				+ SearchManager.SUGGEST_COLUMN_ICON_2);

		map.put(SearchManager.SUGGEST_COLUMN_QUERY, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_QUERY);

		// map.put("text_anchor_bar", "null AS text_anchor_bar");
		// map.put("text_anchor_bar_top", "null AS text_anchor_bar_top");
		return map;

	}

	private static final HashMap<String, String> mColumnMap = buildColumnMap();

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// String ssh = "";
		// if (selectionArgs == null) {
		// selectionArgs = new String[] { "" };
		// } else {
		// ssh = selectionArgs[0];
		// }

		if (mDb == null) {
			return null;
		}

		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

		String recordid = "";
		String tablename = getType(uri).split("/", 2)[1];

		if (getType(uri).contentEquals(
				"vnd.android.cursor.item/"
						+ ctx.getResources().getString(R.string.mytablea))) {
			recordid = uri.getLastPathSegment();
			tablename = ctx.getResources().getString(R.string.mytablea);
			// qBuilder.setTables(tablename);
		} else if (getType(uri).contentEquals(
				"vnd.android.cursor.dir/"
						+ ctx.getResources().getString(R.string.mytablea))) {
			tablename = ctx.getResources().getString(R.string.mytablea);
			recordid = "";
		} else if (getType(uri).contentEquals("vnd.android.cursor.item/filter")) {
			recordid = uri.getLastPathSegment();
			tablename = "filter";
			// qBuilder.setTables(tablename);
		} else if (getType(uri).contentEquals(
				"vnd.android.cursor.item/perspective")) {
			recordid = uri.getLastPathSegment();
			tablename = "perspective";
		}

		switch (URI_MATCHER.match(uri)) {
		case ALL_CHAT:
			tablename = "chat";
			recordid = "";
			break;
		case THIS_CHAT:
			recordid = uri.getLastPathSegment();
			tablename = "chat";
			break;
		case ALL_HERE:
			recordid = "";
			tablename = "here";
			break;
		case THIS_HERE:
			recordid = uri.getLastPathSegment();
			tablename = "here";
			break;
		}

		qBuilder.setTables(tablename);

		// " "

		// Set the table we're querying.

		// If the query ends in a specific record number, we're
		// being asked for a specific record, so set the
		// WHERE clause in our query.

		if (recordid.length() > 0 && !recordid.contains("-")) {
			// Log.i("ok", "Updating query with " + recordid);
			qBuilder.appendWhere("rowid = " + recordid);
		} else if (uri.getFragment() != null
				&& !uri.getFragment().contains("-1")
				&& uri.getFragment().length() > 0) {// (URI_MATCHER.match(uri))
			// == SPECIFIC_MESSAGE){
			Log.i("ok", "Updating query with frag " + recordid);
			qBuilder.appendWhere("rowid = " + uri.getFragment()); // +
																	// uri.getPathLeafId());
		}// else{qBuilder.appendWhere("atitle MATCH ''");}
			// Set sort order. If none specified, use default.
		// if (TextUtils.isEmpty(sortOrder)) {
		// sortOrder = Custom.DEFAULT_SORT_ORDER;
		// }

		// +
		// c.getCount()
		// +

		String groupby = null;
		String having = null;
		if (selection.toUpperCase().contains("GROUP BY")) {
			String us = selection.toUpperCase();

			groupby = selection.substring(
					us.indexOf("GROUP BY") + 8,
					us.indexOf("HAVING") > -1 ? us.indexOf("HAVING") - 1 : us
							.length());
			if (us.contains("HAVING")) {
				having = selection.substring(us.indexOf("HAVING") + 6);
			}

			selection = selection.substring(0, us.indexOf("GROUP BY") - 1);
		}

		// || URI_MATCHER.match(uri) == SEARCH_MOMENT
		if ((URI_MATCHER.match(uri) == REFRESH_SHORTCUT)) {
			projection = new String[] { BaseColumns._ID,
					SearchManager.SUGGEST_COLUMN_QUERY,
					SearchManager.SUGGEST_COLUMN_TEXT_1,
					SearchManager.SUGGEST_COLUMN_TEXT_2,
					SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
					SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
					SearchManager.SUGGEST_COLUMN_ICON_1,
					SearchManager.SUGGEST_COLUMN_ICON_2,
					SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
					SearchManager.SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING };

			qBuilder.setProjectionMap(mColumnMap);

		}

		if ((URI_MATCHER.match(uri) == SEARCH_SUGGEST)) {
			// switch (URI_MATCHER.match(uri)) {
			// case SEARCH_SUGGEST:

			// tablename = "moment";
			{
				Bundle bi = new Bundle();
				bi.putString("table",
						ctx.getResources().getString(R.string.mytablea));
				Message mi = new Message();
				mi.setData(bi);
				mi.what = 2;
				tableReview.sendMessageDelayed(mi, 75);
			}
			if (selectionArgs != null && selectionArgs[0].length() > 0) {

				if (ts3) {
					selection = "atitle MATCH ?";// atitle like ?";
					selectionArgs = new String[] { "*" + selectionArgs[0] + "*" };
				} else {
					selection = "atitle like ?";// atitle like ?";
					selectionArgs = new String[] { "%" + selectionArgs[0] + "%" };
				}

			} else {
				selection = "1=1";
				selectionArgs = null;
			}

			// May I recieve funds to employ felony holders?
			sortOrder = "created desc";
			projection = new String[] { BaseColumns._ID,
					SearchManager.SUGGEST_COLUMN_QUERY,
					SearchManager.SUGGEST_COLUMN_TEXT_1,
					SearchManager.SUGGEST_COLUMN_TEXT_2,
					SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
					SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
					SearchManager.SUGGEST_COLUMN_ICON_1,
					SearchManager.SUGGEST_COLUMN_ICON_2,
					SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
					SearchManager.SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING };
			qBuilder.setProjectionMap(mColumnMap);

			// qBuilder.setTables(tablename);

			// selection = "_id, atitle as suggest_text_1";
			/*
			 * SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, (only if you want to
			 * refresh shortcuts)
			 */
			// "_id as suggest_intent_data_id" };
			// Log.i("ok", "SEARCH  " + getType(uri));
			// if (tablename.contains("moment")) {
			// HashMap<String, String> mColumnMap = buildColumnMap();
			// }

		} else {

			// boolean distinct = false;
			// qBuilder.buildQueryString(distinct, tables, columns, where,
			// groupBy, having, orderBy, limit);

			// if (selection.contentEquals(BaseColumns._ID)) {
			// HashMap<String, String> map = new HashMap<String, String>();
			// map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
			// qBuilder.setProjectionMap(map);
			// } else {
			// if (projection != null) {
			// for (int i = 0; i < projection.length; i++) {
			// if (projection[i].contentEquals(BaseColumns._ID)) {
			// HashMap<String, String> map = new HashMap<String,
			// String>();
			// map.put(BaseColumns._ID, "rowid AS "
			// + BaseColumns._ID);
			// qBuilder.setProjectionMap(map);
			// break;
			// }
			// }
			// }
			// }
		}

		// Make the query.
		try {

			boolean ji8 = false;
			if (ji8) {
				Log.i(TAG,
						"query() uri("
								+ uri
								+ ") type("
								+ getType(uri)
								+ "["
								+ URI_MATCHER.match(uri)
								+ "]) lastsegment("
								+ uri.getLastPathSegment()
								+ ") fragment("
								+ uri.getFragment()
								+ ") selection("
								+ selection
								+ ") ri("
								+ recordid
								+ ") tnm("
								+ tablename
								+ ") :"
								+ (selectionArgs != null
										&& selectionArgs.length > 0 ? selectionArgs[0]
										: ""));
			}
			// qBuilder.setProjectionMap(mColumnMap);
			Cursor c = null;
			c = qBuilder.query(mDb, projection, selection, selectionArgs,
					groupby, having, sortOrder);

			if (c != null) {
				c.setNotificationUri(getContext().getContentResolver(), uri);

				if (c.moveToFirst()) {
					if (ji8) {
						Log.i("ok", "Cursor WIN " + c.getCount());
					}
					return c;
				} else {
					if (ji8) {
						Log.i("ok", "Cursor EMPTY " + c.getCount());
					}
					c.close();
				}

			} else {
			}
			// Log.i("ok", "Cursor GONE "
			// + (groupby != null ? groupby : "nogroup") + ", "
			// + (having != null ? having : "nhaving") + ", "
			// + (sortOrder != null ? sortOrder : "nosort") + ", ");

		} catch (IllegalArgumentException h3) {
			Log.e("ok", "Cursor failure b: " + h3.getMessage());
		} catch (SQLiteException bh) {
			Log.e("ok", "Cursor failure a: " + bh.getMessage());
		} finally {
		}

		// Log.i("ok", "query for " + c.getCount());
		// if (selectionArgs != null) {
		return null;

	}

	Handler tableReview = new Handler() {
		public void handleMessage(Message msg) {
			{
				Log.i("ok",
						"sql review "
								+ ctx.getResources().getString(
										R.string.mytablea)
								+ " ====================================================");

				Cursor b = query(
						ctx,
						ctx.getContentResolver(),
						Uri.parse("content://"
								+ ctx.getPackageName()
								+ "/"
								+ ctx.getResources().getString(
										R.string.mytablea)),
						new String[] { "*" }, "1=1", null, "rowid desc limit 1");

				if (b != null && b.moveToFirst()) {
					Log.i("ok", "sql count " + b.getInt(0));
				}

				if (b != null) {
					Log.i("ok", "sql count " + b.getCount());
					String[] bn = b.getColumnNames();
					for (int i = 0; i < bn.length; i++) {
						Log.i("ok", "sql count @" + i + " " + bn[i]);
					}
					b.close();

				} else {
					Log.i("ok", "sql review x");
				}
			}
		}
	};

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		Log.i(TAG,
				"update() uri(" + uri + ") lastsegment("
						+ uri.getLastPathSegment() + ") selection(" + selection
						+ ") " + getType(uri));

		String recordid = "";
		String tablename = getType(uri).split("/", 2)[1];

		if (getType(uri).contentEquals(
				"vnd.android.cursor.item/"
						+ ctx.getResources().getString(R.string.mytablea))
				|| tablename.matches(ctx.getResources().getString(
						R.string.mytablea))) {
			recordid = uri.getLastPathSegment();
			tablename = ctx.getResources().getString(R.string.mytablea);
		}else if (getType(uri).contentEquals("vnd.android.cursor.item/chat")
				|| tablename.matches("chat")) {
			recordid = uri.getLastPathSegment();
			tablename = "chat";
		} else if (getType(uri).contentEquals("vnd.android.cursor.item/here")
				|| tablename.matches("here")) {
			recordid = uri.getLastPathSegment();
			tablename = "here";
		}


		if (selection == null) {
			selection = "";
		}
		// Mitt you should know he is being hurt by you and he wants you to win.
		// He recognizes you are an excellent finance mind and a horrible
		// historian so you can humanize yourself to everyone by holding onto
		// this that you own.
		if (selection.length() == 0 && recordid.length() > 0
				&& !recordid.contains("-")) {
			Log.i("ok", "Updating query with " + recordid);
			selection += "rowid = " + recordid;
		}

		if (!values.containsKey("updated")) {
			values.put("updated", datetime());
		}
		// NOTE Argument checking code omitted. Check your parameters!
		int updateCount = mDb.update(tablename, values, selection,
				selectionArgs);

		// Notify any listeners and return the updated row count.
		// getContext().getContentResolver().notifyUpdate(uri, null);
		getContext().getContentResolver().notifyChange(uri, null);
		// Log.i(TAG,
		// "update() uri(" + uri + ") lastsegment("
		// + uri.getLastPathSegment() + ") selection(" + selection
		// + ") count("+updateCount+")");

		return updateCount;
	}

	// public final class CaseFace {
	// private static final String TAG = "CaseFace";
	private static final String SQLITE_EXCEPTION_DETAIL_MESSAGE = "unable to open database file";

	// private CaseFace() {
	// Forbidden being instantiated.
	// }

	// FIXME: It looks like outInfo.lowMemory does not work well as we
	// expected.
	// after run command: adb shell fillup -p 100, outInfo.lowMemory is
	// still
	// false.
	private static boolean isLowMemory(Context context) {
		if (null == context) {
			return false;
		}

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);

		return outInfo.lowMemory;
	}

	// FIXME: need to optimize this method.
	private static boolean isLowMemory(SQLiteException e) {
		return e.getMessage().equals(SQLITE_EXCEPTION_DETAIL_MESSAGE);
	}

	public static void checkSQLiteException(Context context, SQLiteException e) {
		if (isLowMemory(e)) {
			Toast.makeText(context, "Low Memory", Toast.LENGTH_SHORT).show();
		} else {
			throw e;
		}
	}

	public static Cursor query(Context context, ContentResolver resolver,
			Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		Log.i("ok", "QUERY: (" + selection + ") " + uri.toString());
		try {
			return resolver.query(uri, projection, selection == null ? ""
					: selection, selectionArgs, sortOrder);
		} catch (SQLiteException e) {
			Log.e("ok", "SQLiteException", e);
			checkSQLiteException(context, e);
			return null;
		}

	}

	public static boolean requery(Context context, Cursor cursor) {
		try {
			return cursor.requery();
		} catch (SQLiteException e) {
			Log.e(TAG, "Catch a SQLiteException when requery: ", e);
			checkSQLiteException(context, e);
			return false;
		}
	}

	public static int update(Context context, ContentResolver resolver,
			Uri uri, ContentValues values, String where, String[] selectionArgs) {

		try {
			return resolver.update(uri, values, where, selectionArgs);
		} catch (SQLiteException e) {
			Log.e(TAG, "Catch a SQLiteException when update: ", e);
			checkSQLiteException(context, e);
			return -1;
		}
	}

	public static int delete(Context context, ContentResolver resolver,
			Uri uri, String where, String[] selectionArgs) {
		try {
			return resolver.delete(uri, where, selectionArgs);
		} catch (SQLiteException e) {
			Log.e(TAG, "Catch a SQLiteException when delete: ", e);
			checkSQLiteException(context, e);
			return -1;
		}
	}

	public static Uri insert(Context context, ContentResolver resolver,
			Uri uri, ContentValues values) {
		try {
			return resolver.insert(uri, values);
		} catch (SQLiteException e) {
			Log.w(TAG,
					"Catch a SQLiteException when insert: "
							+ e.getLocalizedMessage());
			Log.w(TAG, "uri " + uri.toString());
			if (values.containsKey("remoteid")) {
				Log.w(TAG, "remoteid " + values.getAsString("remoteid"));
			}
			checkSQLiteException(context, e);
			return null;
		}
	}
	// }

}
