package com.au.boston;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import com.au.kai.R;
import com.au.kai.Tumbler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class Grip extends Service {

	private static String llg = "ok";
	private NotificationManager mNM;
	private Context mCtx;
	private int pRate = 72;

	@Override
	public IBinder onBind(Intent arg0) {
		Log.w(llg, "GRIP onBind(S)");

		bucketBind.sendEmptyMessage(2);
		return mbinder;
	}

	@Override
	public void onDestroy() {
		Log.w(llg, "GRIP onDestroy(S)");

		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		Log.w(llg, "GRIP REBIND(S)");

		super.onRebind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w(llg, "GRIP START COMMAND(S)");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.w(llg, "GRIP UNBIND(S)");
		return super.onUnbind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.w(llg, "GRIP onStart(S)");

		new runpull().execute();
	}

	Handler setStatus = null;

	public void runpully(Handler updateStatus) {
		Log.i("ok", "GRIP RUN");
		setStatus = updateStatus;

		new runpull().execute();
	}

	public class runpull extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			reg = getSharedPreferences("Preferences", MODE_WORLD_WRITEABLE);
			edt = reg.edit();

			int lastwhen = reg.getInt("relast", 0);

			updateStatus("Acquiring"
					+ (lastwhen > 0 ? " since "
							+ ((System.currentTimeMillis() / 1000) - lastwhen)
							: ""));
			Uri hereuri = null;
			{
				ContentValues cu = new ContentValues();
				hereuri = Tumbler.insert(mCtx, getContentResolver(),
						Uri.withAppendedPath(Tumbler.CONTENT_URI, "here"), cu);
				if(hereuri == null){Log.e("ok","HERE ALERT  ALERT    ALERT");}
			}

			long ms = SystemClock.uptimeMillis();
			Bundle br = new Bundle();
			br.putString("title", "ip");
			br.putString("dest", "http://newdream.net/~haven/ideal.pl?since=" + lastwhen);
			br.putString("uri", hereuri.toString());

			Bundle bx = pullFile(br);

			{
				ContentValues cu = new ContentValues();
				cu.put("ms", (SystemClock.uptimeMillis() - ms));
				Tumbler.update(mCtx, getContentResolver(), hereuri, cu, null,
						null);
			}

			Set<String> k = bx.keySet();
			Object[] b = k.toArray();

			for (int n = 0; n < b.length; n += 1) {
				Log.i("ok", "GRIP Colu row " + b[n] + " ("
						+ (n + 1 >= b.length || b[n + 1] == null) + ")");
			}
			if (bx.containsKey("acontent") && bx.getString("acontent") != null) {
				long ms5 = SystemClock.uptimeMillis();

				DocumentBuilderFactory bf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder b5 = null;
				try {
					b5 = bf.newDocumentBuilder();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				NodeList cd = null;
				try {
					InputStream is = new ByteArrayInputStream(bx.getString(
							"acontent").getBytes("UTF-8"));
					Document d = b5.parse(is);

					cd = d.getElementsByTagName("chat");

				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (cd != null && cd.getLength() > 0) {

					Log.i("ok",
							"Found chats " + cd.getLength() + " "
									+ bx.getString("acontent"));

					String who = "";
					String said = "";
					int when = 0;
					int x = 0;
					NodeList cin = null;

					String recentsaid = "";
					int recentcnt = 0;

					for (int i = 0; i < cd.getLength(); i++) {

						said = "";
						when = 0;
						who = "";
						if (cd.item(i).hasChildNodes()) {
							cin = cd.item(i).getChildNodes();

							for (x = 0; x < cin.getLength(); x++) {
								if (cin.item(x).getNodeName()
										.contentEquals("said")) {
									said = cin.item(x).getTextContent();
								} else if (cin.item(x).getNodeName()
										.contentEquals("when")) {
									try {
										when = Integer.parseInt(cin.item(x)
												.getTextContent());
									} catch (NumberFormatException eb) {
									}
								} else if (cin.item(x).getNodeName()
										.contentEquals("who")) {
									who = cin.item(x).getTextContent();
								}

							}
						}

						// said =
						// cd.item(i).getAttributes().getNamedItem("said")
						// .getTextContent();
						// when =
						// cd.item(i).getAttributes().getNamedItem("when")
						// .getTextContent();

						if (when > lastwhen) {

							{
								ContentValues cu = new ContentValues();
								cu.put("said", said);
								cu.put("who", who);
								cu.put("cwhen", when);

								Tumbler.insert(mCtx, getContentResolver(), Uri
										.withAppendedPath(Tumbler.CONTENT_URI,
												"chat"), cu);
							}

							lastwhen = when;
							Log.i("ok", "CHAT READ (" + said + ")(" + when
									+ ")");
							recentcnt++;
							if (!said.startsWith("%7B")) {
								recentsaid += said + " ";
							}
						}
					}

					edt.putInt("relast", lastwhen);
					edt.commit();

					if (recentcnt > 0) {
						// Notif
						// mNM
						Notification notif = new Notification(
								R.drawable.ic_launcher, recentsaid,
								System.currentTimeMillis());

						Intent intentJump2 = new Intent();
						intentJump2.setClass(mCtx, com.au.kai.KAi.class);
						// intentJump2.setAction("com.au.boston.Nice");
						// EC
						// intentJump2.putExtra("uri", geturi.toString());
						// intentJump2.putExtra("keyref", rowid);
						// intentJump2.setData(geturi);

						intentJump2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_NO_HISTORY
								| Intent.FLAG_FROM_BACKGROUND
								| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

						int rowid = 1;
						PendingIntent pi2 = PendingIntent
								.getActivity(
										mCtx,
										rowid,
										intentJump2,
										Intent.FLAG_ACTIVITY_NEW_TASK
												| Intent.FLAG_ACTIVITY_NO_HISTORY
												| Intent.FLAG_FROM_BACKGROUND
												| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						notif.ledARGB = Color.argb(255, 0, 0, 250);
						notif.defaults = Notification.DEFAULT_ALL;
						notif.icon = R.drawable.ic_launcher;
						notif.setLatestEventInfo(mCtx, "Chat " + recentcnt,
								recentsaid, pi2);
						notif.vibrate = new long[] { 100, 200, 500, 200, 100,
								200 };
						mNM.notify(1, notif);

					}

					updateStatus("Processed " + cd.getLength() + " in "
							+ (SystemClock.uptimeMillis() - ms5) + " ms");
				} else {
					updateStatus("Complete in "
							+ (SystemClock.uptimeMillis() - ms) + " ms");
				}

			}
			return null;
		}
	}

	void updateStatus(String st) {

		if (setStatus != null) {
			Log.i("ok", "GRIP status");

			Bundle ba = new Bundle();
			ba.putString("status", st);

			Message mr = new Message();
			mr.setData(ba);
			setStatus.sendMessage(mr);
		} else {
			Log.i("ok", "Status UPDATE " + st);
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		mCtx = this.getApplicationContext();
		Log.w(llg, "GRIP onCreate(S)");

		try {
			mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// mLog.setNotificationManager(mNM);
			reg = getSharedPreferences("Preferences", MODE_WORLD_WRITEABLE);
			edt = reg.edit();

		} catch (SecurityException e) {
			Log.e(llg, "GRIP Security exception: " + e);
		}

	}

	public void unbind() {

	}

	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i(llg, "GRIP onServiceConnected(S) MarketBillingService connected.");

	}

	private final IBinder mbinder = new uiBinder();

	public class uiBinder extends Binder {
		public Grip getService() {
			return Grip.this;
		}
	}

	Account[] ec;

	private Editor edt;
	private SharedPreferences reg;

	@Override
	public boolean stopService(Intent name) {
		Log.i("ok", "GRIP STOP SERVICE");

		return super.stopService(name);
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		Log.i("ok", "GRIP UNBIND SERVICE");
		super.unbindService(conn);
	}

	int activerowid = 0;
	Handler bucketBind = new Handler() {
		public void handleMessage(Message msg) {
			Log.i("ok", "GRIP BIND " + msg.what);

		}
	};

	Handler bucketPass = new Handler() {
		public void handleMessage(Message msg) {
			Log.i("ok", "GRIP PASS " + msg.what);

		}
	};

	Handler bucketDone = new Handler() {
		public void handleMessage(Message msg) {
			Log.i("ok", "GRIP DONE " + msg.what);
		}
	};

	Handler bucketIngest = new Handler() {
		public void handleMessage(Message msg) {
			Log.i("ok", "GRIP INGEST " + msg.what);
		}
	};

	CookieStore splitCookies(String cshort, CookieStore cs) {

		String[] clist = cshort.split("\n");
		ContentValues cg = new ContentValues();
		for (int h = 0; h < clist.length; h++) {
			String[] c = clist[h].split(" ", 2);
			if (c.length == 2 && c[0].length() > 3) {
				if (cg.containsKey(c[0]) == false) {
					// cg.put(c[0], c[1]);
					Cookie logonCookie = new BasicClientCookie(c[0],
							c[1].replaceAll("; expires=null", ""));
					// Log.w(G,"Carry Cookie mGet2 " + c[0] +
					// ":"+c[1] +
					// " expires("+logonCookie.getExpiryDate()+")" +
					// " myfadepath("+logonCookie.getPath()+") domain("+logonCookie.getDomain()+")");
					cs.addCookie(logonCookie);
				}
			}
		}

		return cs;
	}

	String newcookies(List<Cookie> cl2) {
		Bundle co = new Bundle();
		String cshort2 = "";
		for (int i = cl2.size() - 1; i >= 0; i--) {
			Cookie c3 = cl2.get(i);
			if (co.containsKey(c3.getName())) {
				continue;
			}
			co.putInt(c3.getName(), 1);

			cshort2 += c3.getName()
					+ " "
					+ c3.getValue()
					+ (c3.getExpiryDate() != null ? "; expires="
							+ c3.getExpiryDate() : "")
					+ (c3.getPath() != null ? "; myfadepath=" + c3.getPath()
							: "")
					+ (c3.getDomain() != null ? "; domain=" + c3.getDomain()
							: "") + "\n";
		}

		return cshort2;
	}

	Bundle pullFile(final Bundle bl) {

		ContentValues bv = new ContentValues();

		Log.i(llg,
				"GRIP Acquiring " + bl.getString("title") + "\n"
						+ bl.getString("dest") + " swaping redirect urls("
						+ bl.getString("uri") + ")");

		// final long sh = SystemClock.uptimeMillis();
		// final int rowid = bl.getInt("rowid");

		HttpGet httpget = new HttpGet(bl.getString("dest"));
		// String mUrl = httpget.getURI().toString();

		DefaultHttpClient mHC = new DefaultHttpClient();
		mHC.setCookieStore(splitCookies(
				bl.getString("refreshcookies") != null ? bl
						.getString("refreshcookies") : "",
				(mHC != null) ? mHC.getCookieStore() : new DefaultHttpClient()
						.getCookieStore()));

		String responseCode = "";
		try {
			mHC.setRedirectHandler(new RedirectHandler() {
				public URI getLocationURI(HttpResponse arg0, HttpContext arg1)
						throws ProtocolException {

					if (arg0.containsHeader("Location")) {
						String url = arg0.getFirstHeader("Location").getValue();
						URI uri = URI.create(url);

						ContentValues bv = new ContentValues();
						bv.put("refreshjumpurl", url);
						Tumbler.update(mCtx, mCtx.getContentResolver(),
								Uri.parse(bl.getString("uri")), bv, null, null);

						return uri;
					} else {
						return null;
					}
				}

				public boolean isRedirectRequested(HttpResponse arg0,
						HttpContext arg1) {
					if (arg0.containsHeader("Location")) {
						String url = arg0.getFirstHeader("Location").getValue();
						ContentValues bv = new ContentValues();
						bv.put("refreshjumpurl", url);
						Tumbler.update(mCtx, mCtx.getContentResolver(),
								Uri.parse(bl.getString("uri")), bv, null, null);

						return true;
					}
					return false;
				}

			});

			long ms = SystemClock.uptimeMillis();

			HttpResponse mHR = mHC.execute(httpget);
			// HttpResponse mHR = mHC.execute(httppost);

			if (mHR != null) {
				Log.w(llg, "GRIP safeHttpGet() 436 " + mHR.getStatusLine()
						+ " ");
				// Log.w(llg, "safeHttpGet() 440 response.getEntity()");
				HttpEntity mHE = mHR.getEntity();

				if (mHE != null) {
					String mhpb = EntityUtils.toString(mHE);

					bv.put("acontent", mhpb);
					bv.put("refreshcookies", newcookies(mHC.getCookieStore()
							.getCookies()));

					mHE.consumeContent();
					Log.w(llg,
							"GRIP Downloaded into RAM "
									+ mHE.getContentLength() + " in "
									+ (SystemClock.uptimeMillis() - ms) + " ms");
					updateStatus("Downloaded " + mHE.getContentLength()
							+ " in " + (SystemClock.uptimeMillis() - ms)
							+ " ms");

				} else {
					updateStatus("Download Failure A");
					Log.w("ok", "GRIP HttpEntity fail");
				}
			} else {
				updateStatus("Download Failure B");
				Log.w("ok", "GRIP HttpResponse fail");
			}

			responseCode = mHR.getStatusLine().toString();
			bv.put("refreshstatus", "" + responseCode);

		} catch (UnknownHostException hu) {
			Log.w("ok", "GRIP ERROR CAUGHT");

			hu.printStackTrace();
			responseCode = " " + hu.getLocalizedMessage()
					+ " Unknown Host Exception";
			responseCode = hu.getLocalizedMessage();
			bv.put("refreshfail", "" + responseCode);
			bv.put("refresherror", datetime());

			updateStatus("Download Failure " + responseCode);
		} catch (ClientProtocolException e) {
			Log.w("ok", "GRIP ERROR CAUGHT");

			e.printStackTrace();
			responseCode = " " + e.getLocalizedMessage() + " HTTP ERROR";
			responseCode = e.getLocalizedMessage();
			bv.put("refreshfail", "" + responseCode);
			bv.put("refresherror", datetime());

			updateStatus("Download Failure " + responseCode);
		} catch (NullPointerException e) {
			Log.w("ok", "GRIP ERROR CAUGHT");

			e.printStackTrace();
			responseCode = e.getLocalizedMessage();
			bv.put("refreshfail", "" + responseCode);
			bv.put("refresherror", datetime());
		} catch (IOException e) {
			Log.w("ok", "GRIP ERROR CAUGHT");

			e.printStackTrace();
			responseCode = e.getLocalizedMessage();
			bv.put("refreshfail", "" + responseCode);
			bv.put("refresherror", datetime());

			updateStatus("Download Failure " + responseCode);
		} catch (OutOfMemoryError e) {
			Log.w("ok", "GRIP ERROR CAUGHT");

			responseCode = e.getLocalizedMessage();
			bv.put("refreshfail", "" + responseCode + " MEMORY FAIL");
			bv.put("refresherror", datetime());

			updateStatus("Download Failure " + responseCode);

		} catch (IllegalArgumentException e) {
			Log.w("ok", "GRIP ERROR CAUGHT");

			responseCode = e.getLocalizedMessage();
			bv.put("refreshfail", "" + responseCode);
			bv.put("refresherror", datetime());

			updateStatus("Download Failure " + responseCode);
		} catch (IllegalStateException e) {
			Log.w("ok", "GRIP ERROR CAUGHT");

			e.printStackTrace();
			responseCode = e.getLocalizedMessage();
			bv.put("refreshfail", "" + responseCode);
			bv.put("refresherror", datetime());

			updateStatus("Download Failure " + responseCode);
		} finally {
			Log.w("ok", "GRIP PASS");

			bv.put("refreshpass", datetime());
			edt.putLong("bucket_saved", System.currentTimeMillis());

		}

		// Tumbler.update(mCtx, mCtx.getContentResolver(),
		// Uri.parse(bl.getString("uri")), bv, null, null);

		Bundle bx = new Bundle(bl);
		bx.putString("refresherror", bv.getAsString("refresherror"));
		bx.putString("refreshfail", bv.getAsString("refreshfail"));
		bx.putString("refreshpass", bv.getAsString("refreshpass"));
		bx.putString("refreshstatus", bv.getAsString("refreshstatus"));
		bx.putString("refreshcookies", bv.getAsString("refreshcookies"));
		bx.putString("acontent", bv.getAsString("acontent"));
		// rowid
		edt.commit();

		return bx;
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

	public void setEntryNotification(String who, int rowid, Uri geturi,
			String title, String summary, int keyref) {

		summary = summary.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				.replaceAll("&amp;", "&");
		if (summary.length() > 20 && summary.indexOf("CDATA[") == 3) {
			summary = summary.substring(9, summary.length() - 3);
		}
		summary = summary.replaceAll("<.*?>", "");

		int syncvib = reg.contains("syncvib") ? reg.getInt("syncvib", 1) : 1;

		//
		// CUSOMIZED
		//
		Notification notif = new Notification(R.drawable.ic_launcher, title
				+ " -- " + summary, System.currentTimeMillis()); // This text
																	// scrolls
																	// across
																	// the top.

		Intent intentJump2 = new Intent();
		intentJump2.setAction(Intent.ACTION_VIEW);
		// EC
		intentJump2.putExtra("uri", geturi.toString());
		intentJump2.putExtra("keyref", rowid);
		intentJump2.setData(geturi);

		intentJump2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

		PendingIntent pi2 = PendingIntent.getActivity(this, rowid, intentJump2,
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_FROM_BACKGROUND
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		// PendingIntent pi2 = PendingIntent.getActivity(mContext, 0,
		// intentJump2, Intent.FLAG_ACTIVITY_CLEAR_TOP |
		// Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND |
		// Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
		// Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
		// Intent.FLAG_ACTIVITY_MULTIPLE_TASK );

		// if( syncvib != 3 ){ // NOT OFF
		// notif.defaults = Notification.DEFAULT_LIGHTS |
		// Notification.DEFAULT_VIBRATE;
		// }else{
		// notif.defaults = Notification.DEFAULT_LIGHTS;

		// }
		notif.ledARGB = Color.argb(255, 0, 0, 250);

		notif.defaults = Notification.DEFAULT_ALL;

		// Random runx = new Random();
		// if (runx.nextBoolean()) {
		// notif.icon = R.drawable.skyreader;
		// } else {
		notif.icon = R.drawable.ic_launcher;
		// }
		notif.setLatestEventInfo(mCtx, title, summary, pi2); // This Text
																// appears after
																// the slide is
																// open

		Date da = new Date();
		if (da.getHours() < 10) {
			syncvib = 3;
		}

		switch (syncvib) {
		case 1: // ++_+
			notif.vibrate = new long[] { 100, 200 };
			// notif.vibrate = new long[] { 100, 200, 100, 200, 500, 200 };
			break;
		case 3: // None _
			break;
		case 2: // ++
			notif.vibrate = new long[] { 100, 200, 100, 200 };
			break;
		case 4: // +_++
			notif.vibrate = new long[] { 100, 200, 500, 200, 100, 200 };
			break;
		}

		reg = getSharedPreferences("Preferences", MODE_WORLD_WRITEABLE);
		edt = reg.edit();
		edt.putBoolean("notifier", true);
		edt.commit();
		mNM.notify(keyref, notif);
	}

}
