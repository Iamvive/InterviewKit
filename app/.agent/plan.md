# Project Plan



## Project Brief



## Implementation Steps

### Task_1_Setup_Data_Layer: Configure project dependencies (Hilt, Retrofit, Serialization, Coil, Nav 3, Adaptive), setup Hilt Application, and implement the Data and Domain layers including API service and Repository for JSONPlaceholder posts.
- **Status:** COMPLETED
- **Updates:** Successfully set up the infrastructure and data layer for InterviewKit.
- **Acceptance Criteria:**
  - Project builds successfully
  - Retrofit service for JSONPlaceholder is defined
  - Hilt modules for Network and Repository are implemented
  - Repository returns a Flow of Domain models

### Task_2_UI_ViewModel_PostFeed: Implement the PostFeedViewModel with reactive state management and create the PostFeedScreen using Jetpack Compose Material 3 and Coil for image/placeholder loading.
- **Status:** COMPLETED
- **Updates:** Implemented HomeViewModel and HomeScreen.
- **Acceptance Criteria:**
  - ViewModel uses collectAsStateWithLifecycle
  - PostFeedScreen displays a list of posts
  - Coil is integrated for visual elements
  - Timber is used for logging

### Task_3_Navigation_Adaptive: Integrate Jetpack Navigation 3 for app routing and implement an adaptive UI using Compose Material Adaptive to ensure the post feed is responsive.
- **Status:** IN_PROGRESS
- **Updates:** Code verification passed, but identified minor alignment issues with the original prompt:
- Reverting targetSdk from 36 to 35 as requested.
- Switching from Navigation 3 (Alpha) to Navigation 2.8.x (Stable Type-Safe) to ensure the starter project is stable and follows current production standards.
Reopening Task 3 for these adjustments.
- **Acceptance Criteria:**
  - Jetpack Navigation 3 is used for screen routing
  - UI adapts correctly to different screen sizes (e.g., responsive list/grid)
  - Navigation between screens (if applicable) works smoothly

### Task_4_Run_Verify: Finalize any remaining DI/Timber setup, perform a full build, and verify the application stability and requirement alignment.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - App does not crash on startup or during list loading
  - UI is responsive and matches Material 3 guidelines
  - Build pass and all existing tests pass
  - Critic agent verifies stability and requirement alignment
- **StartTime:** 2026-09-13 18:33:59 IST

