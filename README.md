# Utility Ledger — Android (Kotlin / Jetpack Compose)

A native rewrite of the web ledger: Rent / Electricity / Mobile / Recharge / Other
bills with sub-category, name, min-balance (Other), recurring due dates, plus a
Portal balance tracker (Clinova, Shiprocket, etc.) with its own due date and
recurrence. Data is stored locally in a Room (SQLite) database. Reminders are
real local notifications fired by WorkManager, 2 days before anything unpaid is
due — this works even if the app is closed, which a browser file never could.

## Important — read before building

I wrote this in a sandboxed environment **without an Android SDK**, so none of
this has been compiled or run. It's written carefully against stable, well-known
APIs (Room 2.6, Compose Material3 via compose-bom 2024.02.00, WorkManager 2.9,
DataStore 1.0), but treat it as a strong first draft, not a finished, tested app.
Expect to fix a handful of small issues on first build — most likely:

- Minor Compose API mismatches if Android Studio nudges you to a newer
  compose-bom / Material3 version (the DatePicker APIs in particular have
  moved around between Material3 releases).
- Gradle/AGP version prompts — Android Studio will likely offer to upgrade
  `com.android.application` and the Kotlin Gradle plugin on first sync. Accepting
  those upgrades is usually fine and often fixes more than it breaks.
- The app icon uses a system placeholder drawable (`@android:drawable/sym_def_app_icon`)
  so the project doesn't need generated mipmap resources to build. Swap in a real
  icon via **Android Studio → New → Image Asset** whenever you like.

## How to build

1. Install **Android Studio** (Hedgehog/2023.1.1 or newer).
2. **File → Open**, select the `UtilityLedger` folder (the one with `settings.gradle.kts`).
3. Let Gradle sync. If prompted to create a Gradle wrapper or upgrade AGP, accept it.
4. Run on an emulator or a physical device (**Run ▸ app**).
5. On first launch, Android will ask for notification permission (Android 13+) —
   accept it, or reminders can't be posted.

## What's implemented

- Room database (`entries`, `portals` tables) — survives app restarts, backups
  included via `android:allowBackup="true"`.
- Bills: category, sub-category, name, amount, due date, note, recurring
  interval (day/week/month/year), minimum balance (Other category only).
- Portals: name, minimum balance, current balance (editable inline), due date,
  recurring interval, note. Shows "Recharge now" the moment balance ≤ minimum.
- Marking something paid/recharged on a recurring item auto-advances its due
  date to the next cycle — same behavior as the web version.
- Month navigation and category tabs, same filtering logic as the web app.
- `ReminderWorker` runs roughly daily via WorkManager and posts a real Android
  notification for anything unpaid due within 2 days — no calendar app, no
  browser tab needed, works with the app fully closed.
- Currency symbol persisted via DataStore.

## What's intentionally left for you

- **App icon / branding** — currently a system placeholder.
- **Editing** an existing entry (currently: delete and re-add).
- **Export/sharing** between devices — this is single-device local storage,
  same limitation the web version's "saved in this browser only" mode had.
- Polishing the visual design — this focuses on correct structure and working
  data flow over the passbook aesthetic from the web version. Very doable to
  reskin once it's compiling.
