package bts.co.id.employeepresences.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bts.co.id.employeepresences.Activity.View.MainActivity;
import bts.co.id.employeepresences.BroadcastReceiver.AlarmBroadcast;
import bts.co.id.employeepresences.EmployeePresencesApplication;
import bts.co.id.employeepresences.Manager.Log;
import bts.co.id.employeepresences.Model.ServicesLog;
import bts.co.id.employeepresences.R;

//import android.util.Log;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.message.BasicNameValuePair;

//import android.net.http.AndroidHttpClient;

public class xTrackerService extends Service {
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_LOG = 3;
    public static final int MSG_LOG_RING = 4;
    private static final String TAG = "TripTracker/Service";
    public static xTrackerService service;
    private static boolean isRunning = false;
    private static volatile PowerManager.WakeLock wakeLock;
    final ReentrantReadWriteLock updateLock = new ReentrantReadWriteLock();
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private final String updatesCache = "updates.cache";
    private final int MAX_RING_SIZE = 15;
    ArrayList<ServicesLog> mLogRing = new ArrayList<ServicesLog>();
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    ArrayList<List> mUpdates = new ArrayList<List>();
    EmployeePresencesApplication application;
    private NotificationManager nm;
    private Notification notification;
    private String freqString;
    private int freqSeconds;
    private String endpoint;
    private LocationListener locationListener;
    private AlarmManager alarmManager;
    private PendingIntent pendingAlarm;
    private AsyncTask httpPoster;
    private Notification.Builder builder;

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Services On Create");
        application = (EmployeePresencesApplication) getApplication();

        xTrackerService.service = this;

        endpoint = application.getGlobalManager().getSharedPreferencesManager().getEndpoint();
        freqSeconds = 0;
        freqString = null;

        freqString = application.getGlobalManager().getSharedPreferencesManager().getUpdateFreq();
        if (freqString != null && !freqString.equals("")) {
            try {
                Pattern p = Pattern.compile("(\\d+)(m|h|s)");
                Matcher m = p.matcher(freqString);
                m.find();
                freqSeconds = Integer.parseInt(m.group(1));
                if (m.group(2).equals("h"))
                    freqSeconds *= (60 * 60);
                else if (m.group(2).equals("m"))
                    freqSeconds *= 60;
            } catch (Exception e) {
            }
        }

        if (endpoint == null || endpoint.equals("")) {
            logText("invalid endpoint, stopping service");
            stopSelf();
        }

        if (freqSeconds < 1) {
            logText("invalid frequency (" + freqSeconds + "), stopping " +
                    "service");
            stopSelf();
        }

//		readCache();

        showNotification();

        isRunning = true;

		/* we're not registered yet, so this will just log to our ring buffer,
         * but as soon as the client connects we send the log buffer anyway */
        logText("service started, requesting location update every " +
                freqString);

		/* findAndSendLocation() will callback to this */
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.e("onLocationChanged " + location.getLatitude() + " & " + location.getLongitude());
//				sendLocation(location);
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

		/* we don't need to be exact in our frequency, try to conserve at least
         * a little battery */
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmBroadcast.class);
        pendingAlarm = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), freqSeconds * 1000, pendingAlarm);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (httpPoster != null)
            httpPoster.cancel(true);

        try {
            LocationManager locationManager = (LocationManager)
                    this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
        }

		/* kill persistent notification */
        nm.cancelAll();

        if (pendingAlarm != null)
            alarmManager.cancel(pendingAlarm);

        isRunning = false;
    }

	/* must be done inside of updateLock */
//	public void cacheUpdates() {
//		OutputStreamWriter cacheStream = null;
//
//		try {
//			FileOutputStream cacheFile = xTrackerService.this.openFileOutput(
//				updatesCache, Activity.MODE_PRIVATE);
//			cacheStream = new OutputStreamWriter(cacheFile, "UTF-8");
//
//			/* would be nice to just serialize mUpdates but it's not
//			 * serializable.  create a json array of json objects, each object
//			 * having each key/value pair of one location update. */
//
//			JSONArray ja = new JSONArray();
//
//			for (int i = 0; i < mUpdates.size(); i++) {
//				List<NameValuePair> pair = mUpdates.get(i);
//				JSONObject jo = new JSONObject();
//
//				for (int j = 0; j < pair.size(); j++) {
//					try {
//						jo.put(((NameValuePair)pair.get(j)).getName(),
//							pair.get(j).getValue());
//					}
//					catch (JSONException e) {
//					}
//				}
//
//				ja.put(jo);
//			}
//
//			cacheStream.write(ja.toString());
//			cacheFile.getFD().sync();
//		}
//		catch (IOException e) {
//			Log.w(TAG, e);
//		}
//		finally {
//			if (cacheStream != null) {
//				try {
//					cacheStream.close();
//				}
//				catch (IOException e) {
//				}
//			}
//		}
//	}

	/* read json cache into mUpdates */
//	public void readCache() {
//		updateLock.writeLock().lock();
//
//		InputStreamReader cacheStream = null;
//
//		try {
//			FileInputStream cacheFile = xTrackerService.this.openFileInput(
//				updatesCache);
//			StringBuffer buf = new StringBuffer("");
//			byte[] bbuf = new byte[1024];
//			int len;
//
//			while ((len = cacheFile.read(bbuf)) != -1)
//				buf.append(new String(bbuf));
//
//			JSONArray ja = new JSONArray(new String(buf));
//
//			mUpdates = new ArrayList<List>();
//
//			for (int j = 0; j < ja.length(); j++) {
//				JSONObject jo = ja.getJSONObject(j);
//
//				List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
//
//				Iterator<String> i = jo.keys();
//				while (i.hasNext()) {
//					String k = (String)i.next();
//					String v = jo.getString(k);
//
//					nvp.add(new BasicNameValuePair(k, v));
//				}
//
//				mUpdates.add(nvp);
//			}
//
//			if (mUpdates.size() > 0)
//				logText("read " + mUpdates.size() + " update" +
//					(mUpdates.size() == 1 ? "" : "s") + " from cache");
//		}
//		catch (JSONException e) {
//		}
//		catch (FileNotFoundException e) {
//		}
//		catch (IOException e) {
//			Log.w(TAG, e);
//		}
//		finally {
//			if (cacheStream != null) {
//				try {
//					cacheStream.close();
//				}
//				catch (IOException e) {
//				}
//			}
//		}
//
//		updateLock.writeLock().unlock();
//	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("TrackerServices On Start Command");
        return START_STICKY;
    }

    /* called within wake lock from broadcast receiver, but assert that we have
     * it so we can keep it longer when we return (since the location request
     * uses a callback) and then free it when we're done running through the
     * queue */
    public void findAndSendLocation() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(
                    Context.POWER_SERVICE);

			/* we don't need the screen on */
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "triptracker");
            wakeLock.setReferenceCounted(true);
        }

        if (!wakeLock.isHeld())
            wakeLock.acquire();

        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                locationListener, null);
    }

    private void showNotification() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		notification = new Notification(R.mipmap.ic_launcher,
//			"Trip Tracker Started", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        getBuilder().setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Location is running")
                .setContentIntent(contentIntent);
        notification = builder.getNotification();
//		notification.setLatestEventInfo(this, "Trip Tracker",
//			"Sending location every " + freqString, contentIntent);
//		notification.flags = Notification.FLAG_ONGOING_EVENT;
        nm.notify(1, notification);
    }

    private void updateNotification(String text) {
        if (nm != null) {
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);

            getBuilder().setContentText(text).setContentIntent(contentIntent);

//			notification.setLatestEventInfo(this, "Trip Tracker", text,
//				contentIntent);
            notification.when = System.currentTimeMillis();
            nm.notify(1, notification);
        }
    }

    public void logText(String log) {
        ServicesLog lm = new ServicesLog(new Date(), log);
        mLogRing.add(lm);
        if (mLogRing.size() > MAX_RING_SIZE)
            mLogRing.remove(0);

        updateNotification(log);

        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                Bundle b = new Bundle();
                b.putString("log", log);
                Message msg = Message.obtain(null, MSG_LOG);
                msg.setData(b);
                mClients.get(i).send(msg);
            } catch (RemoteException e) {
				/* client is dead, how did this happen */
                mClients.remove(i);
            }
        }
    }

	/* flatten an array of NameValuePairs into an array of
	 * locations[0]latitude, locations[1]latitude, etc. */
//	public List<NameValuePair> getUpdatesAsArray() {
//		List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
//
//		for (int i = 0; i < mUpdates.size(); i++) {
//			List<NameValuePair> pair = mUpdates.get(i);
//
//			for (int j = 0; j < pair.size(); j++)
//				pairs.add(new BasicNameValuePair("locations[" + i + "][" +
//					((NameValuePair)pair.get(j)).getName() + "]",
//					pair.get(j).getValue()));
//		}
//
//		return pairs;
//	}

    public int getUpdatesSize() {
        return mUpdates.size();
    }

    public void removeUpdate(int i) {
        mUpdates.remove(i);
    }

//	private void sendLocation(Location location) {
//		List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
//		pairs.add(new BasicNameValuePair("time",
//			String.valueOf(location.getTime())));
//		pairs.add(new BasicNameValuePair("latitude",
//			String.valueOf(location.getLatitude())));
//		pairs.add(new BasicNameValuePair("longitude",
//			String.valueOf(location.getLongitude())));
//		pairs.add(new BasicNameValuePair("speed",
//			String.valueOf(location.getSpeed())));
//
//		/* push these pairs onto the queue, and only run the poster if another
//		 * one isn't running already (if it is, it will keep running through
//		 * the queue until it's empty) */
//		updateLock.writeLock().lock();
//		mUpdates.add(pairs);
//		int size = service.getUpdatesSize();
//		cacheUpdates();
//		updateLock.writeLock().unlock();
//
//		logText("location " +
//			(new DecimalFormat("#.######").format(location.getLatitude())) +
//			", " +
//			(new DecimalFormat("#.######").format(location.getLongitude())) +
//			(size <= 1 ? "" : " (" + size + " queued)"));
//
//		if (httpPoster == null ||
//		httpPoster.getStatus() == AsyncTask.Status.FINISHED)
//			(httpPoster = new HttpPoster()).execute();
//	}

    private Notification.Builder getBuilder() {
        if (this.builder == null) {
            this.builder = new Notification.Builder(this);
        }
        return this.builder;
    }

	/* Void as first arg causes a crash, no idea why
	E/AndroidRuntime(17157): Caused by: java.lang.ClassCastException: java.lang.Object[] cannot be cast to java.lang.Void[]
	*/
//	class HttpPoster extends AsyncTask<Object, Void, Boolean> {
//		@Override
//		protected Boolean doInBackground(Object... o) {
//			xTrackerService service = xTrackerService.service;
//
//			int retried = 0;
//			int max_retries = 4;
//
//			while (true) {
//				if (isCancelled())
//					return false;
//
//				boolean failed = false;
//
//				updateLock.writeLock().lock();
//				List<NameValuePair> pairs = service.getUpdatesAsArray();
//				int pairSize = service.getUpdatesSize();
//				updateLock.writeLock().unlock();
//
//				AndroidHttpClient httpClient =
//					AndroidHttpClient.newInstance("TripTracker");
//
//				try {
//					HttpPost post = new HttpPost(endpoint);
//					post.setEntity(new UrlEncodedFormEntity(pairs));
//					HttpResponse resp = httpClient.execute(post);
//
//					int httpStatus = resp.getStatusLine().getStatusCode();
//					if (httpStatus == 200) {
//						/* all good, we can remove everything we've sent from
//						 * the queue (but not just clear it, in case another
//						 * one jumped onto the end while we were here) */
//						updateLock.writeLock().lock();
//						for (int i = pairSize - 1; i >= 0; i--)
//							service.removeUpdate(i);
//						updateLock.writeLock().unlock();
//					}
//					else {
//						logText("POST failed to " + endpoint + ": got " +
//							httpStatus + " status");
//						failed = true;
//					}
//				}
//				catch (Exception e) {
//					logText("POST failed to " + endpoint + ": " + e);
//					Log.w(TAG, e);
//					failed = true;
//				}
//				finally {
//					if (httpClient != null)
//						httpClient.close();
//				}
//
//				if (failed) {
//					/* if our initial request failed, snooze for a bit and try
//					 * again, the server might not be reachable */
//					SystemClock.sleep(15 * 1000);
//
//					if (++retried > max_retries) {
//						/* give up since we're holding the wake lock open for
//						 * too long.  we'll get it next time, champ. */
//						logText("too many failures, retrying later (queue " +
//							"size " + service.getUpdatesSize() + ")");
//						break;
//					}
//				}
//				else
//					retried = 0;
//
//				int q = 0;
//				updateLock.writeLock().lock();
//				q = service.getUpdatesSize();
//				cacheUpdates();
//				updateLock.writeLock().unlock();
//
//				if (q == 0)
//					break;
//				/* otherwise, run through the rest of the queue */
//			}
//
//			return false;
//		}
//
//		protected void onPostExecute(Boolean b) {
//			if (wakeLock != null && wakeLock.isHeld())
//				wakeLock.release();
//		}
//	}

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);

				/* respond with our log ring to show what we've been up to */
                    try {
                        Message replyMsg = Message.obtain(null, MSG_LOG_RING);
                        replyMsg.obj = mLogRing;
                        msg.replyTo.send(replyMsg);
                    } catch (RemoteException e) {
                    }

                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }
}
