# Add "Recommended Store" to Plan Detail Fragment

The user wants to display the "Recommended Store" status in the `PlanDetailFragment`, similar to how it was added to the `SubscriptionAdapter`.

## Proposed Changes

### Layout

#### [MODIFY] [fragment_plan_detail.xml](file:///D:/fluper_project/desynat-adminapp-android/app/src/main/res/layout/fragment_plan_detail.xml)
- Add a new `LinearLayout` (`llRecommendedStore`) with a `tvRecommendedStore` TextView to display the status.
- Place it after the Commission layout.

### Fragment

#### [MODIFY] [PlanDetailFragment.kt](file:///D:/fluper_project/desynat-adminapp-android/app/src/main/java/com/desynatadmin/ui/home/subscriptionPlan/PlanDetailFragment.kt)
- In `setData()`, populate `tvRecommendedStore` with "Yes" or "No" based on `subscriptionData?.isRecommendedStore`.

## Verification Plan

### Manual Verification
- Navigate to the Plan Detail screen for various plans.
- Verify that "Show in Recommended Stores" correctly displays "Yes" or "No" after the Commission field.
