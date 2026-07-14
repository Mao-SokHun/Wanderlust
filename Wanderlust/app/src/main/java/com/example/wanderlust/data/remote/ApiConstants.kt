package com.example.wanderlust.data.remote

object ApiConstants {
    const val PORT = 3000

    /**
     * PC IPv4 on Wi‑Fi — run `ipconfig` on Windows, use "IPv4 Address" of Wi‑Fi adapter.
     * Phone and PC must be on the same Wi‑Fi network.
     */
    /** Your PC Wi‑Fi IPv4 from `ipconfig` (WiFi adapter). Update if you change network. */
    const val WIFI_PC_IP = "192.168.68.94"

    /** Fallback IPs (emulator LAN, old networks). */
    const val WIFI_PC_IP_ALT = "10.10.1.34"

    /**
     * Tried in order until one responds:
     * 1. USB + `adb reverse tcp:3000 tcp:3000`
     * 2. Android Emulator
     * 3. Wi‑Fi (same network as PC)
     */
    val CANDIDATE_BASE_URLS: List<String> = listOf(
        // Local first (fail fast if offline) — race still prefers first healthy;
        // last successful URL is remembered across launches.
        "http://127.0.0.1:$PORT/",
        "http://10.0.2.2:$PORT/",
        "http://$WIFI_PC_IP:$PORT/",
        "http://$WIFI_PC_IP_ALT:$PORT/",
        // Production (Render) — used on real phones / any network
        "https://wanderlust-api-dm3y.onrender.com/",
    ).distinct()
}
