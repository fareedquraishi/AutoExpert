# Petronas Auto Expert Centre — Android App
### Brand Ambassador Field App · Native Kotlin + Jetpack Compose

---

## 📱 App Overview

A fully native Android app for Petronas Auto Expert Centre Brand Ambassadors.  
Built with **Kotlin + Jetpack Compose**, offline-first with **Room**, syncing to **Supabase**.

---

## ✨ Features

| Feature | Details |
|---|---|
| 🔐 PIN Login | 6-digit PIN with shake animation on wrong entry |
| ☝️ Biometric | Fingerprint / Face ID after first PIN login — registers automatically |
| 🔑 PIN Reset | BA taps "Forgot PIN" → message sent to Admin inbox |
| 🏠 Home Dashboard | KPI cards: Reach, Litres, Commission, Attendance with live progress bars |
| ➕ Customer Entry | 3-step flow: Info → Products (3-col grid, scales to 15+) → Confirm |
| 📦 Offline Queue | Room database saves all entries when offline; syncs automatically when connected |
| 📍 GPS Attendance | Geofence triggers auto-attendance when BA arrives within 200m of their station |
| 💬 Instant Messaging | Real-time chat with Admin. Payout/Leave notifications appear inline |
| 🔔 Push Notifications | FCM for: new message, payout received, leave approved/rejected |
| 💰 Wallet | FIFO commission breakdown — oldest days paid first |
| 👤 Profile | Attendance stats, leave requests, sign-out |
| 📢 Notice Board | Admin notices with unread badges |
| 🔄 Background Sync | WorkManager syncs every 15 minutes when online |

---

## 🏗️ Architecture

```
app/
├── data/
│   ├── local/
│   │   ├── entity/     ← Room entities (11 tables)
│   │   ├── dao/        ← Room DAOs with Flow queries
│   │   └── db/         ← AppDatabase
│   ├── remote/
│   │   ├── api/        ← Retrofit SupabaseApi interface
│   │   └── model/      ← Remote DTOs
│   └── repository/     ← (extend here for clean arch)
├── di/                 ← Hilt modules (App, Gson, Location)
├── service/
│   ├── SyncWorker.kt              ← WorkManager offline sync
│   ├── AttendanceGeofenceService  ← GPS auto-attendance
│   ├── GeofenceBroadcastReceiver  ← Geofence trigger handler
│   └── AutoExpertFirebaseService  ← FCM push notifications
├── ui/
│   ├── splash/         ← Animated splash screen
│   ├── login/          ← PIN pad + biometric
│   ├── home/           ← Dashboard + KPIs
│   ├── customers/      ← New entry flow + customer list
│   ├── messaging/      ← Admin chat
│   ├── wallet/         ← Commission/payout FIFO
│   ├── profile/        ← Attendance, leaves, logout
│   ├── notices/        ← Notice board
│   ├── navigation/     ← NavGraph + Routes
│   ├── components/     ← Shared composables
│   └── theme/          ← Petronas Material 3 theme
└── util/
    ├── SessionManager.kt      ← DataStore session
    ├── NetworkConnectivity.kt ← Online/offline Flow
    └── DateUtils.kt           ← Date helpers
```

---

## 🚀 Setup Instructions

### 1. Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35

### 2. Clone & Open
```bash
git clone <your-repo>
# Open in Android Studio → File → Open → select AutoExpertApp/
```

### 3. Configure Supabase
In `app/build.gradle.kts`, update:
```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://YOUR_PROJECT.supabase.co\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"YOUR_ANON_KEY\"")
```

### 4. Add Firebase
- Go to [Firebase Console](https://console.firebase.google.com)
- Create project → Add Android app (package: `com.autoexpert.app`)
- Download `google-services.json` → place in `app/` directory
- Enable **Cloud Messaging (FCM)** in Firebase Console

### 5. Add Logo Assets
Place these files in `app/src/main/res/drawable/`:
- `euro_logo.png` — Euro logo (transparent background, ~120×120px)
- `petronas_logo.png` — Petronas logo (transparent background, ~300×200px)

> Transparent-background versions have been processed from your uploaded logos.  
> Export the processed PNGs from `PAEC_App_Design_V2.html` or use the `/home/claude/` files.

### 6. Messages Table in Supabase
Add a `messages` table to your Supabase project:
```sql
create table messages (
  id uuid primary key default gen_random_uuid(),
  sender_id text not null,
  sender_name text not null,
  receiver_id text not null,
  body text not null,
  is_read boolean default false,
  created_at timestamptz default now()
);

-- Enable RLS (open for MVP)
alter table messages enable row level security;
create policy "Open access" on messages for all using (true);

-- Real-time
alter publication supabase_realtime add table messages;
```

### 7. FCM → Supabase Edge Function (for push notifications)
To send push when Admin sends a message, create a Supabase Edge Function:
```typescript
// supabase/functions/notify-ba/index.ts
import { serve } from "https://deno.land/std/http/server.ts"

serve(async (req) => {
  const { ba_id, title, body, type } = await req.json()
  // Fetch FCM token for ba_id from your ba_fcm_tokens table
  // POST to FCM v1 API
  // ...
})
```

### 8. Geofence / Attendance
Station GPS coordinates are fetched from `stations` table.  
Add `latitude`, `longitude`, `geofence_radius_m` columns if not present:
```sql
alter table stations add column if not exists latitude double precision;
alter table stations add column if not exists longitude double precision;
alter table stations add column if not exists geofence_radius_m integer default 200;
```

---

## 📦 Key Dependencies

| Library | Purpose |
|---|---|
| Jetpack Compose BOM 2024.08 | UI framework |
| Room 2.6.1 | Offline SQLite database |
| Hilt 2.51.1 | Dependency injection |
| Retrofit 2.11 + OkHttp 4.12 | Supabase REST API |
| Firebase BOM 33 | Push notifications (FCM) |
| WorkManager 2.9.1 | Background sync |
| Google Play Services Location 21.3 | GPS + Geofencing |
| DataStore Preferences 1.1.1 | Session storage |
| Biometric | Fingerprint / Face ID |
| Coil 2.7 | Image loading |
| Core SplashScreen 1.0.1 | Android 12+ splash |

---

## 🎨 Design System

| Color | Hex | Usage |
|---|---|---|
| Petronas Green | `#00A86B` | Primary, CTA buttons, active states |
| Petronas Dark | `#007A4D` | Gradients, text |
| Dark BG | `#0A1628` | Splash, login, headers |
| Accent Red | `#DC2626` | Errors, delete, rejected |
| Accent Amber | `#D97706` | Existing, pending |
| Accent Blue | `#2563EB` | Repeat buyers |
| Accent Purple | `#7C3AED` | Applicator |

Font: **Plus Jakarta Sans** (Google Fonts) — weights 400–900

---

## 🔐 Biometric Registration Flow

1. BA logs in with PIN successfully for the first time
2. App prompts: "Register fingerprint for faster login?"
3. BiometricPrompt triggers — BA scans finger
4. On success, PIN is stored encrypted in SharedPreferences
5. Next launch: BA taps fingerprint icon → authenticated → PIN auto-submitted

---

## 📲 SMS via Device (PIN Reset & Admin Messages)

The app can send SMS directly using Android's `SmsManager`:
```kotlin
// In LoginViewModel.sendPinResetRequest() — extend to also SMS:
val smsManager = SmsManager.getDefault()
smsManager.sendTextMessage(
    ADMIN_PHONE_NUMBER,    // Set in BuildConfig
    null,
    "PIN Reset Request from $baName",
    null, null
)
```
Add to Manifest: `<uses-permission android:name="android.permission.SEND_SMS"/>`

---

## 🏗️ Build & Run

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

---

## 📋 Supabase Tables Required

| Table | Purpose |
|---|---|
| `brand_ambassadors` | BA profiles + PIN |
| `stations` | Station info + GPS coords |
| `skus` | Products / lubricants |
| `vehicle_types` | Motorcycle, Car, etc. |
| `competitor_brands` | Shell, Castrol, etc. |
| `sale_entries` | Customer visit records |
| `sale_entry_items` | Products per visit |
| `attendance` | Daily attendance records |
| `notices` | Admin notices to BAs |
| `messages` | BA ↔ Admin chat |
| `daily_payouts` | Commission FIFO ledger |
| `leave_requests` | Leave applications |
| `targets` | BA / Station / Company targets |

---

*Built for Petronas Auto Expert Centre · Powered by Fintectual Pvt Ltd*
