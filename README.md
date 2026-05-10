# 🕌 Pengingat Sholat - Panduan Build APK

## Fitur Aplikasi
- ✅ Pop-up layar penuh 5 menit sebelum adzan
- ✅ Menampilkan hadis tentang sholat berjamaah secara acak (10 hadis)
- ✅ Menggunakan API AlAdhan.com (gratis, tanpa daftar)
- ✅ Mendukung semua kota di seluruh dunia
- ✅ Alarm tetap aktif setelah HP di-restart
- ✅ Membangunkan layar dari mode tidur
- ✅ Desain Islami (tema gelap emas)

## Cara Build APK

### Metode 1: Android Studio (Direkomendasikan)

1. **Install Android Studio**
   - Download: https://developer.android.com/studio
   - Install dan buka Android Studio

2. **Buka Project**
   - File → Open → pilih folder `SholatReminder`
   - Tunggu Gradle sync selesai (butuh internet)

3. **Build APK**
   - Menu: Build → Build Bundle(s) / APK(s) → Build APK(s)
   - APK tersimpan di: `app/build/outputs/apk/debug/app-debug.apk`

4. **Install ke HP**
   - Transfer APK ke HP via kabel USB atau WhatsApp ke diri sendiri
   - Aktifkan "Install dari sumber tidak dikenal" di pengaturan HP
   - Tap file APK untuk install

### Metode 2: Command Line (Gradle)

```bash
# Masuk ke folder project
cd SholatReminder

# Build debug APK
./gradlew assembleDebug

# APK ada di:
# app/build/outputs/apk/debug/app-debug.apk
```

## Cara Pakai Aplikasi

1. **Buka app** → masukkan nama kota (contoh: Indramayu) dan negara (Indonesia)
2. **Tekan "Ambil Jadwal Sholat"** → jadwal akan muncul
3. **Tekan "Aktifkan Izin Notifikasi"** → izinkan semua izin yang diminta:
   - Izin notifikasi
   - Alarm tepat waktu
   - Tampil di atas layar lain (overlay)
4. **Selesai!** Pop-up otomatis muncul 5 menit sebelum setiap adzan

## Izin yang Dibutuhkan
| Izin | Fungsi |
|------|--------|
| INTERNET | Ambil jadwal dari API |
| SCHEDULE_EXACT_ALARM | Alarm tepat waktu |
| USE_FULL_SCREEN_INTENT | Pop-up layar penuh |
| POST_NOTIFICATIONS | Notifikasi |
| WAKE_LOCK | Nyalakan layar saat alarm |
| RECEIVE_BOOT_COMPLETED | Re-jadwalkan setelah restart |

## API yang Digunakan
- **AlAdhan API**: https://api.aladhan.com
- Gratis, tidak butuh API key
- Method 11 = Kementerian Agama Indonesia

## Daftar Hadis (10 Hadis Acak)
1. Sholat berjamaah 27 derajat lebih utama (HR. Bukhari & Muslim)
2. Sholat Isya & Subuh berjamaah (HR. Muslim)
3. Pahala langkah menuju masjid (HR. Bukhari & Muslim)
4. Semakin banyak jamaah semakin dicintai Allah (HR. Abu Dawud)
5. Malaikat bershalawat untuk jamaah (HR. Bukhari)
6. Anjuran keras sholat berjamaah (HR. Bukhari & Muslim)
7. Sholat berjamaah menghapus dosa (HR. Ibnu Majah)
8. Pahala berjalan ke masjid (HR. Muslim)
9. Keutamaan shaf pertama (HR. Bukhari & Muslim)
10. Sholat lima waktu sebagai penebus dosa (HR. Muslim)

## Struktur File
```
SholatReminder/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/muslim/sholatreminder/
│   │   ├── MainActivity.java          ← Layar utama + fetch API
│   │   ├── FullScreenAlertActivity.java ← Pop-up layar penuh
│   │   ├── PrayerAlarmReceiver.java   ← Penerima alarm
│   │   ├── PrayerScheduler.java       ← Penjadwal alarm
│   │   └── PrayerReminderService.java ← Service background
│   └── res/
│       ├── layout/
│       │   ├── activity_main.xml
│       │   └── activity_fullscreen_alert.xml
│       ├── drawable/ (background, card styles)
│       └── values/ (strings, themes)
└── README.md
```

---
*Semoga aplikasi ini membantu meningkatkan ibadah. Barakallahu fiikum!*
