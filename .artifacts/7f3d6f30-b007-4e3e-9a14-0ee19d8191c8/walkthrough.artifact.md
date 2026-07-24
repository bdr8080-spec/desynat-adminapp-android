# Added "Recommended Store" to Plan Detail Screen

I have added the "Show in Recommended Stores" information to the Plan Detail screen.

## Changes Made

### 1. Updated `fragment_plan_detail.xml`
- Added a new `LinearLayout` to display the "Show in Recommended Stores" status.
- Positioned it right after the Commission section.
- Used the existing string resource and added a `tvRecommendedStore` TextView for the value.

### 2. Updated `PlanDetailFragment.kt`
- Added logic in `setData()` to set the text of `tvRecommendedStore`.
- It displays **"Yes"** if the plan is for a recommended store (`isRecommendedStore == 1`) and **"No"** otherwise.

## Verification

The Plan Detail screen now correctly shows whether a subscription plan includes the "Show in Recommended Stores" feature.
