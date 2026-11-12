#!/usr/bin/env bash
# One-time setup for the ProofNest Flutter app (macOS / Linux). Safe to run again.
set -e
cd "$(dirname "$0")"

step() { printf '\n==> %s\n' "$1"; }

step "Checking that Flutter is installed"
if ! command -v flutter >/dev/null 2>&1; then
  echo "Flutter was not found on your PATH. Install it first (see SETUP_WINDOWS.md for the steps), then run this again."
  exit 1
fi
flutter --version

step "Creating the Android and iOS project folders (first run only)"
if [ ! -d android ]; then
  flutter create --project-name proofnest --org com.proofnest --platforms=android,ios .
else
  echo "android/ already exists - skipping."
fi

step "Applying ProofNest platform settings"
dart tool/setup_platforms.dart

step "Downloading packages"
flutter pub get

step "Finding this computer's address for a real phone"
IP=""
if command -v ipconfig >/dev/null 2>&1; then
  IP="$(ipconfig getifaddr en0 2>/dev/null || true)"
elif command -v hostname >/dev/null 2>&1; then
  IP="$(hostname -I 2>/dev/null | awk '{print $1}' || true)"
fi
if [ -n "$IP" ]; then
  printf '{\n  "API_BASE_URL": "http://%s:8080"\n}\n' "$IP" > config/phone.json
  echo "config/phone.json now points to http://$IP:8080 (edit it if this is not your Wi-Fi address)."
else
  echo "Could not detect the address. Edit config/phone.json yourself."
fi

step "Checking the Flutter toolchain"
flutter doctor

printf '\nSetup finished. Next: start the ProofNest server, then run the app (see README.md).\n'
