# ProofNest Mobile — Implementation Status

**Review basis:** Flutter/Dart source tree.  
**Review date:** 11 November 2026.  
**Important:** The Java backend has been removed. Local-only registration and sign-in are implemented in Dart, with accounts stored in device secure storage. This is a development prototype, not production authentication. Shared property/inspection data and Firebase integration are not implemented.

## 1. Implemented in the Flutter source

The following client screens and API-client methods are present in this ZIP:

| Area | Present code | Notes |
|---|---|---|
| App launch/auth gate | `lib/main.dart`, `lib/app.dart` | Shows splash, then login or signed-in shell according to stored token/session. |
| Login | `lib/screens/auth/login_screen.dart` | Validates locally stored device account credentials. |
| Registration | `lib/screens/auth/register_screen.dart` | Creates a local account with landlord/tenant role selection. |
| Secure local account/session storage | `lib/core/secure_store.dart`, `lib/state/auth_controller.dart` | Uses `flutter_secure_storage`; account access is device-local. |
| Dashboard | `lib/screens/dashboard_screen.dart` | Shows an empty local summary until shared data storage is added. |
| Properties | `lib/screens/properties/` | Property list, detail, create/edit, delete, and add tenant by email. |
| Inspections | `lib/screens/inspections/` | List, create/edit, status, areas/rooms, photo upload/delete, photo viewer. |
| Comparison | `lib/screens/comparison/` | Move-in/move-out comparison, comments, and finding creation entry points. |
| Findings | `lib/screens/findings/` | Findings list/detail/form, with dispute/resolve actions based on server flags. |
| Profile | `lib/screens/profile_screen.dart` | Profile and sign-out flow. |
| Drawer navigation | `lib/widgets/app_drawer.dart` | Chat, tenants, settings, and sign-out actions are available from the main tabs. Chat and tenant screens display empty states until shared storage is configured. |
| Shared design | `lib/core/theme.dart`, `lib/widgets/` | Dark theme, crimson accent, branding and reusable UI components. |
| Tests | `test/` | Model parsing, local registration/login, and login-form validation. |

## 2. Platform setup

- `android/` is present in the current workspace.
- `ios/` is optional for the agreed Android-first milestone.
- Run `setup.bat` from the project root if the Android platform folder is missing in another copy.

## 3. BRD features not implemented in this ZIP yet

These are included in `BRD.md`, but there are no corresponding complete screens/models/API methods in the supplied source:

| BRD feature | Status in ZIP | Planned implementation area |
|---|---|---|
| Landlord–tenant chat | Not implemented | Conversations, members, messages, attachments, read state, secure message API. |
| Maintenance requests | Not implemented | Request creation, category/urgency, schedule, status history, repair evidence. |
| Property timeline | Not implemented | Aggregated, timestamped property activity feed. |
| Notifications/reminders | Not implemented | In-app notification centre; push provider can be a later integration. |
| Guided room checklist | Partial foundation only | Generic inspection areas exist; structured checklist items and required-photo tracking are missing. |
| Joint finding responses | Partial only | Finding dispute/resolve exists; separate landlord and tenant response records are not present in client models/API. |
| PDF reports and acknowledgements | Not implemented | Report generation/download and party acknowledgement records. |
| Deposit/handover | Not implemented | Deposit ledger/proposals, keys/access cards, handover record. |
| Inventory | Not implemented | Furniture/appliance/keys inventory and condition observations. |
| Utility meter readings | Not implemented | Meter type, value/unit, photo, reading date, move-in/out comparison. |
| Offline inspection sync | Not implemented | Local persistence, outbox, idempotency keys, retry, conflict resolution. |
| Secure invitation links/codes | Partial only | Add-tenant-by-email exists; one-time invitation token and accept/revoke flow are not present. |
| Evidence version history and audit events | Partial only | Evidence metadata displays server-provided SHA-256; full versioning and audit event client workflows are absent. |

**AI Inspection Assistant is excluded** from the project scope and should not be implemented.

## 4. Data status

There is no Java backend or Firebase connection. Accounts are stored only on the device so registration, sign-in, profile editing, dashboard, and main navigation can be exercised. The property, inspection, finding, and comparison lists are empty; data creation, editing, evidence upload, and cross-device access require a future Firebase implementation.

## 5. Organization assessment

The current code is already separated into understandable layers:

- `core/`: shared configuration, theme, formatting, secure storage, errors, UI helpers.
- `data/`: temporary local-only provider.
- `models/`: typed JSON models shared by screens.
- `state/`: authentication/session state.
- `screens/`: application screens, with subfolders per feature.
- `widgets/`: reusable visual components.
- `test/`: automated tests.
- `tool/`: platform setup helper.

**Recommendation:** Keep this existing structure for the first successful build. A large move to a new `features/` architecture before running the app would create unnecessary import and build risk. As new modules are added, group them under `screens/chat/`, `screens/maintenance/`, `screens/handover/`, and similar feature folders, then move their models/API methods into clear files when they become large enough.

## 6. Fixes applied in this updated package

1. Added the full approved mobile `BRD.md` at the project root.
2. Rewrote `setup.ps1` to detect the project root, generate the missing Android platform with Flutter CLI, apply manifest settings, fetch packages, and print emulator diagnostics.
3. Improved the Android manifest patcher so Internet permission, development cleartext access, and app label are applied idempotently.
4. Replaced API-based sign-in with local-only registration and sign-in while Firebase is undecided.
5. Improved session bootstrap error handling so an unexpected response does not leave the app stuck on the splash screen.
6. Rewrote `README.md`, `SETUP_WINDOWS.md`, and `PROJECT_STRUCTURE.md` for a Dart-only local run without a Java server.
7. Added this status report and VS Code tasks/extensions recommendations.

## 7. Verification status

Run from the folder containing `pubspec.yaml`:

```powershell
.\setup.bat
flutter analyze
flutter test
flutter devices
flutter run
```

`flutter pub get`, `flutter analyze`, and `flutter test` passed after the Java removal. An Android emulator was not connected during this validation, so device runtime behavior still needs confirmation on the developer's machine.
