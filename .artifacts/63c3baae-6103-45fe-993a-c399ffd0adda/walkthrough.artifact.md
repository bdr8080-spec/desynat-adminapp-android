# Walkthrough - Fixed Rejection Reason Visibility

I have fixed the issue where the rejection reason fields (`tvReason` and `tvReasonValue`) were not behaving correctly. They are now explicitly shown ONLY when the request status is "Rejected" (`adminVerify == "2"`).

## Changes Made

### [SellerStoryDetailFragment.kt](file:///D:/fluper_project/desynat-adminapp-android/app/src/main/java/com/desynatadmin/ui/home/bannerRequestManagement/SellerStoryDetailFragment.kt)

- **Explicit Visibility Control**: Updated `setData()` to explicitly hide `tvReason` and `tvReasonValue` for "New" (`"0"`) and "Approved" (`"1"`) statuses. This ensures that if a user views a rejected request and then navigates to a new one, the old rejection reason is cleared and hidden.
- **Code Consolidation**: Removed a redundant `when(bannerData?.adminVerify)` block from `initView()`. This block was overriding the logic in `setData()` and making the code harder to maintain.
- **Improved Maintainability**: All status-based UI updates (text, colors, and visibility) are now centralized in the `setData()` method.

## Verification Results

### Automated Tests
- Successfully ran `./gradlew :app:compileDebugKotlin` to ensure the changes are syntactically correct and the project builds.

### Manual Verification
- Verified that `tvReason` and `tvReasonValue` are only visible in the `adminVerify == "2"` case.
- Verified that they are hidden in the `adminVerify == "0"` and `adminVerify == "1"` cases.
