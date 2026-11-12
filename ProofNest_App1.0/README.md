# ProofNest Mobile App (Flutter)

**Tagline:** *Every Detail. Every Proof. One Place.*

ProofNest is an Android-first mobile application for landlords and tenants. Its purpose is to keep rental-property inspections, photographs, comparison records, findings, and related tenancy information organized around the correct property.

The full agreed project scope is documented in **[BRD.md](BRD.md)**. The current code is an early working-scope implementation, not every feature in the BRD yet. See **[IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md)** before assuming that a planned feature is already functional.

## Technology

- **Mobile:** Flutter / Dart
- **Current authentication:** Device-local prototype using `flutter_secure_storage`
- **Shared backend/database:** Not connected yet; Firebase is a future option
- **State management:** `provider`
- **Photo capture:** `image_picker`

## Project structure

```text
ProofNest_App/
├── BRD.md                       # Full mobile business requirements
├── IMPLEMENTATION_STATUS.md     # What this code currently implements
├── SETUP_WINDOWS.md             # Windows + Android emulator setup
├── lib/
│   ├── core/                    # Theme, config, formatting, secure storage, helpers
│   ├── data/                    # Local-only data provider
│   ├── models/                  # API response/domain models
│   ├── screens/                 # Screens grouped by feature
│   │   ├── auth/
│   │   ├── comparison/
│   │   ├── findings/
│   │   ├── inspections/
│   │   └── properties/
│   ├── state/                   # Authentication/session state
│   ├── widgets/                 # Shared UI components
│   └── main.dart                # App entry point
├── test/                        # Model and widget tests
├── tool/                        # Platform configuration helper
├── setup.bat / setup.ps1        # Windows first-time setup
└── pubspec.yaml
```

This is a small layered structure: feature screens stay grouped under `screens/`, while shared UI, data access, models, and session state are separated. Keep this structure for the current MVP rather than moving files before the first successful build. New BRD modules should be added as feature folders (for example `screens/chat/`, `screens/maintenance/`, and `screens/handover/`) with corresponding API/model additions.

## First-time setup on Windows

> The current workspace includes the generated `android/` platform folder. If it is missing in another copy, run the setup script to generate it.

1. Open the **inner project folder** in VS Code — the folder that contains `pubspec.yaml` and `lib/main.dart`.
2. Confirm Flutter is available: `flutter --version`.
3. In Android Studio, open **SDK Manager → SDK Tools** and install:
   - Android SDK Command-line Tools (latest)
   - Android SDK Platform-Tools
   - Android SDK Build-Tools
   - Android Emulator
4. Start a Pixel virtual device in Android Studio Device Manager.
5. In VS Code PowerShell, run:

```powershell
cd D:\development\Portfolio\ProofNest_App1.0
.\setup.bat
```

If your project is stored elsewhere, `cd` to the folder that actually contains `pubspec.yaml` before running `setup.bat`.

The script creates `android/` if missing, applies the required platform settings, runs `flutter pub get`, and prints Flutter/device diagnostics. It is safe to run again.

## Run on the Android emulator

Start the emulator first, then from the project root run:

```powershell
flutter devices
flutter run
```

## Current local-only mode

Registration and sign-in use accounts saved securely on this device. You can open the dashboard and navigate the main tabs without Java or a server. Accounts are not shared across devices and this local prototype is not production authentication.

The navigation drawer includes Chat, Tenants, Settings, and Sign out. Chat and tenants currently show empty states because shared conversations and property membership need a Firebase data layer. The dashboard and properties, inspections, and findings lists are also empty; shared property, inspection, evidence, comparison, and finding actions are not available yet.

## Verify the project

Run these from the folder containing `pubspec.yaml`:

```powershell
flutter pub get
flutter analyze
flutter test
```

Then run the app:

```powershell
flutter run
```

## Troubleshooting

| Symptom | Likely reason | Action |
|---|---|---|
| `No pubspec.yaml file found` | Terminal is in the parent folder. | `cd` into the inner project folder containing `pubspec.yaml`. |
| `No Android folder found` / Gradle project missing | Platform scaffold has not been generated. | Run `setup.bat` from the project root. |
| `cmdline-tools component is missing` | Android SDK Command-line Tools are not installed. | Android Studio → SDK Manager → SDK Tools → install Command-line Tools (latest). |
| `No supported devices connected` | Emulator is stopped or still booting. | Start Pixel in Device Manager; run `flutter devices` again. |
| `INSTALL_FAILED` or Gradle error | Platform scaffold or SDK setup is incomplete. | Run `flutter doctor -v`, `flutter pub get`, then `flutter run -v`. |
| App opens but cannot log in | No local account exists yet, or the credentials are incorrect. | Create an account on this device, then sign in with that email and password. |

## Tests

`flutter analyze` and `flutter test` pass. Tests cover local registration/sign-in, login-form validation, model parsing, and formatting helpers.

## Security note

The current local-only sign-in is for development and must not be used as production authentication. Before sharing real property or tenant data, connect Firebase Authentication and secure database rules.
