package com.muslim.sholatreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import java.util.Map;

public class PrayerScheduler {

    public static void scheduleAll(Context context, Map<String, String> prayerTimes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        int requestCode = 1000;
        for (Map.Entry<String, String> entry : prayerTimes.entrySet()) {
            String name = entry.getKey();
            String time = entry.getValue(); // format "HH:mm"

            try {
                String[] parts = time.split(":");
                int hour = Integer.parseInt(parts[0].trim());
                int minute = Integer.parseInt(parts[1].trim());

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute - 5);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // Jika waktu sudah lewat hari ini, jadwalkan besok
                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                Intent intent = new Intent(context, PrayerAlarmReceiver.class);
                intent.setAction("com.muslim.sholatreminder.PRAYER_ALARM");
                intent.putExtra("prayer_name", name);
                intent.putExtra("prayer_time", time);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        requestCode++,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                }

                Log.d("PrayerScheduler", "Alarm dijadwalkan: " + name + " pada " + calendar.getTime());

            } catch (Exception e) {
                Log.e("PrayerScheduler", "Gagal jadwalkan " + name + ": " + e.getMessage());
            }
        }
    }
}
