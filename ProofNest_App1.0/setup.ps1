# One-time setup for the ProofNest Flutter Android app on Windows.
# Safe to run again: existing source files and platform configuration are preserved.
$ErrorActionPreference = 'Stop'
Set-Location -Path $PSScriptRoot

function Step([string]$text) {
    Write-Host "`n==> $text" -ForegroundColor Cyan
}
function Fail([string]$text) {
    Write-Host "`n$text" -ForegroundColor Red
    exit 1
}

Step 'Checking that this is the Flutter project root'
if (-not (Test-Path (Join-Path $PSScriptRoot 'pubspec.yaml'))) {
    Fail 'pubspec.yaml was not found. Run setup.bat from the folder that contains pubspec.yaml (the inner ProofNest_App folder).'
}
if (-not (Test-Path (Join-Path $PSScriptRoot 'lib\main.dart'))) {
    Fail 'lib\main.dart was not found. This folder does not look like the ProofNest Flutter project root.'
}

Step 'Checking Flutter installation'
if (-not (Get-Command flutter -ErrorAction SilentlyContinue)) {
    Fail 'Flutter was not found on PATH. Install Flutter, add its bin folder to PATH, reopen VS Code, and run setup.bat again.'
}
flutter --version
if ($LASTEXITCODE -ne 0) { Fail 'Flutter could not be started.' }

Step 'Creating the missing Android platform project'
if (-not (Test-Path (Join-Path $PSScriptRoot 'android'))) {
    Write-Host 'android/ is missing from the uploaded source ZIP. Generating the Android Gradle project now.' -ForegroundColor Yellow
    flutter create --no-pub --project-name proofnest --org com.proofnest --platforms=android .
    if ($LASTEXITCODE -ne 0) { Fail 'flutter create could not generate the Android project. Check flutter doctor -v and Android SDK Command-line Tools.' }
} else {
    Write-Host 'android/ already exists - keeping it.' -ForegroundColor Green
}

$manifest = Join-Path $PSScriptRoot 'android\app\src\main\AndroidManifest.xml'
if (-not (Test-Path $manifest)) {
    Fail "AndroidManifest.xml is missing at $manifest. The Android project was not generated correctly."
}

Step 'Applying ProofNest Android permissions and development network settings'
dart tool/setup_platforms.dart
if ($LASTEXITCODE -ne 0) { Fail 'Could not apply the Android platform settings.' }

Step 'Downloading Flutter packages'
flutter pub get
if ($LASTEXITCODE -ne 0) { Fail 'flutter pub get failed. Check your internet connection and the error above.' }

Step 'Checking the Android toolchain'
flutter doctor -v

Step 'Checking connected devices'
flutter devices

Write-Host "`nSetup completed." -ForegroundColor Green
Write-Host 'If Android toolchain reports missing cmdline-tools, open Android Studio > More Actions > SDK Manager > SDK Tools and install Android SDK Command-line Tools (latest).' -ForegroundColor Yellow
Write-Host 'Start the Pixel emulator in Android Studio Device Manager, then run:' -ForegroundColor Cyan
Write-Host 'flutter run --dart-define-from-file=config/emulator.json' -ForegroundColor White
Write-Host 'The app can open to its login screen without the backend. To log in and load data, the Spring Boot server must expose the /api/v1 mobile REST API.' -ForegroundColor Gray
