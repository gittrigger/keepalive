package com.au.kai;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import com.au.kai.Grip.PurchaseState;

public class Talk extends BroadcastReceiver {

	// private Custom mLog;

	// private static Object mStartingServiceSync = new Object();
	private WakeLock m5;
	private Context ctx;
	boolean priv = false;
	Intent mi;

	protected void onReceiveWithPrivilege(Context context, Intent intent,
			boolean privileged) {
		Log.i("ok", "TALK onReceiveWithPrivilege Receiving Privilege CONTENT "
				+ privileged);
		priv = privileged;
		ctx = context;
		action = intent.getAction();
		mi = intent;
		if (action == null) {
			action = "";
		}

		if (mi != null && mi.getExtras() != null) {
			Set<String> k = intent.getExtras().keySet();
			// Log.i("ok", "Running " + um.toString());
			Log.i("ok", "TALK Colu got " + k.size());
			Object[] b = k.toArray();

			for (int n = 0; n < b.length; n += 1) {
				Log.i("ok", "TALK Colu row " + b[n] + " ("
						+ (n + 1 >= b.length || b[n + 1] == null) + ")");
			}
		}
		serviceStart.sendEmptyMessageDelayed(2, 10);

	}

	public void onReceive(Context context, Intent intent) {
		Log.i("ok", "TALK onReceive Receive Intent " + intent.getAction());
		onReceiveWithPrivilege(context, intent, false);
		return;
	}

	// ctx = context;
	// action = intent.getAction();
	// serviceStart.sendEmptyMessageDelayed(2, 20);

	// Log.i("ok",
	// "99.9999        Internals Interconnections: "
	// + intent.getAction() + "");

	// MapView mx;
	Editor edt;
	SharedPreferences reg;

	Handler rub = new Handler() {
		public void handleMessage(Message msg) {
			pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
			if (pm != null) {
				Log.i("ok", "TALK Activating CPU");
				pm.userActivity(SystemClock.uptimeMillis() + 10, false);
				m5 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						"Activate CPU");
				m5.acquire();
				m5.release();
				m5 = null;
				pm = null;
			}
		}
	};

	Handler serviceStart = new Handler() {
		// long cs = 1;

		public void handleMessage(Message msg) {

			reg = ctx.getSharedPreferences("Preferences",
					Context.MODE_WORLD_WRITEABLE);
			edt = reg.edit();

			if (action.contains("SERVICE_STATE")
					&& reg.getLong("cs7", 1) < System.currentTimeMillis() - 23 * 60 * 1000) {

				Log.i("ok",
						"TALK Internals Interconnections "
								+ action
								+ " "
								+ priv
								+ " "
								+ (System.currentTimeMillis() - reg.getLong(
										"cs7", 1)) + "ms since last");
				// cs = SystemClock.uptimeMillis();
				edt.putLong("cs7", System.currentTimeMillis());
				edt.commit();
				// rub.sendEmptyMessageDelayed(2, 75);
				// gpsupdate.sendEmptyMessageDelayed(2, 200);
				networkstate.sendEmptyMessageDelayed(2, 200);
				// / try {
				// edt.commit();
				// } catch (OutOfMemoryError eh) {
				// Log.i("ok",
				// "no saving occured reasonable to ignore at once per scooch rate.");
				// }
			} else if (action
					.contains("com.android.vending.billing.RESPONSE_CODE")) {
				// Log.i("ok", "TALK PURCHASE REPLY");

				Log.i("ok",
						"TALK PURCHASE REPLY ("
								+ mi.getIntExtra("response_code", -2)
								+ " THIS IS("
								+ PurchaseState.valueOf(mi.getIntExtra(
										"response_code", -1)) + "), YES IS ("
								+ PurchaseState.PURCHASED.ordinal() + ", "
								+ PurchaseState.PURCHASED + ") ID("
								+ mi.getLongExtra("request_id", -1) + ")");

//				edt.putLong("play_in", System.currentTimeMillis());
				edt.putLong("play_code", System.currentTimeMillis());
				edt.putInt("play_billing_response_code",
						mi.getIntExtra("response_code", -1));
				edt.putLong("play_billing_request_id",
						mi.getLongExtra("request_id", -1));
				edt.commit();

			} else if (action
					.contains("com.android.vending.billing.IN_APP_NOTIFY")) {
				Log.i("ok",
						"TALK IN APP NOTIFY ("
								+ mi.getStringExtra("notification_id") + ")");

				edt.putLong("play_in", System.currentTimeMillis());

				int cx = 0;
				while (reg.contains("play_billing_notification_id_" + cx)) {
					cx++;
				}
				edt.putString("play_billing_notification_id_" + cx,
						mi.getStringExtra("notification_id"));

				edt.commit();

			} else if (action
					.contains("com.android.vending.billing.PURCHASE_STATE_CHANGED")) {

				// PURCHASE CHANGED ++++++++++++++++++++++++++++++
				// ({"nonce":-1390066432523881735,"orders":[{"notificationId":"android.test.purchased","orderId":"transactionId.android.test.purchased","packageName":"com.ag.whitehouseblog","productId":"android.test.purchased","purchaseTime":1332017312635,"purchaseState":0}]})
				// (ZHqgVuuytE5+FpafECFoKqxaQmN6hR96TA3jgeqduMa6QDbyWROU3VQx7WSAAiM2jmbsWgi3O+HCIsYvi7oPU6AN1GbHZg588RQnBmKD3WYlxRKyCWKYNyYwgOoiE5cOfnPkpY5fIhsZXZ0A+Kkc4vVLo53LPRwuXLNQT+/ECeXjm4WhMX1Dd2WmieHzv78syVm0PcX7qFwUofMrAtAT3v7atYiIS0UT/DOVf2gBWKxz/5j8ysHD4JNaqG9+FNFqXdbXd7jaXB3C3AzSIb3KG57iAn1WKLljFlgQEHK0WypKkLIrZcnhJjjlqCfWouX/xfECcvRBMvZvCfBEYp3cXA==)

				// EMPTY RECOVERY
				// PURCHASE CHANGED ++++++++++++++++++++++++++++++
				// ({"nonce":-7317996892148745570,"orders":[]})
				// (gnTuDniC2bgeSMElRShLjlQixuZpad+nf2d0cT7KG/Lm7Q8E3m2uHfGBKy0uiFHT5T3uzwyHT8DUOdBXMQPVM8cVrQWtNUpspFeqgb9hMBOi1dKBFZIHc8uTYUmaO+3Mzl9NooYLWEMBg7MVwaQJAddztILgOgPBKt9YxaUyUzdreR6scUH+b1CWVUkAF7E26Ebofk4VlWGX2Tp5tW9r6D16saUGBm/soIrRPLehnJGgn6iY8DJSVDXAnC9r3u+xvZrY5yVUkkUPDgdA3ZH8ig5dg/JWrJ64/GFIDOUWb1YedMtGcCoaB5tN6Pi/Fwk17DeVADK4L5L3lv1A4pNNuw==)

				String ssb = mi.getStringExtra("inapp_signed_data");
				String sg = mi.getStringExtra("inapp_signature");
				Log.i("ok", "TALK PURCHASE CHANGED (" + ssb + ") (" + sg + ")");
				if (ssb != null) {
					String inno = ssb.replaceAll(".*\"nonce\":", "");
					inno = inno.replaceAll(",.*", "");
					Log.i("ok",
							"TALK PURCHASE VERIFY (" + inno + ") ("
									+ (reg.contains("play_nonce_" + inno))
									+ ") ");

					if (reg.contains("play_nonce_" + inno)) {
						edt.putLong("play_changed", System.currentTimeMillis());
						edt.putString("play_billing_inapp_signed_data", ssb);
						edt.putString("play_billing_inapp_signature", sg);
						edt.remove("play_security");
						edt.remove("play_nonce_" + inno);
						// play_in
					} else {
						edt.putLong("play_security", System.currentTimeMillis());
						Log.e("ok", "TALK UNVERIFIED NONCE SECURITY(" + inno
								+ ")");
					}

					edt.commit();
				} else {
					Log.e("ok", "TALK HEY SERVICE REPLIED NULL");
					edt.putLong("play_in", System.currentTimeMillis());

				}

			} else {
				Log.w("ok", "TALK UNHANDLED REPLY " + action);
			}

			// if (reg.getLong("cs8", 1) < System.currentTimeMillis()
			// - reg.getInt("gpsminutes", 8) * 60 * 1000) {
			// edt.putLong("cs8", System.currentTimeMillis());
			// edt.commit();
			// rub.sendEmptyMessageDelayed(2, 75);
			// gpsupdate.sendEmptyMessageDelayed(2, 200);
			// networkstate.sendEmptyMessageDelayed(2, 400);
			// }
		}
	};

	Handler networkstate = new Handler() {
		public void handleMessage(Message msg) {
			ConnectivityManager cnnm = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cnnm == null) {
				Log.w("ok", "TALK Network Off");
				edt.putLong("anetwork", 3);
				edt.commit();
				return;
			}

			NetworkInfo ninfo = cnnm.getActiveNetworkInfo();
			if (ninfo == null) {
				Log.w("ok", "TALK Network Off");
				edt.putLong("anetwork", 2);
				edt.commit();
			} else {
				if (ninfo.isAvailable()) {
					if (ninfo.isConnectedOrConnecting() || ninfo.isConnected()) {
						edt.putLong("anetwork", System.currentTimeMillis());
						edt.commit();
					}
					ninfo = null;
				} else {
					ninfo = null;
					edt.putLong("anetwork", 1);
					edt.commit();
				}

			}

		}
	};

	Handler gpsupdate = new Handler() {
		public void handleMessage(Message msg) {
			gpsupdate.removeMessages(2);

			if (reg.getLong("adeepset", 1) < System.currentTimeMillis() - 20000) {
				Log.i("ok", "TALK Internals Interconnections: gps " + msg.what);

				LocationManager mLocator = (LocationManager) ctx
						.getSystemService(Context.LOCATION_SERVICE);

				Location l = mLocator
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (l == null) {
					l = mLocator
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}

				if (l != null) {
					if (reg.getFloat("lon", 1f) != (float) l.getLongitude()
							|| reg.getFloat("lat", 1f) != (float) l
									.getLatitude()
							|| reg.getFloat("adeep", 1f) != (float) l
									.getAccuracy()
							|| reg.getFloat("altitude", 1f) != (float) l
									.getAltitude()) {

						if (reg.getLong("adeepset", 1) > System
								.currentTimeMillis() - 5 * 60000
								&& reg.getFloat("adeep", 99999f) < l
										.getAccuracy()) {
							Log.i("ok",
									"TALK Internals Interconnections:          BONUS B");

						} else {
							Log.i("ok",
									"TALK Internals Interconnections: gps "
											+ l.getLatitude() + ","
											+ l.getLongitude() + ","
											+ l.getAccuracy() + ","
											+ l.getAltitude());

							edt.putFloat("lon", (float) l.getLongitude());
							edt.putFloat("lat", (float) l.getLatitude());
							edt.putFloat("adeep", (float) l.getAccuracy());
							edt.putLong("adeepset", System.currentTimeMillis());
							edt.putFloat("altitude", (float) l.getAltitude());
							try {
								edt.commit();
							} catch (OutOfMemoryError eh) {
								Log.i("ok",
										"TALK b no saving occured reasonable to ignore at once per scooch rate.");
							}
						}
					}
				}

				l = null;
				mLocator = null;

			}
		}
	};

	PowerManager pm;
	String action = "";

}
