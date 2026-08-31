# Implementation Plan - Default Country Kuwait in HomeFragment

Set "Kuwait" as the default selected country in `HomeFragment` when the screen loads.

## Proposed Changes

### [app](file:///C:/Users/tausi/Android Projects/Desynate/desynat-adminapp-android/app)

#### [MODIFY] [HomeFragment.kt](file:///C:/Users/tausi/Android Projects/Desynate/desynat-adminapp-android/app/src/main/java/com/desynatadmin/ui/home/fragment/HomeFragment.kt)

- Add `setDefaultCountry()` method to identify "Kuwait" from the `countryList`, update the UI, and trigger data fetching.
- Update `initView()` to call `setDefaultCountry()` after loading countries from `SharedPreference`.
- Update `observe()` to call `setDefaultCountry()` after receiving the country list from the API.
- Adjust `initView()` to avoid redundant `getDashBoardData()` calls.

## Verification Plan

### Manual Verification
- Launch the app and navigate to `HomeFragment`.
- Verify that `etCountry` shows "Kuwait" (or its Arabic equivalent) by default.
- Verify that the dashboard data is automatically fetched for Kuwait.
- Open the country picker and verify that Kuwait is present and other countries can still be selected.
