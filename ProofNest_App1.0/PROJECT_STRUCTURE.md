# ProofNest code organization

The current app is a Flutter/Dart-only local prototype. Keep feature screens grouped by feature and keep shared state, storage, and data access in their existing layers.

```text
lib/
├── main.dart
├── app.dart
├── core/
│   ├── api_exception.dart
│   ├── config.dart
│   ├── format.dart
│   ├── secure_store.dart
│   ├── theme.dart
│   └── ui_helpers.dart
├── data/
│   └── proofnest_api.dart
├── models/
│   └── models.dart
├── state/
│   └── auth_controller.dart
├── screens/
│   ├── auth/
│   ├── comparison/
│   ├── findings/
│   ├── inspections/
│   ├── properties/
│   ├── dashboard_screen.dart
│   ├── home_shell.dart
│   └── profile_screen.dart
└── widgets/
```

## Rules for future additions

1. Keep feature UI in `screens/<feature>/` and reusable UI in `widgets/`.
2. Keep response/domain parsing in model classes rather than parsing raw data in widgets.
3. Keep local account/session secrets in `SecureStore`; never store raw passwords.
4. The current authentication and data provider are development-only and device-local. Do not treat them as production security or shared storage.
5. Before storing real tenancy data, choose and configure Firebase (or another approved backend), authenticate users, and enforce record-level access in its security rules.
6. Add unit/widget tests with each new feature.
7. Update `BRD.md`, `IMPLEMENTATION_STATUS.md`, and user documentation when behavior changes.

Future feature areas include chat, maintenance, timeline, notifications, handover, and reports. Cloud storage and synchronization remain future work.
