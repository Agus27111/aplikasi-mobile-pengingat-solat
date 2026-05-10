package com.muslim.sholatreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private EditText etCity, etCountry;
    private TextView tvStatus, tvPrayerTimes;
    private Button btnFetch, btnEnable;
    private SharedPreferences prefs;

    public static final String CHANNEL_ID = "sholat_reminder_channel";
    public static final String PREFS_NAME = "SholatPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        createNotificationChannel();

        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        tvStatus = findViewById(R.id.tvStatus);
        tvPrayerTimes = findViewById(R.id.tvPrayerTimes);
        btnFetch = findViewById(R.id.btnFetch);
        btnEnable = findViewById(R.id.btnEnable);

        String savedCity = prefs.getString("city", "Jakarta");
        String savedCountry = prefs.getString("country", "Indonesia");
        etCity.setText(savedCity);
        etCountry.setText(savedCountry);

        String savedTimes = prefs.getString("prayer_times_display", "");
        if (!savedTimes.isEmpty()) {
            tvPrayerTimes.setText(savedTimes);
            tvStatus.setText("✅ Pengingat aktif untuk: " + savedCity);
        }

        btnFetch.setOnClickListener(v -> {
            String city = etCity.getText().toString().trim();
            String country = etCountry.getText().toString().trim();
            if (city.isEmpty() || country.isEmpty()) {
                Toast.makeText(this, "Isi kota dan negara terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchPrayerTimes(city, country);
        });

        btnEnable.setOnClickListener(v -> {
            requestPermissions();
        });
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }

        Toast.makeText(this, "Izin diperiksa. Silakan aktifkan semua izin yang diminta.", Toast.LENGTH_LONG).show();
    }

    private void fetchPrayerTimes(String city, String country) {
        tvStatus.setText("⏳ Mengambil jadwal sholat...");
        btnFetch.setEnabled(false);

        new Thread(() -> {
            try {
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH) + 1;
                int year = cal.get(Calendar.YEAR);

                String urlStr = "https://api.aladhan.com/v1/timingsByCity/"
                        + day + "-" + month + "-" + year
                        + "?city=" + Uri.encode(city)
                        + "&country=" + Uri.encode(country)
                        + "&method=11";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject root = new JSONObject(sb.toString());
                JSONObject timings = root.getJSONObject("data").getJSONObject("timings");

                Map<String, String> prayerMap = new LinkedHashMap<>();
                prayerMap.put("Subuh", timings.getString("Fajr"));
                prayerMap.put("Dzuhur", timings.getString("Dhuhr"));
                prayerMap.put("Ashar", timings.getString("Asr"));
                prayerMap.put("Maghrib", timings.getString("Maghrib"));
                prayerMap.put("Isya", timings.getString("Isha"));

                StringBuilder display = new StringBuilder("📅 Jadwal Sholat Hari Ini:\n\n");
                for (Map.Entry<String, String> entry : prayerMap.entrySet()) {
                    display.append("🕌 ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                display.append("\n⏰ Pengingat 5 menit sebelum adzan aktif!");

                // Save to prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("city", city);
                editor.putString("country", country);
                editor.putString("prayer_times_display", display.toString());
                for (Map.Entry<String, String> e : prayerMap.entrySet()) {
                    editor.putString("time_" + e.getKey(), e.getValue());
                }
                editor.apply();

                // Schedule alarms
                PrayerScheduler.scheduleAll(this, prayerMap);

                runOnUiThread(() -> {
                    tvPrayerTimes.setText(display.toString());
                    tvStatus.setText("✅ Pengingat aktif untuk: " + city);
                    btnFetch.setEnabled(true);
                    Toast.makeText(this, "Jadwal berhasil dimuat & pengingat diatur!", Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvStatus.setText("❌ Gagal memuat jadwal: " + e.getMessage());
                    btnFetch.setEnabled(true);
                });
            }
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Sholat",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifikasi 5 menit sebelum adzan");
            channel.enableVibration(true);
            channel.setShowBadge(true);
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }
}
