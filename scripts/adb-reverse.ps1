# USB / wireless ADB: forward phone port 3000 to PC backend (npm start)
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    Write-Error "adb not found. Install Android SDK Platform-Tools in Android Studio."
    exit 1
}
& $adb devices
& $adb reverse tcp:3000 tcp:3000
Write-Host "OK: phone can use http://127.0.0.1:3000 while backend runs on PC."
& $adb reverse --list
