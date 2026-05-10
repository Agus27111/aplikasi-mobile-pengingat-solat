package com.muslim.sholatreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class PrayerAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("PrayerAlarmReceiver", "Received: " + action);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Re-schedule alarms setelah reboot
            rescheduleAfterBoot(context);
            return;
        }

        if ("com.muslim.sholatreminder.PRAYER_ALARM".equals(action)) {
            String prayerName = intent.getStringExtra("prayer_name");
            String prayerTime = intent.getStringExtra("prayer_time");

            // Wake up screen
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                    "SholatReminder::AlarmWakeLock"
            );
            wl.acquire(3000);

            // Launch full screen activity
            Intent fullScreenIntent = new Intent(context, FullScreenAlertActivity.class);
            fullScreenIntent.putExtra("prayer_name", prayerName);
            fullScreenIntent.putExtra("prayer_time", prayerTime);
            fullScreenIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            );
            context.startActivity(fullScreenIntent);

            wl.release();
        }
    }

    private void rescheduleAfterBoot(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String[] names = {"Subuh", "Dzuhur", "Ashar", "Maghrib", "Isya"};
        Map<String, String> prayerTimes = new LinkedHashMap<>();

        for (String name : names) {
            String time = prefs.getString("time_" + name, "");
            if (!time.isEmpty()) {
                prayerTimes.put(name, time);
            }
        }

        if (!prayerTimes.isEmpty()) {
            PrayerScheduler.scheduleAll(context, prayerTimes);
            Log.d("PrayerAlarmReceiver", "Alarms re-scheduled after boot");
        }
    }
}
