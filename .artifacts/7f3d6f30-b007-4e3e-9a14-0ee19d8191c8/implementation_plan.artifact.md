# Fix Null Value for `rejectReason` in `RejectStoryResponse`

The user is experiencing an issue where `response.data?.rejectReason` is returning `null` even though the JSON response contains a value for `reject_reason`.

## Analysis

The root cause is a mismatch between the serialization library used in the data model and the one used by Retrofit:
1.  **Retrofit Configuration**: The project uses `GsonConverterFactory` in `AppModule.kt`, meaning `Gson` is used for deserializing network responses.
2.  **Data Model**: `RejectStoryResponse.kt` uses `kotlinx.serialization` annotations (`@Serializable`, `@SerialName`).
3.  **Conflict**: `Gson` does not recognize `@SerialName`. It requires `@SerializedName` from the `com.google.gson.annotations` package. Since it doesn't see its expected annotation, it tries to match the Kotlin property name (`rejectReason`) with the JSON key (`reject_reason`), which fails.

## Proposed Changes

### [Component Name]

#### [MODIFY] [RejectStoryResponse.kt](file:///D:/fluper_project/desynat-adminapp-android/app/src/main/java/com/desynatadmin/ui/home/bannerRequestManagement/response/RejectStoryResponse.kt)
- Remove `kotlinx.serialization` imports and annotations.
- Add `com.google.gson.annotations.SerializedName` imports.
- Replace all `@SerialName` annotations with `@SerializedName`.
- Remove `@Serializable` annotations.

## Verification Plan

### Manual Verification
- Ask the user to run the app and perform the reject story action again.
- Verify that the toast now shows the correct reject reason instead of `null`.
