package com.au.kai;

import java.util.Date;

import android.accounts.Account;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;

import com.android.vending.billing.IMarketBillingService;

public class Grip extends Service implements ServiceConnection {

	private static String llg = "ok";
	private Context mCtx;

	@Override
	public IBinder onBind(Intent arg0) {

		Log.w(llg, "onBind(S)");
		// getlatestrun();
		return mbinder;
	}

	@Override
	public void onDestroy() {
		Log.w(llg, "onDestroy(S)");
		// SharedPreferences mReg = getSharedPreferences("Preferences",
		// MODE_WORLD_WRITEABLE);
		// Editor mEdt = mReg.edit();

		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.w(llg, "REBIND(S)");

		super.onRebind(intent);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.w(llg, "START COMMAND(S)");

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.w(llg, "UNBIND(S)");
		return super.onUnbind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.w(llg, "onStart(S)");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mCtx = this.getApplicationContext();
		Log.w(llg, "onCreate(S)");

		try {
			mpref = getSharedPreferences("Preferences", MODE_WORLD_WRITEABLE);
			medt = mpref.edit();

			boolean bindResult = mCtx.bindService(new Intent(
					"com.android.vending.billing.MarketBillingService.BIND"),
					this, Context.BIND_AUTO_CREATE);

			if (bindResult) {
				Log.i(llg, "Service bind successful.");
			} else {
				Log.e(llg, "Could not bind to the MarketBillingService.");
			}

		} catch (SecurityException e) {
			Log.e(llg, "Security exception: " + e);
		}

		// mHandler = new Handler();
		// Thread thr = new Thread(null, this, llg + "_service_thread");
		// thr.start();

	}


	public void unbind() {

		try {
			unbindService(this);
		} catch (IllegalArgumentException e) {
			// This might happen if the service was disconnected
		}
	}

	/*
	 * / class CheckBillingSupported extends BillingRequest { public
	 * CheckBillingSupported() { // This object is never created as a side
	 * effect of starting this // service so we pass -1 as the startId to
	 * indicate that we should // not stop this service after executing this
	 * request. super(-1); }
	 * 
	 * @Override protected long run() throws RemoteException { Bundle request =
	 * cumbCup("CHECK_BILLING_SUPPORTED"); Bundle response =
	 * mPlay.sendBillingRequest(request); int responseCode =
	 * response.getInt("RESPONSE_CODE"); // if (Consts.DEBUG) { Log.i("ok",
	 * "CheckBillingSupported response code: " +
	 * ResponseCode.valueOf(responseCode)); // }
	 * 
	 * boolean billingSupported = (responseCode == ResponseCode.RESULT_OK
	 * .ordinal());
	 * ResponseHandler.checkBillingSupportedResponse(billingSupported); return
	 * -1; } }
	 * 
	 * public boolean checkBillingSupported() { return new
	 * CheckBillingSupported().runRequest(); }//
	 */

	IMarketBillingService mPlay;

	protected Bundle cumbCup(String method) {

		Bundle request = new Bundle();
		request.putString("BILLING_REQUEST", method);
		request.putInt("API_VERSION", 1);
		request.putString("PACKAGE_NAME", getPackageName());
		return request;
	}

	public void onServiceDisconnected(ComponentName arg0) {
		Log.i(llg,
				"onServiceDisconnected(S) MarketBillingService Disconnected.");

	}

	public enum ResponseCode {
		RESULT_OK, RESULT_USER_CANCELED, RESULT_SERVICE_UNAVAILABLE, RESULT_BILLING_UNAVAILABLE, RESULT_ITEM_UNAVAILABLE, RESULT_DEVELOPER_ERROR, RESULT_ERROR;

		// Converts from an ordinal value to the ResponseCode
		public static ResponseCode valueOf(int index) {
			ResponseCode[] values = ResponseCode.values();
			if (index < 0 || index >= values.length) {
				return RESULT_ERROR;
			}
			return values[index];
		}
	}

	// The possible states of an in-app purchase, as defined by Android Market.
	public enum PurchaseState {
		// Responses to requestPurchase or restoreTransactions.
		PURCHASED, // User was charged for the order.
		CANCELED, // The charge failed on the server.
		REFUNDED; // User received a refund for the order.

		// Converts from an ordinal value to the PurchaseState
		public static PurchaseState valueOf(int index) {
			PurchaseState[] values = PurchaseState.values();
			if (index < 0 || index >= values.length) {
				return CANCELED;
			}
			return values[index];

		}
	}

	boolean playbuy = false;

	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.i(llg, "onServiceConnected(S) MarketBillingService connected.");
		mPlay = IMarketBillingService.Stub.asInterface(service);

		Bundle dig = cumbCup("CHECK_BILLING_SUPPORTED");
		try {

			// int b = android.test.
			Bundle hat = mPlay.sendBillingRequest(dig);
			Log.i("ok", "GRIP BILLING REPLY (" + hat.getInt("RESPONSE_CODE")
					+ ", " + hat.getInt("RESPONSE_CODE") + " THIS IS("
					+ ResponseCode.valueOf(hat.getInt("RESPONSE_CODE"))
					+ "), YES IS (" + ResponseCode.RESULT_OK.ordinal() + ", "
					+ ResponseCode.RESULT_OK + ")");

			playbuy = (hat.getInt("RESPONSE_CODE") == ResponseCode.RESULT_OK
					.ordinal());

			if (playbuy) {
				Log.i("ok", "GRIP Buy Play!!!");
				// Toast.makeText(mCtx, "Invest In Play", 1880).show();
			} else {
				Log.i("ok", "GRIP Billing Veer");
			}

		} catch (NullPointerException en) {
			Log.i("ok", "GRIP REMOTE HERE EXCEPTION");
			// Toast.makeText(mCtx, "Play Billing Service REPLY failed", 1880)
			// .show();
			en.printStackTrace();
		} catch (RemoteException e) {
			Log.i("ok", "GRIP REMOTE EXCEPTION");
			// Toast.makeText(mCtx, "Play Billing Service Failed", 1880).show();
			e.printStackTrace();
		}

	}

	private final IBinder mbinder = new uiBinder();

	public class uiBinder extends Binder {
		Grip getService() {
			return Grip.this;
		}
	}

	Account[] ec;

	/*
	 * / public void run() { Log.w(llg,
	 * "run() ++++++++++++++++++++++++++++++++++"); { Message ml = new
	 * Message(); Bundle bl = new Bundle(); bl.putLong("ms",
	 * SystemClock.uptimeMillis()); ml.setData(bl); getlatest.sendMessage(ml); }
	 * } //
	 */
	private SharedPreferences mpref;
	private Editor medt;

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

	// public String datetime() {
	// String g = "";
	// Date d = new Date();
	// g = (d.getYear() + 1900) + "-" + ((d.getMonth() < 9) ? "0" : "")
	// + ((d.getMonth() + 1)) + "-" + ((d.getDate() < 10) ? "0" : "")
	// + d.getDate() + "T" + ((d.getHours() < 10) ? "0" : "")
	// + d.getHours() + ":" + ((d.getMinutes() < 10) ? "0" : "")
	// + d.getMinutes() + ":00";
	// return g;
	// }

	private String fixDate(String updated) {
		// day, month dd, yyyy hh:mm tt
		// m/d/year hh:mm tt
		// 2010-07-15T19:07:30+05:00
		if (updated.indexOf("CDATA[") > -1) {
			updated = updated.substring(updated.indexOf("CDATA[") + 6,
					updated.lastIndexOf("]]"));
		}
		String[] dateparts = updated.split(" ");
		if (dateparts.length == 1) {
			dateparts = updated.replaceAll("T", " ").split(" ");
		}

		// Log.i(llg, "fixDate (" + updated + ") parts(" + dateparts.length
		// + ") length(" + updated.length() + ")");
		if (updated.length() > 35) {
			return datetime();
		}
		// if( dateparts[0].contains(",") ){ dateparts =
		// updated.replaceFirst("T", " ").replaceFirst("..., ", "").split(" ");
		// }

		if (dateparts[0].contains("/") && dateparts[0].contains(":")) {

			int year = Integer.parseInt(dateparts[0].substring(
					dateparts[0].lastIndexOf("/") + 1,
					dateparts[0].lastIndexOf("/") + 5));
			int mon = Integer.parseInt(dateparts[0].substring(0,
					dateparts[0].indexOf("/")));
			int day = Integer.parseInt(dateparts[0].substring(
					dateparts[0].indexOf("/") + 1,
					dateparts[0].lastIndexOf("/")));
			if (mon < 10) {
				updated = year + "-0" + mon + "-";
			} else {
				updated = year + "-" + mon + "-";
			}
			if (day < 10) {
				updated += "0" + day + " ";
			} else {
				updated += day + " ";
			}
			int h = 0;
			int m = 0;
			h = Integer.parseInt(dateparts[0].substring(
					dateparts[0].indexOf(":") - 2,
					dateparts[0].lastIndexOf(":")));
			m = Integer.parseInt(dateparts[0].substring(dateparts[0]
					.indexOf(":") + 1));
			if (dateparts[1].toLowerCase().contains("pm") && h < 12) {
				h += 12;
			}
			if (dateparts[1].toLowerCase().contains("am") && h == 12) {
				h -= 12;
			}
			if (h < 10) {
				updated += "0" + h + ":";
			} else {
				updated += h + ":";
			}
			if (m < 10) {
				updated += "0" + m;
			} else {
				updated += m;
			}

			Log.i("ok", "GRIP Updated date to SQLite Format(" + updated
					+ ") #3");

		}
		// 2010-07-15T19:07:30+05:00
		if (dateparts[0].contains("-") && dateparts[1].contains(":")) {
			String[] dp = dateparts[0].replaceAll("-0", "-").split("-");
			int year = Integer.parseInt(dp[0]);
			int mon = Integer.parseInt(dp[1]);
			int day = Integer.parseInt(dp[2]);
			if (mon < 10) {
				updated = year + "-0" + mon + "-";
			} else {
				updated = year + "-" + mon + "-";
			}
			if (day < 10) {
				updated += "0" + day + " ";
			} else {
				updated += day + " ";
			}
			int h = 0;
			int m = 0;
			String[] t = dateparts[1].replaceAll(":0", ":").split(":");
			h = Integer.parseInt(t[0]);
			m = Integer.parseInt(t[1]);
			/*
			 * if(dateparts[2].toLowerCase().contains("pm") && h < 12){ h+=12;
			 * }if(dateparts[2].toLowerCase().contains("am") && h == 12){ h-=12;
			 * }//
			 */
			if (h < 10) {
				updated += "0" + h + ":";
			} else {
				updated += h + ":";
			}
			if (m < 10) {
				updated += "0" + m;
			} else {
				updated += m;
			}

			Log.i("ok", "GRIP Updated date to SQLite Format(" + updated
					+ ") #2");
		}
		if (dateparts[0].contains("/") && dateparts[1].contains(":")) {
			String[] dp = dateparts[0].split("/");
			int year = Integer.parseInt(dp[2]);
			int mon = Integer.parseInt(dp[0]);
			int day = Integer.parseInt(dp[1]);
			if (mon < 10) {
				updated = year + "-0" + mon + "-";
			} else {
				updated = year + "-" + mon + "-";
			}
			if (day < 10) {
				updated += "0" + day + " ";
			} else {
				updated += day + " ";
			}
			int h = 0;
			int m = 0;
			String[] t = dateparts[1].split(":");
			h = Integer.parseInt(t[0]);
			m = Integer.parseInt(t[1]);
			if (dateparts[2].toLowerCase().contains("pm") && h < 12) {
				h += 12;
			}
			if (dateparts[2].toLowerCase().contains("am") && h == 12) {
				h -= 12;
			}
			if (h < 10) {
				updated += "0" + h + ":";
			} else {
				updated += h + ":";
			}
			if (m < 10) {
				updated += "0" + m;
			} else {
				updated += m;
			}

			Log.i("ok", "GRIP Updated date to SQLite Format(" + updated
					+ ") #2");

		}
		if (dateparts.length > 5
				|| (dateparts.length == 5 && dateparts[3].contains(":"))) {
			// Month
			String[] month = new String(
					"xxx Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec xxx")
					.split(" ");
			int mon = 0;
			for (; mon < month.length; mon++) {
				if (month[mon].equalsIgnoreCase(dateparts[2])) {
					break;
				}
				if (dateparts[1].startsWith(month[mon])) {
					break;
				}
			}

			if (mon == 13) {
				Log.i("ok", "GRIP Unable to determine month in fixDate("
						+ updated + ")");
				return updated;
			}

			// Year
			Date d = new Date();
			int year = d.getYear() + 1900;
			if (dateparts[2].length() == 4) {
				year = Integer.parseInt(dateparts[2]);
			} else if (dateparts[3].length() == 4) {
				year = Integer.parseInt(dateparts[3]);
			} else if (dateparts[4].length() == 4) {
				year = Integer.parseInt(dateparts[4]);
			} else {
				Log.i("ok", "GRIP Unable to determine year in fixDate("
						+ updated + ") 2(" + dateparts[2] + ") 3("
						+ dateparts[3] + ")");
			}

			// Day
			int day = -1;

			if (dateparts[2].length() == 4 && !dateparts[0].contains(",")) {
				day = Integer.parseInt(dateparts[0]);
			} else if (dateparts[3].length() == 4) {// && dateparts[1].length()
													// > 0){// &&
													// dateparts[2].contains(",")){
				// dateparts[1] = dateparts[1].replaceAll(",", "");
				day = Integer.parseInt(dateparts[1]);
			}

			// Date == updated
			updated = year + "-";
			updated += (mon < 10 ? "0" + mon : mon) + "-";
			// if( mon < 10 ){
			// updated = year + "-0" + mon + "-";
			// }else{
			// updated = year + "-" + mon + "-";
			// }
			updated += (day < 10 ? "0" + day : day) + " ";
			// if( day < 10 ){updated += "0"+ day + " ";}else{updated += day +
			// " ";}

			// Hour Minute

			int h = 0;
			int m = 0;

			if (dateparts[3].contains(":")) {
				String[] t = dateparts[3].split(":");
				h = Integer.parseInt(t[0]);
				m = Integer.parseInt(t[1]);
			} else if (dateparts[4].contains(":")) {
				String[] t = dateparts[4].split(":");
				h = Integer.parseInt(t[0]);
				m = Integer.parseInt(t[1]);
				if (dateparts[5].toLowerCase().contains("pm") && h < 12) {
					h += 12;
				}
				if (dateparts[5].toLowerCase().contains("am") && h == 12) {
					h -= 12;
				}
			}

			// Time
			updated += (h < 10 ? "0" + h : h) + ":";
			updated += (m < 10 ? "0" + m : m);
			// if( h < 10 ){updated += "0"+ h + ":";}else{updated += h + ":";}
			// if( m < 10 ){updated += "0"+ m;}else{updated += m;}

			// {Message mx = new Message(); Bundle bx = new
			// Bundle();bx.putString("text","Updated date to SQLite Format("+updated+")");bx.putInt("l",1);mx.setData(bx);logoly.sendMessageDelayed(mx,pRate);}
		}

		return updated;
	}

}
