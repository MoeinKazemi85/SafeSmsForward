# SafeSmsForward

Minimal Android app (Kotlin) that forwards SMS messages only from allowed senders.
- Add allowed senders in the app UI.
- Set a target phone number to forward to.
- App ignores other messages (e.g., short codes / bank OTPs) by default.

**How to build**
1. Open this folder in Android Studio.
2. Let Android Studio sync Gradle.
3. Connect an Android device (or use emulator) and run 'app' in debug mode, or Build -> Build Bundle(s) / APK(s).

**Security**
- App requires READ_SMS, RECEIVE_SMS, SEND_SMS and READ_CONTACTS.
- Review source before installing. Don't use with third-party SIMs.

