# Implementation Plan - Fix Reason Visibility and Update Logic in SellerStoryDetailFragment

The goal is to ensure `tvReasonValue` is visible and correctly updated when a story is rejected, even if it was previously approved.

## Proposed Changes

### [app] Component

#### [MODIFY] [SellerStoryDetailFragment.kt](file:///D:/fluper_project/desynat-adminapp-android/app/src/main/java/com/desynatadmin/ui/home/bannerRequestManagement/SellerStoryDetailFragment.kt)

- **Consolidate UI Logic**: Move all `adminVerify` related UI updates from `initView()` to `setData()`.
- **Update Rejection Logic**:
    - In `setData()`, ensure `btnReject` is visible when `adminVerify == "1"` to allow rejection of approved stories.
    - In `setData()`, ensure `tvReason` and `tvReasonValue` are visible and correctly populated when `adminVerify == "2"`.
- **Fix Observers**:
    - Update `rejectStoryRequestResult` observer to call `ProgressDialogUtils.getInstance().hideProgress()` and set `isObserver = false`.
    - Update `approveBannerRequestResult` observer similarly.
- **Sub-Admin Access**: Ensure sub-admin access check (`actionAccess`) is correctly integrated with the status-based visibility.

## Verification Plan

### Automated Tests
- Use `analyze_file` to check for syntax errors after modifications.

### Manual Verification
1.  Navigate to `SellerStoryDetailFragment` with a new story (`adminVerify == "0"`).
2.  Reject the story. Verify that:
    - Success dialog appears.
    - Progress dialog is hidden.
    - `tvReasonValue` becomes visible with the entered reason.
    - Status changes to "Rejected".
3.  Navigate with an approved story (`adminVerify == "1"`).
4.  Verify `btnReject` is now visible.
5.  Reject the approved story. Verify that it correctly transitions to the "Rejected" state with the reason visible.
6.  Verify that sub-admin permissions still correctly hide/show the buttons based on `actionAccess`.

> [!IMPORTANT]
> The user specifically mentioned that if `adminVerify == "1"`, they want to be able to hit the reject API. This requires changing the visibility logic for `btnReject` in the `"1"` state.
