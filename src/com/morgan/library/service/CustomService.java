package com.morgan.library.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.morgan.library.R;
import com.morgan.library.utils.Logger;

public class CustomService extends Service {

    private static final String TAG = CustomService.class.getName();

    private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
    private static final Class<?>[] mStartForegroundSignature = new Class[] { int.class, Notification.class };
    private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

    private NotificationManager mNM;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

    public static final int mNotificationId = 0x0002;
    private static final String PACKAGE = "";
    private static final String LAUNCHCLASS = "";

    private void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            // Should not happen.
            Logger.e(TAG, "Unable to invoke method", e);
        } catch (IllegalAccessException e) {
            // Should not happen.
            Logger.e(TAG, "Unable to invoke method", e);
        }
    }

    /**
     * This is a wrapper around the new startForeground method, using the older APIs if it is not available.
     */
    private void startForegroundCompat(int id, Notification notification) {
        // If we have the new startForeground API, then use it.
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            invokeMethod(mStartForeground, mStartForegroundArgs);
        } else {
            // Fall back on the old API.
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNM.notify(id, notification);
        }

    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_launcher).setContentTitle(getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_name));
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(false);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(PACKAGE, LAUNCHCLASS));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(contentIntent);

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.contentIntent = contentIntent;
        return notification;
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older APIs if it is not available.
     */
    private void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mStopForeground, mStopForegroundArgs);
        } else {
            // Fall back on the old API. Note to cancel BEFORE changing the
            // foreground state, since we could be killed at that point.
            mNM.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        }

    }

    @Override
    public void onCreate() {
        initForegroundCompat();
        startForegroundCompat(mNotificationId, createNotification());
    }

    private void initForegroundCompat() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
            return;
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
        }
        try {
            mSetForeground = getClass().getMethod("setForeground", mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
        }
    }

    @Override
    public void onDestroy() {
        // Make sure our notification is gone.
        stopForegroundCompat(mNotificationId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
