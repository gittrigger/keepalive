package com.au.kai;

import android.view.Display;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.Set;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.content.res.AssetFileDescriptor;
import com.au.boston.Grip.uiBinder;
import javax.microedition.khronos.opengles.GL;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.graphics.PixelFormat;
import android.database.Cursor;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;
import android.os.Environment;
import android.os.PowerManager;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.au.kai.Grip.PurchaseState;
import com.au.kai.Grip.ResponseCode;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

//import com.swarmconnect.Swarm;

public class KAi extends Activity {

	Context ctx;
	Activity axt;
	RelativeLayout bm;
	GameBoard mGame;
	Vibrator vibrator;
	GamePlayer[] us = new GamePlayer[5];
	InAppPurchases mPlay;
	InAds mAds;

	SharedPreferences reg;
	Editor edt;
	// Menu shareMenu;
	Random runx = new Random();
	int myn = 0;
	Typeface[] face;
	// cu
	// ImageView[] dpad = new ImageView[7];// 0 base

	final static int UP = 1;
	final static int DOWN = 2;
	final static int LEFT = 3;
	final static int RIGHT = 4;
	final static int JUMPUP = 5;
	final static int JUMPDOWN = 6;

	@Override
	protected void onRestoreInstanceState(Bundle si) {
		Log.i("ok", "RESTORE");

		if (mGame != null) {
			mGame.level = si.getInt("level", 0);
		}
		super.onRestoreInstanceState(si);
	}

	@Override
	protected void onSaveInstanceState(Bundle si) {
		Log.i("ok", "SAVE");

		if (mGame != null) {
			si.putInt("level", mGame.level);
		}
		super.onSaveInstanceState(si);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("ok", "PAUSE");
		
		if(mGame != null){
			mGame.onPause();
		}
		if(mPlay != null){
			mPlay = null;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("ok", "RESUME");
		if(mGame != null){
			mGame.onResume();
		}
	}

//	GLSurfaceView mGLView;
	RelativeLayout base;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

// context
		ctx = getApplicationContext();
		axt = this;

// seems required to accomplish transparent opengl frame
		setContentView(R.layout.main);


// preferences: 
		reg = ctx.getSharedPreferences("Preferences",
				Context.MODE_WORLD_WRITEABLE);
		edt = reg.edit();

// base->frame(bm, mGLView)
		base = (RelativeLayout) findViewById(R.id.base);
		bm = (RelativeLayout) findViewById(R.id.bm);

// fonts
		face = new Typeface[3];
		face[0] = Typeface.createFromAsset(getAssets(), "Roboto-Black.ttf");
		face[1] = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
		face[2] = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

		startParts.sendEmptyMessage(1);
		
	}
	int pRate = 72;


	int mSensoro = 1;






	
	
	int mServiceo = -1;







	int sizebase = 120;
	//int landscape = -1;
	Handler startParts = new Handler() {
		public void handleMessage(Message msg) {

			Display sd = getWindowManager().getDefaultDisplay();
			if(sd.getWidth() > sd.getHeight()){
				//landscape = 2;	
				sizebase = sd.getHeight();
			}else{
				//landscape = 1;
				sizebase = sd.getWidth();
			}

			//Toast.makeText(ctx,"START " + sizebase + " " + (int)(base.getWidth()) +" "+ bm.getWidth(), 4880).show();
//Screen
			vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

			// banner ads
			//mAds = new InAds(getMainLooper(), bm);

			// Game Board
			mGame = new GameBoard(getMainLooper(), bm);

			// in application purchaes
			//mPlay = new InAppPurchases(getMainLooper());

			//SensorService.sendEmptyMessageDelayed(2,pRate);

			if(1==2){
				Bundle hu = new Bundle();
				//hu.putByteArray("audio", fg);
				//hu.putInt("id", ab.getId());
				Message mu = new Message();
				mu.what = 2;
				mu.setData(hu);
				playaudio.sendMessageDelayed(mu, 20);
			}

			if(1==2){
				Bundle b8 = new Bundle();
				//b8.putString("path", "music1.mp3");
				b8.putString("path", "01 - Erik Friend - Interstellar.mp3");
				Message m8 = new Message();
				m8.setData(b8);
				// xuut.setDataSource(hx[c]);
				// xuut.prepare();
				Log.i("ok", "Expecting audio " + b8.getString("path") );
				m8.what = 2;

				//playaudio2.sendMessageDelayed(m8, 1750);
			}

		//	new GetAudio().execute();

	//		bort.sendEmptyMessageDelayed(2,2112);

		}
	};


	Handler bort = new Handler(){
		public void handleMessage(Message mg){

		//mGLView = new android.opengl.GLSurfaceView(ctx);
		//mGLView.setLayoutParams(getRelativeLayout(-1, -1));
		//while (findViewById(++hid) != null)
			//Log.w("ok", "hid catch ");

		//mGLView.setId(hid);
		//bm.addView(mGLView,0);

		Toast.makeText(ctx,"BORT",880).show();

		}
	};



	Handler LightSense = new Handler() {

		long running = 1;

		public void handleMessage(Message msg) {

			if (running > 1) {
				return;
			}

			running = System.currentTimeMillis();

			Thread gx = new Thread() {

				public void run() {

					// getSensorList(Sensor.TYPE_ALL);

					Log.i(G, "Sensor Light Begun");

					SensorManager sm = null;
					try {
						sm = (SensorManager) ctx
								.getSystemService(SENSOR_SERVICE);
					} finally {
					}

					Sensor sms = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

					SensorEventListener sl = new SensorEventListener() {

						int ix = 1;
						String pp = "";

						public void onAccuracyChanged(Sensor sensor,
								int accuracy) {
						}

						float[] s;

						public void onSensorChanged(SensorEvent event) {

							if (running > System.currentTimeMillis()) {
								return;
							}
							running = System.currentTimeMillis() + 130;
							pp = "";

							s = event.values;
							for (ix = 0; ix < s.length; ix++) {
								pp += "[" + ix + "]" + s[ix] + " ";
							}

							{
								Message ml = new Message();
								Bundle bl = new Bundle();
								bl.putInt("color", (int) s[0]);
								ml.setData(bl);
								bklight.sendMessage(ml);
							}

							Log.i(G, "" + event.sensor.getName() + " Sensor ["
									+ pp);
							// + event.sensor.getResolution() + "ct "
							// + event.sensor.getPower() + "mA] " + pp);

						}

					};

					sm.registerListener(sl, sms,
							SensorManager.SENSOR_DELAY_FASTEST);

				}
			};
			gx.start();

		}
	};

	Handler bklight = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bdl = msg.getData();
			int c = bdl.getInt("color");
		//	mRenderer.setBGColor = c;

		}
	};















	com.au.boston.Grip.uiBinder autBind;
        com.au.boston.Grip autSer;
        boolean boundBind = false;
        private ServiceConnection serBind = new ServiceConnection() {

                public void onServiceConnected(ComponentName arg0, IBinder arg1) {
                        // TODO Auto-generated method stub

                        Log.w("ok", "NICE BIND onServiceConnected Service Connected");
                        com.au.boston.Grip.uiBinder ub = (com.au.boston.Grip.uiBinder) arg1;
                        autSer = ub.getService();
                        boundBind = true;
                        //cgo.setVisibility(View.VISIBLE);
                        // autSer.runpully(updateStatus);

                        cgoon.sendEmptyMessage(2);
                        // if (!BUY_UPGRADED) {
                        // checkPurchase.sendEmptyMessageDelayed(2, 125);
                        // }

                        Log.i("ok", "NICE BIND goto refreshCheck");
                        // refreshCheck.sendEmptyMessageDelayed(2, 75);

                }

                public void onServiceDisconnected(ComponentName arg0) {
                        Log.w("ok", "NICE BIND onServiceDisconnected Service Disconnected");
                        boundBind = false;
                }
        };


	Handler cgoon = new Handler() {
                public void handleMessage(Message msg) {
                        AnimationSet r = new AnimationSet(true);
                        {
                                Animation r2 = new RotateAnimation(0f, 360f,
                                                Animation.RELATIVE_TO_SELF, 0.5f,
                                                Animation.RELATIVE_TO_SELF, 0.5f);
                                // r2.setFillAfter(true);
                                r2.setDuration(520);
                                r2.setInterpolator(AnimationUtils.loadInterpolator(ctx,
                                                android.R.anim.decelerate_interpolator));
                                r.addAnimation(r2);
                        }
                        //cgo.startAnimation(r);

			Toast.makeText(ctx,"PULL FILES HERE",880).show();
                        autSer.runpully(updateStatus);
                }
        };

	Handler updateStatus = new Handler() {
                public void handleMessage(Message msg) {
                        Bundle bx = msg.getData();

                        if (bx == null) {
                                Log.i("ok", "BUNDLE EMPTY");
                        } else if (bx.containsKey("status")) {

                                Log.i("ok",
                                                "update Status handler received bundle ("
                                                                + bx.getString("status") + ")");
                                new setStatus().execute("" + bx.getString("status"));

                        } else {
                                Log.i("ok", "BUNDLE has no status");
                        }

                }
        };

	public class setStatus extends AsyncTask<String, Void, Void> {
                String st = "";

                @Override
                protected Void doInBackground(String... s) {

                        st = s[0];
                        return null;
                }

                @Override
                protected void onPostExecute(Void result) {
			Toast.makeText(ctx,"Status: "+st,1880).show();
                        //cstatus.setText(st);
                        //cstatus.setBackgroundColor(my9a[runx.nextInt(my9a.length - 1)]);

                        //getChat.sendEmptyMessageDelayed(2, 10);

                        super.onPostExecute(result);
                }

        }





	private class GetAudio extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... a) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			//long ms = SystemClock.uptimeMillis();
                        //Bundle br = new Bundle();
                        //br.putString("title", "ip");
                        //br.putString("dest", "http://newdream.net/~haven/ideal.pl?since=" + lastwhen);
			//br.putString("dest", "http://erikfriend.com/music/Sombrero%20Galaxy/05%20-%20Erik%20Friend%20-%20Galactic%20Empire.mp3");
                        //br.putString("uri", hereuri.toString());
			Toast.makeText(ctx,"Pull File",1880).show();
                	//Bundle bx = pullFile(br);
			Log.i("ok","PULL FILE\n");
		}

	}








	boolean upause = false;
	int vin = 1;
	Handler playaudio = new Handler() {
		public void handleMessage(Message msg) {
			if (upause) {
				return;
			}
			try {
				if (xuut != null) {
					xuut.stop();
					xuut.release();
				}
				playaudio2.removeMessages(2);

				Bundle ba = msg.getData();
				byte[] s = ba.getByteArray("audio");


	File file = null;

				if(1==1){
					//File filea = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ok/opt");//Asset
					//filea.mkdirs();
					//file = new File(filea.getAbsolutePath(), "vh" + (vsv) + ".3gp");
					
					//FileInputStream fs = null;
					//InputStream fs = null;

				//	try {
						//fs = new FileInputStream(file);
				//		fs = getAsset().open("");
				//		byte[] bf = new byte[(int) file.length()];
				//		fs.read(bf, 0, (int) file.length());
					//	e.put("aback", bf);
					//} catch (FileNotFoundException e2) {
				//		e2.printStackTrace();
				//	} catch (IOException e2) { 
				//		e2.printStackTrace();
				//	}

				}else{

					File filea = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ok/opt");
					filea.mkdirs();

					file = new File(filea.getAbsolutePath(), "bh" + (++vin) + ".3gp");

					FileOutputStream fs = null;
					fs = new FileOutputStream(file);
					// byte[] bf = new byte[(int) file.length()];
					fs.write(s);// , 0, (int) s.length);
					// e.put("aback", bf);
					// Toast.makeText(
					// ctx,
					// file.getAbsolutePath() + "\n"
					// + file.length() + "\n"
					// + e.getAsByteArray("aback").length,
					// 2300).show();
					fs.flush();
					fs.close();
				}

				Bundle b8 = new Bundle();
				b8.putString("path", file.getAbsolutePath());
				Message m8 = new Message();
				m8.setData(b8);
				m8.what = 2;
				playaudio2.sendMessageDelayed(m8, 200);

			} catch (FileNotFoundException e2) {
				Log.e("whoo", e2.getLocalizedMessage());
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				Log.e("IO WH", e2.getLocalizedMessage());
			} catch (RuntimeException e2) {
				Log.i("ok", "audio stop error n5 " + e2.getMessage());
			}

			// try {
			// } catch (IOException k5) {
			// setr("Drat " + k5.getLocalizedMessage());
			// }

		}
	};
	int lastplay = 0;
	Handler playaudio2 = new Handler() {
		public void handleMessage(Message msg) {
			if (upause) {
				return;
			}
			Bundle ba = msg.getData();
			String path = ba.getString("path");
			int id = ba.getInt("id");
			Log.i("ok", "playaudio " + id + " " + (path != null ? path : "-no path-") + " ");

			//ImageView ho4 = (ImageView) findViewById(id);
			//if (ho4 == null) {
			//	return;
				// ho4.setBackgroundColor(Color.argb(55, 0, 0, 240));
			//}

			xuut = new MediaPlayer();
			xuut.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

			try {
				//if (path.length() == 0 && aheardlist.length() > 0) {
				//	path = aheardlist;
				//}
				if (path.contains(",")) {
					String[] hx = path.split(",");
					File apng;
					int ddc = 1;
					for (int c = 0; c < hx.length; c++) {
						if (hx[c].length() > 0) {
							apng = new File(hx[c]);
							if (apng.exists()) {

								Bundle b8 = new Bundle(ba);
								b8.putString("path", hx[c]);
								Message m8 = new Message();
								m8.setData(b8);
								// xuut.setDataSource(hx[c]);
								// xuut.prepare();
								Log.i("ok", "Expecting audio " + hx[c] + " in "
										+ ddc + " + "
										+ ((apng.length() / 1024) * 4));
								m8.what = 2;

								playaudio2.sendMessageDelayed(m8, ddc);
								ddc += 850 + (int) (apng.length() / 1.3);
								// new MediaPla
							}

						}
					}
				} else if (path.length() > 0) {

					//File ap = new File(path);
					AssetFileDescriptor ap = getAssets().openFd(path);
					// xuut =
					if ( ap == null) {
						Log.i("ok","No File " + path);
					}else{
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						int result = audioManager.requestAudioFocus(newb,
								AudioManager.STREAM_MUSIC,
								AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

						if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
							Log.i("ok", "no audio grant");
							// could not get audio focus.

						} else {
							Log.i("ok",
									"playing audio file ");
											//+ ap.getAbsolutePath() + " "
											//+ ap.length());

							// if (ap.length() > 1000) {
							//Animation a6 = AnimationUtils.loadAnimation(ctx, R.anim.playon);

							//a6.setStartOffset(75);
							//a6.setDuration((int) ((ap.length())));
							//ho4.clearAnimation();
							//xuut.setDataSource(ctx, Uri.fromFile(ap));
							Toast.makeText(ctx, "Playing", 1880).show();
							xuut.setDataSource(ap.getFileDescriptor());
							//xuut.setVolume(1f, 1f);
							xuut.prepare();
							xuut.start();

							//ho4.startAnimation(a6);
							// }

							// ho4.setImageResource(R.drawable.ic_media_pause);
						}
					}
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				Log.i("ok", "playing audio b " + e.getMessage());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				Log.i("ok", "playing audio c " + e.getMessage());

				e.printStackTrace();
			} catch (IOException e) {
				Log.i("ok", "playing audio d " + e.getMessage());
				// if (e.getLocalizedMessage().contains("Prepare")) {
				// } else {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// }
			} catch (RuntimeException e2) {
				Log.i("ok", "runerror n " + e2.getMessage());

			}// .setDataSource(file.getAbsolutePath());

		}
	};

	OnAudioFocusChangeListener newb = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int fc) {
			Log.i("ok", "Focus change audio " + fc);

			try {
				switch (fc) {
				case AudioManager.AUDIOFOCUS_GAIN:
					// resume playback
					if (xuut == null) {
						Log.i("ok", "HUH, GAIN AUDIO nothing to play.");
						// initMediaPlayer();
					} else if (!xuut.isPlaying()) {
						xuut.start();
						xuut.setVolume(1.0f, 1.0f);
					}
					break;

				case AudioManager.AUDIOFOCUS_LOSS:
					// Lost focus for an unbounded amount of time: stop playback
					// and
					// release media player
					if (xuut != null && xuut.isPlaying()) {
						xuut.stop();
					}

					xuut.release();
					xuut = null;
					break;

				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					// Lost focus for a short time, but we have to stop
					// playback. We don't release the media player because
					// playback
					// is likely to resume
					if (xuut != null && xuut.isPlaying()) {
						xuut.pause();
					}

					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
					// Lost focus for a short time, but it's ok to keep playing
					// at an attenuated level
					if (xuut != null && xuut.isPlaying()) {
						xuut.setVolume(0.1f, 0.1f);
					}
					break;
				// case default:
				// Log.i("ok","AUDIO FOCUS INSTRUCTION " + fc);
				// break;
				}
			} catch (RuntimeException e2) {
				Log.w("ok", "audio running " + e2.getMessage());
			}

		}
	};
	MediaPlayer xuut;




























	String G = "ok";
       Handler SensorService = new Handler(){
		boolean running = false;
		public void handleMessage(Message msg){
			final Bundle bdl = msg.getData();

			SharedPreferences mReg = getSharedPreferences("Preferences", MODE_WORLD_READABLE);
		//	if( !mReg.contains("waveopt") ){
				//Editor mEdt = mReg.edit();  mEdt.putBoolean("wave", true); mEdt.putBoolean("waveopt", true); mEdt.commit();
		//	}else

			//{
                		//if( !mReg.contains("wave") ){ return; }
                		if(running){return;}

//final int mpt = mPurpose.length;
				running = true;
				Thread tx = new Thread(){
					boolean mStable = true;
					int position = 0;float[] lastvalues;
					long smooth = 34;//long smoothtext = 32;//String cn = "";

					public void run(){




			}
		};
		tx.start();
	}
};







	class GamePlayer extends Handler {
		private String who;
		public int atlevel = -1;
		public int screenwidth = 240;
		public int screenheight = 120;

		Canvas me;
		public ImageView see;
		private RelativeLayout.LayoutParams here;

		public Bitmap hnk;
		public Canvas cnkh;
		public ImageView ink;

		long[] patht = new long[7];
		int pathat = 0;
		int pathwas = 0;
		Paint dap;

		// boolean injump = false;

		public GamePlayer(String m, Looper clooper) {
			new GamePlayer(50, runx.nextInt(200), m, clooper);
		}

		public int left = 0;
		public int top = 0;
		public int color = Color.BLACK;
		Context ctx;

		public GamePlayer(int mleft, int mtop, String m, Looper clooper) {
			super(clooper);
			Log.i("ok", "Starting " + m + "  @(" + left + "," + top + ")");

			if(bm.getWidth() > bm.getHeight()){
				basesize = bm.getHeight();
			}else{
				basesize = bm.getWidth();
			}

			who = m;
			here = new RelativeLayout.LayoutParams(-2, -2);
			here.setMargins(left, top, 0, 0);
			left = mleft;
			top = mtop;
			Bitmap b9 = Bitmap.createBitmap(101, 101, Bitmap.Config.ARGB_8888);
			me = new Canvas(b9);

			see = (ImageView) getView("ImageView");
			see.setLayoutParams(here);
			see.setScaleType(ScaleType.MATRIX);
			see.setImageBitmap(b9);
			//mGame.board.addView(see);

			if(mGame.level >= 0){
				if(mGame.level < mGame.pk.length && mGame.pk[mGame.level] != null ){ 
					mGame.pk[mGame.level].addView(see);
				}
			}
			
			Log.i("ok", "Starting " + m + "  @(" + left + "," + top + ") ("
					+ see.getId() + ") at level " + mGame.level );

			dap = new Paint();
			dap.setColor(Color.BLUE);
			dap.setAntiAlias(true);
			dap.setStrokeWidth(14f);

			//bnk2

			this.jump();
		}

		Paint p9;
		long powderit = 0;
		int powderleft = 0;
		int powdertop = 0;
		int basesize = 220;//clas
		public void powder() {

			if( powderit > SystemClock.uptimeMillis() ){
				return;
			}
			powderit = SystemClock.uptimeMillis() + 32;

			if(hnk == null){
				try {

				Log.i("ok","Smoke Next create dust image");
				hnk = Bitmap.createBitmap(basesize, basesize, Bitmap.Config.ARGB_8888);
				cnkh = new Canvas(hnk);
				ink = (ImageView) getView("ImageView");
//drawRoundRect
				RelativeLayout.LayoutParams br = getRelativeLayout(-2, -2);
				br.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
				//br.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);
				ink.setLayoutParams(br);
				ink.setScaleType(ScaleType.MATRIX);
				ink.setImageBitmap(hnk);
				p9 = new Paint();
				p9.setStrokeWidth(8f);
				p9.setAntiAlias(true);
//cnk.drawCircle
				//p9.setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);
				//cnk.drawLine(0, (int) (bnk.getHeight() / 2), bnk.getWidth(), (int) (bnk.getHeight() / 2), p9);
				mGame.board.addView(ink);

				} catch (OutOfMemoryError eb){
					Log.w("ok","DUST IMAGE ALERT");
					cnkh = null;
				}
			}


			if(cnkh != null){
				powderleft = left;
				if(injump){powderleft = startleft;}
				powdertop = top;
				if(injump){powdertop = starttop;}
				

				p9.setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);

				if(injump){
				cnkh.drawCircle((float)(powderleft + see.getWidth()/2 -18+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-5+runx.nextInt(15)), (float)( 4 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawCircle((float)(powderleft + see.getWidth()/2 -5+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-5+runx.nextInt(15)), (float)( 2 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawCircle((float)(powderleft + see.getWidth()/2 +5+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-0+runx.nextInt(15)), (float)( 2 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawCircle((float)(powderleft + see.getWidth()/2 +18+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-5+runx.nextInt(15)), (float)( 5 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawCircle((float)(powderleft + see.getWidth()/2 +38+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-9+runx.nextInt(15)), (float)( 4 + runx.nextInt(injump?15:2) ), p9);
				}

				if(!injump){

				cnkh.drawRect((float)(powderleft + see.getWidth()/2 -18+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-5+runx.nextInt(15)), (float)(powderleft + see.getWidth()/2 -18+15+4 ), (float)( powdertop+see.getHeight()-5+15+4 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawRect((float)(powderleft + see.getWidth()/2 -5+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-5+runx.nextInt(15)), (float)(powderleft + see.getWidth()/2 -5+15+2 ), (float)( powdertop+see.getHeight()-5+15+2 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawRect((float)(powderleft + see.getWidth()/2 +5+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-0+runx.nextInt(15)), (float)(powderleft + see.getWidth()/2 -0+15+5 ), (float)( powdertop+see.getHeight()-0+15+2 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawRect((float)(powderleft + see.getWidth()/2 +18+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-5+runx.nextInt(15)), (float)(powderleft + see.getWidth()/2 -18+15+9 ), (float)( powdertop+see.getHeight()-5+15+5 + runx.nextInt(injump?15:2) ), p9);
				cnkh.drawRect((float)(powderleft + see.getWidth()/2 +38+runx.nextInt(15) ), (float)(powdertop+see.getHeight()-9+runx.nextInt(15)), (float)(powderleft + see.getWidth()/2 -18+15+4 ), (float)( powdertop+see.getHeight()-5+15+4 + runx.nextInt(injump?15:2) ), p9);

				}
				ink.postInvalidate();
			}
		}

		public void jump() {

//Log.i("ok", "JUMP " + who + " " + forcen + " " + injump + "/" + injumpup + " (" + left + "," + (top + see.getHeight()) + ") " + SystemClock.uptimeMillis());

			if (!injump) {
				//forcen = 7;
				jumpup(6);
			//} else if (injumpup && forcen > 2 && forcen < 18) {
			// Log.i("ok", "PUSHING JUMP " + forcen);
			//	forcen += 2;

			}
		}

		int forcen = 20;
		int forcer = 7;

		boolean injump = false;
		boolean injumpup = false;

		void headbody(int w) {

			if (w == DOWN) {
				dap.setColor(color);
				me.drawCircle(50, 38, ah+9, dap);

				dap.setColor(Color.BLACK);
				me.drawCircle(50, 39, ah+7, dap);
				me.drawLine(50, 40, 50, 70, dap);
			}
//zoomHere
			if (w == UP) {
				dap.setColor(Color.BLACK);
				me.drawCircle(50, 39, ah+7, dap);

				dap.setColor(color);
				me.drawCircle(50, 38, ah+9, dap);
				me.drawLine(50, 40, 50, 70, dap);
			}

			if (w == RIGHT) {
				dap.setColor(color);
				me.drawCircle(49, 38, ah+9, dap);

				dap.setColor(Color.BLACK);
				me.drawCircle(50, 39, ah+7, dap);
				me.drawLine(50, 40, 50, 70, dap);
			}

			if (w == LEFT) {
				dap.setColor(color);
				me.drawCircle(51, 38, ah+9, dap);

				dap.setColor(Color.BLACK);
				me.drawCircle(50, 39, ah+7, dap);
				me.drawLine(50, 40, 50, 70, dap);
			}

			if (w == JUMPDOWN) {
				dap.setColor(color);
				me.drawCircle(50, 39, ah+9, dap);

				dap.setColor(Color.BLACK);
				me.drawCircle(50, 42, ah+7, dap);
				me.drawLine(50, 40, 50, 70, dap);
			}
			if (w == JUMPUP) {
				dap.setColor(Color.BLACK);
				me.drawCircle(50, 39, ah+7, dap);

				dap.setColor(color);
				me.drawLine(50, 40, 50, 70, dap);
				me.drawCircle(50, 38, ah+9, dap);
			}
		}

		int armstep = 0;

		int ah = 22;
		void arms(int w) {

			if (w == DOWN) {
				dap.setColor(color);

				// a
				if (runx.nextBoolean() && runx.nextBoolean()) {// crossing
					// arms
					me.drawLine(ah+50, 45, ah+60, 62, dap);
					me.drawLine(ah+60, 62, ah+50, 64, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// right
					// down
					me.drawLine(ah+50, 45, ah+60, 62, dap);// left
					me.drawLine(ah+60, 62, ah+50, 64, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// left
					// down
					me.drawLine(ah+50, 45, ah+60, 62, dap);
					me.drawLine(ah+60, 62, ah+51, 62, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {
					// holding self hands
					me.drawLine(ah+50, 45, ah+60, 62, dap);
					me.drawLine(ah+60, 62, ah+52, 71, dap);
				} else {// at side
					me.drawLine(ah+50, 45, 55, ah+62, dap);
					me.drawLine(ah+55, 62, 55, ah+74, dap);
				}

				if (runx.nextBoolean() && runx.nextBoolean()) {// crossing
					me.drawLine(50-ah, 45, 40-ah, 62, dap);
					me.drawLine(40-ah, 62, 50-ah, 64, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// right
					me.drawLine(50-ah, 45, 45-ah, 62, dap);// right
					me.drawLine(45-ah, 62, 45-ah, 74, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// left
					me.drawLine(50-ah, 45, 40-ah, 62, dap);
					me.drawLine(40-ah, 62, 49-ah, 72, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {
					me.drawLine(50-ah, 45, 40-ah, 62, dap);
					me.drawLine(40-ah, 62, 48-ah, 71, dap);
				} else {
					me.drawLine(50-ah, 45, 45-ah, 62, dap);
					me.drawLine(45-ah, 62, 45-ah, 74, dap);
				}

			}

			if (w == UP) {
				dap.setColor(color);
				// a
				if (runx.nextBoolean() && runx.nextBoolean()) {// crossing
					me.drawLine(ah+50, 45, ah+60, 62, dap);
					me.drawLine(ah+60, 62, ah+50, 64, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// right
					me.drawLine(ah+50, 45, ah+60, 62, dap);// left
					me.drawLine(ah+60, 62, ah+50, 64, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// left
					me.drawLine(ah+50, 45, ah+60, 62, dap);
					me.drawLine(ah+60, 62, ah+50, 64, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {
					me.drawLine(ah+50, 45, ah+60, 62, dap);
					me.drawLine(ah+60, 62, ah+50, 73, dap);
				} else {
					me.drawLine(ah+50, 45, ah+55, 62, dap);
					me.drawLine(ah+55, 62, ah+55, 73, dap);
				}

				if (runx.nextBoolean() && runx.nextBoolean()) {// crossing
					me.drawLine(50-ah, 45, 40-ah, 62, dap);
					me.drawLine(40-ah, 62, 50-ah, 64, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// right
					me.drawLine(50-ah, 45, 45-ah, 62, dap);// right
					me.drawLine(45-ah, 62, 45-ah, 73, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {// left
					me.drawLine(50-ah, 45, 40-ah, 62, dap);
					me.drawLine(40-ah, 62, 50-ah, 73, dap);
				} else if (runx.nextBoolean() && runx.nextBoolean()) {
					me.drawLine(50-ah, 45, 40-ah, 62, dap);
					me.drawLine(40-ah, 62, 50-ah, 73, dap);
				} else {// at side
					me.drawLine(50-ah, 45, 45-ah, 62, dap);
					me.drawLine(45-ah, 62, 45-ah, 73, dap);
				}
			}

			if (w == RIGHT) {

				if (armstep == 0) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 45, 51, 62, dap);
					me.drawLine(51, 62, 55, 75, dap);

					headbody(RIGHT);
					dap.setColor(color);
					me.drawLine(50, 45, 50, 62, dap);
					me.drawLine(50, 62, 51, 76, dap);
				} else if (armstep == 1) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 45, 50, 62, dap);
					me.drawLine(50, 62, 51, 76, dap);

					headbody(RIGHT);
					dap.setColor(color);
					me.drawLine(50, 45, 45, 62, dap);
					me.drawLine(45, 62, 45, 75, dap);
				} else if (armstep == 2) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 45, 45, 62, dap);
					me.drawLine(45, 62, 45, 75, dap);

					headbody(RIGHT);
					dap.setColor(color);
					me.drawLine(50, 45, 51, 62, dap);
					me.drawLine(51, 62, 55, 75, dap);

				}
			}

			if (w == LEFT) {

				if (armstep == 0) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 45, 49, 62, dap);
					me.drawLine(49, 62, 45, 75, dap);

					headbody(LEFT);
					dap.setColor(color);
					me.drawLine(50, 45, 50, 62, dap);
					me.drawLine(50, 62, 49, 76, dap);
				} else if (armstep == 1) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 45, 50, 62, dap);
					me.drawLine(50, 62, 49, 76, dap);

					headbody(LEFT);
					dap.setColor(color);
					me.drawLine(50, 45, 55, 62, dap);
					me.drawLine(55, 62, 55, 75, dap);
				} else if (armstep == 2) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 45, 55, 62, dap);
					me.drawLine(55, 62, 55, 75, dap);

					headbody(LEFT);
					dap.setColor(color);
					me.drawLine(50, 45, 49, 62, dap);
					me.drawLine(49, 62, 45, 75, dap);
				}
			}
			if (w == JUMPDOWN) {
				dap.setColor(color);
				me.drawLine(ah+50, 48, ah+60, 49, dap);
				me.drawLine(ah+60, 49, ah+65, 62, dap);

				me.drawLine(50-ah, 48, 40-ah, 49, dap);
				me.drawLine(40-ah, 49, 35-ah, 62, dap);

			}

			if (w == JUMPUP) {
				dap.setColor(color);

				if (patht[LEFT] > patht[RIGHT]
						&& patht[LEFT] > SystemClock.uptimeMillis() - 1200) {
					me.drawLine(ah+50, 45, ah+60, 62, dap);
					me.drawLine(ah+60, 62, ah+60, 75, dap);

					me.drawLine(50-ah, 45, 35-ah, 48, dap);
					me.drawLine(35-ah, 48, 35-ah, 25, dap);

				} else if (patht[LEFT] < patht[RIGHT]
						&& patht[RIGHT] > SystemClock.uptimeMillis() - 1200) {
					me.drawLine(ah+50, 45, ah+65, 48, dap);
					me.drawLine(ah+65, 48, ah+65, 25, dap);

					me.drawLine(50-ah, 45, 40-ah, 62, dap);
					me.drawLine(40-ah, 62, 40-ah, 75, dap);

				} else {
					me.drawLine(ah+50, 45, ah+55, 62, dap);
					me.drawLine(ah+55, 62, ah+55, 75, dap);

					me.drawLine(50-ah, 45, 45-ah, 62, dap);
					me.drawLine(45-ah, 62, 45-ah, 75, dap);
				}
			}
		}

		void legs(int w) {

			if (w == DOWN) {
				dap.setColor(Color.DKGRAY);
				me.drawLine(ah+50, 70, ah+54, 82, dap);
				me.drawLine(ah+54, 82, ah+54, 98, dap);

				// me.drawLine(50, 70, 35, 90, dap);
				me.drawLine(50-ah, 70, 46-ah, 82, dap);
				me.drawLine(46-ah, 82, 46-ah, 98, dap);
			}
			if (w == UP) {
				dap.setColor(Color.DKGRAY);
				me.drawLine(ah+50, 70, ah+52, 82, dap);
				me.drawLine(ah+52, 82, ah+52, 98, dap);

				// me.drawLine(50, 70, 35, 90, dap);
				me.drawLine(50-ah, 70, 48-ah, 82, dap);
				me.drawLine(48-ah, 82, 48-ah, 98, dap);
			}

			if (w == RIGHT) {
				// bm.setBackgroundColor(Color.BLACK);

				if (armstep == 0) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 69, 49, 82, dap);// left
					me.drawLine(49, 82, 45, 96, dap);

					dap.setColor(Color.GRAY);
					me.drawLine(50, 69, 55, 82, dap);// right
					me.drawLine(55, 82, 55, 96, dap);
				} else if (armstep == 1) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 69, 55, 82, dap);// left
					me.drawLine(55, 82, 55, 96, dap);

					dap.setColor(Color.GRAY);
					me.drawLine(50, 69, 50, 82, dap);// right
					me.drawLine(50, 82, 50, 98, dap);
				} else if (armstep == 2) {

					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 69, 50, 82, dap);// left
					me.drawLine(50, 82, 50, 98, dap);

					dap.setColor(Color.GRAY);
					me.drawLine(50, 69, 49, 82, dap);// right
					me.drawLine(49, 82, 45, 96, dap);
				}

			}

			if (w == LEFT) {

				if (armstep == 0) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 69, 51, 82, dap);// right
					me.drawLine(51, 82, 55, 96, dap);

					dap.setColor(Color.GRAY);
					me.drawLine(50, 69, 45, 82, dap);// left
					me.drawLine(45, 82, 45, 96, dap);
				} else if (armstep == 1) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 69, 45, 82, dap);// right
					me.drawLine(45, 82, 45, 96, dap);

					dap.setColor(Color.GRAY);
					me.drawLine(50, 69, 50, 82, dap);// left
					me.drawLine(50, 82, 50, 98, dap);
				} else if (armstep == 2) {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 69, 50, 82, dap);// right
					me.drawLine(50, 82, 50, 98, dap);

					dap.setColor(Color.GRAY);
					me.drawLine(50, 69, 51, 82, dap);// left
					me.drawLine(51, 82, 55, 96, dap);
				}

				// me.drawLine(50, 70, 45, 82, dap);
				// me.drawLine(45, 82, 45, 98, dap);
			}

			if (w == JUMPDOWN) {
				dap.setColor(Color.DKGRAY);
				me.drawLine(50, 70, 60, 77, dap);
				me.drawLine(60, 77, 60, 90, dap);

				me.drawLine(50, 70, 40, 77, dap);
				me.drawLine(40, 77, 40, 90, dap);
			}

			if (w == JUMPUP) {// standing

				if (patht[LEFT] > patht[RIGHT]
						&& patht[LEFT] > SystemClock.uptimeMillis() - 1200) {

					dap.setColor(Color.GRAY);
					me.drawLine(50, 70, 55, 82, dap);// right
					me.drawLine(55, 82, 55, 98, dap);

					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 70, 45, 70, dap);// left
					me.drawLine(45, 70, 45, 87, dap);

				} else if (patht[LEFT] < patht[RIGHT]
						&& patht[RIGHT] > SystemClock.uptimeMillis() - 1200) {

					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 70, 55, 70, dap);// right
					me.drawLine(55, 70, 55, 87, dap);

					dap.setColor(Color.GRAY);
					me.drawLine(50, 70, 45, 82, dap);
					me.drawLine(45, 82, 45, 98, dap);

				} else {
					dap.setColor(Color.DKGRAY);
					me.drawLine(50, 70, 52, 82, dap);
					me.drawLine(52, 82, 52, 98, dap);

					me.drawLine(50, 70, 48, 82, dap);
					me.drawLine(48, 82, 48, 98, dap);
				}
			}

		}

		int forcenstart = 0;
		int jumpip = 0;
		int jumpustep = 0;
		int startleft = 50;
		int starttop = 50;
		public void jumpup(int setforcen) {
			//Log.i("ok", "JUMPUP " + who + " " + forcen + " " + top + " at " + setforcen);
			if(mGame.pnk[mGame.level] == null){
				return;
			}


			forcen = setforcen;
			if (forcen <= 0
					|| patht[JUMPDOWN] > SystemClock.uptimeMillis() - 100) {
				injumpup = false;
				forcenstart = 0;
				//jumpdown();
				jumpdownnext.sendEmptyMessageDelayed(2, 32);
				if(cnkh != null){
					cnkh.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					ink.postInvalidate();
				}

				return;
			}
			if(forcenstart == 0){
				startleft = left;
				starttop = top;
				forcenstart = forcen;
				if(cnkh != null){
					cnkh.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					ink.postInvalidate();
				}
			}

			injumpup = true;
			injump = true;
			if(pathat != JUMPUP){pathwas = pathat;}
			pathat = JUMPUP;
			patht[JUMPUP] = SystemClock.uptimeMillis();

			me.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			legs(JUMPUP);
			arms(JUMPUP);
			headbody(JUMPUP);

			jumpustep = (int) (forcenstart * forcer * forcer * forcen * .05f);
			if(jumpustep <= 0){ jumpustep = 15; }
			if(jumpustep > 45){ 
//Log.w("ok","HIT JUMP MAX " + jumpustep);
jumpustep = 45; 
 }

			//jumpustep = 35;

			// (forcen * forcer * .15f)


			if(mGame != null && see != null && mGame.bnk[mGame.level] != null)
			{

				if(top >= 0){
					jumpip = (int) ((left + see.getWidth() / 2) + (top + see.getHeight() - 25) * mGame.bnk[mGame.level].getWidth());
					if( jumpip > 0 && mGame.pnk[mGame.level] != null && jumpip < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][jumpip] != 0){
						moveinmud = true;
					}else{
						moveinmud =false;
					}

					if(1==1){
					for(int j = 0; j < jumpustep; j++){
						jumpip = (int) ((left + see.getWidth() / 2) + (top + 5 - j) * mGame.bnk[mGame.level].getWidth());
						if( mGame.pnk[mGame.level] != null && (jumpip > mGame.pnk[mGame.level].length || jumpip < 0 || ( mGame.pnk[mGame.level][jumpip] != 0 && !moveinmud ) )){ 
							jumpustep = j - 1;

							if(cnkh != null){
								cnkh.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
								p9.setColor(Color.argb(240,190,90,20));//mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);
								p9.setStrokeWidth((float)(5));
								cnkh.drawLine( (float)(left + see.getWidth()/2 -18 ), (float)(top+5-jumpustep-3), (float)(left + see.getWidth()/2 +18), (float)(top+5-jumpustep-3), p9);
								ink.postInvalidate();
							}
							forcen = 0;
							break;
						}
					}}

					jumpip = (int) ((left + see.getWidth() / 2) + (top + 5 - jumpustep) * mGame.bnk[mGame.level].getWidth());
					if(jumpip > 0 && jumpip < mGame.pnk[mGame.level].length && (mGame.pnk[mGame.level][jumpip] == 0 || moveinmud ) ){
						//Log.w("ok","JUMP UP top "+top+" at " + forcen + " jump  by " +jumpustep);
						top = top + 5 - jumpustep;
						here.setMargins(left, top, 0, 0);
						see.setLayoutParams(here);
						see.postInvalidate();

						if(jumpustep <= 1){
							forcen = 0;
						}else if(cnkh != null && forcenstart - forcen < 4 && (mGame.pnk[mGame.level][jumpip] == 0) && jumpustep > 25 ){ 
							//cnk.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

							//cnk.drawLine(0, (int) (bnk[mGame.level].getHeight() / 2), bnk[mGame.level].getWidth(), (int) (bnk[mGame.level].getHeight() / 2), p9);
//Stroke
							p9.setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);


							p9.setStrokeWidth((float)(2 + runx.nextInt(2)) );
							//cnk.drawLine( (float)(startleft + see.getWidth()/2 -18+runx.nextInt(5) ), (float)(starttop+see.getHeight()+runx.nextInt(5)), (float)(left + see.getWidth()/2 -18+runx.nextInt(5) ), (float)(top+see.getHeight()+runx.nextInt(5)), p9);

							//p9.setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);
							//p9.setStrokeWidth((float)(2 + runx.nextInt(2)) );
							cnkh.drawLine(
								(float)(startleft + see.getWidth()/2 +18+runx.nextInt(5) ),
								(float)(starttop+see.getHeight()+runx.nextInt(5)), 
								(float)(left + see.getWidth()/2 +18+runx.nextInt(5) ),
								(float)(top+see.getHeight()+runx.nextInt(5)), 
				 			p9);
			
							p9.setStrokeWidth((float)(1 + runx.nextInt(2)) );
							cnkh.drawLine(
								(float)(startleft + see.getWidth()/2 -5+runx.nextInt(5) ),
								(float)(starttop+see.getHeight()+runx.nextInt(5)), 
								(float)(left + see.getWidth()/2 -5+runx.nextInt(5) ),
								(float)(top+see.getHeight()+runx.nextInt(5)), 
				 			p9);
			
							cnkh.drawLine(
								(float)(startleft + see.getWidth()/2 +5+runx.nextInt(5) ),
								(float)(starttop+see.getHeight()+runx.nextInt(5)), 
								(float)(left + see.getWidth()/2 + 5+runx.nextInt(5) ),
								(float)(top+see.getHeight()+runx.nextInt(5)), 
				 			p9);

//drawCircle

			
							//cnk.drawLine( (float)(startleft + see.getWidth()/2 +38+runx.nextInt(5) ), (float)(starttop+see.getHeight()+runx.nextInt(5)), (float)(left + see.getWidth()/2 +38+runx.nextInt(5) ), (float)(top+5-jumpustep+see.getHeight()+runx.nextInt(5)), p9);
			
//							ink.postInvalidate();
powder();
						}


					}else{
						Log.w("ok","JUMP UPc Force top "+top+" at " + forcen);
						forcen = 1;
					}

					//int ip = (int) ((left + see.getWidth() / 2) + (top - 25) * mGame.bnk[mGame.level].getWidth());


					//if(top > 0 && jumpip < mGame.pnk.length && mGame.pnk[jumpip] == 0){
					//	here.setMargins(left, top, 0, 0);
					//	see.setLayoutParams(here);
					//}else{
					//}

				}else{
					Log.w("ok","JUMP UPb Force top "+top+" at " + forcen);
					forcen = 1;
				}


			}else{
				Log.w("ok","JUMP UPa Force top "+top+" at " + forcen);
	//			top = top + 5 - jumpustep;
				forcen = 1;
			}



			forcen--;
			jumpupnext.sendEmptyMessageDelayed(2, 32);

			// coinscan();
		}

		Handler jumpupnext = new Handler() {
			public void handleMessage(Message msg) {
				jumpup(forcen);
			}
		};

		public Handler jumpdownnext = new Handler() {
			public void handleMessage(Message msg) {
				jumpdown();
			}
		};

		int jumpdp = 0;
		int jumpdstep = 0;
		public void jumpdown() {

//if(who != "Organizer"){
//return;
//}
			//Log.i("ok", "JUMP DOWN " + who + " " + forcen + " " + top + " " + injumpup + " <<"+inmove+">>");
			inmove = false;

			if (forcen >= 20 && top + see.getHeight() > 0) {
				injump = false;
				//down();

				if(pathwas == RIGHT){
					//right();
				}else if(pathwas == LEFT){
					//left();
				}else{
				//	up();
				}
				if(pathwas == UP){
					//up();
				}else{
					down(0);
				}

//			Log.i("ok", "JUMP DOWN CONCLUDED    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				if(cnkh != null){
					cnkh.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					ink.postInvalidate();
				}
				powder();

				return;
			}
			if(see == null){
				Log.w("ok","SEE ALERT");
				return;
			}
			if(mGame.level < 0 || mGame.level > mGame.bnk.length || mGame.bnk[mGame.level] == null){
				Log.w("ok","BNK " + mGame.level + " ALERT");
				return;
			}

			int jumpdp = (int) ((left + see.getWidth() / 2) + (top + see.getHeight()) * mGame.bnk[mGame.level].getWidth());
			if(jumpdp >= 0 && mGame.pnk[mGame.level] != null && jumpdp < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][jumpdp] != 0){
//				Log.i("ok", "JUMP DOWN "+jumpdp+"    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			}

			int stepmax = (int) (forcen * forcen * forcer * .15f);
			if(stepmax > 45){
				stepmax = 45;
			}
			int jumpdstep = stepmax;
			//int jumpdstep = stepmax;
			moveinmud = false;

//			Log.i("ok", "JUMP DOWN+ " + who + " " + forcen + " " + top + " " + injumpup + " <<< " + jumpdp + " by " + jumpdstep +" <<<<<<<<<<<<");
			if ( (jumpdp > 0 || top < 0) && 1==1) {

				if(mGame.pnk == null || mGame.pnk[mGame.level] == null){
					Log.e("ok","WONDER WONDER	WONDER WONDER");
					//Toast.makeText(ctx,"WONDER WONDER",1880).show();
					return;
				}
				if(jumpdp < mGame.pnk[mGame.level].length && jumpdp >= 0 && mGame.pnk[mGame.level][jumpdp] != 0){
					moveinmud = true;
				}

				for (jumpdstep = 0; jumpdstep < stepmax; jumpdstep++) {
					//Log.i("ok", "JUMP DOWN+ TEST " + who + " " + forcen + " " + top + " " + injumpup + " <<< " + jumpdp + " by " + jumpdstep +" <<<<<<<<<<<< ");

					jumpdp = (int) ((left + see.getWidth() / 2) + ((top + see.getHeight()) + jumpdstep - 5 ) * mGame.bnk[mGame.level].getWidth());

					if (jumpdp > 0 && jumpdp < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][jumpdp] != 0) {
//					 	Log.i("ok", "end end end end end end END JUMP " + who + " color is " + mGame.pnk[mGame.level][jumpdp] + " " + "(" + jumpdp + ") (" + jumpdstep + ")");
						forcen = 20;
						break;
					}

				}

				// if (pnk[jumpdp] != 0) {
				// injump = false;
				// Log.i("ok", "Jump Early ended");
				// return;
				// }

			}
			// if (jumpdp < 0 || jumpdp >= pnk.length) {
			// Log.i("ok", "JUMP OVERFLOW");
			// return;
			// }


			if(pathat != JUMPDOWN){pathwas = pathat;}
			pathat = JUMPDOWN;
			patht[pathat] = SystemClock.uptimeMillis();

			if (patht[LEFT] < SystemClock.uptimeMillis() - 1200
					&& patht[RIGHT] < SystemClock.uptimeMillis() - 1200) {

				me.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				legs(JUMPDOWN);
				arms(JUMPDOWN);
				headbody(JUMPDOWN);

			}


			//if(top + jumpdstep < mGame.bm.getHeight()/2){
			if(top + jumpdstep < basesize){
				top += jumpdstep;
			}else{
				//Log.w("ok","ALARM       --     jumpdstep > reasonable: " + jumpdstep + ">"+(mGame.bm.getHeight()/2) );
				Log.w("ok","ALARM       --     jumpdstep > reasonable: " + jumpdstep + ">"+ basesize );
				top = basesize - jumpdstep - see.getHeight() - 5;
				//top = mGame.bm.getHeight()/2 - jumpdstep - see.getHeight() - 5;
			}

			if(see != null){
				here.setMargins(left, top, 0, 0);
				see.setLayoutParams(here);
				see.postInvalidate();
			}


			forcen++;
			if (forcen >= 20 && jumpdp > 0 && jumpdp < mGame.pnk[mGame.level].length) {
				if (mGame.pnk[mGame.level][jumpdp] == 0) {
					forcen--;
				}
			} else if (forcen >= 20 && top < 0) {
				forcen--;
			}

			jumpdownnext.sendEmptyMessageDelayed(2, 32);

			// coinscan();
		}

		boolean inmove = false;

		public void left() {

//Log.i("ok","direction " + myn + " <--- <" + inmove + ">"); 

			if (!inmove) {
				inmove = true;
				if(pathat != LEFT){pathwas = pathat;}
				pathat = LEFT;
				patht[LEFT] = SystemClock.uptimeMillis();

				if(injump){
					//forcen -= 2;
					if(forcen <= 0){jumpup(0);}else{ moveme(-15);}
				}else{
					moveme(-15);
				}
			}
		}

		public void right() {
//Log.i("ok","direction " + myn + " ---> <" + inmove + ">"); 
			if (!inmove) {
				inmove = true;

				if(pathat != RIGHT){pathwas = pathat;}
				pathat = RIGHT;
				patht[RIGHT] = SystemClock.uptimeMillis();

				/*
				 * / if (!injump) { me.drawColor(Color.TRANSPARENT,
				 * PorterDuff.Mode.CLEAR); armstep++; if (armstep > 2) { armstep
				 * = 0; } legs(RIGHT); arms(RIGHT);
				 * 
				 * see.postInvalidate(); }//
				 */

				if(injump){
					//forcen -= 2;
					if(forcen <= 0){jumpup(0);}else{moveme(15);}
				}else{
					moveme(15);
				}
			}

		}

		public void up() {
			//Log.i("ok","direction " + myn + " ^^^^ <" + injump + ">"); 
			if (injump) {
				return;
			}

			if(pathat != UP){pathwas = pathat;}
			pathat = UP;
			//inmove = false;
			patht[pathat] = SystemClock.uptimeMillis();

			me.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			legs(UP);
			arms(UP);
			headbody(UP);

			moveme(-5);
			//see.postInvalidate();
		}

		public void down(int distance) {
			//Log.i("ok","direction " + myn + " vvvv <" + injump + ">"); 
			if (injump) {
				return;
			}

			//inmove = false;
			if(pathat != DOWN){pathwas = pathat;}
			pathat = DOWN;
			patht[pathat] = SystemClock.uptimeMillis();

			me.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			legs(DOWN);
			arms(DOWN);
			headbody(DOWN);

			moveme(distance);
			//see.postInvalidate();
		}


		float atval = 99990f;
		public boolean nearBy(double cl, double ct, float radius){
			//double cl = see.getWidth();
			//double ct = mGame.bm.getHeight()/2-see.getHeight();
	
			double cloc = Math.sqrt( ( cl * cl ) + (ct * ct) );
			double pl = left + see.getWidth()/2;
			double pt = top - see.getHeight()/2;
			double ploc = Math.sqrt( (pl * pl) + (pt * pt) );

			float val = (float)(cloc > ploc?(cloc-ploc):(ploc-cloc));
			//if(val < see.getWidth()*4)
			{
		
				//powder
				if(cnkh != null){
					


					//cnkh.drawCircle((float)(cl), (float)(ct), (float)(val), p9);
					//p9.setColor(Color.argb(150,0,0,10));
					//cnkh.drawCircle((float)(cl), (float)(ct), ( val -1f) , p9);
					//p9.setColor(Color.argb(215,0,0,10));

//					for (int t = lathing + 1; t < mPurpose.length && t < mPurpose.length && mPurpose[t] != null && !mAnPause; t++) {
//						mPurposeU[t] = val * val * (t * t * -0.035f);
//					}
//mRecent

					//mRenderer.mCameraY = (float)cl+105;//650f;// +>> <<-
					//mRenderer.mCameraX = (float)ct-55;//280f;// +^^ vv-
					//mRenderer.mCameraZ = -535f;
//if(val < see.getWidth()*4 ) {

{
// Micro refraction in UV/H2O, gold to black repeating gradient.
cnkh.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
if(pl > mGame.bnk[mGame.level].getWidth() - mGame.bnk[mGame.level].getWidth() * .25){
	//mPurpose[1].setAngle();
//	mPurpose[1].setPivot((float)ct+110f,(float)cl-65f,-535f);
	//mPurpose[1].mAngleY = 0f; // mAngle.*=
	//mPurpose[1].mAngleX = 135f;
	//mPurpose[1].mAngleZ = 0f;
	//mPurpose[1].mTrimY = (float)cl-65;// +>>>> <<<<-
	//mPurpose[1].mTrimX = (float)ct+110;// +^^^^ vvvv-
	//mPurpose[1].mTrimZ = (float)(-535f);
	//mWorlds[1].m1Ay = 0f; // mAngle.*=
	//mWorlds[1].m1Ax = 135f;
	//mWorlds[1].m1Az = 0f;
	//mWorlds[1].m1Cy = (float)cl-65;// +>>>> <<<<-
	//mWorlds[1].m1Cx = (float)ct+110;// +^^^^ vvvv-
	//mWorlds[1].m1Cz = (float)(-535f);
	p9.setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);
	cnkh.drawCircle((float)(cl), (float)(ct), (float)(radius), p9);
	atval = val;
//animate
}else if(pl < (basesize * .25)  ){
//else if(pl < (bm.getWidth() * .25)  )
	//mPurpose[1].setPivot((float)ct+110f,(float)(cl*-1f)-565f,-535f);

	//mPurpose[1].mAngley = 0f; // 
	//mPurpose[1].mAnglex = 45f;
	//mPurpose[1].mAnglez = 0f;
	//mPurpose[1].mTrimy = (float)(cl * -1) - 565f;// +>>>> <<<<-
	//mPurpose[1].mTrimx = (float)ct+110;// +^^^^ vvvv-
	//mPurpose[1].mTrimz = -535f;

	//mWorlds[1].m1Ay = 0f; // 
	//mWorlds[1].m1Ax = 45f;
	//mWorlds[1].m1Az = 0f;
	//mWorlds[1].m1Cy = (float)(cl * -1) - 565f;// +>>>> <<<<-
	//mWorlds[1].m1Cx = (float)ct+110;// +^^^^ vvvv-
	//mWorlds[1].m1Cz = -535f;


	p9.setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);
	cnkh.drawCircle((float)(cl), (float)(ct), (float)(radius), p9);
	atval = val;

}else{
	atval = 99999f;
	m1Ay = 0f; // 
	m1Ax = 0f;
	m1Az = 0f;
	m1Cy = (float)0;// +>>>> <<<<-
	m1Cx = (float)65;// +^^^^ vvvv-
	m1Cz = (float)-525;

}
//animate
//	updateInPurpose();
	ink.postInvalidate();
}

	//InPurpose[] mPurpose;

				}

				if(val < radius){
					return true;
				}
			}
			return false;
		}



		long barrier = 0;

		int movemeip = 0;
		int movemeipleft = 0;
		int movemeipright = 0;
		int movemeiphands = 0;
		int movemestep = 0;
		boolean moveinmud = false;


	boolean portalLevel(double cl,double ct,int nextlevel){

		if(cnkh != null){
			cnkh.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
	
			if( nearBy(cl,ct,25f) ){
				p9.setStyle(Style.STROKE);
				p9.setColor(mGame.my9[runx.nextInt(mGame.my9.length - 1)]);
				cnkh.drawCircle((float)(cl), (float)(ct), (float)(55f), p9);
			}

			if( nearBy(cl,ct,15f) ){
				//left = (int)(mGame.bm.getWidth() -  see.getWidth() - see.getWidth()/2 );

				p9.setStyle(Style.STROKE);
				p9.setColor(mGame.my9[runx.nextInt(mGame.my9.length - 1)]);
				cnkh.drawCircle((float)(cl), (float)(ct), (float)(35f), p9);
			}
		}

		if(nearBy(cl,ct,5f) ){
			left = (int)(basesize -  see.getWidth() - see.getWidth()/2 );
			here.setMargins(left, top, 0, 0);
			us[0].see.setLayoutParams(here);

			if(cnkh != null){
				p9.setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);
				p9.setStyle(Style.FILL);
				cnkh.drawCircle((float)(cl), (float)(ct), (float)(15f), p9);
			}

			mGame.changeLevel( nextlevel );
			if(cnkh != null){
				ink.postInvalidate();
			}
			return true;
		}

		if(nearBy(cl,ct,35f) ){
			if(cnkh != null){
				ink.postInvalidate();
			}
		}


		return false;
	}
		private void moveme(int maxstep) {

			//Log.i("ok","move me <<"+inmove+">>");
			if (barrier > SystemClock.uptimeMillis() || mGame.bnk[mGame.level] == null || mGame.pnk[mGame.level] == null) {
				inmove = false;
				return;
			}

			movemestep = maxstep;
			movemeip = 0;
			movemeipleft = 0;
			movemeipright = 0;
			movemeiphands = 0;

			if(pathat == DOWN || pathat == UP){
				movemeip = (int) ((left + see.getWidth() / 2) + ((top + see.getHeight()) - 5 + movemestep ) * mGame.bnk[mGame.level].getWidth());
				movemeiphands = (int) ((left + see.getWidth() / 2) + (top + see.getHeight()/2 + movemestep ) * mGame.bnk[mGame.level].getWidth());
			}else{
				movemeip = (int) ((left + see.getWidth() / 2) + movemestep + ((top + see.getHeight()) - 5) * mGame.bnk[mGame.level].getWidth()); 
				movemeipright = (int) ((left + see.getWidth() / 2) + 15 + movemestep + ((top + see.getHeight()) - 15) * mGame.bnk[mGame.level].getWidth()); 
				movemeipleft = (int) ((left + see.getWidth() / 2) - 15 + movemestep + ((top + see.getHeight()) - 15) * mGame.bnk[mGame.level].getWidth()); 
				movemeiphands = (int) ((left + see.getWidth() / 2) + movemestep + ((top + see.getHeight()/2) - 5) * mGame.bnk[mGame.level].getWidth()); 
			}

			if(!injump && movemeip > 0 && mGame.pnk[mGame.level].length > movemeip && movemeiphands > 0 && movemeiphands < mGame.pnk[mGame.level].length){
				if(mGame.pnk[mGame.level][movemeip] != 0 || mGame.pnk[mGame.level][movemeiphands] != 0){
					if(pathat == DOWN || pathat == UP){
						//Log.i("ok", "gravel start travel over " + movemeip + " " + left + "," + top + " of " +see.getWidth()+"x"+see.getHeight() + " " + mGame.pnk[mGame.level][movemeip]);
						moveinmud = true;
					}else{

						if(mGame.pnk[mGame.level][movemeipleft] == 0 || mGame.pnk[mGame.level][movemeipright] == 0){
							moveinmud = false;
						}else{
							moveinmud = true;
						}
						//Log.i("ok", "gravel travel over " + movemeip + " " + left + "," + top + " of " +see.getWidth()+"x"+see.getHeight() + " " + mGame.pnk[mGame.level][movemeip]);
					}
				}else{
					//Log.i("ok", "travel over " + movemeip + " " + left + "," + top + " of " +see.getWidth()+"x"+see.getHeight());
					moveinmud = false;
				}
			}else{
				moveinmud = false;
				//Log.i("ok", "unknown travel over " + movemeip + " " + left + "," + top + " of " +see.getWidth()+"x"+see.getHeight());
			}


// Math.sqrt
			{
				//double cl = mGame.bm.getWidth()-see.getWidth();
				//double ct = mGame.bm.getHeight()/2-see.getHeight()*1.5;

				double cl = basesize-see.getWidth()/2;
				double ct = basesize-see.getHeight()*1.5;

		//		if( portalLevel(cl,ct, mGame.level + 1) ){ return; }
			}

			if(mGame.level > 0){
				double cl = see.getWidth()/2;
				double ct = basesize-see.getHeight()*1.5;
			//	if( portalLevel(cl,ct, mGame.level - 1) ){ return; }
			}

			if(see == null){
				Log.e("ok","see ALERT");
				return;
			}
			if(mGame.level > mGame.bnk.length || mGame.level < 0){
				Log.e("ok","bnk size " + mGame.level + " ALERT");
				return;
			}
			if(mGame.bnk[mGame.level] == null){
				Log.e("ok","bnk " + mGame.level + " ALERT");
				//Toast.makeText(ctx, "Prime Image Fail", 880).show();
				return;
			}
			if(mGame.pnk[mGame.level] == null){
				Log.e("ok","pnk " + mGame.level + " ALERT");
				//Toast.makeText(ctx, "Prime Landscape Fail", 880).show();
				return;
			}



			if (((top < 0 && movemeip < 0) || injump) && left > 0 && left + see.getWidth() < mGame.bnk[mGame.level].getWidth()) {
				//Log.i("ok", "direction a " + movemeip + " " + left + "," + top);

				left += movemestep;
				if( 
					(movemeipright > 0 && movemeipright < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][movemeipright] != 0)
					|| (movemeipleft > 0 && movemeipleft < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][movemeipleft] != 0)
				){
					top -= 10;
				}

				if(mGame.level < mGame.pnk.length && movemeip < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][movemeip] == 0){
					moveinmud = false;
				}
				here.setMargins(left, top, 0, 0);
				see.setLayoutParams(here);

			} else if ( 
				(left < 0 || left+see.getWidth() > mGame.bnk[mGame.level].getWidth() || top < 0 )
				||
				(
					movemeip > 0 && movemeip < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][movemeip] != 0 
					&& (!moveinmud && !injump && (pathat != DOWN && pathat != UP ) && top+see.getHeight()-5 < basesize )
				)
			 ) {
				//&& (!moveinmud && !injump && (pathat != DOWN && pathat != UP ) && top+see.getHeight()-5 < mGame.bm.getHeight()/2 )
				if(left < 0 || left+see.getWidth() > mGame.bnk[mGame.level].getWidth() || top < 0 ){
					if(left < 0){
						left = runx.nextInt(25) + 5;
					}else if(left+see.getWidth() > mGame.bnk[mGame.level].getWidth()){
						left = mGame.bnk[mGame.level].getWidth() - see.getWidth() - ( runx.nextInt(25) + 5);
					}
					if(top < 0){
						top = 5 + runx.nextInt(25);
					}
					here.setMargins(left, top, 0, 0);
					see.setLayoutParams(here);
				}else{
					if(pathat == LEFT){
						left -= (5 + runx.nextInt(25));
					}else if(pathat == RIGHT){
						left += (5 + runx.nextInt(25));
					}
					here.setMargins(left, top, 0, 0);
					see.setLayoutParams(here);
				}
				barrier = SystemClock.uptimeMillis() + 100;
				//new vibme().execute();
				Log.i("ok", "barrier " + movemeip + " " + left + "," + top);

				//vib.sendEmptyMessage(2);
				inmove = false;
				moveinmud = false;
				return;

			} else if (movemeip > 0 && movemeip < mGame.pnk[mGame.level].length && ( mGame.pnk[mGame.level][movemeip] == 0 || moveinmud ) ) {

				Log.i("ok", "direction b " + movemeip + " " + left + "," + top);

				if(pathat == DOWN || pathat == UP){
					top += movemestep;
					movemeip = (int) ((left + see.getWidth() / 2) + ((top + see.getHeight()) - 5 + movemestep ) * mGame.bnk[mGame.level].getWidth());
					//if( top + see.getHeight() - 5 + movemestep < mGame.bm.getHeight() / 2 && top + movemestep > 0){
					if( top + see.getHeight() - 5 + movemestep < basesize && top + movemestep > 0){
						here.setMargins(left, top, 0, 0);
					}
					see.setLayoutParams(here);
				}else{
					left += movemestep;
					movemeip = (int) ((left + see.getWidth() / 2) + movemestep + ((top + see
						.getHeight()) - 5) * mGame.bnk[mGame.level].getWidth());
					here.setMargins(left, top, 0, 0);
					see.setLayoutParams(here);
				}

				Log.i("ok", "movemestep " + movemeip);
				// + (movemestep)


				if ( !(injump ) && movemeip >= 0 && movemeip < mGame.pnk[mGame.level].length && mGame.pnk[mGame.level][movemeip] == 0 && patht[JUMPUP] < SystemClock.uptimeMillis() - 200) {
					//Log.i("ok", "jump drop " + movemeip + " " + left + "," + top);

					if(pathwas == RIGHT || pathwas == LEFT){
						if (movemestep > 0) {
							right();
						} else {
							left();
						}
					}

					//forcen = 0;

					jumpup(0);
				}
			}

			if (!injump) {
				me.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				armstep++;
				if (armstep > 2) {
					armstep = 0;
				}
				legs(pathat);
				headbody(pathat);
				arms(pathat);
				powder();

				see.postInvalidate();
			}

			inmove = false;
		}

	}



	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.i("ok", "onMenuItemSelected " + featureId + " " + item.getItemId()
				+ " " + item.getTitle() + " in " + item.getGroupId());

		/*
		 * / if (item.getGroupId() == 5) { switch (item.getItemId()) {
		 * 
		 * case 22: Swarm.showLogin(); return true; case 23:
		 * Swarm.showDashboard(); return true; case 24: Swarm.showStore();
		 * return true; case 25: Swarm.showLeaderboards(); return true; case 26:
		 * Swarm.showAchievements(); return true; case 27: Swarm.logOut();
		 * return true; }
		 * 
		 * } else { //
		 */
		{

			switch (item.getItemId()) {
			case 8:// verify purchases
				mPlay.restorePurchases.sendEmptyMessage(2);
				return true;
			case 7:// purchase new
				mPlay.purbuy.sendEmptyMessage(2);
				return true;
				// case 6:
				// if (!Swarm.isInitialized()) {// !reg.getBoolean("swarminit",
				// false)) {
				// swarmInit.sendEmptyMessage(2);
				// } else {
				// Swarm.showLogin();
				// }
				// return true;
			case 1:

				info.sendEmptyMessage(2);
				return true;
			case 0:

				return true;
			}
		}

		return false;
	}

	Handler infoHide = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == 5) {
				infobox.setVisibility(View.GONE);
				bm.removeView(infobox);
				return;
			}

			AnimationSet dz = new AnimationSet(true);
			{
				Animation d5 = new AlphaAnimation(1f, 0f);
				d5.setInterpolator(AnimationUtils.loadInterpolator(ctx,
						android.R.anim.linear_interpolator));
				d5.setDuration(280);
				dz.addAnimation(d5);
			}
			dz.setFillAfter(true);
			infobox.startAnimation(dz);
			infoclose.startAnimation(dz);

			infoHide.sendEmptyMessageDelayed(5, 280);

		}
	};
	OnFocusChangeListener textLight = new OnFocusChangeListener() {
		public void onFocusChange(View b, boolean y) {
			TextView bt = (TextView) b;
			if (y) {
				bt.setBackgroundColor(mGame.my9a[runx
						.nextInt(mGame.my9a.length - 1)]);
			} else {
				bt.setBackgroundColor(Color.TRANSPARENT);
			}

		}
	};
	ScrollView infobox;
	ImageView infoclose;

	int basesize = 320;
	Handler info = new Handler() {
		public void handleMessage(Message msg) {

			Log.i("ok", "INFO");
			if(bm.getWidth() > bm.getHeight()){
				basesize = bm.getHeight();
			}else{
				basesize = bm.getWidth();
			}


			infobox = (ScrollView) getView("ScrollView");
			RelativeLayout.LayoutParams l = getRelativeLayout(-2, -2);
			l.setMargins((int) (basesize * .2f), (int) (basesize * .1f), (int) (basesize * .2f), (int) (basesize * .1f));
			l.addRule(RelativeLayout.CENTER_IN_PARENT);
			infobox.setLayoutParams(l);
			infobox.setBackgroundResource(R.drawable.d);
			infobox.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_INSET);

			LinearLayout bl = (LinearLayout) getView("LinearLayout");
			bl.setPadding(8, 8, 8, 8);
			bl.setLayoutParams(getScrollLayout(-2, -2));
			bl.setScrollContainer(true);
			bl.setOrientation(LinearLayout.VERTICAL);

			TextView t4 = (TextView) getView("TextView");
			t4.setTextColor(Color.BLACK);
			t4.setTextSize(18f);
			t4.setPadding(8, 0, 8, 0);
			t4.setTypeface(face[0]);
			t4.setText("Keep Alive\nby Liquidity Velocity");
			bl.addView(t4);

			TextView t5 = (TextView) getView("TextView");
			t5.setTextColor(Color.BLACK);
			// t5.setBackgroundColor(Color.argb(50, 0, 0, 0));
			t5.setPadding(8, 16, 8, 16);
			t5.setTextSize(16f);
			t5.setTypeface(face[1]);
			t5.setText("(Alpha Release)\nEngineer, Haven Skys, owns Liquidity Velocity.\nKeep Alive: Score to keep the patient alive.");
			bl.addView(t5);

			TextView t6 = (TextView) getView("TextView");
			t6.setBackgroundColor(Color.argb(50, 0, 0, 0));
			t6.setTextColor(Color.BLACK);
			t6.setPadding(8, 16, 8, 16);
			t6.setTextSize(16f);
			t6.setTypeface(face[1]);

			t6.setText("Support or Business\nhavenskys@gmail.com");
			t6.setOnClickListener(new OnClickListener() {
				public void onClick(View b) {
					Intent jump = new Intent(Intent.ACTION_SEND);
					jump.putExtra(Intent.EXTRA_TEXT, "\n\n");
					jump.putExtra(Intent.EXTRA_EMAIL, new String[] { "\""
							+ getString(R.string.app_name)
							+ " Support\" <havenskys@gmail.com>" });
					jump.putExtra(Intent.EXTRA_SUBJECT, "Support Request: "
							+ getString(R.string.app_name));
					jump.setType("message/rfc822");
					startActivity(Intent.createChooser(jump, "Email"));

					infoHide.sendEmptyMessageDelayed(2, 880);
				}
			});
			t6.setFocusable(true);
			t6.setOnFocusChangeListener(textLight);
			bl.addView(t6);

			TextView t7 = (TextView) getView("TextView");
			t7.setTextColor(Color.BLACK);
			// t5.setBackgroundColor(Color.argb(50, 0, 0, 0));
			t7.setTextSize(16f);
			t7.setPadding(8, 16, 8, 16);
			t7.setTypeface(face[1]);
			/*
			 * if (!Swarm.isInitialized()) {
			 * t7.setText("Cloud Play by Swarm\nMessage Friends\nPlay together"
			 * ); t7.setOnClickListener(new OnClickListener() { public void
			 * onClick(View b) { swarmInit.sendEmptyMessage(2);
			 * infoHide.sendEmptyMessage(2); } }); } else {
			 * 
			 * if (Swarm.isLoggedIn()) {
			 * t7.setText("Cloud Play by Swarm\nDashboard");
			 * t7.setOnClickListener(new OnClickListener() { public void
			 * onClick(View b) { Swarm.showDashboard();
			 * infoHide.sendEmptyMessage(2); } }); } else {
			 * t7.setText("Cloud Play by Swarm\nLogin");
			 * t7.setOnClickListener(new OnClickListener() { public void
			 * onClick(View b) { Swarm.showLogin();
			 * infoHide.sendEmptyMessage(2); } }); } } t7.setFocusable(true);
			 * t7.setOnFocusChangeListener(textLight); bl.addView(t7); //
			 */
			TextView t8 = (TextView) getView("TextView");
			t8.setTextColor(Color.BLACK);
			// t5.setBackgroundColor(Color.argb(50, 0, 0, 0));
			t8.setPadding(8, 16, 8, 16);
			t8.setTextSize(16f);
			t8.setTypeface(face[1]);
			if (mPlay.BUY_UPGRADED) {
				t8.setText("Secure In App Purchase by Google Play\nPurchased.");
			} else {
				t8.setText("Secure In App Purchase by Google Play\nPurchase gives power and removes advertising.");
				t8.setOnClickListener(new OnClickListener() {
					public void onClick(View b) {
						mPlay.purbuy.sendEmptyMessage(2);
						infoHide.sendEmptyMessage(2);
					}
				});
			}
			t8.setFocusable(true);
			t8.setOnFocusChangeListener(textLight);
			bl.addView(t8);

			TextView t9 = (TextView) getView("TextView");
			t9.setTextColor(Color.BLACK);
			// t5.setBackgroundColor(Color.argb(50, 0, 0, 0));
			t9.setTextSize(16f);
			t9.setPadding(8, 16, 8, 16);
			t9.setTypeface(face[1]);
			t9.setText("Ads by AdMob");
			bl.addView(t9);

			infobox.addView(bl);
			bm.addView(infobox);

			AnimationSet dz = new AnimationSet(true);

			{
				Animation d1 = new ScaleAnimation(4f, 1f, 4f, 1f,
						Animation.RELATIVE_TO_PARENT,
						(runx.nextInt(100) * .01f),
						Animation.RELATIVE_TO_PARENT, 1f);// (.5f +
															// runx.nextInt(50)
															// * .01f)
				d1.setInterpolator(AnimationUtils.loadInterpolator(ctx,
						android.R.anim.linear_interpolator));
				d1.setZAdjustment(50);
				d1.setDuration(1880);
				dz.addAnimation(d1);
			}

			{
				Animation d5 = new AlphaAnimation(0f, 1f);
				d5.setInterpolator(AnimationUtils.loadInterpolator(ctx,
						android.R.anim.linear_interpolator));
				d5.setDuration(1880);
				dz.addAnimation(d5);
			}

			infobox.startAnimation(dz);

			{
				ImageView g = (ImageView) getView("ImageView");
				g.setImageResource(R.drawable.yclose);
				g.setScaleType(ScaleType.MATRIX);
				RelativeLayout.LayoutParams gb = getRelativeLayout(-2, -2);
				gb.addRule(RelativeLayout.ALIGN_RIGHT, infobox.getId());
				gb.addRule(RelativeLayout.ALIGN_TOP, infobox.getId());

				gb.setMargins(0, -24, -24, 0);
				g.setLayoutParams(gb);
				g.setOnClickListener(new OnClickListener() {
					public void onClick(View b) {
						infoHide.sendEmptyMessage(2);
					}
				});
				bm.addView(g);
				infoclose = g;

				AnimationSet dza = new AnimationSet(true);
				{
					Animation h9 = new TranslateAnimation(basesize * -1, 0f, 0f, 0f);
					//Animation h9 = new TranslateAnimation(bm.getWidth() * -1, 0f, 0f, 0f);
					h9.setInterpolator(AnimationUtils.loadInterpolator(ctx,
							android.R.anim.linear_interpolator));
					h9.setDuration(2880);
					dza.addAnimation(h9);
				}

				{
					Animation d5 = new AlphaAnimation(0f, 1f);
					d5.setInterpolator(AnimationUtils.loadInterpolator(ctx,
							android.R.anim.linear_interpolator));
					d5.setDuration(2880);
					dza.addAnimation(d5);
				}
				infoclose.startAnimation(dza);
			}

		}
	};

	private class MenuAsync extends AsyncTask<Menu, Void, Void> {
		Menu menu;

		@Override
		protected Void doInBackground(Menu... arg0) {
			// TODO Auto-generated method stub
			menu = arg0[0];
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// shareMenu = menu;

			if (menu.findItem(7) == null) {
				MenuItem ib = menu.add(Menu.NONE, 7, 2, "Purchase\n$1.99");
				ib.setIcon(R.drawable.icon_play);
			}

			if (menu.findItem(8) == null) {
				MenuItem ib = menu.add(Menu.NONE, 8, 2, "Reactivate");
				ib.setIcon(R.drawable.icon_play);
			}

			if (mPlay.BUY_UPGRADED || mPlay.autSer == null) {
				menu.findItem(7).setVisible(false);
				menu.findItem(8).setVisible(false);
			} else if (!mPlay.BUY_UPGRADED && mPlay.autSer != null) {
				menu.findItem(8).setVisible(true);
				menu.findItem(7).setVisible(true);
			}

			/*
			 * / if (menu.findItem(5) == null) { SubMenu ib =
			 * menu.addSubMenu(Menu.NONE, 5, 2, "Swarm");
			 * ib.setHeaderIcon(R.drawable.swarm_head);
			 * ib.setIcon(R.drawable.swarm_icon); ib.add(5, 22, 1, "Login");
			 * ib.add(5, 23, 2, "Dashboard"); ib.add(5, 24, 3, "Store");
			 * ib.add(5, 25, 4, "Leaderboards"); ib.add(5, 26, 5,
			 * "Achievements"); ib.add(5, 27, 1, "Logout"); }
			 * 
			 * if (menu.findItem(6) == null) { MenuItem ib = menu.add(Menu.NONE,
			 * 6, 2, "Play Online"); ib.setIcon(R.drawable.swarm_icon); }
			 * 
			 * if (!Swarm.isInitialized() || (Swarm.isInitialized() &&
			 * !Swarm.isLoggedIn())) {// !reg.getBoolean("swarminit", // false))
			 * // { menu.findItem(5).setVisible(false);
			 * menu.findItem(6).setVisible(true); } else {
			 * 
			 * menu.findItem(5).setVisible(true);
			 * menu.findItem(6).setVisible(false); if (Swarm.isLoggedIn()) {//
			 * reg.getBoolean("swarmlogin", false)) // {
			 * menu.findItem(22).setVisible(false);
			 * menu.findItem(23).setVisible(true);
			 * menu.findItem(24).setVisible(true);
			 * menu.findItem(25).setVisible(true);
			 * menu.findItem(26).setVisible(true);
			 * menu.findItem(27).setVisible(true); } else {
			 * menu.findItem(23).setVisible(false);
			 * menu.findItem(24).setVisible(false);
			 * menu.findItem(25).setVisible(false);
			 * menu.findItem(26).setVisible(false);
			 * menu.findItem(27).setVisible(false); } } /
			 */
		}
	}


	class InAppPurchases extends Handler implements ServiceConnection {

		public InAppPurchases(Looper ml) {
			super(ml);
			Log.i("ok", "INAPP");
			serviceinit.sendEmptyMessageDelayed(3, 25);

		}

		public boolean BUY_UPGRADED = false;
		String UPGRADET = "android.test.purchased";
		String UPGRADEB = "permit";
		String UPGRADEC = "free";

		String UPGRADE = UPGRADEB;

		String[] sales = new String[] { UPGRADET, UPGRADEB, UPGRADEC };
		String[] salest = new String[] { "Test Account", "Feature Permit",
				"Free King" };

		com.au.kai.Grip autSer;
		com.au.kai.Grip.uiBinder autBind;

		boolean boundBind = false;

		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Log.w("ok", "INAPP onServiceConnected Service Connected");
			com.au.kai.Grip.uiBinder ub = (com.au.kai.Grip.uiBinder) arg1;
			//ub.audioMode();
			autSer = ub.getService();
			boundBind = true;

			if (!BUY_UPGRADED) {
				Log.i("ok", "INAPP CHECKING PURCHASES on CONNECT");
				checkPurchase.sendEmptyMessageDelayed(2, 125);
			} else {
				Log.i("ok", "INAPP UPGRADE ALREADY");
			}
		}

		public void onServiceDisconnected(ComponentName arg0) {
			Log.w("ok", "INAPP onServiceDisconnected Service Disconnected");
			boundBind = false;
			autSer = null;
		}

		Handler checkPurchase = new Handler() {
			public void handleMessage(Message msg) {
				Log.i("ok", "INAPP CHECKING PURCHASES");

				// cimagea
				if (reg.getLong("play_buy", -1) > 0
						&& reg.getLong("play_buy", 0) > reg.getLong(
								"play_changed", 0)) {
					// && reg.getLong("play_changed", -1) > 0
					{
						// START
						Log.i("ok",
								"INAPP play billing Start ("
										+ reg.getLong("play_buy", -1) + ") ("
										+ reg.getInt("play_start", -1) + ") ("
										+ reg.getLong("play_changed", -1) + ")");
					}

					{
						// REQUEST REPLY
						Log.i("ok",
								"INAPP play billing SAID ("
										+ reg.getLong("play_code", -1)
										+ ") (response "
										+ reg.getInt(
												"play_billing_response_code",
												-1)
										+ ") (request "
										+ reg.getLong(
												"play_billing_request_id", -1)
										+ ") RECENT (code received recently "
										+ (reg.getLong("play_code", -1) > reg
												.getLong("play_buy", -1)) + ")");
					}

					String[] idall;
					{
						// VERIFY REPLY
						Log.i("ok", "INAPP VERIFY PARSE");
						int cx = 0;
						String idcup = ",";
						String idverify = ",";
						while (reg.contains("play_billing_notification_id_"
								+ cx)) {

							if (reg.getString(
									"play_billing_notification_id_" + cx, "")
									.length() == 0) {
								cx++;
								continue;
							}

							Log.i("ok",
									"INAPP PREPARE VERIFY A "
											+ cx
											+ " ("
											+ reg.getString(
													"play_billing_notification_id_"
															+ cx, "") + ") ");

							if (reg.getLong(
									"play_confirm_"
											+ reg.getString(
													"play_billing_notification_id_"
															+ cx, ""), -1) > 0) {

								// + ",";
								if (!idcup.contains(","
										+ reg.getString(
												"play_billing_notification_id_"
														+ cx, "") + ",")) {
									idcup += reg.getString(
											"play_billing_notification_id_"
													+ cx, "")
											+ ",";
									Log.i("ok",
											"INAPP PREPARE VERIFY B "
													+ cx
													+ " ("
													+ reg.getString(
															"play_billing_notification_id_"
																	+ cx, "")
													+ ") VERIFIED (" + idcup
													+ ")");
								} else {
									Log.i("ok",
											"INAPP PREPARE VERIFY SKIP repeat "
													+ reg.getString(
															"play_billing_notification_id_"
																	+ cx, ""));
								}
							} else {
								idverify += reg.getString(
										"play_billing_notification_id_" + cx,
										"");

								Log.i("ok",
										"INAPP PREPARE VERIFY "
												+ cx
												+ " ("
												+ reg.getString(
														"play_billing_notification_id_"
																+ cx, "")
												+ ") RUN (" + idverify + ")");

							}
							cx++;
						}
						idverify = idverify.replaceAll("^,", "");
						idverify = idverify.replaceAll(",,", ",");
						idverify = idverify.replaceAll(",$", "");

						if (idverify.length() > 0) {

							String[] idverifyall = idverify.split(",");
							Log.i("ok", "INAPP VERIFY play IDs(" + idverify
									+ ")");
							Bundle request = autSer
									.cumbCup("CONFIRM_NOTIFICATIONS");
							request.putStringArray("NOTIFY_IDS", idverifyall);
							try {
								Bundle response = autSer.mPlay
										.sendBillingRequest(request);
								{
									Set<String> k = response.keySet();
									Log.i("ok", "INAPP VERIFY PURCHASE READY "
											+ k.size());
									Object[] b = k.toArray();

									for (int n = 0; n < b.length; n += 2) {
										Log.i("ok",
												"INAPP VERIFY PURCHASE READY IN "
														+ b[n]
														+ " ("
														+ (n + 1 >= b.length || b[n + 1] == null)
														+ ")");
									}
								}

								Log.i("ok",
										"INAPP play VERIFIED REPLY command CONFIRM_NOTIFICATIONS w/("
												+ idverify
												+ ") ("
												+ response
														.getString("REQUEST_ID")
												+ ") ");

								for (int ci = 0; ci < idverifyall.length; ci++) {
									Log.i("ok", "INAPP play VERIFIED "
											+ idverifyall[ci]);
									edt.putLong("play_confirm_"
											+ idverifyall[ci],
											System.currentTimeMillis());

								}

							} catch (RemoteException e) {
								Log.w("ok",
										"INAPP play VERIFIED FAILED RECIEPT ");
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						idcup = idcup.replaceAll("^,", "");
						idcup = idcup.replaceAll(",,", ",");
						idcup = idcup.replaceAll(",$", "");
						idall = idcup.split(",");

						// BILLING IN APP REPLY
						Log.i("ok",
								"INAPP play VERIFIED billing In ("
										+ reg.getLong("play_in", -1)
										+ ") IDs("
										+ idcup
										+ ") RECENT (reply newer than request is "
										+ (reg.getLong("play_in", -1) > reg
												.getLong("play_buy", -1)) + ")");
					}

					{
						// CHANGE IS REAL
						Log.i("ok",
								"INAPP play billing Change ("
										+ reg.getLong("play_changed", -1)
										+ ") ("
										+ reg.getString(
												"play_billing_inapp_signed_data",
												"na")
										+ ") ("
										+ reg.getString(
												"play_billing_inapp_signature",
												"na")
										+ ") RECENT ("
										+ (reg.getLong("play_changed", -1) > reg
												.getLong("play_buy", -1)) + ")");
					}

					{
						// GET DETAILS
						Log.i("ok",
								"INAPP GET PURCHASE DETAILS (replied: "
										+ (reg.getLong("play_in", -1) > reg
												.getLong("play_buy", -1))
										+ ") && (" + (idall.length > 0) + ")");

						if ((reg.getLong("play_in", -1) > reg.getLong(
								"play_buy", -1)) && idall.length > 0) {

							Bundle request = autSer
									.cumbCup("GET_PURCHASE_INFORMATION");
							Long nonce = runx.nextLong();
							edt.putLong("play_nonce_" + (nonce),
									System.currentTimeMillis());
							request.putLong("NONCE", nonce);
							request.putStringArray("NOTIFY_IDS", idall);

							try {
								Bundle response = autSer.mPlay
										.sendBillingRequest(request);

								{
									Set<String> k = response.keySet();
									Log.i("ok", "INAPP PURCHASE VERIFY REVIEW "
											+ k.size());
									Object[] b = k.toArray();

									for (int n = 0; n < b.length; n += 2) {
										Log.i("ok",
												"INAPP PURCHASE REVIEW IN VERIFY "
														+ b[n]
														+ " ("
														+ (n + 1 >= b.length || b[n + 1] == null)
														+ ")");
									}
								}

								String ssb = reg.getString(
										"play_billing_inapp_signed_data", "na");
								String[] mybuy = ssb.split("productId\":\"");

								String titem = "";
								int tstate = -1;
								for (int i = 1; i < mybuy.length; i++) {
									tstate = -1;
									titem = mybuy[i].replaceAll("\".*", "");
									try {
										tstate = Integer
												.parseInt((mybuy[i]
														.replaceAll(
																".*purchaseState.:",
																""))
														.replaceAll("\\}.*", ""));
									} catch (NumberFormatException en) {
										Log.e("ok",
												"INAPP PURCHASE VERIFY ///////// (PURCHASE STATE UNVERIFIED)");
									}

									edt.putInt("purchased_state_" + titem,
											tstate);
									Log.i("ok",
											"INAPP PURCHASE VERIFY ///////// (PURCHASE STATE "
													+ tstate + ") (" + titem
													+ ")");

									if (tstate == 0) {
										edt.putLong("purchased_" + titem,
												System.currentTimeMillis());
										Toast.makeText(ctx, "Purchasing...", 1880).show();
									}

									// UPGRADED
								}

								// Refresh purbuy
							} catch (RemoteException e) {
								Log.i("ok",
										"INAPP      FAILED PURCHASE DETAIL ("
												+ nonce + ")");
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							Log.i("ok", "INAPP REPLY NOT IN YET");
						}
					}

					edt.commit();

					// BUY_UPGRADED, sales
					for (int i = 0; i < sales.length; i++) {

						if (reg.getLong("purchased_" + sales[i], -1) != -1) {
							Log.i("ok", "INAPP VALID BUY " + sales[i]);
							BUY_UPGRADED = true;
						}

					}
					// checkPurchase.sendEmptyMessageDelayed(2, 125);

					if (BUY_UPGRADED) {
						// new saythis().execute("Purchase Verified");
						Log.i("ok", "INAPP PURCHASE VERIFIED AND TAGGED");
						mAds.adHide.sendEmptyMessage(2);

						// shareMenu.setGroupVisible(26, true);
						// shareMenu.setGroupVisible(3, true);
						// shareMenu.setGroupVisible(4, true);
						// upgradem.setVisible(false);
						// openOptionsMenu();

					}

					Log.i("ok", "INAPP VERIFY COMPLETE UPGRADED("
							+ BUY_UPGRADED + ")");
				} else {
					Log.i("ok", "INAPP PURCHASE BUFFER EMPTY using recovery");

					restorePurchases.sendEmptyMessage(2);
				}

			}
		};

		Handler purbuy = new Handler() {
			public void handleMessage(Message msg) {

				Log.i("ok", "INAPP PURCHASE");
				if (BUY_UPGRADED) {
					Log.w("ok", "INAPP ALREADY PURCHASED");
					return;
				}
				checkUp.sendEmptyMessageDelayed(2, 5880);

				Bundle bbs = autSer.cumbCup("REQUEST_PURCHASE");
				edt.putLong("play_buy", System.currentTimeMillis());

				bbs.putString("ITEM_ID", UPGRADE);
				// Note that the developer payload is optional.

				// String payload = "not required 256 chars";
				// if (payload != null) {
				// request.putString("DEVELOPER_PAYLOAD", payload);
				// }
				try {
					Bundle rx = autSer.mPlay.sendBillingRequest(bbs);

					if (rx != null) {
						Log.i("ok",
								"INAPP PURCHASE REPLY ("
										+ rx.getInt("RESPONSE_CODE")
										+ ", "
										+ rx.getInt("RESPONSE_CODE")
										+ " THIS IS("
										+ ResponseCode.valueOf(rx
												.getInt("RESPONSE_CODE"))
										+ "), YES IS ("
										+ ResponseCode.RESULT_OK.ordinal()
										+ ", " + ResponseCode.RESULT_OK
										+ ")  ID(" + rx.getLong("REQUEST_ID")
										+ ") CONTAINS INTENT("
										+ (rx.containsKey("PURCHASE_INTENT"))
										+ ")");

						edt.putLong("play_id", rx.getLong("REQUEST_ID"));
						edt.putInt("play_start", rx.getInt("RESPONSE_CODE"));

						if (rx.containsKey("PURCHASE_INTENT")) {
							startBuyPageActivity(
									(PendingIntent) rx.get("PURCHASE_INTENT"),
									new Intent());
						}

					} else {
						Log.i("ok", "INAPP billing failed");
					}

				} catch (RemoteException e) {
					Toast.makeText(ctx,
							"Help: Purchase had an issue at this time.", 1880)
							.show();
					// Toast.makeText(ctx, "failed purchase", 1880).show();
					Log.i("ok", "INAPP Problem with Purchase");
					e.printStackTrace();
				}

				edt.commit();

			}
		};

		private Method mStartIntentSender;
		private Object[] mStartIntentSenderArgs = new Object[9];
		private final Class[] START_INTENT_SENDER_SIG = new Class[] {
				IntentSender.class, Intent.class, int.class, int.class,
				int.class };

		private void initCompatibilityLayer() {

			try {
				mStartIntentSender = ctx.getClass().getMethod(
						"startIntentSender", START_INTENT_SENDER_SIG);
			} catch (SecurityException e) {
				mStartIntentSender = null;
			} catch (NoSuchMethodException e) {
				mStartIntentSender = null;
			}
		}

		void startBuyPageActivity(PendingIntent pendingIntent, Intent intent) {
			Log.i("ok", "INAPP BUY PAGE");
			initCompatibilityLayer();
			if (mStartIntentSender != null) {
				// This is on Android 2.0 and beyond. The in-app buy page
				// activity
				// must be on the activity stack of the application.

				try {
					// This implements the method call:
					// mActivity.startIntentSender(pendingIntent.getIntentSender(),
					// intent, 0, 0, 0);
					mStartIntentSenderArgs[0] = pendingIntent.getIntentSender();
					mStartIntentSenderArgs[1] = intent;
					mStartIntentSenderArgs[2] = Integer.valueOf(0);
					mStartIntentSenderArgs[3] = Integer.valueOf(0);
					mStartIntentSenderArgs[4] = Integer.valueOf(0);
					mStartIntentSender.invoke(axt, mStartIntentSenderArgs);

				} catch (Exception e) {
					Log.e("ok", "INAPP error starting activity", e);
				}
			} else {
				// This is on Android version 1.6. The in-app buy page activity
				// must
				// be on its
				// own separate activity stack instead of on the activity stack
				// of
				// the application.
				try {

					pendingIntent.send(axt, 0 /* code */, intent);
				} catch (CanceledException e) {
					Log.e("ok", "INAPP error starting activity", e);
				}
			}
		}

		Handler restorePurchases = new Handler() {
			public void handleMessage(Message msg) {
				Log.w("ok", "INAPP RESTORE PURCHASES");

				if (autSer == null) {
					Log.w("ok", "INAPP service play unavailable yet");
					// new saythis().execute("Service Unavailable");
					Toast.makeText(ctx, "Google Play Service Unavailable", 1880)
							.show();
					return;
				}
				// Toast.makeText(ctx)
				// new saythis().execute("Verifying Purchases");
				checkUp.sendEmptyMessageDelayed(2, 1880);

				Bundle rbs = autSer.cumbCup("RESTORE_TRANSACTIONS");
				// edt.putLong("play_buy", System.currentTimeMillis());
				// bbs.putString("ITEM_ID", UPGRADE);

				Long nonce = runx.nextLong();
				edt.putLong("play_buy", System.currentTimeMillis());
				edt.putLong("play_nonce_" + (nonce), System.currentTimeMillis());
				rbs.putLong("NONCE", nonce);

				try {
					if (autSer != null && autSer.mPlay != null) {
						Bundle rx = autSer.mPlay.sendBillingRequest(rbs);

						if (rx != null) {

							Log.i("ok",
									"INAPP RECOVERY REPLY ("
											+ rx.getInt("RESPONSE_CODE")
											+ ", "
											+ rx.getInt("RESPONSE_CODE")
											+ " THIS IS("
											+ ResponseCode.valueOf(rx
													.getInt("RESPONSE_CODE"))
											+ "), YES IS ("
											+ ResponseCode.RESULT_OK.ordinal()
											+ ", "
											+ ResponseCode.RESULT_OK
											+ ") ID("
											+ rx.getLong("REQUEST_ID")
											+ ") CONTAINS INTENT("
											+ (rx.containsKey("PURCHASE_INTENT"))
											+ ")");

						}
					}

				} catch (RemoteException e) {
					// new saythis()
					// .execute("Help: Recovery has an issue at this time.");
					// Toast.makeText(ctx, "Recovery purchase", 1880).show();
					Log.i("ok",
							"INAPP Problem with Purchase, retry in menu later.");
					e.printStackTrace();
				}

				edt.commit();

			}
		};

		Handler checkUp = new Handler() {
			long lc = 0;

			public void handleMessage(Message msg) {

				Log.i("ok",
						"INAPP CHECKUP "
								+ (lc > 0 ? System.currentTimeMillis() - lc
										: -0));
				lc = System.currentTimeMillis();

				if (reg.getLong("play_buy", 0) < reg.getLong("play_code", 0)) {

					int tx = reg.getInt("play_billing_response_code", 6);
					String rx = PurchaseState.valueOf(tx).name();

					if (tx > 3) {
						Toast.makeText(ctx, "Thanks from Doc Chomps and Family.\n\nFree Use Mode\n\nPurchase for $1.99 in Menu.", 2880)
								.show();
						return;
					} else {
if(1==1){
						Toast.makeText(
								ctx,
								rx
										+ "\nThanks!\n\nVerified Purchase In "
										+ ((System.currentTimeMillis() - (reg
												.getLong("play_buy", 0))) / 1000)
										+ " seconds", 1880).show();
}
					}

					Log.i("ok", "INAPP CHECKING PURCHASES on CODE " + tx + " = " + rx);
					//checkPurchase.sendEmptyMessageDelayed(2, 5125);
					return;
				}

				int secx = (int) ((System.currentTimeMillis() - (reg.getLong(
						"play_buy", 0))) / 1000);

				if (secx < 4) {
					Toast.makeText(ctx, "Verifying Purchase", 1880).show();
				} else {
					Toast.makeText(ctx,
							"Verifying Purchase took " + secx + " seconds", 1880)
							.show();
				}

				if (msg.what > 15) {
					Log.w("ok", "Google Play Service not Reachable.\nTired waiting.\nTimeout in " + msg.what + " tries.");
					return;
				}

				if (reg.getLong("play_buy", 0) < reg.getLong("play_in", 0)) {

					Toast.makeText(
							ctx,
							"Verified Purchase "
									+ ((reg.getLong("play_in", 0) - (reg
											.getLong("play_buy", 0))) / 1000)
									+ " seconds", 1880).show();

					Log.i("ok", "CHECKING PURCHASES replied NOTIFIED");
					int cx = 0;
					while (reg.contains("play_billing_notification_id_" + cx)) {
						Log.i("ok",
								"CHECKING PURCHASES replied ("
										+ reg.getString(
												"play_billing_notification_id_"
														+ cx, "na") + ")");
						cx++;
					}
					Log.i("ok", "INAPP USER IS CLIENT");
					checkPurchase.sendEmptyMessageDelayed(2, 125);
					// mAds.adHide.sendEmptyMessage(2);
					return;
				}

				if (reg.getLong("play_buy", 0) < reg.getLong("play_changed", 0)) {
					Toast.makeText(
							ctx,
							"Google Play Service Replied in "
									+ (reg.getLong("play_changed", 0) - (reg
											.getLong("play_buy", 0)) / 1000) + " seconds",
							1880).show();

					String ssb = reg.getString(
							"play_billing_inapp_signed_data", "na");

					if (ssb.contains("productId")) {
						Log.i("ok",
								"INAPP CHECKING PURCHASES on SECURITY VERIFIED "
										+ ssb);
						checkPurchase.sendEmptyMessageDelayed(2, 125);
					} else {
						Log.i("ok",
								"INAPP USER IS FREE CLIENT (OMITED SECURITY VERIFICATION)");
						Toast.makeText(ctx, "Verified Trial User", 1880).show();
						return;
					}

				}

				checkUp.sendEmptyMessageDelayed(msg.what + 1,
						8880 + msg.what * 253);

			}
		};

		Handler serviceinit = new Handler() {
			public void handleMessage(Message msg) {
				Log.i("ok",
						"INAPP SERVICE START (bound " + boundBind
								+ ") (upgraded " + BUY_UPGRADED
								+ ") (been run " + reg.getLong("been_run", -1)
								+ ")");

				if (boundBind) {
					// // play_nonce
					// new myemail().execute();
					// new myurls().execute();

					int runcnt = reg.getInt("refresh_run", 0);
					long ben = reg.getLong("been_run",
							System.currentTimeMillis());

					if (!(reg.getLong("been_run", -1) == -1)) {
						edt.putLong("been_run", System.currentTimeMillis());
					}

					int days = (int) ((System.currentTimeMillis() - ben) / 1000 / 60 / 60 / 24);

					if (!BUY_UPGRADED) {
						if (autSer.playbuy) {
							// && reg.getInt("play_billing_response_code", -1)
							// != 0) {

							if (runcnt > 5 && runx.nextBoolean()
									&& runx.nextBoolean()) {

								if (days == 0) {

								} else {
									Toast.makeText(
											ctx,
											"Investment.\nRefreshed "
													+ runcnt
													+ " && "
													+ days
													+ " days\n(interrupting heart)",
											1880).show();
								}

								// purbuy.sendEmptyMessageDelayed(2, 75);
							}

						}
					}

					// checkerror = System.currentTimeMillis();
					/*
					 * / checkin.sendEmptyMessageDelayed(2, 1880);
					 * 
					 * {
					 * 
					 * Animation xxhx = AnimationUtils.loadAnimation(ctx,
					 * R.anim.refreshin);
					 * 
					 * Log.w("ok", "START ANIMATION cfresh");
					 * crefresh.startAnimation(xxhx);
					 * crefresh.setVisibility(View.VISIBLE); // new
					 * screenLayer().execute();
					 * 
					 * if (lCursor == null) {
					 * keepreal.sendEmptyMessageDelayed(2, 50); } }//
					 */

					edt.putInt("refresh_run", runcnt + 1);
					edt.commit();
					// autSer.getlatestrun();
				} else {

					//Log.i("ok", "INAPP BINDING TO SERVICE FROM init");

					Intent service2 = new Intent();
					service2.setClass(ctx, com.au.kai.Grip.class);
					bindService(service2, mPlay, Context.BIND_AUTO_CREATE);
				}

			}
		};

		// long checkerror = -1;
	}

	// private ServiceConnection mPlay = new ServiceConnection() {
	// public void onServiceConnected(ComponentName arg0, IBinder arg1) {
	// refreshCheck.sendEmptyMessageDelayed(2, 75);
	// }
	// public void onServiceDisconnected(ComponentName arg0) {
	// }
	// };


	private class InAds extends Handler implements AdListener {
		RelativeLayout cbm;
		AdRequest adq;
		// LinearLayout adl;
		ImageView ade, adei;

		public InAds(Looper looper, RelativeLayout inbm) {
			super(looper);

			cbm = inbm;

			{
				adbox = new AdView(axt, AdSize.BANNER,
						getString(R.string.admob_id));
				while (findViewById(++hid) != null)
					Log.w("ok", "hid catch ");

				adbox.setId(hid);
			}

			if(1==2){
				adei = (ImageView) getView("ImageView");
				adei.setScaleType(ScaleType.FIT_XY);
				adei.setImageResource(R.drawable.adei);

				{
					RelativeLayout.LayoutParams br = getRelativeLayout(-2, -2);
					br.addRule(RelativeLayout.ALIGN_TOP, adbox.getId());
					br.addRule(RelativeLayout.ALIGN_BOTTOM, adbox.getId());
					br.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
					adei.setLayoutParams(br);
					adei.setVisibility(View.INVISIBLE);
				}
				cbm.addView(adei);
			}

			{
				RelativeLayout.LayoutParams br = getRelativeLayout(-2, -2);
				if(adei != null){
					br.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
					br.addRule(RelativeLayout.RIGHT_OF, adei.getId());
				}else{
					br.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
					br.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);
				}
				adbox.setLayoutParams(br);
			}

			if(1==2){
				ade = (ImageView) getView("ImageView");
				ade.setScaleType(ScaleType.FIT_XY);
				ade.setImageResource(R.drawable.ade);

				{
					RelativeLayout.LayoutParams br = getRelativeLayout(88, -2);
					br.addRule(RelativeLayout.ALIGN_TOP, adbox.getId());
					br.addRule(RelativeLayout.ALIGN_BOTTOM, adbox.getId());
					br.addRule(RelativeLayout.RIGHT_OF, adbox.getId());
					ade.setLayoutParams(br);
					ade.setVisibility(View.INVISIBLE);
				}
				ade.setOnClickListener(new OnClickListener() {
					public void onClick(View b) {
						adHide.sendEmptyMessage(2);
					}
				});

				cbm.addView(ade);
			}

			cbm.addView(adbox);
			adbox.setAdListener(this);
		}

		AdView adbox;
		boolean adhidden = false;
		Handler adHide = new Handler() {
			public void handleMessage(Message msg) {
				if (adhidden) {
					return;
				}

				adhidden = true;
				AnimationSet dz = new AnimationSet(true);

				{
					Animation d1 = new ScaleAnimation(1f, .5f, 1f, .5f,
							Animation.RELATIVE_TO_PARENT,
							(runx.nextInt(100) * .01f),
							Animation.RELATIVE_TO_PARENT, -1f);// (.5f +
																// runx.nextInt(50)
																// * .01f)
					d1.setInterpolator(AnimationUtils.loadInterpolator(ctx,
							android.R.anim.linear_interpolator));
					d1.setZAdjustment(50);
					d1.setDuration(880);
					// dz.addAnimation(d1);
				}

				{
					Animation d5 = new AlphaAnimation(1f, 0f);
					d5.setInterpolator(AnimationUtils.loadInterpolator(ctx,
							android.R.anim.linear_interpolator));
					d5.setDuration(1280);
					dz.addAnimation(d5);
				}
				dz.setFillAfter(true);

				adbox.startAnimation(dz);
				if(ade != null){ade.startAnimation(dz);}
				if(adei != null){adei.startAnimation(dz);}

			}
		};
		Handler adeshow = new Handler() {
			public void handleMessage(Message msg) {
				if(ade != null){
					AnimationSet db = new AnimationSet(true);
					{
						Animation h9 = null;
						h9 = new TranslateAnimation(ade.getWidth() * -1, 0f, 0f, 0f);
						h9.setInterpolator(AnimationUtils.loadInterpolator(ctx,
								android.R.anim.linear_interpolator));
						// h9.setZAdjustment(-21);
						h9.setDuration(1880);
						// h9.setStartOffset(1880);
						db.addAnimation(h9);
					}

					ade.setVisibility(View.VISIBLE);
					ade.startAnimation(db);
				}

				if(adei != null){
					AnimationSet db = new AnimationSet(true);
					{
						Animation h9 = null;
						h9 = new TranslateAnimation(adei.getWidth(), 0f, 0f, 0f);
						h9.setInterpolator(AnimationUtils.loadInterpolator(ctx, android.R.anim.linear_interpolator));
						h9.setDuration(1880);
						db.addAnimation(h9);
					}

					adei.setVisibility(View.VISIBLE);
					adei.startAnimation(db);
				}

			}
		};

		Handler reAd = new Handler() {
			public void handleMessage(Message msg) {

				adHide.sendEmptyMessage(2);

				if (mPlay != null && !mPlay.BUY_UPGRADED) {
					// bm.setBackgroundColor(Color.DKGRAY);
					adq = new AdRequest();
					adq.addTestDevice(AdRequest.TEST_EMULATOR);
					// adq.addTestDevice("E0FAAE3B817B3FC1A49E4EAC296E91BF");
					adbox.loadAd(adq);
				}

			}
		};

		public void onDismissScreen(Ad ad) {
			Log.i("ok", "admob dismiss please");
		}

		public void onFailedToReceiveAd(Ad ad, ErrorCode error) {
			Log.i("ok", "receive ad failed: admob dismiss please");
		}

		public void onLeaveApplication(Ad ad) {
			Log.i("ok", "close application: admob dismiss please");
		}

		public void onPresentScreen(Ad ad) {
			Log.i("ok", "show add");
			// adbox.setVisibility(View.VISIBLE);

		}

		public void onReceiveAd(Ad ad) {
			Log.i("ok", "receive ad");
			adhidden = false;

			AnimationSet dz = new AnimationSet(true);

			{
				Animation d1 = new ScaleAnimation(.5f, 1f, .5f, 1f,
						Animation.RELATIVE_TO_PARENT,
						0f,
						Animation.RELATIVE_TO_PARENT, 1f);// (.5f +
															// runx.nextInt(50)
															// * .01f)
				d1.setInterpolator(AnimationUtils.loadInterpolator(ctx,
						android.R.anim.linear_interpolator));
				d1.setZAdjustment(50);
				d1.setDuration(1880);
				dz.addAnimation(d1);
			}

			{
				Animation d5 = new AlphaAnimation(0f, 1f);
				d5.setInterpolator(AnimationUtils.loadInterpolator(ctx,
						android.R.anim.linear_interpolator));
				d5.setDuration(1880);
				dz.addAnimation(d5);
			}

			adbox.startAnimation(dz);

			if(ade != null && adei != null){
				RelativeLayout.LayoutParams br = getRelativeLayout(88,
						(adbox.getHeight() > 0 ? adbox.getHeight() : -2));
				br.addRule(RelativeLayout.ALIGN_TOP, adbox.getId());
				br.addRule(RelativeLayout.ALIGN_BOTTOM, adbox.getId());
				br.addRule(RelativeLayout.RIGHT_OF, adbox.getId());
				ade.setLayoutParams(br);
				ade.setVisibility(View.INVISIBLE);
				adei.setVisibility(View.INVISIBLE);
			}

			adeshow.sendEmptyMessageDelayed(2, 1880);
		}
	}


	class GameBoard extends Handler {

		int[][] pnk = new int[51][];
		int coinn = 0;
		ImageView coin;

		RelativeLayout bm;
		RelativeLayout board;
		RelativeLayout meteor;
		RelativeLayout xboard;
		RelativeLayout.LayoutParams dpadrl;
		RelativeLayout mpad;

// Rotate AnimationSet
public void rotateHere(){
						AnimationSet dz = new AnimationSet(true);

						{
							Animation d1 = new RotateAnimation(45f, 45f, Animation.RELATIVE_TO_PARENT, .5f, Animation.RELATIVE_TO_PARENT, .5f);
									//(runx.nextInt(100) * .01f));
							d1.setInterpolator(AnimationUtils.loadInterpolator(
									ctx, android.R.anim.linear_interpolator));
							//d1.setZAdjustment(-50 + ci);
							d1.setZAdjustment(1);
							d1.setDuration(1880);
							d1.setRepeatCount(-1);
	//						dd1.setRepeatMode(h9.RESTART);
	d1.setRepeatMode(d1.REVERSE);
							// d1.setFillBefore(true);
							// d1.setFillAfter(true);
							dz.addAnimation(d1);
						}
						dz.setFillEnabled(true);
						dz.setFillBefore(true);
						dz.setFillAfter(true);
						meteor.startAnimation(dz);
						// board
}


int zoomat = 0;
long zoomatwhen = 0;
int zoomleft = 0;
int zoomtop = 0;
// dropCoin
public void zoomHere(int setzoom){

	//if(zoomat == 0 || (setzoom == 2 || setzoom == 5 ) ){
		if(zoomatwhen > SystemClock.uptimeMillis() ){
			return;
		}
	
	//}

	if(zoomat == 2 && setzoom == 2){

		if( 
 			(( zoomleft <= us[myn].left && us[myn].left - zoomleft < 42) || ( zoomleft >= us[myn].left && zoomleft - us[myn].left < 12))
				&&
 			(( zoomtop <= us[myn].top && us[myn].top - zoomtop < 22) || ( zoomtop >= us[myn].top && zoomtop - us[myn].top < 12))
 		){
			return;
		}
//Toast.makeText(ctx,"LEFT " +us[myn].left,880).show();
//Toast.makeText(ctx,"TOP " +us[myn].top,880).show();

	}


	AnimationSet dz = new AnimationSet(true);

									//(us[myn].left),

						{
							Animation d1 = null;
							if(setzoom == 5){
								if(zoomat == 0){
									//d1 = new ScaleAnimation(1f, 4.2f, 1f, 4.2f, Animation.ABSOLUTE, us[myn].left, Animation.ABSOLUTE, us[myn].top);
									//d1 = new ScaleAnimation(1f, 4.2f, 1f, 4.2f, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
									d1 = new ScaleAnimation(1f, 4.2f, 1f, 4.2f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0f);
									zoomat = 5;
								}else if(zoomat == 2){
									//d1 = new ScaleAnimation(2.2f, 4.2f, 2.2f, 4.2f, Animation.ABSOLUTE, us[myn].left, Animation.ABSOLUTE, us[myn].top);
									//d1 = new ScaleAnimation(2.2f, 4.2f, 2.2f, 4.2f, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
									d1 = new ScaleAnimation(2.2f, 4.2f, 2.2f, 4.2f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0f);
									zoomat = 5;
								}
								zoomatwhen = SystemClock.uptimeMillis() + 1880;
							}else if(setzoom == 4){
								if(zoomat == 2){
									//d1 = new ScaleAnimation(2.2f, 1f, 2.2f, 1f, Animation.ABSOLUTE, us[myn].left + us[myn].see.getWidth()/2, Animation.ABSOLUTE, us[myn].top);
									//d1 = new ScaleAnimation(2.2f, 1f, 2.2f, 1f, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
									d1 = new ScaleAnimation(2.2f, 1f, 2.2f, 1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0f);
								}else if(zoomat == 4){
									//d1 = new ScaleAnimation(4.2f, 4.2f, 4.2f, 4.2f, Animation.ABSOLUTE, us[myn].left + us[myn].see.getWidth()/2, Animation.ABSOLUTE, us[myn].top);
									//d1 = new ScaleAnimation(4.2f, 4.2f, 4.2f, 4.2f, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
									d1 = new ScaleAnimation(4.2f, 4.2f, 4.2f, 4.2f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0f);
								}else if(zoomat == 5){
									//d1 = new ScaleAnimation(4.2f, 1f, 4.2f, 1f, Animation.ABSOLUTE, us[myn].left + us[myn].see.getWidth()/2, Animation.ABSOLUTE, us[myn].top);
									//d1 = new ScaleAnimation(4.2f, 1f, 4.2f, 1f, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
									d1 = new ScaleAnimation(4.2f, 1f, 4.2f, 1f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0f);
								}
								zoomat = 0;
							}else if( zoomat == 0 && setzoom == 2 ){

								d1 = new ScaleAnimation(1f, 2.2f, 1f, 2.2f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0f);
								//d1 = new ScaleAnimation(1f, 2.2f, 1f, 2.2f, Animation.ABSOLUTE,0f, Animation.ABSOLUTE, 0f);
								//d1 = new ScaleAnimation(1f, 2.2f, 1f, 2.2f, Animation.ABSOLUTE,us[myn].left + us[myn].see.getWidth()/2, Animation.ABSOLUTE,us[myn].top);
							}else{
								//else{
									//d1 = new ScaleAnimation(2.2f, 2.2f, 2.2f, 2.2f, Animation.ABSOLUTE,us[myn].left+us[myn].see.getWidth()/2, Animation.ABSOLUTE,us[myn].top);
									//d1 = new ScaleAnimation(2.2f, 2.2f, 2.2f, 2.2f, Animation.ABSOLUTE,zoomleft, Animation.ABSOLUTE, zoomtop);
									d1 = new ScaleAnimation(2.2f, 2.2f, 2.2f, 2.2f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
									
//d1 = new TranslateAnimation( Animation.ABSOLUTE, us[myn].left-21, Animation.ABSOLUTE, us[myn].left, Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f );

								//}
								zoomat = 2;
							}


if(1==2){
							if(zoomat == 2 || setzoom == 2){
								Animation d2 = null;

								int zleft = ( (zoomleft - us[myn].see.getWidth() )*-1);
								int ztop = ( (zoomtop -50) * -1);
								if(zleft > 0){zleft = 0;}
								if(zleft < mGame.bnk[mGame.level].getWidth() * -1 ){zleft = mGame.bnk[mGame.level].getWidth() * -1;}
								if(ztop > 0){ztop = 0;}

								int zpartsw = 10;
								zleft = (int) (( (us[myn].left / (mGame.bnk[mGame.level].getWidth() / zpartsw)) * (mGame.bnk[mGame.level].getWidth() / zpartsw) ) * -1);
								if(zleft > zpartsw-2 * (mGame.bnk[mGame.level].getWidth() / zpartsw)){
									zleft = (zpartsw -2) * (mGame.bnk[mGame.level].getWidth() / zpartsw);
								}

								//d2 = new TranslateAnimation( Animation.ABSOLUTE, zleft, Animation.ABSOLUTE, ( (us[myn].left - us[myn].see.getWidth() ) * -1), Animation.ABSOLUTE, ztop, Animation.ABSOLUTE, ( (us[myn].top - 50) * -1));
								d2 = new TranslateAnimation( Animation.ABSOLUTE, zleft, Animation.ABSOLUTE, zleft, Animation.ABSOLUTE, ztop, Animation.ABSOLUTE, ztop);


								d2.setInterpolator(AnimationUtils.loadInterpolator( ctx, android.R.anim.overshoot_interpolator));
								d2.setZAdjustment(1);
								d2.setDuration(880);
								dz.addAnimation(d2);
							}else if( zoomat == 0 ){
								Animation d2 = null;

								if(setzoom == 4){
									if(zoomat == 2){
										d2 = new TranslateAnimation( 
											Animation.ABSOLUTE, 0, 
											Animation.ABSOLUTE, 0, //( (us[myn].left - us[myn].see.getWidth() ) * -1), 
											Animation.ABSOLUTE, 0,
											Animation.ABSOLUTE, 0 //( (us[myn].top - 50) * -1)
										);
										d2.setInterpolator(AnimationUtils.loadInterpolator( ctx, android.R.anim.linear_interpolator));
										d2.setZAdjustment(1);
										d2.setDuration(880);
										dz.addAnimation(d2);
									}
								}

							}
}

			
							zoomleft = us[myn].left;
							zoomtop  = us[myn].top;
	if(d1 == null){return;}

							d1.setInterpolator(AnimationUtils.loadInterpolator( ctx, android.R.anim.linear_interpolator));
							d1.setZAdjustment(1);
							zoomatwhen = SystemClock.uptimeMillis() + 880;

							if(setzoom == 4){
								d1.setDuration(880);
								if(zoomat == 2){
								}else if(zoomat == 5){
								}else if(zoomat == 4){
								}
								zoomat = 0;
							}else if(setzoom == 5){
								d1.setDuration(880);
							}else if(zoomat != 2 && setzoom == 2){
								zoomatwhen = SystemClock.uptimeMillis() + 1880;
								d1.setDuration(1880);
							}else if(zoomat == 2 && setzoom == 2 ){
								d1.setDuration(80);
								d1.setRepeatCount(-1);
								d1.setRepeatMode(d1.REVERSE);
								zoomatwhen = SystemClock.uptimeMillis() + 80;
							}else{
								zoomatwhen = SystemClock.uptimeMillis() + 1880;
								d1.setDuration(1880);
							}
							if( zoomat == 0 && setzoom == 2 ){
								zoomat = 2;
							}

							if(setzoom == 5){
								d1.setRepeatCount(1);
								d1.setRepeatMode(d1.REVERSE);
							}
							dz.addAnimation(d1);


						}


Log.i("ok", "Here #"+myn+" ("+setzoom+") at ("+zoomat+") " + zoomleft + " x " + zoomtop);
//Toast.makeText(ctx,"Here #"+myn+" ("+setzoom+") at ("+zoomat+") " + zoomleft + " x " + zoomtop,1880).show();

							//d1.setZAdjustment(-50 + ci);
							//d1.setRepeatMode(d1.RESTART);
							// d1.setFillBefore(true);
							// d1.setFillAfter(true);
						dz.setFillEnabled(true);
						dz.setFillBefore(true);
						dz.setFillAfter(true);

board.clearAnimation();
						board.startAnimation(dz);
}

//clas


	SensorEventListener or = new SensorEventListener(){
		int position = 0;
		float[] lastvalues;
		long smooth = 34;//long smoothtext = 32;//String cn = "";
//final int mpt = mPurpose.length;
		boolean smoothfresh = true;

		SharedPreferences mReg = getSharedPreferences("Preferences", MODE_WORLD_READABLE);
		public void onAccuracyChanged(Sensor arg0, int arg1) {

		}

		public void onSensorChanged(SensorEvent event) {
			if(smooth > SystemClock.uptimeMillis() ){return;}
			if(smoothfresh){
				lastvalues = null;
				smoothfresh = false;
			}
			smooth = SystemClock.uptimeMillis() + 70;//bdl.getInt("sensorspeed",250);
			float[] values = event.values;
			float valence = 0;


			if(lastvalues == null){
				Log.w(G,"Loading Initial Sensor Values");
				lastvalues = values;
				for(int b = 0; b < values.length; b++){lastvalues[b] = 0;}
			}

			if( lastvalues != null && values.length == lastvalues.length && values.length >= 2){

				//if( !board.isShown() ){
					//smooth = SystemClock.uptimeMillis() + 1270;//bdl.getInt("sensorspeed",250);
					//Log.i("ok","Sensor Offline");
					//return;
				//}

				boolean flowon = false;
				//for(int b = 0; b < values.length; b++){

				//if(event.sensor.getType() == 2)
				{

					int b = 0;
					//valence = (lastvalues[b]>values[b]?lastvalues[b]-values[b]:values[b]-lastvalues[b]);

					if(landscape == 1){
//Log.i("ok","path a");
						if(values[b] < 0 && values[b] < -1.5f){ 
							if(values[b] < -3){bid.sendEmptyMessageDelayed(RIGHT,25);} 
							if(values[b] < -4){bid.sendEmptyMessageDelayed(RIGHT,125);} 
							bid.sendEmptyMessage(RIGHT); }
	
						if(values[b] > 0 && values[b] > 1.5f){
							if(values[b] > 3){bid.sendEmptyMessageDelayed(LEFT,25);} 
							if(values[b] > 4){bid.sendEmptyMessageDelayed(LEFT,125);} 
							bid.sendEmptyMessage(LEFT); }

					}else if(landscape == 2){
					//	Log.i("ok","path b");
						
						b = 0;
						values[b] *= -1;

						if(values[b] < 0 && values[b] < -1.5f){ 
							if(values[b] < -3){bid.sendEmptyMessageDelayed(LEFT,25);} 
							if(values[b] < -4){bid.sendEmptyMessageDelayed(LEFT,125);} 
							bid.sendEmptyMessage(LEFT); }
	
						if(values[b] > 0 && values[b] > 1.5f){
							if(values[b] > 3){bid.sendEmptyMessageDelayed(RIGHT,25);} 
							if(values[b] > 4){bid.sendEmptyMessageDelayed(RIGHT,125);} 
							bid.sendEmptyMessage(RIGHT); }
					}


//Log.i("ok","SENSOR ONLINE "+event.sensor.getType()+" " +event.sensor.getName() + " " + b + ": " + valence + " " + ( values[b] < lastvalues[b] ? "left":"right") + " == " + values[b] );

					lastvalues[b] = values[b];

					//if(valence <= 0.5){return;}

					b = 2;
					if(landscape == 1){
						b = 2;
					}
					valence = (lastvalues[b]>values[b]?lastvalues[b]-values[b]:values[b]-lastvalues[b]);

					if(lastvalues[b] > values[b] && valence > 4.0f){ 
					//values[b] < -4f && values[b] > -9f)
						if(valence > 6){bid.sendEmptyMessageDelayed(DOWN,25);} 
						//if(valence > 6){bid.sendEmptyMessageDelayed(DOWN,125);} 
						bid.sendEmptyMessage(DOWN); 
						smooth = SystemClock.uptimeMillis() + 170;
						smoothfresh = true;
					}else if(lastvalues[b] < values[b] && valence > 2.0f){ bid.sendEmptyMessage(JUMPUP); smoothfresh = true; }

					lastvalues[b] = values[b];


					//values[b] > 0 && values[b] >= 6f && values[b] <= 8)
//Log.i("ok","SENSOR ONLINE "+event.sensor.getType()+" " +event.sensor.getName() + " " + b + ":  " + ( values[b] > 0 ? "^^^^":"vvvv") + " == " + values[b] );


					//break;
				}


				{
					for (int b = 0; b < values.length; b++) {
						valence = (lastvalues[b] > values[b] ? lastvalues[b] - values[b] : values[b] - lastvalues[b]);
						lastvalues[b] = values[b];
						if (b == 0) {// left right roll
							// orbit

//nearBy

if(1==1){
		//					for (int t = lathing + 1; t < mPurpose.length && t < mPurpose.length && mPurpose[t] != null && !mAnPause; t++) {
			//					mPurposeU[t] = values[b] * (t * t * -0.035f);
				//			}
//camera-console
//mWorlds[1].mTrimx = 

}

					//		if (mGLworkid == -1) {
					//			return;
					//		}
					//		mPurposeU[mGLworkid] = (values[b]) * 0.8f;
					//		mPurposeU[1] = (values[b]) * TOUCH_SCALE_FACTOR; // rotate
						} else if (b == 1) {
					//		mPurposeU[0] = (values[b]) * TOUCH_SCALE_FACTOR;
						} else if (b == 2) {
					//		mPurposeU[2] = (values[b]) * TOUCH_SCALE_FACTOR;
							if (mAnPause) {
							}

						}

					}

				}



			}
		}
	};


	boolean upause = false;
	public void onPause(){
		upause = true;
		if(sm != null){
			Log.i("ok","SENSOR PAUSE");
			sm.unregisterListener(or);
			sm = null;
		}

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.abandonAudioFocus(newb);
		// Listener
		// Focus
		// playon
		try {
			if (xuut != null) {
				xuut.stop();
				xuut.release();
			}

			// Audio
			playaudio2.removeMessages(2);
		} catch (RuntimeException e2) {
			Log.i("ok", "audio onpause stop error " + e2.getMessage());
		}



	}

	int landscape = -1;
	
	public void onResume(){
		Log.i("ok","SENSOR RESUME");
		upause = false;
		try{ sm = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);}finally{}


		sm.registerListener(or, sm.getDefaultSensor(SensorManager.SENSOR_ORIENTATION) , SensorManager.SENSOR_DELAY_GAME );

	}

	SensorManager sm;

		public GameBoard(Looper looper, RelativeLayout mbm) {
			super(looper);
			bm = mbm;

			{
				xboard = (RelativeLayout) getView("RelativeLayout");
				xboard.setLayoutParams(getRelativeLayout(-1, -1));
				bm.addView(xboard);
			}

			Display sd = getWindowManager().getDefaultDisplay();
			//mwidth = sd.getWidth();
			//mheight = sd.getHeight();
			if( (int)(sd.getWidth()) > (int)(sd.getHeight()) ){
				landscape = 2;
			}else{
				landscape = 1;
			}
			//Toast.makeText(ctx,"BOARD " + landscape + " _ " + sd.getWidth()+ " _ " + bm.getWidth(), 1880).show();

			{
				board = (RelativeLayout) getView("RelativeLayout");
//CENTER_IN_PARENT
				RelativeLayout.LayoutParams br = getRelativeLayout(-2, -2);
				br.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
				br.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);
				board.setLayoutParams(br);
				board.setBackgroundColor(Color.argb(100, 10, 10, 10));
				board.setKeepScreenOn(true);
				bm.addView(board);
//*
board.setOnTouchListener(new OnTouchListener(){

	public boolean onTouch(View b, MotionEvent a) {
		if ( a.getAction() == a.ACTION_DOWN) {
			Toast.makeText(ctx,"Show Player "+myn+" Stats Here " +us[myn].left+" : "+us[myn].see.getLeft(),1880).show();
			//us[myn].
			bid.sendEmptyMessage(JUMPUP);

			//zoomHere(4);
			return true;
		}
		return false;
	}
	});
//*/

			}
			{
				meteor = (RelativeLayout) getView("RelativeLayout");
				meteor.setLayoutParams(getRelativeLayout(-1, -1));
				//meteor.setBackgroundColor(Color.argb(100, 10, 10, 10));
				// meteor.setKeepScreenOn(true);
				//board.addView(meteor);
				bm.addView(meteor,0);
				//rotateHere();
			}

			{
Log.i("ok","SENSOR ONLINE");
				try{ sm = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);}finally{}

				//Display sd = getWindowManager().getDefaultDisplay();
				//if( (int)(sd.getWidth()) > (int)(sd.getHeight()) ){
					//landscape = 2;
				//}else{
					//landscape = 1;
				//} 
				//Toast.makeText(ctx,"SENSOR " + landscape + " " + sd.getWidth()+ " :" + bm.getWidth(), 4880).show();

				sm.registerListener(or, sm.getDefaultSensor(SensorManager.SENSOR_ORIENTATION) , SensorManager.SENSOR_DELAY_GAME );
			}

			controlPad();
			changeLevel( level + 1 );
		}



		public void changeLevel(int setlevel) {

			if (edt != null) {
				edt.putInt("atlevel", level);
				edt.commit();
			}

			if(pk == null){
				if(nextlevel != setlevel){// && level != nextlevel){
					Log.w("ok","RECOVERY PK2");
					nextlevel = setlevel;
					new levelSet().execute();
				}else{
					Log.w("ok","RECOVERY PK");
				}
				return;
			}

			if(setlevel > pk.length){
				Log.w("ok"," CHANGE TO " + setlevel + " from " + level);
				return;
			}

			if(level >= 0){
				if(us[0] != null && pk[level] != null){
					pk[level].removeView(us[0].see);
				}
			}

			if(level >= 0 && setlevel >= 0 && setlevel != level && pk[setlevel] != null ){

				pk[level].removeView(us[myn].see);

				//pk[setlevel].addView(us[myn].see);
				//pk[setlevel].addView(us[0].see);

				board.removeView(pk[level]);
				xboard.removeView(pk[level]);
				xboard.addView(pk[level]);

				xboard.removeView(pk[setlevel]);
				//board.addView(pk[setlevel]);

				pk[setlevel].clearAnimation();
				{
					AnimationSet dz = new AnimationSet(true);
					{
						Animation d1 = new ScaleAnimation((float) (.4f - setlevel/10) , 1f, (float) (.4f - setlevel/10), 1f,
								Animation.RELATIVE_TO_PARENT,
								(runx.nextInt(100) * .1f),
								Animation.RELATIVE_TO_PARENT,
								(runx.nextInt(100) * .1f));
						d1.setInterpolator(AnimationUtils.loadInterpolator(
								ctx, android.R.anim.linear_interpolator));
						d1.setZAdjustment(10);
						d1.setDuration(1880);
						// d1.setFillBefore(true);
						// d1.setFillAfter(true);
						dz.addAnimation(d1);
					}
					dz.setFillEnabled(true);
					dz.setFillBefore(true);
					dz.setFillAfter(true);
					pk[setlevel].startAnimation(dz);
				}

				pk[level].clearAnimation();

				{
					AnimationSet dz = new AnimationSet(true);

					{
						Animation d1 = new ScaleAnimation(1f, (float) (.4f) , 1f, (float) (.4f), Animation.RELATIVE_TO_PARENT, ( (level * 120)/bnk[level].getWidth() ), Animation.RELATIVE_TO_PARENT, 0.5f);
						d1.setInterpolator(AnimationUtils.loadInterpolator( ctx, android.R.anim.linear_interpolator));
						d1.setZAdjustment(-50 + level);
						d1.setDuration(1880);
						//d1.setRepeatCount(-1);
						//d1.setRepeatMode(h9.REVERSE);
						//d1.setStartOffset(4890);
						// d1.setFillBefore(true);
						// d1.setFillAfter(true);
						dz.addAnimation(d1);
					}

					{
						Animation d1 = new ScaleAnimation(0.4f, (float) (.4f) , 0.4f, (float) (.4f), Animation.RELATIVE_TO_PARENT, ( (level * 120)/bnk[level].getWidth() ), Animation.RELATIVE_TO_PARENT, 0.5f);
						//Animation d1 = new ScaleAnimation(1f, (float) (.4f - level/10) , 1f, (float) (.4f - level/10), Animation.RELATIVE_TO_PARENT, (runx.nextInt(100) * .01f), Animation.RELATIVE_TO_PARENT, (runx.nextInt(100) * .01f));
						d1.setInterpolator(AnimationUtils.loadInterpolator(
								ctx, android.R.anim.linear_interpolator));
						d1.setZAdjustment(-50 + level);
						d1.setDuration(1880);
						d1.setRepeatCount(-1);
						d1.setRepeatMode(d1.REVERSE);
						d1.setStartOffset(1890);
						// d1.setFillBefore(true);
						// d1.setFillAfter(true);
						dz.addAnimation(d1);
					}
//Scale
					dz.setFillAfter(false);
					pk[level].startAnimation(dz);
					//pk[level].setVisibility(View.GONE);
				}

				pnk[level] = null;

				nextlevel = setlevel;
				new levelSet().execute();

			}else{

				if(level >= 0 && pk[level] != null){

					cnk[level].drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					//Bitmap bx = Bitmap.createScaledBitmap(bnk[level], (int) (bnk[level].getWidth() * .5f), (int) (bnk[level].getHeight() * .5f), false);
					//ink.setImageBitmap(bx);
					//ink.setScaleType(ScaleType.FIT_XY);
					//ink.setLayoutParams(getRelativeLayout(-1, -1));

					board.removeView(pk[level]);
					xboard.removeView(pk[level]);
					xboard.addView(pk[level]);

					AnimationSet dz = new AnimationSet(true);
					{
						//Animation d1 = new ScaleAnimation(1f, .4f, 1f, .4f, Animation.RELATIVE_TO_PARENT, (float) ( ((level+1) * 160) / bnk[level].getWidth() ), Animation.RELATIVE_TO_PARENT, (float) ( 0.5f ));
						Animation d1 = new ScaleAnimation(1f, .4f, 1f, .4f, Animation.RELATIVE_TO_PARENT, (float) ( ((level+2) * 160) ), Animation.RELATIVE_TO_PARENT, (float) ( 0.5f ));
						d1.setInterpolator(AnimationUtils.loadInterpolator( ctx, android.R.anim.linear_interpolator));
						d1.setZAdjustment(-50 + level);
						d1.setDuration(1880);
							// d1.setFillBefore(true);
							// d1.setFillAfter(true);
						dz.addAnimation(d1);
					}
					{
						//Animation d1 = new ScaleAnimation(.4f, .4f, .4f, .4f, Animation.RELATIVE_TO_PARENT, (float) ( ((level+1) * 160) / bnk[level].getWidth() ), Animation.RELATIVE_TO_PARENT, (float) ( 0.5f ));
						Animation d1 = new ScaleAnimation(.4f, .5f, .4f, .5f, Animation.RELATIVE_TO_PARENT, (float) ( ((level+2) * 160) ), Animation.RELATIVE_TO_PARENT, (float) ( 0.5f ));
						d1.setInterpolator(AnimationUtils.loadInterpolator( ctx, android.R.anim.linear_interpolator));
						d1.setZAdjustment(-50 + level);
						d1.setDuration(1880);
						d1.setStartOffset(1890);
						d1.setRepeatCount(-1);
						d1.setRepeatMode(d1.REVERSE);
							// d1.setFillBefore(true);
							// d1.setFillAfter(true);
						dz.addAnimation(d1);

					}
					dz.setFillEnabled(true);
					dz.setFillBefore(true);
					dz.setFillAfter(true);
					pk[level].startAnimation(dz);
				}

				if(nextlevel != setlevel){// && nextlevel != level){
					nextlevel = setlevel;
					new levelSet().execute();
				}
			}


		}

		public RelativeLayout[] pk = new RelativeLayout[51];
		Bitmap[] bnk = new Bitmap[51];
		Canvas[] cnk = new Canvas[51];
		ImageView[] ink = new ImageView[51];
		Bitmap bnk2;
		Canvas cnk2;
		ImageView ink2;
		int nextlevel = -2;

		public int[] my9 = new int[] { Color.argb(245, 51, 181, 229),
				Color.argb(245, 170, 102, 204), Color.argb(245, 153, 204, 0),
				Color.argb(245, 255, 187, 51), Color.argb(245, 255, 68, 68),
				Color.argb(245, 0, 153, 204), Color.argb(245, 153, 51, 204),
				Color.argb(245, 102, 153, 0), Color.argb(245, 255, 136, 0),
				Color.argb(245, 204, 0, 0) };

		public int[] my9a = new int[] { Color.argb(145, 51, 181, 229),
				Color.argb(145, 170, 102, 204), Color.argb(145, 153, 204, 0),
				Color.argb(145, 255, 187, 51), Color.argb(145, 255, 68, 68),
				Color.argb(145, 0, 153, 204), Color.argb(145, 153, 51, 204),
				Color.argb(145, 102, 153, 0), Color.argb(145, 255, 136, 0),
				Color.argb(145, 204, 0, 0) };

		public int level = -1;
		Bitmap nnk;
		Canvas snk;
		ImageView sj;

		public void controlPad(){

				// Control Pad
				{
					mpad = (RelativeLayout) getView("RelativeLayout");
					mpad.setLayoutParams(getRelativeLayout(-2, -2));
					mpad.setGravity(Gravity.CENTER);
					mpad.setBackgroundResource(R.drawable.dir0);

					mpad.setOnTouchListener(new OnTouchListener() {

						float sx = 0;
						float sy = 0;
						long vf = 0;
						boolean onm = false;
						RelativeLayout ij;
						int lastd = -1;
						long flip = 0;

						public boolean onTouch(View b, MotionEvent a) {
							if (a.getAction() == a.ACTION_MOVE
									|| a.getAction() == a.ACTION_DOWN) {

//if( a.getAction() == a.ACTION_DOWN) {
//	Toast.makeText(ctx,"Show Player "+myn+" Stats Here " +us[myn].left,1880).show();
//}

								if (flip > SystemClock.uptimeMillis() || level < 0 || level > bnk.length || bnk[level] == null) {
									return true;
								}

								flip = SystemClock.uptimeMillis() + 1000/32;
								sx = a.getX();
								sy = a.getY();
								onm = false;
								// Log.i("ok",
								// "ACTION  " + a.getActionIndex() + " ("
								// + sx + "," + sy + ") "
								// + b.getWidth() + " "
								// + b.getHeight());


								if (sx < b.getWidth() / 3) {
									bid.sendEmptyMessage(LEFT);
									// new bid().execute(LEFT);
									vf = 0;
									onm = true;
									if (lastd != LEFT) {
										lastd = LEFT;
										ij = (RelativeLayout) b;
										ij.setBackgroundResource(R.drawable.dirleft);
									}

									if(us[myn].left + us[myn].see.getWidth()/2 < bnk[level].getWidth() / 3 ){//bnk.getWidth
										zoomHere(4);
									}else{
									}
									if(us[myn].left + us[myn].see.getWidth()/2 < bnk[level].getWidth() - bnk[level].getWidth() / 3 ){//bnk.getWidth
										zoomHere(2);
									}
								} else if (sx > b.getWidth() - b.getWidth() / 3) {
									bid.sendEmptyMessage(RIGHT);
									// new bid().execute(RIGHT);
									vf = 0;
									onm = true;
									if (lastd != RIGHT) {
										lastd = RIGHT;
										ij = (RelativeLayout) b;
										ij.setBackgroundResource(R.drawable.dirright);
									}
									if(us[myn].left + us[myn].see.getWidth()/2 > bnk[level].getWidth() - bnk[level].getWidth() / 3 ){//bnk.getWidth
										zoomHere(4);
									}else{
										zoomHere(2);
									}
								} 

								if (sy < b.getHeight() / 3) {
									// new bid().execute(UP);
									bid.sendEmptyMessage(UP);
									vf = 0;
									onm = true;
									if (lastd != UP) {
										lastd = UP;
										ij = (RelativeLayout) b;
										ij.setBackgroundResource(R.drawable.dirup);
									}
								} else

								if (sy > b.getHeight() - b.getHeight() / 3) {
									bid.sendEmptyMessage(DOWN);
									// new bid().execute(DOWN);
									vf = 0;
									onm = true;
									if (lastd != DOWN) {
										lastd = DOWN;
										ij = (RelativeLayout) b;
										ij.setBackgroundResource(R.drawable.dirdown);
									}
								}

								if (a.getAction() == a.ACTION_DOWN) {
									// dpadset.sendEmptyMessage(0);
								} else if (!onm){
										//&& vf < SystemClock.uptimeMillis())

										//&& sy > b.getHeight() / 2 - b.getHeight() / 4
										//&& sy < b.getHeight() / 2 + b.getHeight() / 4
										//&& sx > b.getWidth() / 2 - b.getWidth() / 4
										//&& sx < b.getWidth() / 2 + b.getWidth() / 4) 

									vf = SystemClock.uptimeMillis() + 75;
									//vib.sendEmptyMessage(2);

									bid.sendEmptyMessageDelayed(JUMPUP,2);

zoomHere(4);

									if ( lastd != -1) {
										lastd = -1;
										ij = (RelativeLayout) b;
										ij.setBackgroundResource(R.drawable.dir0);
									} // dpadset.sendEmptyMessage(0);
								}

								return true;
							} else if (a.getAction() == a.ACTION_UP) {
								// Log.i("ok", "ACTION UP " +
								// a.getActionIndex());
								// new bjhide().execute();
								// dpadset.sendEmptyMessage(0);
zoomHere(4);

								if (lastd != -1) {
									lastd = -1;
									ij = (RelativeLayout) b;

									ij.setBackgroundResource(R.drawable.dir0);
								}
								bid.sendEmptyMessage(JUMPDOWN);
								return true;
							} else if (a.getAction() == a.ACTION_POINTER_2_DOWN) {

								// Log.i("ok", "JUMP");
								bid.sendEmptyMessage(JUMPUP);
								// new bidjump().execute(-1);
								return true;
							} else {
								Log.i("ok", "TOUCH ACTION " + a.getAction());
							}

							return true;

						}
					});
				}

				dpadrl = getRelativeLayout(-2, -2);
				dpadrl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				dpadrl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mpad.setLayoutParams(dpadrl);
				mpad.setVisibility(View.VISIBLE);

				bm.addView(mpad);

			}

		Handler dropcoinT = new Handler() {
			public void handleMessage(Message msg) {

				if (coin != null) {
					for (coinn = 0; coinn < coini.length; coinn++) {
					if(coini[coinn] == null){
						continue;
					}

					double cl = coinl[coinn] + coinb[coinn].getWidth()/2;
					double ct = coint[coinn] + coinb[coinn].getHeight()/2;
					double cloc = Math.sqrt( ( cl * cl ) + (ct * ct) );
					double pl = us[myn].left + us[myn].see.getWidth()/2;
					double pt = us[myn].top + us[myn].see.getHeight()/2;
					double ploc = Math.sqrt( (pl * pl) + (pt * pt) );

					if( 15f > (cloc > ploc?(cloc-ploc):(ploc-cloc)) ){

					//if (us[myn].left > coinl[coinn]
					//		&& us[myn].left + us[myn].see.getWidth() < coinl[coinn] + 100
					//		&& us[myn].top > coint[coinn]
					//		&& (us[myn].top + us[myn].see.getHeight()) < coint[coinn] + 100
					//		&& coini[coinn].getVisibility() == View.VISIBLE) {
						Log.i("ok", "LEVEL " + level + " " + coinn);

						if(pk[mGame.level] != null){
						coini[coinn].clearAnimation();
						coini[coinn].setVisibility(View.GONE);
						pk[mGame.level].removeView(coini[coinn]);
						coini[coinn] = null;

						// dropCoin

						
					}

//zoomHere(5);
//Toast
Toast.makeText(ctx,"Huray "+myn+" " +us[myn].left + "\nTODO: music, boom\nMan you're good.\n\n" + mGame.level ,1880).show();

						mGame.changeLevel( mGame.level + 1 );
						
//pulseColor.sendEmptyMessageDelayed(2,175);



						// DUFF
						{
							snk.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
							// snk.drawColor(Color.GRAY,
							// PorterDuff.Mode.SCREEN);

							mycoin++;


							Paint tp = new Paint();
							tp.setTypeface(face[0]);
							tp.setColor(Color.argb(120, 250, 250, 250));
							tp.setTextSize(42f);
							tp.setAntiAlias(true);
							snk.drawText("Score " + mycoin, (int) (10), (int) (tp.getTextSize()), tp);

							edt.putInt("score", mycoin);
							edt.commit();

							//
							// RelativeLayout.LayoutParams sl =
							// getRelativeLayout(-2,
							// -2);
							// sl.setMargins(0, bnk.getHeight() / 2 + 88, 0, 0);
							// sj.setLayoutParams(sl);

						}
						}
					}

				}
				scancoin = false;
			}
		};

// Change Color of pulse
Handler pulseColor = new Handler(){
public void handleMessage(Message mg){
					Bitmap jub = BitmapFactory.decodeResource(getResources(), R.drawable.pulseme);
					Bitmap jb = Bitmap.createBitmap(jub.getWidth(), jub.getHeight(), Bitmap.Config.ARGB_8888);
					Canvas juc = new Canvas(jb);

					Paint jup = new Paint();
					jup.setAntiAlias(true);
					jup.setColor(my9a[runx.nextInt(my9a.length - 1)]);

					juc.drawBitmap(jub, 0, 0, jup);

					jup.setStrokeWidth(8f);

					//juc.drawLine(jub.getWidth() / 2 - 16, jub.getHeight() / 2 - 14, jub.getWidth() / 2 + 7, jub.getHeight() / 2 - 14, jup);
					juc.drawLine(jub.getWidth() / 2 - 111, jub.getHeight() / 2 - 11, jub.getWidth() / 2 + 105, jub.getHeight() / 2 - 11, jup);
					// dropCoin

					for(int ki = 0; ki < pulsemerow.length; ki++){
						if(pulsemerow[ki] == null){
							break;
						}
						pulsemerow[ki].setImageBitmap(jb);
					}
}
};

		int mycoin = 0;

		// Handler dpadset = new Handler() {
		// public void handleMessage(Message msg) {
		// for (int x = 1; x < dpad.length && dpad[x] != null; x++) {
		// dpad[x].setVisibility(View.INVISIBLE);
		// }
		// }
		// };

		boolean scancoin = false;
		long scancoint = 0;
		Handler bid = new Handler() {
			public void handleMessage(Message msg) {

				if (!scancoin && scancoint < SystemClock.uptimeMillis()) {
					scancoin = true;
					scancoint = SystemClock.uptimeMillis() + 50;
					dropcoinT.sendEmptyMessage(2);
				}
				if(mGame.level < 0 || mGame.level > mGame.pnk.length){
					return;
				}
				if(mGame.pnk[mGame.level] == null){
					return;
				}


				if (msg.what == LEFT) {
					us[myn].left();
				} else if (msg.what == UP) {
					us[myn].up();
				} else if (msg.what == RIGHT) {
					us[myn].right();
				} else if (msg.what == DOWN) {
					us[myn].down(5);
				} else if (msg.what == JUMPUP) {
					us[myn].jump();
				} else if (msg.what == JUMPDOWN) {
					us[myn].jumpup(0);
					//us[myn].jumpdown();
					//us[myn].jumpdownnext.sendEmptyMessageDelayed(2, 32);
				}

			}
		};

Bitmap[] coinb = new Bitmap[15];
Canvas[] coinc = new Canvas[15];
		int[] cointb = new int[15];
		int[] coinrl = new int[15];
int[] coinl = new int[15];
int[] coint = new int[15];
ImageView[] coini = new ImageView[15];
RelativeLayout.LayoutParams[] coinr = new RelativeLayout.LayoutParams[15];
Paint[] coinp = new Paint[15];
public void dropCoin( int left, int top, int right, int bottom) {
	//coinn = 0;
	coinn++;

	if(coinn >= coinr.length){
		coinn = 0;
	}
	hid++;
	while (findViewById(++hid) != null)
	Log.w("ok", "coin catch ");

	coin = (ImageView) new ImageView(ctx);
	coin.setId(hid);

	coinr[coinn] = getRelativeLayout(-2, -2);
	coinr[coinn].setMargins(left,top, right, bottom);
	coinl[coinn] = left;
	coint[coinn] = top;
	coinrl[coinn] = LEFT;
	cointb[coinn] = DOWN;
	coin.setLayoutParams(coinr[coinn]);
	coin.setScaleType(ScaleType.MATRIX);
	//coin.setImageResource(R.drawable.coin);

	coinb[coinn] = Bitmap.createBitmap(101,101,Bitmap.Config.ARGB_8888);
	coinc[coinn] = new Canvas(coinb[coinn]);
	coin.setImageBitmap(coinb[coinn]);
	pk[level].addView(coin);
	coini[coinn] = coin;


	AnimationSet h8 = new AnimationSet(true);
	{
	Animation d5 = new AlphaAnimation(0f, 1f);
	d5.setInterpolator(AnimationUtils.loadInterpolator(
	ctx, android.R.anim.linear_interpolator));
	d5.setDuration(3880);
	h8.addAnimation(d5);
	}

	{
	Animation h9 = new TranslateAnimation(0f, 0f, 0f, -50f);
	// h9.setInterpolator(AnimationUtils.loadInterpolator(
	// ctx, R.anim.cycle_interpolator));
	h9.setZAdjustment(1);
	h9.setDuration(1880);
	h9.setRepeatCount(-1);
	h9.setRepeatMode(h9.REVERSE);
	h8.addAnimation(h9);
	}
	h8.setStartOffset(120 * coinn);
	coin.startAnimation(h8);

	//drawRect
	if(coinp[coinn] == null){
		coinp[coinn] = new Paint();
		coinp[coinn].setAntiAlias(true);
		coinp[coinn].setColor(mGame.my9a[runx.nextInt(mGame.my9a.length - 1)]);
	}

	coinh.sendEmptyMessageDelayed(coinn,25);


	}


	Paint ptbl;
	Handler coinh = new Handler(){
		public void handleMessage(Message mg){

		int coinn = mg.what;
		coinc[coinn].drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		
		Path sp = new Path();
		RectF spd = new RectF();


		spd.set( (float)(coinb[coinn].getWidth()/2-40), (float)(coinb[coinn].getHeight()/2-40), (float) (coinb[coinn].getWidth()/2+40), (float) (coinb[coinn].getHeight()/2+40));
		sp.addRoundRect(spd,(float)(4), (float)(4), Path.Direction.CCW);
		coinc[coinn].clipPath(sp);
		

		coinc[coinn].drawRect((float)(coinb[coinn].getWidth()/2-30+runx.nextInt(15) ), (float)(coinb[coinn].getHeight()/2-30+runx.nextInt(15)), (float)(coinb[coinn].getWidth()/2+30-runx.nextInt(15) ), (float)( coinb[coinn].getHeight()/2+30-runx.nextInt(15) ), coinp[coinn]);

		coinc[coinn].drawRect((float)(coinb[coinn].getWidth()/2-26+runx.nextInt(15) ), (float)(coinb[coinn].getHeight()/2-26+runx.nextInt(15)), (float)(coinb[coinn].getWidth()/2+26-runx.nextInt(15) ), (float)( coinb[coinn].getHeight()/2+26-runx.nextInt(15) ), coinp[coinn]);

		coinc[coinn].drawRect((float)(coinb[coinn].getWidth()/2-26+runx.nextInt(15) ), (float)(coinb[coinn].getHeight()/2-26+runx.nextInt(15)), (float)(coinb[coinn].getWidth()/2+26-runx.nextInt(15) ), (float)( coinb[coinn].getHeight()/2+26-runx.nextInt(15) ), coinp[coinn]);

		coinc[coinn].drawRect((float)(coinb[coinn].getWidth()/2 -5+runx.nextInt(15) ), (float)(coinb[coinn].getHeight()/2-5-runx.nextInt(15)), (float)(coinb[coinn].getWidth()/2 -5+25+2 ), (float)( coinb[coinn].getHeight()/2-5+25+2 ), coinp[coinn]);
		coinc[coinn].drawRect((float)(coinb[coinn].getWidth()/2 +5+runx.nextInt(15) ), (float)(coinb[coinn].getHeight()/2-0-runx.nextInt(15)), (float)(coinb[coinn].getWidth()/2 -0+25+5 ), (float)( coinb[coinn].getHeight()/2-0+25+2 ), coinp[coinn]);



		if(ptbl == null){
			ptbl = new Paint();
			ptbl.setAntiAlias(true);
			ptbl.setColor(Color.argb(200,0,0,80));
		}
		coinc[coinn].drawRect((float)(coinb[coinn].getWidth()/2 - 1), (float)(coinb[coinn].getHeight()/2-11), (float)(coinb[coinn].getWidth()/2+4), (float)( coinb[coinn].getHeight()/2+14 ), ptbl);

		coinc[coinn].drawRect((float)(coinb[coinn].getWidth()/2 - 2), (float)(coinb[coinn].getHeight()/2-12), (float)(coinb[coinn].getWidth()/2+2), (float)( coinb[coinn].getHeight()/2+12 ), coinp[coinn]);

		spd.set( (float)(coinb[coinn].getWidth()/2-20), (float)(coinb[coinn].getHeight()/2-30), (float) (coinb[coinn].getWidth()/2+20), (float) (coinb[coinn].getHeight()/2+30));
		sp.addRoundRect(spd,(float)(8), (float)(8), Path.Direction.CCW);
		coinc[coinn].clipPath(sp);
		//coinc[coinn].drawPaint(coinp[coinn]);



		if(coini[coinn] != null && coini[coinn].getVisibility() == View.VISIBLE){
			coinh.sendEmptyMessageDelayed(coinn,75);

			
			coinr[coinn].setMargins( coinl[coinn], coint[coinn] , 0, 0);
			if( coinrl[coinn] == LEFT ){
				coinl[coinn] -= runx.nextInt(34);
			}else{
				coinl[coinn] += runx.nextInt(34);
			}
			if( cointb[coinn] == DOWN ){
				coint[coinn] += runx.nextInt(24);
			}else{
				coint[coinn] -= runx.nextInt(24);
			}
//			coinl[coinn] += runx.nextInt(14) - runx.nextInt(18);
//			coint[coinn] += runx.nextInt(2) - runx.nextInt(2);
			
			
			if(bnk != null){

				if(coinl != null && coint != null && coinb != null && coini != null){

					if( coinn < coinl.length && coinn < coinb.length && level > 0 && level < bnk.length

						&& coinl[coinn] > 0+55 && coinl[coinn] < bnk[level].getWidth() - coini[coinn].getWidth() - 55

						&& coint[coinn] > 0+115 && coint[coinn] + coinb[coinn].getHeight() + 115 < basesize
						){
						//&& coint[coinn] > 0 && coint[coinn] + coinb[coinn].getHeight() < bm.getHeight()/2 
				
						coini[coinn].setLayoutParams(coinr[coinn]);
					}else{
						if( coinl[coinn] > 0+55 && coinl[coinn] < bnk[level].getWidth() - coini[coinn].getWidth() - 55 ){
							if(cointb[coinn] == DOWN){cointb[coinn] = UP;}else{cointb[coinn] = DOWN;}
						}else{
							if(coinrl[coinn] == LEFT){coinrl[coinn] = RIGHT;}else{coinrl[coinn] = LEFT;}
						}

					}

					coini[coinn].postInvalidate();
				}
			}

		}


		}
	};




	// dropCoin
	ImageView[] pulsemerow = new ImageView[15];


	class levelSet extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... a) {

			return null;
		}



			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				//level = reg.getInt("atlevel", level);
				if (level < 0) { level = 0; }
				if (nextlevel < 0) { nextlevel = 0; }

			//	Toast.makeText(ctx, level + " SET "+myn+" Here ",1880).show();

					for(int i = 0; i < pk.length; i++){
						if(pnk[i] == null){continue;}
						if(bnk[i] == null){continue;}
						//bnk[i] = null;
						cnk[i] = null;
						pnk[i] = null;
//createScale.4f
					}

				boolean review = true;
				if (bnk[nextlevel] == null && 1==1) {
					review = false;
//cnk.*=

					if(bm.getHeight() < bm.getWidth()){
						basesize = bm.getHeight();
					}else{
						basesize = bm.getWidth();
					}
if(basesize == 0){
basesize = 240;
}

Toast.makeText(ctx, level + " :" + basesize+"<-- " + bm.getWidth() ,1880).show();

					pk[nextlevel] = (RelativeLayout) getView("RelativeLayout");
try {
					bnk[nextlevel] = Bitmap.createBitmap(basesize, (int)(basesize), Bitmap.Config.ARGB_8888);
}catch(OutOfMemoryError i9f){
try {
					bnk[nextlevel] = Bitmap.createBitmap(basesize, (int)(basesize), Bitmap.Config.ARGB_4444);
}catch(OutOfMemoryError i9e){
	i9e.printStackTrace();
}
}

					//bnk[nextlevel] = Bitmap.createBitmap(bm.getWidth(), (int)(bm.getHeight()/2), Bitmap.Config.ARGB_8888);
					cnk[nextlevel] = new Canvas(bnk[nextlevel]);

					//Paint p9 = new Paint(); p9.setAntiAlias(true); p9.setColor(Color.argb(150,190,190,190));//my9[runx.nextInt(my9.length - 1)]);
					//cnk[nextlevel].drawLine((int) (bnk[level].getWidth() - 4), 0, bnk[level].getWidth() - 4, (int) (bm.getHeight() / 2+25), p9);

					ink[nextlevel] = (ImageView) getView("ImageView");
					ink[nextlevel].setLayoutParams(getRelativeLayout(-2, -2));
					ink[nextlevel].setScaleType(ScaleType.MATRIX);
					ink[nextlevel].setImageBitmap(bnk[nextlevel]);


				}else{
					//if(mGame.pnk[mGame.level] == null)
					//mGame.pnk[mGame.level] = bnk

					pnk[nextlevel] = new int[bnk[nextlevel].getWidth() * bnk[nextlevel].getHeight()];
					bnk[nextlevel].getPixels(pnk[nextlevel], 0, bnk[nextlevel].getWidth(), 0, 0, bnk[nextlevel].getWidth(), bnk[nextlevel].getHeight());
					{
						Paint tp = new Paint();
						tp.setAntiAlias(true);
						tp.setTypeface(face[0]);
						tp.setColor(Color.argb(40, 50, 250, 50));
						tp.setTextSize(42f);
						// tp.setTextScaleX(1.5f);
						tp.setTextSkewX(.2f);

						cnk[nextlevel] = new Canvas(bnk[nextlevel]);

						Paint p9 = new Paint(); p9.setAntiAlias(true); p9.setColor(Color.argb(150,190,190,190));//my9[runx.nextInt(my9.length - 1)]);
						cnk[nextlevel].drawLine((int) (bnk[level].getWidth() - 4), (bnk[level].getHeight() -25) , bnk[level].getWidth() - 4, (int) (bnk[level].getHeight()-25), p9);

						//cnk[nextlevel].skew(.2f, 0f);//dangerous?
						cnk[nextlevel].drawText("reLevel " + (nextlevel + 1), (int) (10), (int) (bnk[level].getHeight() - 8f), tp);
						ink[nextlevel].setImageBitmap(bnk[nextlevel]);//barrier
	
						pk[nextlevel].clearAnimation();
						xboard.removeView(pk[nextlevel]);
					//board.*pk
					}
				}

				//us = pk =
				if(us[0] != null){
					pk[level].removeView(us[0].see);
				}
				//pk[level].addView(us[0].see);


				if(snk == null){ // one layer

Toast.makeText(ctx, level + " Player "+mycoin+"  :" + basesize,1880).show();

try {
					nnk = Bitmap.createBitmap(basesize, basesize - 16, Bitmap.Config.ARGB_8888);
}catch (OutOfMemoryError e9){

try {
					nnk = Bitmap.createBitmap(basesize, basesize - 16, Bitmap.Config.ARGB_4444);
}catch (OutOfMemoryError e9b){
	e9b.printStackTrace();
}

}


					//nnk = Bitmap.createBitmap(bm.getWidth(), bm.getHeight() / 2 - 16, Bitmap.Config.ARGB_8888);

					snk = new Canvas(nnk);
					{
						mycoin = reg.getInt("score",0);

						Paint tp = new Paint();
						tp.setTypeface(face[0]);
						tp.setColor(Color.argb(120, 250, 250, 250));
						tp.setTextSize(42f);
						tp.setAntiAlias(true);
						snk.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
						snk.drawText("Score " + mycoin, (int) (10), (int) (tp.getTextSize()), tp);
					}


					sj = (ImageView) getView("ImageView");
					RelativeLayout.LayoutParams sl = getRelativeLayout(-2, -2);
					sl.setMargins(0, 8, 0, 0);
					sj.setLayoutParams(sl);
					sj.setScaleType(ScaleType.MATRIX);
					sj.setImageBitmap(nnk);

					Paint p9 = new Paint();
					p9.setStrokeWidth(28f);
					p9.setColor(my9[runx.nextInt(my9.length - 1)]);
					p9.setAntiAlias(true);

					//snk.drawLine(0, (int) (bm.getHeight() / 2), bm.getWidth(), (int) (bm.getHeight() / 2), p9);
					//snk.drawLine(0, 0, bm.getWidth(), 0, p9);
					//snk.drawLine(0, 0, 0, (int)(bm.getHeight() / 2), p9);
					//snk.drawLine((int) (bm.getWidth()), 0, bm.getWidth(), (int) (bm.getHeight() / 2), p9);


					//p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
					//snk.drawCircle(bm.getWidth() / 2, bm.getHeight(), (float)(bm.getHeight()*.9), p9);

					p9.setStrokeWidth(24f);
					p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);

					board.addView(sj);

				}



				{

					if(mAds != null){
						mAds.reAd.sendEmptyMessageDelayed(2, 4380);
					}

//					for (coinn = 0; coinn < coini.length; coinn++) {
					//pk[level].removeView(coin[coinn]);
//						if(coini[coinn] == null){
//							continue;
//						}
//						coini[coinn].setVisibility(View.GONE);
//						coini[coinn] = null;
//					}
					//coin = null;

					// for (int ci = level; ci >= 0; ci--) {
//					if (level > 0 && pk[level - 1] != null && pk[level-1] != null) {
//
//						int ci = level - 1;
//
//						// if (level == ci) {
//
//					}

					// wed 10am

					{


//Toast
//Toast.makeText(ctx, level + " Player "+myn+" Here ",1880).show();

						// level++;

						if(us[0] != null){
							pk[level].removeView(us[0].see);
						}

						level = nextlevel;
						Paint p9 = new Paint();
						p9.setStrokeWidth(28f);
						p9.setColor(my9[runx.nextInt(my9.length - 1)]);
						p9.setAntiAlias(true);

//Circle
						//cnk[level].drawLine(0, (int) (bm.getHeight() / 2), bnk[level].getWidth(), (int) (bm.getHeight() / 2), p9);
						//cnk[level].drawLine(0, 4, bnk[level].getWidth(), 4, p9);
						//cnk[level].drawLine(4, 0, 4, (int)(bm.getHeight() / 2), p9);
//cnk[ =
						//cnk[level].drawLine((int) (bnk[level].getWidth() - 4), 0, bnk[level].getWidth() - 4, (int) (bm.getHeight() / 2), p9);

						RectF rect = new RectF();
						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						p9.setStrokeWidth(18f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.STROKE);



						p9.setColor(my9a[level]);
						rect.set(
								(int) (14),
								(int) (14),
								(int) (bnk[level].getWidth() - 14),
								(int) (bnk[level].getHeight() -14));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(Color.argb(150,190,190,190));//my9[runx.nextInt(my9.length - 1)]);
						rect.set(
								(int) (40),
								(int) (bnk[level].getHeight() -45),
								(int) (bnk[level].getWidth() - 40),
								(int) (bnk[level].getHeight() -20));// + bnk[level].getHeight() / 14));
						//cnk[level].drawRoundRect(rect, 12, 12, p9);




						if( review ){

						}else{
							pk[level].addView(ink[level]);
						}
						xboard.removeView(pk[level]);
						board.removeView(pk[level]);
						RelativeLayout.LayoutParams br = getRelativeLayout(-2, -2);
						br.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
						//br.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);
						pk[level].setLayoutParams(br);

						board.addView(pk[level], 0);

						// board.bringChildToFront(sj);
					}


					{

						try {
						bnk2 = Bitmap.createBitmap(basesize, basesize, Bitmap.Config.ARGB_8888);
						cnk2 = new Canvas(bnk2);

						ink2 = (ImageView) getView("ImageView");
						ink2.setLayoutParams(getRelativeLayout(-2, -2));

						ink2.setScaleType(ScaleType.MATRIX);
						ink2.setImageBitmap(bnk2);

						Paint p9 = new Paint();
						p9.setStrokeWidth(28f);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);


						p9.setAntiAlias(true);

						//cnk2.drawLine(0, (int) (bnk2.getHeight() ), bnk2.getWidth(), (int) (bnk2.getHeight() ), p9);

						//cnk2.drawLine(10, 14, bnk2.getWidth()-10, 14, p9);
						//cnk2.drawLine(14, 10, 14, (int)(bnk2.getHeight() -10 ), p9);

						//cnk2.drawLine((int) (bnk2.getWidth() - 14), 10, bnk2.getWidth() - 14, (int) (bnk2.getHeight() -10), p9);

						RectF rect = new RectF();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.STROKE);

						p9.setColor(my9[level]);
						rect.set(
								(int) (4),
								(int) (4),
								(int) (bnk2.getWidth() - 4),
								(int) (bnk2.getHeight() -4));
						//cnk2.drawRoundRect(rect, 12, 12, p9);


						p9.setColor(Color.argb(50,0,0,0));//my9[runx.nextInt(my9.length - 1)]);
						rect.set(
								(int) (38),
								(int) (bnk2.getHeight() -47),
								(int) (bnk2.getWidth() - 42),
								(int) (bnk2.getHeight() -22));
						//cnk2.drawRoundRect(rect, 12, 12, p9);




						pk[level].addView(ink2);
						}catch(OutOfMemoryError eb){
							Log.w("ok","BNK2 CALLS ALERT");
						}
					}


					if(!review){

					switch (level) {

					case 5: {
						//dropCoin((int) (bnk.getWidth() / 2 + 50), (int) (bnk.getHeight() / 2 - 190), 0, 0);
						//dropCoin((int) (bnk.getWidth() -200 + 45), (int) (bnk.getHeight() / 2 - 90), 0, 0);
						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);

						p9.setStyle(Style.FILL_AND_STROKE);
						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						cnk[level].drawCircle(bnk[level].getWidth() / 2,
								bnk[level].getHeight() - 100, 80f, p9);

						p9.setStyle(Style.STROKE);
						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						cnk[level].drawCircle(bnk[level].getWidth() / 2 - 120,
								bnk[level].getHeight() - 120, 80f, p9);

						break;
					}

					case 0: {
						dropCoin((int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 8 + (bnk[level].getWidth()/8/2) ), (int) (bnk[level].getHeight() / 2 - bnk[level].getHeight()/14*2 - 40), 0, 0);
						//dropCoin((int) (bnk.getWidth() -200 + 45), (int) (bnk.getHeight() / 2 - 90), 0, 0);


						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 8),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 28 * 5),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 8 * 2),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14 * 2));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + 0),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14 * 3),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 8),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14 * 2));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 8),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 28 * 5),
								(int) (bnk[level].getWidth() / 2 - 0),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14 * 2));
						cnk[level].drawRoundRect(rect, 12, 12, p9);



						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 14 * 2),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 2 * 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 14 * 1.5));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 2 * 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 14 * 2),
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 14 * 1.5));
						cnk[level].drawRoundRect(rect, 12, 12, p9);
//zoomHere
						break;
					}

					case 10: {
						//dropCoin((int) (bnk[level].getWidth() / 2 + 50), (int) (bnk[level].getHeight() / 2 - 190), 0, 0);
						//dropCoin((int) (bnk[level].getWidth() -200 + 45), (int) (bnk[level].getHeight() / 2 - 90), 0, 0);
						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.FILL_AND_STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 8),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14 * 2),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 8 * 2),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + 0),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14 * 3),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 8),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 14 * 2));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 8),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 14 * 4),
								(int) (bnk[level].getWidth() / 2 - 0),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 14 * 3));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						break;
					}

					case 9: {
						//dropCoin((int) (bnk[level].getWidth() / 2 + 50), (int) (bnk[level].getHeight() / 2 - 190), 0, 0);
						//dropCoin((int) (bnk[level].getWidth() -200 + 45), (int) (bnk[level].getHeight() / 2 - 90), 0, 0);
						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.FILL_AND_STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 7 * 2),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 7 * 1.5),
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 7),
								(int) (bnk[level].getHeight() - bnk[level].getHeight() / 7));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 7),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 2),
								(int) (bnk[level].getWidth() / 2 - 0),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 1.5));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - 0),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 2.5),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 2));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 1.5),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7 * 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7));
						cnk[level].drawRoundRect(rect, 12, 12, p9);

						break;
					}

					case 8: {
						dropCoin((int) (bnk[level].getWidth() / 2  + bnk[level].getWidth() / 7 * 1.5 ), (int) (bnk[level].getHeight() / 2 - bnk[level].getHeight() / 7 - 40), 0, 0);

						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.FILL_AND_STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 1.5),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7 * 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 7 * 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 1.5),
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 7),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7));
						cnk[level].drawRoundRect(rect, 8, 8, p9);



						break;
					}

					case 7: {
						dropCoin((int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7 + 50), (int) (bnk[level].getHeight() - 110), 0, 0);

						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.FILL_AND_STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7),
								(int) (bnk[level].getHeight()  - 70),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 7 * 2),
								(int) (bnk[level].getHeight()  - 50));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 7 * 3),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7 * 2),
								(int) (bnk[level].getWidth() / 2 - bnk[level].getWidth() / 7 * 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 7));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						break;
					}

					case 6: {
						dropCoin((int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6 + 50), (int) (bnk[level].getHeight()  - 110), 0, 0);
						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.FILL_AND_STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6),
								(int) (bnk[level].getHeight()  - 70),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6 * 2),
								(int) (bnk[level].getHeight()  - 50));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6 * 2),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 6 * 2),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6 * 3),
								(int) (bnk[level].getHeight()  - bnk[level].getHeight() / 6));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						break;
					}

					case 1: {
						dropCoin((int) ( (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6) + 50), (int) (bnk[level].getHeight() - 110), 0, 0);

						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.FILL_AND_STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6),
								(int) (bnk[level].getHeight()  - 70),
								(int) (bnk[level].getWidth() / 2 + bnk[level].getWidth() / 6 * 2),
								(int) (bnk[level].getHeight()  - 50));
						cnk[level].drawRoundRect(rect, 8, 8, p9);
						break;
					}

					case 2: {
						dropCoin((int) (bnk[level].getWidth() / 2 + 50), (int) (bnk[level].getHeight() / 2 - 190), 0, 0);
						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setStyle(Style.FILL_AND_STROKE);
						RectF rect = new RectF();

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set((int) (bnk[level].getWidth() / 2 - 50),
								(int) (bnk[level].getHeight()  - 150),
								(int) (bnk[level].getWidth() / 2 + 20),
								(int) (bnk[level].getHeight()  - 50));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						rect.set((int) (bnk[level].getWidth() / 2),
								(int) (bnk[level].getHeight()  - 120),
								(int) (bnk[level].getWidth() / 2 + 100),
								(int) (bnk[level].getHeight()  - 20));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						p9.setColor(my9[runx.nextInt(my9.length - 1)]);
						rect.set(
								(int) (bnk[level].getWidth() / 2 - (bnk[level].getWidth() / 6)),
								(int) (bnk[level].getHeight()  - 58),
								(int) (bnk[level].getWidth() / 2 - 60),
								(int) (bnk[level].getHeight()  - 8));
						cnk[level].drawRoundRect(rect, 8, 8, p9);

						break;
					}
					case 3: {
						dropCoin((int) (bnk[level].getWidth() -200 + 45), (int) (bnk[level].getHeight()  - 90), 0, 0);
						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						p9.setAntiAlias(true);
						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						p9.setStyle(Style.FILL_AND_STROKE);
						cnk[level].drawRect(bnk[level].getWidth() - 200,
								bnk[level].getHeight() - 50, bnk[level].getWidth() - 70,
								bnk[level].getHeight() - 8, p9);

						break;
					}





					case 4: {

						dropCoin((int) (bnk[level].getWidth() - 200 + 45), (int) (bnk[level].getHeight() - 90), 0, 0);

						Paint p9 = new Paint();
						p9.setStrokeWidth(8f);
						// p9.setColor(my9[runx.nextInt(my9.length - 1)]);
						p9.setAntiAlias(true);

						// 1
						p9.setStyle(Style.STROKE);
						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						cnk[level].drawRect(bnk[level].getWidth() - 200,
								bnk[level].getHeight()  - 50, bnk[level].getWidth() - 70,
								bnk[level].getHeight()  - 8, p9);

						// 2
						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						cnk[level].drawRect(bnk[level].getWidth() - 300,
								bnk[level].getHeight()  - 150,
								bnk[level].getWidth() - 270,
								bnk[level].getHeight()  - 108, p9);

						// 3
						p9.setColor(my9a[runx.nextInt(my9a.length - 1)]);
						cnk[level].drawRect(bnk[level].getWidth() - 350,
								bnk[level].getHeight()  - 180,
								bnk[level].getWidth() - 300 - 8,
								bnk[level].getHeight()  - 150 - 8, p9);

						break;
					}

					}//switch
					}

	


					{
						AnimationSet dz = new AnimationSet(true);
						{
							Animation d1 = new ScaleAnimation(.5f, 1f, .5f, 1f,
									Animation.RELATIVE_TO_PARENT,
									(0f),
									Animation.RELATIVE_TO_PARENT,
									(1f));
							d1.setInterpolator(AnimationUtils.loadInterpolator(
									ctx, android.R.anim.linear_interpolator));
							d1.setZAdjustment(10);
							d1.setDuration(1880);
							d1.setFillAfter(false);
							dz.addAnimation(d1);
						}
						dz.setFillAfter(false);
						pk[level].startAnimation(dz);
						//us[myn].see.startAnimation(dz);
					}

				}

				// for (int c = 0; c < level; c++) {
				// // View b = pk[level].getChildAt(c);
				// pk[level - 1].removeViewAt(c);
				// }

				try {
					//if(level > 0){
					//	pnk[level - 1] = new int[1];
					//}

					for(int i = 0; i < level; i++){
						if(pnk[i] == null){continue;}
						if(bnk[i] == null){continue;}
						//bnk[i] = null;
						cnk[i] = null;
						pnk[i] = null;
//createScale.4f
					}
			//11 camera calculator stockmarketreport calendar alarm/clock/timmer
			//500	
					pnk[level] = new int[bnk[level].getWidth() * bnk[level].getHeight()];
					bnk[level].getPixels(pnk[level], 0, bnk[level].getWidth(), 0, 0, bnk[level].getWidth(), bnk[level].getHeight());
					cnk[level] = new Canvas(bnk[level]);

					Paint tp = new Paint();
					tp.setTypeface(face[0]);
					tp.setColor(Color.argb(40, 50, 250, 50));

					tp.setAntiAlias(true);
					tp.setTextSize(42f);
					// tp.setTextScaleX(1.5f);
					tp.setTextSkewX(.2f);
					//cnk[level].skew(.2f, 0f);//dangerousX
					//cnk[level].drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					cnk[level].drawText("Level " + (level + 1), (int) (bnk[level].getWidth()/2), (int) (bnk[level].getHeight()/2 + 8f), tp);



					// - tp.getTextSize()
				}catch(OutOfMemoryError eom) {
					eom.printStackTrace();
				}

				// int jic = 0;
				// for (int ji = 0; ji <= level && jic < 10; ji++) {

				// jic++;

				if(us[0] == null){
					String m = "self";
					for (int n = 0; n < 5; n++) {

						m = "self";
						switch (n) {
						case 0:
							m = "Organizer";
							break;
						case 1:
							m = "Doctor";
							break;
						case 2:
							m = "Sister";
							break;
						case 3:
							m = "person";
							break;
						case 4:
							m = "person";
							break;
						}

						// HandlerThread mx = new HandlerThread("ok",
						// Thread.NORM_PRIORITY);

						us[n] = new GamePlayer((int) (101+ n * 101 * .15 ),
								(int) (board.getHeight() * .8 ), m,
								getMainLooper());


						us[n].screenwidth = basesize;
						us[n].screenheight = basesize;
						us[n].atlevel = level;

						us[n].color = my9[runx.nextInt(my9.length - 1)];




						us[n].jump();
					}



				}else{
					// pk[.*=
					try{
						//RelativeLayout ap2 = (RelativeLayout) us[0].see.getParent();
						//if(ap2 != null){
							//ap2.removeView(us[0].see);
						//}
						pk[us[0].atlevel].removeView(us[0].see);
						pk[level].addView(us[0].see);
						us[0].atlevel = level;
					}catch(IllegalStateException ie){
						ie.printStackTrace();
					}
				}
			
if (level < 4) {
					Bitmap jub = BitmapFactory.decodeResource(getResources(), R.drawable.pulseme);
					Bitmap jb = Bitmap.createBitmap(jub.getWidth(), jub.getHeight(), Bitmap.Config.ARGB_8888);
					Canvas juc = new Canvas(jb);

					Paint jup = new Paint();
					jup.setColor(my9a[runx.nextInt(my9a.length - 1)]);
					juc.drawBitmap(jub, 0, 0, jup);
					jup.setStrokeWidth(8f);

					juc.drawLine(jub.getWidth() / 2 - 111, jub.getHeight() / 2 - 18, jub.getWidth() / 2 + 105, jub.getHeight() / 2 - 11, jup);
					// dropCoin



					ImageView ju = (ImageView) getView("ImageView");
					RelativeLayout.LayoutParams br = getRelativeLayout(-2, -2);
					//br.setMargins(0, (int) (basesize - runx.nextInt( ((int)(basesize/21)) ) * 21 ), 0, 0);
					int where = runx.nextInt( basesize/21 );

					br.setMargins(0, (int) (basesize - where * 21 ), 0, 0);
					ju.setLayoutParams(br);
					ju.setScaleType(ScaleType.MATRIX);
					ju.setImageBitmap(jb);

					// ju.setImageResource(R.drawable.pulseme);
					meteor.addView(ju, 0);

					AnimationSet h8 = new AnimationSet(true);
					{
						Animation h9 = new TranslateAnimation(-100f, bnk[level].getWidth() + 100f, 0f, 0f);
						// h9.setInterpolator(AnimationUtils.loadInterpolator(ctx,
						// R.anim.cycle_interpolator));
						h9.setZAdjustment(1);
						h9.setDuration(1880);
						h9.setRepeatCount(-1);
						h9.setRepeatMode(h9.RESTART);
						h8.addAnimation(h9);
					}
					h8.setStartOffset(level * 820);
					ju.startAnimation(h8);

					//dropCoin
					pulsemerow[level] = ju;
				}

				if (us[myn] != null && level < bnk.length && level >= 0) {

					for (int n = 0; n < 5; n++) {

						if (n == myn && us[n].left > bnk[level].getWidth() / 2) {
							us[n].left = us[n].see.getWidth();
							us[n].left();
							us[n].jump();
						}
					}

				}










			}

		











}





	}



	Handler vib = new Handler() {
		public void handleMessage(Message msg) {
			new vibme().execute();
		}
	};

	public class vibme extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... a) {
			vibrator.vibrate(new long[] { 1, 30 }, -1);

			return null;
		}

	}

	public RelativeLayout.LayoutParams getRelativeLayout(int width, int height) {
		return new RelativeLayout.LayoutParams(width, height);
	}

	public LinearLayout.LayoutParams getLinearLayout(int width, int height) {
		return new LinearLayout.LayoutParams(width, height);
	}

	public ScrollView.LayoutParams getScrollLayout(int width, int height) {
		return new ScrollView.LayoutParams(width, height);
	}

	int hid = (int) (SystemClock.uptimeMillis() / 2);

	public View getView(String... type) {
		while (findViewById(++hid) != null)
			Log.w("ok", "getView catch " + type[0]);

		if (type[0].contentEquals("TextView")) {
			TextView x = new TextView(ctx);
			x.setId(hid);
			return (View) x;
		} else if (type[0].contentEquals("LinearLayout")) {
			LinearLayout x = new LinearLayout(ctx);
			x.setId(hid);
			return (View) x;
		} else if (type[0].contentEquals("RelativeLayout")) {
			RelativeLayout x = new RelativeLayout(ctx);
			x.setId(hid);
			return (View) x;
		} else if (type[0].contentEquals("ImageView")) {
			ImageView x = new ImageView(ctx);
			x.setId(hid);
			return (View) x;
		} else if (type[0].contentEquals("ScrollView")) {
			ScrollView x = new ScrollView(ctx);
			x.setId(hid);
			return (View) x;
		} else {
			Log.e("ok", "getView " + type[0]);
		}

		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Log.i("ok", "onCreateOptionsMenu " + menu.size());

		{
			MenuItem o3 = menu.add(1, 1, 1, "KAi");
			o3.setTitle("KAi");
			o3.setIcon(R.drawable.ic_launcher);
		}

		if (menu != null) {
			new MenuAsync().execute(menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		// Log.i("ok", "menu open " + (menu != null));

		if (menu != null) {
			new MenuAsync().execute(menu);
		}
		return super.onMenuOpened(featureId, menu);
	}


	@Override
	protected void onDestroy() {

		if (mAds != null && mAds.adbox != null) {
			mAds.adbox.destroy();
		}

		super.onDestroy();
	}



//	InRenderer mRen;
	private boolean mHome = true;
	private boolean mAtHome = false;
	float m1Ay = 0f; // 
	float m1Ax = 135f;
	float m1Az = 0f;
	float m1Cy = 650f;// +>>>> <<<<-
	float m1Cx = 280f;// +^^^^ vvvv-
	float m1Cz = -2135f;
	//InPurpose[] mPurpose;
	//float[] mPurposeU = new float[201];
	//int[] mA = new int[1001];
	int console = 1;
	int lathing = 0;
	boolean mAnPause = false;
	//InPartShape[][] mPart = new InPartShape[201][201];
	float mRAngleY = 0f;
	float mRAngleX = 50f;
	float mRAngleZ = 0;
	float mRecentY = -250f;
	float mRecentX = -230f;
	float mRecentZ = -1250f;
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	int mSelected = -1;
	//GLColor black = new GLColor(110, 110, 110);
	//GLColor red = new GLColor(210, 110, 0);
	//GLColor green = new GLColor(0, 210, 110);
	//GLColor blue = new GLColor(0, 110, 210);
	//GLColor yellow = new GLColor(210, 210, 0);
	//GLColor orange = new GLColor(210, 110, 110);
	//GLColor white = new GLColor(210, 210, 210);
	int GRAPH_SIZE = 82;
	int mGLworkid = -1;
	//World[] mWorlds = new World[201];
	boolean mgl = true;
	int orbitlimit = 1;
//private InRenderer mRenderer;

	int glsize = 201;

//	InPurpose[] mPurpose = new InPurpose[glsize];
	float[] mPurposeU = new float[glsize];

//	InPartShape[][] mPart = new InPartShape[glsize][glsize * 2];
//	World[] mWorlds = new World[glsize];
	int[] mA = new int[glsize];

	int one = 0x10000;
	int half = 0x08000;
//	GLColor black = new GLColor(0, 0, 0);
//	GLColor red = new GLColor(one, 0, 0);
//	GLColor green = new GLColor(0, one, 0);
//	GLColor blue = new GLColor(0, 0, one);
//	GLColor yellow = new GLColor(one, one, 0);
//	GLColor orange = new GLColor(one, half, 0);
//	GLColor white = new GLColor(one, one, one);
	float skylimit = 659000f;



	private Handler logoly = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bx = msg.getData();
			int l = bx.getInt("l");
			String text = bx.getString("text");
			switch (l) {
			case 2:
				Log.e(G, ":" + text);
				break;
			case 3:
				Log.w(G, ":" + text);
				break;
			default:
				Log.i(G, ":" + text);
				break;
			}
		}
	};




	private Handler mToastHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Log.i(TAG,"mToastHandler()");
			Bundle b = msg.getData();
			String message = b.getString("message");
			Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
		}
		/*
		 * Message msg = new Message(); Bundle b = new Bundle();
		 * b.putString("message", "Mode " + mode); msg.setData(b);
		 * mToastHandler.sendMessage(msg); //
		 */

	};





}
