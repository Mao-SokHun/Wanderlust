# Google + Facebook login setup

Wanderlust supports **Google** and **Facebook** sign-in (Apple removed). Guests still browse without an account; social login is for saving places.

## 1) Android `Wanderlust/local.properties`

```properties
GOOGLE_WEB_CLIENT_ID=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
FACEBOOK_APP_ID=your_facebook_app_id
FACEBOOK_CLIENT_TOKEN=your_facebook_client_token
```

### Google
1. [Google Cloud Console](https://console.cloud.google.com/) → APIs & Services → Credentials  
2. Create **OAuth client ID** type **Web application** (this ID is used as `serverClientId`)  
3. Also create an **Android** OAuth client with your app package `com.example.wanderlust` + SHA-1  
4. Put the **Web** client ID into `GOOGLE_WEB_CLIENT_ID`  
5. On Render / API `.env`: `GOOGLE_CLIENT_IDS=` same Web client ID (comma-separated if several)

### Facebook
1. [developers.facebook.com](https://developers.facebook.com/) → Create app → Facebook Login  
2. Add Android platform: package `com.example.wanderlust`, key hashes  
3. Copy **App ID** + **Client token** into `local.properties`  
4. On Render: `FACEBOOK_APP_ID` + `FACEBOOK_APP_SECRET` (secret validates tokens)

## 2) Rebuild the app

After editing `local.properties`, sync Gradle and install a new APK.

## API

`POST /api/auth/social`

```json
{ "provider": "google", "idToken": "..." }
{ "provider": "facebook", "accessToken": "..." }
```
