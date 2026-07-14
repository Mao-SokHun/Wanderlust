# Wanderlust — Cambodia Place Discovery App

Wanderlust helps users **discover and save places in Cambodia** (temples, beaches, mountains, cities, food). It is **not** a booking or payment app — inspiration and saved lists only.

Design reference: `Wanderlust.pdf` in this folder.

---

## Table of contents

1. [Architecture](#architecture)
2. [Tech stack](#tech-stack)
3. [Project structure](#project-structure)
4. [Prerequisites](#prerequisites)
5. [Database setup](#database-setup)
6. [Backend (API) setup](#backend-api-setup)
7. [Android app — build & run](#android-app--build--run)
8. [Connect phone to API](#connect-phone-to-api)
9. [Test accounts](#test-accounts)
10. [Main features](#main-features)
11. [API overview](#api-overview)
12. [Troubleshooting](#troubleshooting)

---

## Architecture

```
┌─────────────────┐     HTTP (port 3000)      ┌──────────────────┐     SQL      ┌──────────────┐
│  Android App    │ ────────────────────────► │  Node.js API     │ ───────────► │  PostgreSQL  │
│  (Jetpack       │   Retrofit + JSON         │  (Express)       │   pg Pool    │  wanderlust  │
│   Compose)      │                           │  backend/        │              │  database    │
└─────────────────┘                           └──────────────────┘              └──────────────┘
        │
        └── Room (local cache for tours when offline)
```

- The **phone never connects to PostgreSQL directly**.
- All user data (profile, favorites, auth) goes through the **REST API**.
- DB connection settings live in **`backend/.env`** (see [Database setup](#database-setup)).

---

## Tech stack

| Layer | Technologies |
|--------|----------------|
| **Android** | Kotlin, Jetpack Compose, Material 3, MVVM, Retrofit, Gson, Room, Coil |
| **Backend** | Node.js, Express, `pg`, bcrypt, JWT |
| **Database** | PostgreSQL |
| **Min Android** | API 26 (Android 8.0) |
| **Target / compile SDK** | 35 |

---

## Project structure

```
FinalProject/
├── README.md                 ← This file
├── Wanderlust.pdf            ← UI / design reference
├── Wanderlust/               ← Android application
│   ├── app/
│   │   └── src/main/java/com/example/wanderlust/
│   │       ├── MainActivity.kt
│   │       ├── data/         ← API, session, repositories
│   │       ├── viewmodel/
│   │       ├── navigation/
│   │       └── ui/
│   └── NAVIGATION.md         ← Screen flow notes
└── backend/                  ← REST API + database scripts
    ├── server.js             ← API entry point
    ├── db/
    │   ├── pool.js           ← PostgreSQL connection
    │   └── schema.sql        ← Tables (users, tours, favorites, …)
    ├── scripts/
    │   ├── setup-db.js       ← Create tables + seed users/tours
    │   └── seed-cambodia.js  ← Reload Cambodia tour data only
    ├── .env.example          ← Copy to .env
    └── package.json
```

---

## Prerequisites

Install before building:

| Tool | Purpose |
|------|---------|
| **Android Studio** (latest stable) | Build & run the app |
| **JDK 11+** | Kotlin / Gradle |
| **Node.js 18+** | Backend API |
| **PostgreSQL 14+** | Database |
| **adb** (Android SDK Platform-Tools) | USB debugging, `adb reverse` |

Optional: Git, Postman/curl for API testing.

---

## Database setup

### 1. Install PostgreSQL

Use [PostgreSQL for Windows](https://www.postgresql.org/download/windows/) or your OS package manager. Remember the **postgres user password** you set during install.

### 2. Create the database

Open **pgAdmin**, **psql**, or any SQL client:

```sql
CREATE DATABASE wanderlust;
```

### 3. Configure connection (`.env`)

In `backend/`, copy the example file and edit your credentials:

```bash
cd backend
copy .env.example .env    # Windows CMD
# or: cp .env.example .env   # macOS / Linux / PowerShell
```

Edit **`backend/.env`**:

```env
PGHOST=localhost
PGPORT=5432
PGUSER=postgres
PGPASSWORD=your_postgres_password
PGDATABASE=wanderlust

PORT=3000
JWT_SECRET=wanderlust-student-secret-change-in-production
```

Connection is implemented in **`backend/db/pool.js`** (reads these variables).

### 4. Create tables and sample data

```bash
cd backend
npm install
npm run db:setup
```

This runs `db/schema.sql` and inserts:

- Test users (see [Test accounts](#test-accounts))
- 18 Cambodia sample places in `tours`

To **only** refresh tour data later:

```bash
npm run db:seed-cambodia
```

---

## Backend (API) setup

```bash
cd backend
npm install
npm start
```

You should see:

```text
Connected to PostgreSQL: wanderlust
Wanderlust API: http://localhost:3000
Test login: user@test.com / 123456
Admin login: admin@test.com / admin123
```

Health check in a browser or terminal:

```bash
curl http://localhost:3000/
```

Expected: `{"message":"Wanderlust API is running","database":"PostgreSQL"}`

Keep this terminal open while using the app.

---

## Android app — build & run

### Option A — Android Studio (recommended)

1. Open Android Studio → **File → Open** → select the **`Wanderlust`** folder (not the whole `FinalProject` parent, unless your IDE expects the monorepo root).
2. Wait for **Gradle sync** to finish.
3. Start the backend (`npm start` in `backend/`).
4. Connect a device or start an **Android Emulator** (API 26+).
5. Click **Run** (green play) on module `app`.

### Option B — Command line

```bash
cd Wanderlust
# Windows
gradlew.bat assembleDebug
gradlew.bat installDebug

# macOS / Linux
./gradlew assembleDebug
./gradlew installDebug
```

Debug APK output:

`Wanderlust/app/build/outputs/apk/debug/app-debug.apk`

### Unit tests

```bash
cd Wanderlust
gradlew.bat test          # Windows
./gradlew test            # macOS / Linux
```

---

## Connect phone to API

The app tries these base URLs in order (`ApiConstants.kt`):

| Order | URL | When to use |
|-------|-----|-------------|
| 1 | `http://127.0.0.1:3000/` | Physical phone over **USB** + `adb reverse` |
| 2 | `http://10.0.2.2:3000/` | **Android Emulator** on same PC |
| 3 | `http://<PC_IP>:3000/` | **Wi‑Fi** — phone and PC on same network |

### USB (most reliable for demos)

With phone connected and USB debugging on:

```bash
adb reverse tcp:3000 tcp:3000
```

Then run the app. No need to change IP in code.

### Emulator

- Start `npm start` on the PC.
- Emulator uses `10.0.2.2` to reach the host machine automatically.

### Wi‑Fi

1. On Windows: `ipconfig` → note **IPv4** of your Wi‑Fi adapter (e.g. `10.10.1.34`).
2. Edit `Wanderlust/app/src/main/java/com/example/wanderlust/data/remote/ApiConstants.kt`:

   ```kotlin
   const val WIFI_PC_IP = "10.10.1.34"   // your PC IP
   ```

3. Phone and PC must be on the **same Wi‑Fi**.
4. Allow port **3000** through Windows Firewall if needed.

---

## Test accounts

| Role | Email | Password |
|------|--------|----------|
| User | `user@test.com` | `123456` |
| Admin | `admin@test.com` | `admin123` |

- **Guest:** tap **Get Started** on Welcome — preview **4 places** only.
- **Register:** each email can only be used **once** (stored in PostgreSQL).
- Debug builds pre-fill test login and offer **Fill test user** on the login screen.

---

## Main features

### All users (guest)

- Welcome, theme toggle, English / Khmer UI
- Browse home & explore (limited to 4 destinations without login)
- System back navigates through screens (does not exit immediately)

### Logged-in user

- Full catalog, search, categories, tour detail
- **Save / remove** favorites (synced to DB)
- Open location in **Google Maps**
- Profile: edit name & bio, settings (language, theme, notifications, location) — **saved to PostgreSQL**
- Change password, forgot / reset password
- My saved plans, help, privacy, terms, about

### Admin (`admin@test.com`)

- Admin dashboard, add / edit tours
- Manage users (list), analytics, export CSV preview

### Not in scope

- Real payments or bookings
- Google / Apple OAuth (buttons redirect to login / register only)

---

## API overview

Base URL: `http://localhost:3000` (or device-specific host above)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/` | No | Health check |
| POST | `/api/auth/login` | No | Login → JWT |
| POST | `/api/auth/register` | No | Register (unique email) |
| GET | `/api/auth/profile` | Bearer | Load user profile from DB |
| PUT | `/api/auth/profile` | Bearer | Update name, bio, language, theme, toggles |
| POST | `/api/auth/forgot-password` | No | Request reset token |
| POST | `/api/auth/reset-password` | No | Reset with token |
| PUT | `/api/auth/change-password` | Bearer | Change password |
| GET | `/api/tours` | Optional | List tours |
| GET/POST/DELETE | `/api/favorites` | Bearer | Saved places |
| GET/POST/PUT | `/api/admin/...` | Admin | Stats, tours, users, analytics |

### Database tables (`backend/db/schema.sql`)

| Table | Stores |
|-------|--------|
| `users` | Account, profile, preferences |
| `tours` | Cambodia place suggestions |
| `favorites` | User ↔ saved tour |
| `password_reset_tokens` | Password reset flow |

---

## Troubleshooting

| Problem | What to check |
|---------|----------------|
| **Cannot reach server** | `npm start` running? `adb reverse` for USB? Firewall? |
| **HTTP 401 on login** | Run `npm run db:setup`. Use exact test password `123456`. |
| **Email already registered** | Sign in instead, or use a new email. |
| **Database not connected** | PostgreSQL service running? `.env` password correct? DB `wanderlust` exists? |
| **Gradle sync failed** | JDK 11+, internet for dependencies, open `Wanderlust` folder. |
| **Cleartext / HTTP blocked** | App uses `network_security_config.xml` for local dev HTTP — rebuild after changes. |

### Reset database (destructive)

```bash
cd backend
# Drop and recreate DB in psql if needed, then:
npm run db:setup
```

---

## App info

- **App ID:** `com.example.wanderlust`  
- **Version:** 1.0 (`versionCode` 1)

For navigation flow between screens, see `Wanderlust/NAVIGATION.md`.

---

## Quick command cheat sheet

```bash
# Terminal 1 — API + DB
cd backend
npm install
npm run db:setup
npm start

# Terminal 2 — USB phone
adb reverse tcp:3000 tcp:3000

# Android Studio — Run Wanderlust/app
```
