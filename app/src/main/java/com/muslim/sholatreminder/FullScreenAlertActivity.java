package com.muslim.sholatreminder;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class FullScreenAlertActivity extends AppCompatActivity {

    // Kumpulan Hadis tentang Sholat Berjamaah
    private static final String[][] HADIS_LIST = {
        {
            "Keutamaan Sholat Berjamaah",
            "Sholat berjamaah lebih utama dari sholat sendirian sebanyak dua puluh tujuh derajat.",
            "HR. Bukhari & Muslim"
        },
        {
            "Sholat Isya & Subuh Berjamaah",
            "Barangsiapa yang sholat Isya berjamaah, seolah-olah ia sholat separuh malam. Dan barangsiapa yang sholat Subuh berjamaah, seolah-olah ia sholat semalam suntuk.",
            "HR. Muslim"
        },
        {
            "Pahala Langkah Menuju Masjid",
            "Manusia yang paling besar pahalanya dalam sholat adalah yang paling jauh jalannya (ke masjid). Orang yang menunggu sholat hingga melaksanakannya bersama imam lebih besar pahalanya daripada orang yang sholat sendirian kemudian tidur.",
            "HR. Bukhari & Muslim"
        },
        {
            "Sholat Berjamaah di Masjid",
            "Sholat seseorang bersama orang lain lebih baik daripada sholat sendirian. Dan sholat bersama dua orang lebih baik daripada sholat sendirian. Semakin banyak, semakin dicintai Allah.",
            "HR. Abu Dawud"
        },
        {
            "Malaikat Bershalawat untuk Jamaah",
            "Para malaikat senantiasa bershalawat kepada salah seorang di antara kalian selama ia berada di tempat shalatnya dan belum berhadats, mereka berkata: 'Ya Allah, ampunilah dia. Ya Allah, rahmatilah dia.'",
            "HR. Bukhari"
        },
        {
            "Anjuran Sholat Berjamaah",
            "Demi Allah yang jiwaku ada di tangan-Nya, sungguh aku ingin memerintahkan agar kayu bakar dikumpulkan, kemudian aku perintahkan agar sholat didirikan, lalu dikumandangkan adzan, kemudian aku perintahkan seseorang mengimami orang-orang, lalu aku pergi menemui orang-orang yang tidak menghadiri sholat berjamaah dan aku bakar rumah mereka.",
            "HR. Bukhari & Muslim"
        },
        {
            "Sholat Berjamaah Menghapus Dosa",
            "Tidaklah seorang Muslim berwudhu dengan sempurna kemudian berangkat ke masjid, tidak ada yang mendorongnya kecuali sholat, melainkan Allah akan menyambut kedatangannya dengan wajah berseri, sebagaimana seorang yang bepergian disambut oleh keluarganya yang ditinggalkan.",
            "HR. Ibnu Majah"
        },
        {
            "Niat Sholat Berjamaah",
            "Barangsiapa bersuci di rumahnya kemudian berjalan ke salah satu rumah Allah untuk menunaikan salah satu kewajiban Allah, maka langkah-langkahnya — satu langkah menghapus dosa dan satu langkah lainnya mengangkat derajat.",
            "HR. Muslim"
        },
        {
            "Keutamaan Shaf Pertama",
            "Seandainya manusia mengetahui apa yang terdapat dalam adzan dan shaf pertama, kemudian mereka tidak bisa mendapatkannya kecuali dengan undian, niscaya mereka akan berundi untuk mendapatkannya.",
            "HR. Bukhari & Muslim"
        },
        {
            "Sholat Lima Waktu Berjamaah",
            "Lima sholat dan sholat Jumat ke Jumat berikutnya adalah penebus dosa-dosa yang ada di antara keduanya, selama dosa-dosa besar dijauhi.",
            "HR. Muslim"
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tampilkan di atas layar kunci & nyalakan layar
        setupWindowFlags();

        setContentView(R.layout.activity_fullscreen_alert);

        String prayerName = getIntent().getStringExtra("prayer_name");
        String prayerTime = getIntent().getStringExtra("prayer_time");

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvPrayerName = findViewById(R.id.tvPrayerName);
        TextView tvCountdown = findViewById(R.id.tvCountdown);
        TextView tvHadisTitle = findViewById(R.id.tvHadisTitle);
        TextView tvHadisContent = findViewById(R.id.tvHadisContent);
        TextView tvHadisSource = findViewById(R.id.tvHadisSource);
        Button btnDismiss = findViewById(R.id.btnDismiss);

        // Set informasi sholat
        tvPrayerName.setText("🕌 Waktu " + prayerName);
        tvCountdown.setText("⏰ Adzan " + prayerName + " dalam 5 menit lagi\n🕐 " + prayerTime + " WIB");

        // Pilih hadis secara acak
        Random random = new Random();
        String[] hadis = HADIS_LIST[random.nextInt(HADIS_LIST.length)];
        tvHadisTitle.setText("📖 " + hadis[0]);
        tvHadisContent.setText("\"" + hadis[1] + "\"");
        tvHadisSource.setText("— " + hadis[2]);

        // Vibrate
        vibratePhone();

        // Tombol tutup
        btnDismiss.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        });

        // Hide system UI untuk full screen
        hideSystemUI();
    }

    private void setupWindowFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                keyguardManager.requestDismissKeyguard(this, null);
            }
        } else {
            getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        } else {
            decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
    }

    private void vibratePhone() {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 500, 300, 500, 300, 500};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    vibrator.vibrate(pattern, -1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // Blokir tombol back agar tidak ditutup sembarangan
        // User harus tekan tombol "Tutup"
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }
}
