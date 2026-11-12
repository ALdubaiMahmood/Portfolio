# Windows setup — ProofNest Mobile

This guide applies to the folder containing `pubspec.yaml` and `lib/main.dart`.

## 1. Required tools

- Flutter SDK (the user machine currently reports Flutter 3.47.9 at `C:\flutter`).
- Android Studio.
- Android SDK Platform, Build-Tools, Platform-Tools, Command-line Tools (latest), and Android Emulator.
- A Pixel virtual device in Android Studio Device Manager.

In Android Studio, use **More Actions → SDK Manager → SDK Tools** and install the Android SDK Command-line Tools (latest), Platform-Tools, Build-Tools, and Android Emulator.

Check the tools in PowerShell:

```powershell
flutter --version
flutter doctor -v
flutter emulators
flutter devices
```

A Visual Studio warning is not relevant when building only for Android. An Android toolchain warning about missing Command-line Tools is relevant and should be fixed in SDK Manager.

## 2. Use the correct project directory

The uploaded ZIP extracts to a folder named `ProofNest_App`. In the user's current nested arrangement, the actual project root is:

```text
D:\development\Portfolio\ProofNest_App1.0
```

The correct root contains `pubspec.yaml`, `lib\main.dart`, `setup.bat`, and `tool\setup_platforms.dart`. If the terminal says `No pubspec.yaml file found`, it is one folder too high or in the wrong directory.

## 3. Generate the Android platform project

The current workspace includes `android/`. If the folder is missing from another copy, run this from the project root:

```powershell
.\setup.bat
```

The script creates `android/` when needed, applies the platform settings, and runs `flutter pub get`.

You can also run the key commands manually:

```powershell
flutter create --no-pub --project-name proofnest --org com.proofnest --platforms=android .
dart tool/setup_platforms.dart
flutter pub get
```

Do not run `flutter create` from the parent folder and do not create a second nested project.

## 4. Start the emulator and run the app

Start `Pixel_8_Pro` in Android Studio Device Manager, or from PowerShell:

```powershell
flutter emulators
flutter emulators --launch Pixel_8_Pro
flutter devices
flutter run
```

No Java service or API server is required. Registration and sign-in are currently local to the device; shared data and Firebase integration are not implemented yet.

## 5. Validate

```powershell
flutter pub get
flutter analyze
flutter test
```

Fix any analyzer/test errors before adding more modules.

## 6. Production authentication and data

The local account mode is only for development. Before using real tenant/property data, connect Firebase Authentication and configure Firebase database security rules.
